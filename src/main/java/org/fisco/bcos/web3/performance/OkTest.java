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
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class OkTest {
    private static List<Web3j> web3j;

    private final static Logger LOGGER = LoggerFactory.getLogger(OkTest.class);

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println("===== OkTest ===========");
        System.out.println(
                " \t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.web3.performance.OkTest [count] [qps].");
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            Usage();
            return;
        }

        Toml connectionToml = ConfigUtils.getToml("classpath:config.toml");
        ConnectionConfigParser connectionConfigParser = new ConnectionConfigParser(connectionToml.toMap());
        Web3jConnection web3jConnection = new Web3jConnection(connectionConfigParser);
        web3j = web3jConnection.getWeb3j();
        Credentials credentials = Credentials.create("0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80");


        int count = Integer.parseInt(args[0]);
        int qps = Integer.parseInt(args[1]);

        System.out.println("====== Deploy Ok ====== ");
        StaticEIP1559GasProvider gasProvider = new StaticEIP1559GasProvider(31337, BigInteger.valueOf(875000000L)
                , BigInteger.valueOf(30000000L), BigInteger.valueOf(30000000L));
        Ok ok = Ok.deploy(web3j.get(0), credentials, gasProvider).send();
        System.out.println("====== Deploy Ok, contract address: " + ok.getContractAddress());

        List<Ok> oks = new ArrayList<>(web3j.size());
        for (Web3j subWeb3j : web3j) {
            Ok subOk = Ok.load(ok.getContractAddress(), subWeb3j, credentials, gasProvider);
            oks.add(subOk);
        }
        Collector collector = new Collector();
        collector.setTotal(count);
        RateLimiter limiter = RateLimiter.create(qps);

        System.out.println("Web3J object size: " + web3j.size() + ", count: " + count + ", qps: " + qps + ", oks size: " + oks.size());
        ProgressBar sendedBar =
                new ProgressBarBuilder()
                        .setTaskName("Send   :")
                        .setInitialMax(count)
                        .setStyle(ProgressBarStyle.UNICODE_BLOCK)
                        .build();
        ProgressBar receivedBar =
                new ProgressBarBuilder()
                        .setTaskName("Receive:")
                        .setInitialMax(count)
                        .setStyle(ProgressBarStyle.UNICODE_BLOCK)
                        .build();
        CountDownLatch transactionLatch = new CountDownLatch(count);
        AtomicLong totalCost = new AtomicLong(0);

        System.out.println("====== PerformanceOk trans start ======");
        collector.setStartTimestamp(System.currentTimeMillis());
        IntStream.range(0, count).parallel().forEach(
                i -> {
                    limiter.acquire();
                    long startTime = System.currentTimeMillis();
                    try {
                        oks.get(i % oks.size())
                                .trans(new BigInteger("4")).sendAsync().whenComplete(
                                        (receipt, e) -> {
                                            long receiveTime = System.currentTimeMillis() - startTime;
                                            receivedBar.step();
                                            transactionLatch.countDown();
                                            totalCost.addAndGet(System.currentTimeMillis() - startTime);

                                            if (e != null || receipt == null) {
                                                if (e != null) {
                                                    LOGGER.error("error:", e);
                                                }
                                                TransactionReceipt errorReceipt = new TransactionReceipt();
                                                errorReceipt.setStatus("0x2");
                                                errorReceipt.setRevertReason(e != null ? e.getMessage() : "receipt is null");
                                                collector.onMessage(errorReceipt, receiveTime);
                                                return;
                                            }
                                            collector.onMessage(receipt, receiveTime);
                                        }
                                );
                        sendedBar.step();
                    } catch (Exception e) {
                        TransactionReceipt receipt = new TransactionReceipt();
                        receipt.setStatus("0x3");
                        collector.onMessage(receipt, System.currentTimeMillis() - startTime);
                        transactionLatch.countDown();
                    }
                }
        );
        transactionLatch.await();
        sendedBar.close();
        receivedBar.close();
        collector.report();
        System.exit(0);
    }
}
