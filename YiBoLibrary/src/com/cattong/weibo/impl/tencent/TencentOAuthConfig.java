package com.cattong.weibo.impl.tencent;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.OAuthParameterStyle;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class TencentOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = 5829964633105832430L;

	public TencentOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("d793a554c70746e5bf2c8c18f91845d5");
		this.setConsumerSecret("e7d311a073b653c0ef4bdd63183820ea");
		this.setCallbackUrl("http://www.yibo.me/authorize/getAccessToken.do");

		this.setAccessTokenUrl("http://open.t.qq.com/cgi-bin/access_token");
		this.setAuthorizeUrl("http://open.t.qq.com/cgi-bin/authorize");
		this.setRequestTokenUrl("http://open.t.qq.com/cgi-bin/request_token");

		this.setOAuthParameterStyle(OAuthParameterStyle.QUERY_STRING.toString());
	}

}
