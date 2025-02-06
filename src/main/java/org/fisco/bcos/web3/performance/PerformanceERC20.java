package org.fisco.bcos.web3.performance;

import com.google.common.util.concurrent.RateLimiter;
import com.moandjiezana.toml.Toml;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.fisco.bcos.web3.connection.Web3jConnection;
import org.fisco.bcos.web3.contract.MyERC20;
import org.fisco.bcos.web3.utils.Collector;
import org.fisco.bcos.web3.utils.ConfigUtils;
import org.fisco.bcos.web3.utils.ConnectionConfigParser;
import org.fisco.bcos.web3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.RevertReasonExtractor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

public class PerformanceERC20 {
    private final static Logger LOGGER = LoggerFactory.getLogger(PerformanceERC20.class);

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println("===== PerformanceERC20 ===========");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.web3.performance.PerformanceERC20 [users] [total] [qps].");
    }

    public static class TransactionCallback implements Consumer<TransactionReceipt>
    {
        private final long startTime;
        private final Collector collector;
        private final ProgressBar receiveBar;
        private final CountDownLatch transactionLatch;
        public TransactionCallback(Collector collector,  ProgressBar receiveBar, CountDownLatch transactionLatch){
            this.startTime = System.currentTimeMillis();
            this.collector = collector;
            this.receiveBar = receiveBar;
            this.transactionLatch = transactionLatch;
        }
        @Override
        public void accept(TransactionReceipt transactionReceipt) {
            this.receiveBar.step();
            transactionLatch.countDown();
            collector.onMessage(transactionReceipt, System.currentTimeMillis() - this.startTime);
        }
    }

    public static class TransactionErrorCallback implements Function<Throwable, TransactionReceipt>
    {
        private final long startTime;
        private final Collector collector;
        private final ProgressBar receiveBar;
        private final CountDownLatch transactionLatch;

        public TransactionErrorCallback(Collector collector, ProgressBar receiveBar, CountDownLatch transactionLatch){
            this.startTime = System.currentTimeMillis();
            this.collector = collector;
            this.receiveBar = receiveBar;
            this.transactionLatch = transactionLatch;
        }
        @Override
        public TransactionReceipt apply(Throwable throwable) {
            this.receiveBar.step();
            transactionLatch.countDown();
            System.out.println("Send transaction failed, error: " + throwable.getMessage());
            TransactionReceipt errorReceipt = new TransactionReceipt();
            errorReceipt.setRevertReason(throwable.getMessage());
            errorReceipt.setStatus("-1");
            collector.onMessage(errorReceipt, System.currentTimeMillis() - this.startTime);
            return errorReceipt;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            Usage();
            return;
        }
        Toml connectionToml = ConfigUtils.getToml("classpath:config.toml");
        ConnectionConfigParser connectionConfigParser = new ConnectionConfigParser(connectionToml.toMap());

        int users = Integer.parseInt(args[0]);
        int total = Integer.parseInt(args[1]);
        int qps = Integer.parseInt(args[2]);

        System.out.println("=== PerformanceERC20, users: " + users + ", total: " + total + ", qps: " + qps);

        ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(0), BigInteger.valueOf(300000));
        Web3jConnection createWeb3Connection = new Web3jConnection(connectionConfigParser);
        Web3j deployWeb3j = createWeb3Connection.getWeb3j();
        Credentials mgrCredential = Credentials.create(String.format("0x%032d", users + 1));

        Integer pollingInterval = connectionConfigParser.getService().getReceiptPollingInterval();
        TransactionManager fastRawTxMgr =new FastRawTransactionManager(deployWeb3j, mgrCredential,
                new PollingTransactionReceiptProcessor(deployWeb3j,
                        pollingInterval, 40));
        String name = "testERC20";
        MyERC20 erc20 = MyERC20.deploy(deployWeb3j, fastRawTxMgr, gasProvider, name, name, BigInteger.valueOf(8)).send();
        System.out.println("====== PerformanceERC20 Deploy , contract address: " + erc20.getContractAddress() +  ", Receipt polling interval: " + pollingInterval + " ===");

        // mint total count of erc20
        long mintCount = (long)2 * (long)total;
        TransactionReceipt receipt = erc20.mint(mgrCredential.getAddress(), BigInteger.valueOf(mintCount)).send();
        if(receipt.isStatusOK()) {
            System.out.println("====== PerformanceERC20 mint success , mintCount: " + mintCount + ", to: " + mgrCredential.getAddress() + " ===");
        }else{
            System.out.println("====== PerformanceERC20 mint failed , mintCount: " +
                    mintCount + ", to: " + mgrCredential.getAddress() + "status: " + receipt.getStatus()+ " ===");
        }

        // transfer to all accounts
        long txsPerUser = total / users;
        Collector collector = new Collector();
        collector.setTotal(total);

        ProgressBar sendedBar = new ProgressBarBuilder().setTaskName("Send   :").setInitialMax(total)
                .setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
        ProgressBar receivedBar = new ProgressBarBuilder().setTaskName("Receive:").setInitialMax(total)
                .setStyle(ProgressBarStyle.UNICODE_BLOCK).build();
        CountDownLatch transactionLatch = new CountDownLatch(total);

        List<Thread> threads = new ArrayList<Thread>();
        RateLimiter limiter = RateLimiter.create(qps);
        for (int i = 0; i < users; ++i) {
            final  int index = i;
            threads.add(Thread.ofVirtual().name("RPC-" + i).start(() -> {
                try{
                    Credentials credentials = Credentials.create(String.format("0x%032d", index + 1));
                    for (long j = 0; j < txsPerUser; ++j) {
                        long startTime = System.currentTimeMillis();
                        try {
                            limiter.acquire();
                            // transfer 1 every time
                            CompletableFuture<TransactionReceipt> cf = erc20.transfer(
                                    credentials.getAddress(), BigInteger.valueOf(1)).sendAsync();
                            sendedBar.step();
                            cf.thenAccept(new TransactionCallback(collector, receivedBar, transactionLatch));
                            cf.exceptionally(new TransactionErrorCallback(collector, receivedBar, transactionLatch));
                        } catch (Exception e) {
                            LOGGER.error("error:", e);
                            TransactionReceipt errorReceipt = new TransactionReceipt();
                            errorReceipt.setStatus("0x2");
                            errorReceipt.setRevertReason(e != null ? e.getMessage() : "receipt is null");
                            long receiveTime = System.currentTimeMillis() - startTime;
                            collector.onMessage(errorReceipt, receiveTime);
                        }
                    }
                }catch(Exception e){
                    LOGGER.warn("send transaction exception, e: ", e);
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