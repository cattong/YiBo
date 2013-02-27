package net.dev123.commons.http.auth;

import net.dev123.commons.ServiceProvider;

public class NullAuthorization extends Authorization {

	private static final long serialVersionUID = 2844450882780349140L;

	public NullAuthorization(ServiceProvider serviceProvider) {
		super(serviceProvider);
	}

	@Override
	public String getAuthToken() {
		return null;
	}

	@Override
	public String getAuthSecret() {
		return null;
	}

}