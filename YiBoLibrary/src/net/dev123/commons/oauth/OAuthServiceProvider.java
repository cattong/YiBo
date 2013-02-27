package net.dev123.commons.oauth;

import java.io.Serializable;

/**
 * OAuth Service Provider.
 */
public class OAuthServiceProvider implements Serializable {

	private static final long serialVersionUID = 3306534392621038574L;

	public final String requestTokenURL;
	public final String userAuthorizationURL;
	public final String accessTokenURL;

	public OAuthServiceProvider(String requestTokenURL, String userAuthorizationURL, String accessTokenURL) {
		this.requestTokenURL = requestTokenURL;
		this.userAuthorizationURL = userAuthorizationURL;
		this.accessTokenURL = accessTokenURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessTokenURL == null) ? 0 : accessTokenURL.hashCode());
		result = prime * result + ((requestTokenURL == null) ? 0 : requestTokenURL.hashCode());
		result = prime * result + ((userAuthorizationURL == null) ? 0 : userAuthorizationURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OAuthServiceProvider other = (OAuthServiceProvider) obj;
		if (accessTokenURL == null) {
			if (other.accessTokenURL != null)
				return false;
		} else if (!accessTokenURL.equals(other.accessTokenURL))
			return false;
		if (requestTokenURL == null) {
			if (other.requestTokenURL != null)
				return false;
		} else if (!requestTokenURL.equals(other.requestTokenURL))
			return false;
		if (userAuthorizationURL == null) {
			if (other.userAuthorizationURL != null)
				return false;
		} else if (!userAuthorizationURL.equals(other.userAuthorizationURL))
			return false;
		return true;
	}

}
