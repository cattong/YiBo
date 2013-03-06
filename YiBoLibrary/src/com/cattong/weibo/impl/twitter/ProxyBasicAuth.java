package com.cattong.weibo.impl.twitter;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;

public class ProxyBasicAuth extends Authorization {

	/** serialVersionUID */
	private static final long serialVersionUID = -7108008646051298533L;
	private String restApiServer;
	private String searchApiServer;

	public ProxyBasicAuth(String authToken, String authSecret, ServiceProvider serviceProvider) {
		super(serviceProvider, authToken, authSecret);
	}

	public String getRestApiServer() {
		return restApiServer;
	}

	public void setRestApiServer(String restApiServer) {
		this.restApiServer = restApiServer;
	}

	public String getSearchApiServer() {
		return searchApiServer;
	}

	public void setSearchApiServer(String searchApiServer) {
		this.searchApiServer = searchApiServer;
	}

}
