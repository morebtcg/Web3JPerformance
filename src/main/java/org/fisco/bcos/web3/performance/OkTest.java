package org.fisco.bcos.web3.performance;

import com.google.common.util.concurrent.RateLimiter;
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
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticEIP1559GasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

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

		var connectionToml = ConfigUtils.getToml("classpath:config.toml");
		var connectionConfigParser = new ConnectionConfigParser(connectionToml.toMap());

		int users = Integer.parseInt(args[0]);
		int count = Integer.parseInt(args[1]);

		var gasProvider = new StaticGasProvider(BigInteger.valueOf(0), BigInteger.valueOf(300000));
		var createWeb3Connection = new Web3jConnection(connectionConfigParser);
		var createWeb3j = createWeb3Connection.getWeb3j();
		var createCredentials = Credentials.create(String.format("0x%032d", users + 1));
		var createOk = Ok.deploy(createWeb3j, createCredentials, gasProvider).send();
		System.out.println("====== Deploy Ok, contract address: " + createOk.getContractAddress());

		var total = users * count;
		Collector collector = new Collector();
		collector.setTotal(total);

		var sendedBar = new ProgressBarBuilder().setTaskName("Send   :").setInitialMax(total)
				.setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
		var receivedBar = new ProgressBarBuilder().setTaskName("Receive:").setInitialMax(total)
				.setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
		var transactionLatch = new CountDownLatch(total);
		var totalCost = new AtomicLong(0);

		var threads = new ArrayList<Thread>();
		for (var i = 0; i < users; ++i) {
			final var index = i;
			threads.add(Thread.ofVirtual().name("RPC-" + i).start(() -> {
				var web3jConnection = new Web3jConnection(connectionConfigParser);
				var web3j = web3jConnection.getWeb3j();

				Credentials credentials = Credentials.create(String.format("0x%032d", index + 1));
				var ok = Ok.load(createOk.getContractAddress(), web3j, credentials, gasProvider);

				for (var j = 0; j < count; ++j) {
					long startTime = System.currentTimeMillis();
					try {
						var receipt = ok.trans(BigInteger.valueOf(4)).send();
						var receiveTime = System.currentTimeMillis() - startTime;
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
