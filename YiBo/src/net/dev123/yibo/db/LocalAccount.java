package net.dev123.yibo.db;

import java.io.Serializable;

import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.OAuth2Authorization;
import net.dev123.commons.oauth2.OAuth2AccessToken;
import net.dev123.yibome.entity.Account;

public class LocalAccount extends Account implements Serializable{

	private static final long serialVersionUID = -8828934924532937238L;

	private Authorization authorization;

	private boolean verified;

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
		if (authorization != null) {
			setAuthToken(authorization.getAuthToken());
			setAuthSecret(authorization.getAuthSecret());
			setAuthVersion(authorization.getAuthVersion());
			setServiceProvider(authorization.getServiceProvider());
			if (authorization.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
				OAuth2Authorization oauth2 = (OAuth2Authorization) authorization;
				OAuth2AccessToken token = oauth2.getOAuth2AccessToken();
				setTokenExpiresAt(token.getExpiresDate());
				setTokenScopes(token.getScope());
			}
		}
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean isSnsAccount() {
		return getServiceProvider().isSns();
	}

	public String toString() {
		return "accountId:" + getAccountId() + " verified:" + verified + ", User: " + getUser();
	}
}
