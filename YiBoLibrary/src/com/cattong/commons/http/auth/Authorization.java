package com.cattong.commons.http.auth;

import java.util.Date;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.commons.oauth.config.OAuthConfigFactory;
import com.cattong.commons.util.StringUtil;

public class Authorization implements java.io.Serializable {
	private static final long serialVersionUID = -413250347293002921L;
	
	public static final int AUTH_VERSION_BASIC = 0;
	public static final int AUTH_VERSION_OAUTH_1 = 1;
	public static final int AUTH_VERSION_OAUTH_2 = 2;

	protected ServiceProvider serviceProvider;
	protected int serviceProviderNo;
	protected int authVersion;

	/*
	 * 如果是basic的话，accessToken相当于username,accessSecret是password
	 */
	private String accessToken;
	private String accessSecret;
	
	//配置信息，包括comsumerkey,comsumerSecrete, callbackUrl;
	private OAuthConfig oAuthConfig;

	
	//2.0
	private Date expiredAt;
	private String refreshToken;
	
	public Authorization(ServiceProvider sp) {
		if (sp == null) {
			return;
		}
		this.serviceProvider = sp;		
		this.oAuthConfig = OAuthConfigFactory.getOAuthConfig(sp);
		this.authVersion = AUTH_VERSION_OAUTH_1;
		if (oAuthConfig != null) {
		    this.authVersion = oAuthConfig.getAuthVersion();
		}
	}
	
//	public Authorization(ServiceProvider serviceProvider, int authVersion) {
//		if (serviceProvider == null) {
//			return;
//		}
//		this.serviceProvider = serviceProvider;
//		
//		this.authVersion = authVersion;
//		
//		switch(authVersion) {
//		case AUTH_VERSION_BASIC:
//			break;
//		case AUTH_VERSION_OAUTH_1:
//		case AUTH_VERSION_OAUTH_2:
//			this.oAuthConfig = OAuthConfigFactory.getOAuthConfig(serviceProvider);
//			break;
//		}
//	}
	
	public Authorization(ServiceProvider serviceProvider, String accessToken, String accessSecret) {
		this(serviceProvider);
		this.accessToken = accessToken;
		this.accessSecret = accessSecret;
	}
	
	public ServiceProvider getServiceProvider() {
		if (serviceProvider == null) {
			serviceProvider = ServiceProvider.getServiceProvider(serviceProviderNo);
		}
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		this.serviceProviderNo = serviceProvider.getSpNo();
	}

	public int getAuthVersion() {
		return authVersion;
	}

	public void setAuthVersion(int authVersion) {
		this.authVersion = authVersion;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public OAuthConfig getoAuthConfig() {
		return oAuthConfig;
	}

	public void setoAuthConfig(OAuthConfig oAuthConfig) {
		this.oAuthConfig = oAuthConfig;
	}

	public Date getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(Date expiredAt) {
		this.expiredAt = expiredAt;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Authorization other = (Authorization) obj;
		if (!StringUtil.isEquals(accessToken, other.getAccessToken())) {
			return false;
		} 
		if (!StringUtil.isEquals(accessSecret, other.getAccessSecret())) {
			return false;
		}
		if (authVersion != other.authVersion) {
			return false;
		}
		if (serviceProvider != other.getServiceProvider()) {
			return false;
		}
		if (other.getoAuthConfig() == null || oAuthConfig == null) {
			return false;
		}
		
		if (!StringUtil.isEquals(oAuthConfig.getConsumerKey(), other.getoAuthConfig().getConsumerKey()) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		String code = accessToken + accessSecret + serviceProvider + 
		    oAuthConfig == null ? "" : oAuthConfig.getConsumerKey() + oAuthConfig.getConsumerSecret();
		return code.hashCode();
	}
	
	
}