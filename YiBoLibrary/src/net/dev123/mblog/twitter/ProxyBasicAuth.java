package net.dev123.mblog.twitter;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.BasicAuthorization;

public class ProxyBasicAuth extends BasicAuthorization {

	/** serialVersionUID */
	private static final long serialVersionUID = -7108008646051298533L;
	private String restApiServer;
	private String searchApiServer;

	public ProxyBasicAuth(String authToken, String authSecret, ServiceProvider serviceProvider) {
		super(authToken, authSecret, serviceProvider);
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
