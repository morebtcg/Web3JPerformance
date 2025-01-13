package org.fisco.bcos.web3.performance;

import com.moandjiezana.toml.Toml;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.fisco.bcos.web3.connection.Web3jConnection;
import org.fisco.bcos.web3.contract.Ok;
import org.fisco.bcos.web3.utils.Collector;
import org.fisco.bcos.web3.utils.ConfigUtils;
import org.fisco.bcos.web3.utils.ConnectionConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class OkTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(OkTest.class);

	public static void Usage() {
		System.out.println(" Usage:");
		System.out.println("===== OkTest ===========");
		System.out.println(
				" \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.web3.performance.OkTest [users] [count] [qps].");
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			Usage();
			return;
		}

		Toml connectionToml = ConfigUtils.getToml("classpath:config.toml");
		ConnectionConfigParser connectionConfigParser = new ConnectionConfigParser(connectionToml.toMap());

		int users = Integer.parseInt(args[0]);
		int count = Integer.parseInt(args[1]);

		ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(0), BigInteger.valueOf(300000));
		Web3jConnection createWeb3Connection = new Web3jConnection(connectionConfigParser);
		Web3j createWeb3j = createWeb3Connection.getWeb3j();
		Credentials createCredentials = Credentials.create(String.format("0x%032d", users + 1));
		Integer pollingInterval = 500;
		TransactionManager fastRawTxMgr =new FastRawTransactionManager(createWeb3j, createCredentials, new PollingTransactionReceiptProcessor(createWeb3j, pollingInterval, 40));
		Ok createOk = Ok.deploy(createWeb3j, fastRawTxMgr, gasProvider).send();
		System.out.println("====== Deploy Ok, contract address: " + createOk.getContractAddress());

		int total = users * count;
		Collector collector = new Collector();
		collector.setTotal(total);

		ProgressBar sendedBar = new ProgressBarBuilder().setTaskName("Send   :").setInitialMax(total)
				.setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
		ProgressBar receivedBar = new ProgressBarBuilder().setTaskName("Receive:").setInitialMax(total)
				.setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
		CountDownLatch transactionLatch = new CountDownLatch(total);
		AtomicLong totalCost = new AtomicLong(0);

		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < users; ++i) {
			final  int index = i;
			threads.add(Thread.ofVirtual().name("RPC-" + i).start(() -> {
				Web3jConnection web3jConnection = new Web3jConnection(connectionConfigParser);
				Web3j web3j = web3jConnection.getWeb3j();
				Credentials credentials = Credentials.create(String.format("0x%032d", index + 1));

				TransactionManager txMgr =new FastRawTransactionManager(web3j, credentials, new PollingTransactionReceiptProcessor(web3j, pollingInterval, 40));
				Ok ok = Ok.load(createOk.getContractAddress(), web3j, txMgr, gasProvider);

				for (int j = 0; j < count; ++j) {
					long startTime = System.currentTimeMillis();
					try {
						TransactionReceipt receipt = ok.trans(BigInteger.valueOf(4)).send();
						long receiveTime = System.currentTimeMillis() - startTime;
						receivedBar.step();
						transactionLatch.countDown();
						totalCost.addAndGet(System.currentTimeMillis() - startTime);
						collector.onMessage(receipt, receiveTime);
					} catch (Exception e) {
						LOGGER.error("error:", e);
						TransactionReceipt errorReceipt = new TransactionReceipt();
						errorReceipt.setStatus("0x2");
						errorReceipt.setRevertReason(e != null ? e.getMessage() : "receipt is null");
						long receiveTime = System.currentTimeMillis() - startTime;
						collector.onMessage(errorReceipt, receiveTime);
					}
				}
			}));
		}

		transactionLatch.await();
		sendedBar.close();
		receivedBar.close();
		collector.report();
		System.exit(0);
	}
}
