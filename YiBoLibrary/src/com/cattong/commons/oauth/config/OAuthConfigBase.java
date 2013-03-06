package com.cattong.commons.oauth.config;



public class OAuthConfigBase implements OAuthConfig {
	private static final long serialVersionUID = -4049728839015806608L;
	
	private int authVersion;
	private String consumerKey;
	private String consumerSecret;
	
	private String requestTokenUrl;
	private String authorizeUrl;
	private String accessTokenUrl;
	private String callbackUrl;

	private String oauthParameterStyle;
	private String oauthScope;

	public void setAuthVersion(int authVersion) {
		this.authVersion = authVersion;
	}
	
	@Override
	public int getAuthVersion() {
		return authVersion;
	}
	
	@Override
	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}

	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}

	@Override
	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	@Override
	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}

	@Override
	public String getConsumerKey() {
		return consumerKey;
	}

	@Override
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	@Override
	public String getConsumerSecret() {
		return consumerSecret;
	}

	@Override
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	@Override
	public String getOAuthParameterStyle() {
		return oauthParameterStyle;
	}

	public void setOAuthParameterStyle(String oauthParameterStyle) {
		this.oauthParameterStyle = oauthParameterStyle;
	}

	@Override
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@Override
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getOAuthScope() {
		return oauthScope;
	}

	public void setOAuthScope(String oauthScope) {
		this.oauthScope = oauthScope;
	}
}
