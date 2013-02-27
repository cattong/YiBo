package net.dev123.commons.oauth;

public final class OAuthRequestToken extends OAuthToken implements java.io.Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 6963293016190904297L;
	private String authorizationURL;
	private String callbackUrl;

	public OAuthRequestToken(String token, String tokenSecret) {
		super(token, tokenSecret);
	}

	public String getAuthorizationURL() {
		return authorizationURL;
	}

	public void setAuthorizationURL(String authorizationURL) {
		this.authorizationURL = authorizationURL;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

}
