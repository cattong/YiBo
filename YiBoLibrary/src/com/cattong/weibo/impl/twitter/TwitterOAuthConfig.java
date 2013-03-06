package com.cattong.weibo.impl.twitter;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class TwitterOAuthConfig extends OAuthConfigBase {

	public TwitterOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("ZOsdd8r8cJYbdjQn05LA");
		this.setConsumerSecret("11rw00tM5hTwce2mlSXXKhnScFIG90brdZSwwVO2E");
		this.setCallbackUrl("http://www.cattong.com/getAccessToken.do");

		this.setRequestTokenUrl("https://api.twitter.com/oauth/request_token");
		this.setAuthorizeUrl("https://api.twitter.com/oauth/authorize");
		this.setAccessTokenUrl("https://api.twitter.com/oauth/access_token");
	}

}
