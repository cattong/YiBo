package com.cattong.weibo.impl.netease;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class NetEaseOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -736295359280838212L;

	public NetEaseOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("dXz9uUKWRZ9hZhNe");
		this.setConsumerSecret("uapBMTnUJw00YE3f4N9FoAorTnok2gXS");
		this.setCallbackUrl("http://www.cattong.com/getAccessToken.do");

		this.setRequestTokenUrl("http://api.t.163.com/oauth/request_token");
		this.setAuthorizeUrl("http://api.t.163.com/oauth/authenticate");
		this.setAccessTokenUrl("http://api.t.163.com/oauth/access_token");
	}

}
