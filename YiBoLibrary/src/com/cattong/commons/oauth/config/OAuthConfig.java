package com.cattong.commons.oauth.config;

public interface OAuthConfig extends java.io.Serializable {

	int getAuthVersion();
	
	String getConsumerKey();

	void setConsumerKey(String consumerKey);
	
	String getConsumerSecret();
	
	void setConsumerSecret(String consumerKey);
	
	String getRequestTokenUrl();
	
	String getAuthorizeUrl();
	
	String getAccessTokenUrl();

	String getCallbackUrl();

	void setCallbackUrl(String callbackUrl);
	
	String getOAuthParameterStyle();

	String getOAuthScope();
}