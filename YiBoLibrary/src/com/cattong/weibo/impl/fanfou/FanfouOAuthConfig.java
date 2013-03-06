package com.cattong.weibo.impl.fanfou;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class FanfouOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -5484497746305806131L;

	public FanfouOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("942fbd3fcc8d4574a0e7d4659b63d17c");
		this.setConsumerSecret("f2165e725b32c32a94c6f6fa2282252c");
		this.setCallbackUrl("http://www.cattong.com/getAccessToken.do");

		this.setAccessTokenUrl("http://fanfou.com/oauth/access_token");
		this.setAuthorizeUrl("http://fanfou.com/oauth/authenticate");
		this.setRequestTokenUrl("http://fanfou.com/oauth/request_token");
	}

}
