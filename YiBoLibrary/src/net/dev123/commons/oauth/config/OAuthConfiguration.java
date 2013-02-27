package net.dev123.commons.oauth.config;

public interface OAuthConfiguration {

	String getOAuthAccessTokenURL();

	String getOAuthRequestTokenURL();

	String getOAuthAuthorizeURL();

	String getOAuthCallbackURL();

	String getOAuthConsumerKey();

	String getOAuthConsumerSecret();

	String getOAuthParameterStyle();

	String[] getOAuthScopes();
	
	String getOAuthScopeSeparator();
}