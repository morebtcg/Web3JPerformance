package org.fisco.bcos.web3.connection;

import org.fisco.bcos.web3.utils.ConnectionConfigParser;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Web3jConnection {
    private String url;
    private Web3j web3j;
    private Long chainID;

    public Web3jConnection(ConnectionConfigParser config) {
        this.url = config.getService().getUrl();
        this.chainID = config.getService().getChainID();

        if (url == null || url.isEmpty()) {
            throw new RuntimeException("url not set");
        }
        web3j = Web3j.build(new HttpService(url));
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
