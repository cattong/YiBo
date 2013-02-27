package net.dev123.commons.http.auth;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.oauth2.OAuth2AccessToken;

public final class OAuth2Authorization extends Authorization {

	/** serialVersionUID */
	private static final long serialVersionUID = -6139952865467743763L;

	private OAuth2AccessToken oauth2AccessToken;

	public OAuth2Authorization(OAuth2AccessToken oauth2AccessToken, ServiceProvider serviceProvider) {
		super(serviceProvider);
		this.oauth2AccessToken = oauth2AccessToken;
		this.authVersion = AUTH_VERSION_OAUTH_2;
	}

	public OAuth2AccessToken getOAuth2AccessToken() {
		return oauth2AccessToken;
	}

	public void setOAuth2AccessToken(OAuth2AccessToken oauth2AccessToken) {
		this.oauth2AccessToken = oauth2AccessToken;
	}

	@Override
	public String getAuthToken() {
		return oauth2AccessToken != null ? oauth2AccessToken.getAccessToken() : null;
	}

	@Override
	public String getAuthSecret() {
		return oauth2AccessToken != null ? oauth2AccessToken.getRefreshToken() : null;
	}

	@Override
	public String toString() {
		return "OAuthAuthorization{"
					+ "serviceProvider=\"" + serviceProvider + "\""
					+ ", oAuth2AccessToken=\""  + oauth2AccessToken
					+ "}";
	}
}
