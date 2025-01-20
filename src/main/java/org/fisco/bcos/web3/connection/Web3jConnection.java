package org.fisco.bcos.web3.connection;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.fisco.bcos.web3.utils.ConnectionConfigParser;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.besu.Besu;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

public class Web3jConnection {
	private String url;
	private Web3j web3j;
	private Long chainID;
	private OkHttpClient client;

	public Web3jConnection(ConnectionConfigParser config) throws Exception{
		this.url = config.getService().getUrl();
		this.chainID = config.getService().getChainID();

		if (url == null || url.isEmpty()) {
			throw new RuntimeException("url not set");
		}
		client = HttpService.getOkHttpClientBuilder().connectTimeout(50L, TimeUnit.SECONDS)
				.readTimeout(50L, TimeUnit.SECONDS).writeTimeout(50L, TimeUnit.SECONDS)
				.connectionPool(new ConnectionPool(1, 100, TimeUnit.SECONDS)).addInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request originalRequest = chain.request();
						Request newRequest = originalRequest.newBuilder().header("Accept-Encoding", "identity").build();
						return chain.proceed(newRequest);
					}
				}).build();
		WebSocketService webSocketService = new WebSocketService(url, false);
		webSocketService.connect();
		web3j = Besu.build(webSocketService, 500, Executors.newSingleThreadScheduledExecutor());
	}

	public Web3j getWeb3j() {
		return web3j;
	}

	public Long getChainID() {
		return chainID;
	}

	public String getUrl() {
		return url;
	}
}
