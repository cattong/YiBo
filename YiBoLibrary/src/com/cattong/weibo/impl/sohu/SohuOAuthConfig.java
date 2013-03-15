package com.cattong.weibo.impl.sohu;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class SohuOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = 1318416428421524310L;

	public SohuOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("aXApAcuyUy");
		this.setConsumerSecret("uUJMr8tgymwqcnC^pda0cXUP=rjkl=");
		this.setCallbackUrl("http://www.cattong.com/getAccessToken.do");

		this.setRequestTokenUrl("http://api.t.sohu.com/oauth/request_token");
		this.setAuthorizeUrl("http://api.t.sohu.com/oauth/authorize");
		this.setAccessTokenUrl("http://api.t.sohu.com/oauth/access_token");
	}

}
