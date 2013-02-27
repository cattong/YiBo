package net.dev123.commons.http.auth;

import net.dev123.commons.ServiceProvider;

public abstract class Authorization implements java.io.Serializable {

	public static final int AUTH_VERSION_BASIC = 0;
	public static final int AUTH_VERSION_OAUTH_1 = 1;
	public static final int AUTH_VERSION_OAUTH_2 = 2;

	private static final long serialVersionUID = -413250347293002921L;
	protected ServiceProvider serviceProvider;
	protected int authVersion;

	public Authorization(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public int getAuthVersion() {
		return authVersion;
	}

	public void setAuthVersion(int authVersion) {
		this.authVersion = authVersion;
	}

	public abstract String getAuthToken();

	public abstract String getAuthSecret();

}