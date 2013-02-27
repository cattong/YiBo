package net.dev123.commons.oauth.config;


public class OAuthConfigurationBase implements OAuthConfiguration {

	private String oauthRequestTokenURL;
	private String oauthAccessTokenURL;
	private String oauthAuthorizeURL;
	private String oauthCallbackURL;
	private String oauthConsumerKey;
	private String oauthConsumerSecret;
	private String oauthParameterStyle;
	private String[] oauthScopes;
	private String oauthScopeSeparator = " ";

	@Override
	public String getOAuthRequestTokenURL() {
		return oauthRequestTokenURL;
	}

	public void setOAuthRequestTokenURL(String oauthRequestTokenURL) {
		this.oauthRequestTokenURL = oauthRequestTokenURL;
	}

	@Override
	public String getOAuthAccessTokenURL() {
		return oauthAccessTokenURL;
	}

	public void setOAuthAccessTokenURL(String oauthAccessTokenURL) {
		this.oauthAccessTokenURL = oauthAccessTokenURL;
	}

	@Override
	public String getOAuthAuthorizeURL() {
		return oauthAuthorizeURL;
	}

	public void setOAuthAuthorizeURL(String oauthAuthorizeURL) {
		this.oauthAuthorizeURL = oauthAuthorizeURL;
	}

	@Override
	public String getOAuthConsumerKey() {
		return oauthConsumerKey;
	}

	public void setOAuthConsumerKey(String oauthConsumerKey) {
		this.oauthConsumerKey = oauthConsumerKey;
	}

	@Override
	public String getOAuthConsumerSecret() {
		return oauthConsumerSecret;
	}

	public void setOAuthConsumerSecret(String oauthConsumerSecret) {
		this.oauthConsumerSecret = oauthConsumerSecret;
	}

	@Override
	public String getOAuthParameterStyle() {
		return oauthParameterStyle;
	}

	public void setOAuthParameterStyle(String oauthParameterStyle) {
		this.oauthParameterStyle = oauthParameterStyle;
	}

	@Override
	public String getOAuthCallbackURL() {
		return oauthCallbackURL;
	}

	public void setOAuthCallbackURL(String oauthCallbackURL) {
		this.oauthCallbackURL = oauthCallbackURL;
	}

	public String[] getOAuthScopes() {
		return oauthScopes;
	}

	public void setOAuthScopes(String... oauthScopes) {
		this.oauthScopes = oauthScopes;
	}

	public String getOAuthScopeSeparator() {
		return oauthScopeSeparator;
	}

	public void setOAuthScopeSeparator(String oauthScopeSeparator) {
		this.oauthScopeSeparator = oauthScopeSeparator;
	}

}
