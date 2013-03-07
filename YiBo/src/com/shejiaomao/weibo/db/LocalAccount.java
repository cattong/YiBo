package com.shejiaomao.weibo.db;

import java.io.Serializable;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.entity.Account;

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
			setAccessToken(authorization.getAccessToken());
			setAccessSecret(authorization.getAccessSecret());
			setAuthVersion(authorization.getAuthVersion());
			setServiceProvider(authorization.getServiceProvider());
			if (authorization.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
				setTokenExpiredAt(authorization.getExpiredAt());
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
