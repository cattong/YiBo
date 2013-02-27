package net.dev123.commons.http.auth;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.oauth.OAuthToken;

public final class OAuthAuthorization extends Authorization {

	/** serialVersionUID */
	private static final long serialVersionUID = -6139952865467743763L;

	private OAuthToken oAuthToken;
	private String consumerKey;
	private String consumerSecret;

	public OAuthAuthorization(OAuthToken oAuthToken, ServiceProvider serviceProvider) {
		super(serviceProvider);
		this.oAuthToken = oAuthToken;
		this.authVersion = AUTH_VERSION_OAUTH_1;
	}

	public OAuthAuthorization(String token, String tokenSecret, ServiceProvider serviceProvider) {
		super(serviceProvider);
		this.authVersion = AUTH_VERSION_OAUTH_1;
		this.oAuthToken = new OAuthToken(token, tokenSecret);
	}

	public OAuthToken getOAuthToken() {
		return oAuthToken;
	}

	public void setOAuthToken(OAuthToken oAuthToken) {
		this.oAuthToken = oAuthToken;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	@Override
	public String getAuthToken() {
		return oAuthToken != null ? oAuthToken.getToken() : null;
	}

	@Override
	public String getAuthSecret() {
		return oAuthToken != null ? oAuthToken.getTokenSecret() : null;
	}

	@Override
	public String toString() {
		return "OAuthAuthorization{"
					+ "serviceProvider=\"" + serviceProvider + "\""
					+ ", oAuthToken=\""  + oAuthToken
					+ "}";
	}

}
