package org.fisco.bcos.web3.performance;

import com.google.common.util.concurrent.RateLimiter;
import com.moandjiezana.toml.Toml;
import org.fisco.bcos.web3.connection.Web3jConnection;
import org.fisco.bcos.web3.contract.Ok;
import org.fisco.bcos.web3.utils.ConfigUtils;
import org.fisco.bcos.web3.utils.ConnectionConfigParser;
import org.fisco.bcos.web3.utils.PerformanceCollector;
import org.fisco.bcos.web3.utils.ThreadPoolService;
import org.web3j.protocol.Web3j;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.StaticEIP1559GasProvider;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

public class OkTest {
    private static Web3j web3j;
    private static AtomicInteger sendedTransactions = new AtomicInteger(0);

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
        Credentials credentials = Credentials.create("0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef");


        Integer count = Integer.valueOf(args[0]);
        Integer qps = Integer.valueOf(args[1]);

        System.out.println("====== Deploy Ok ====== ");
        StaticEIP1559GasProvider gasProvider = new StaticEIP1559GasProvider(20200, BigInteger.valueOf(30000000000L)
                , BigInteger.valueOf(30000000000L), BigInteger.valueOf(30000000000L));
        Ok ok = Ok.deploy(web3j, credentials, gasProvider).send();
        System.out.println("====== Deploy Ok, contract address: " + ok.getContractAddress());

        PerformanceCollector collector = new PerformanceCollector();
        collector.setStartTimestamp(System.currentTimeMillis());
        collector.setTotal(count);
        RateLimiter limiter = RateLimiter.create(qps);
        Integer area = count / 10;
        final Integer total = count;

        System.out.println("====== PerformanceOk trans start ======");
        ThreadPoolService threadPoolService = new ThreadPoolService("PerformanceOk", 1000000);

        for (Integer i = 0; i < count; ++i) {
            limiter.acquire();
            threadPoolService
                    .getThreadPool()
                    .execute(
                            () -> {
                                long startTime = System.currentTimeMillis();
                                try {
                                    ok.trans(new BigInteger("4")).sendAsync().whenComplete(
                                            (receipt, e) -> {
                                                long receiveTime = System.currentTimeMillis() - startTime;
                                                if (e != null || receipt == null) {
                                                    TransactionReceipt errorReceipt = new TransactionReceipt();
                                                    errorReceipt.setStatus("0x0");
                                                    collector.onMessage(errorReceipt, receiveTime);
                                                    return;
                                                }
                                                collector.onMessage(receipt, receiveTime);
                                            }
                                    );
                                } catch (Exception e) {
                                    TransactionReceipt receipt = new TransactionReceipt();
                                    receipt.setStatus("0x0");
                                    collector.onMessage(receipt, System.currentTimeMillis() - startTime);
                                }
                                int current = sendedTransactions.incrementAndGet();
                                if (current >= area && ((current % area) == 0)) {
                                    System.out.println(
                                            "Already sended: "
                                                    + current
                                                    + "/"
                                                    + total
                                                    + " transactions");
                                }
                            });
        }

        while (!collector.getReceived().equals(count)) {
            Thread.sleep(1000);
        }

        threadPoolService.stop();
        System.exit(0);
    }
}
