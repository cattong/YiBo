package com.cattong.weibo.impl.tencent;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.OAuthParameterStyle;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class TencentOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = 5829964633105832430L;

	public TencentOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_1);
		this.setConsumerKey("801229968");
		this.setConsumerSecret("76a9cf84a06e8f2f83107c3ec76cf2f0");
		this.setCallbackUrl("http://www.jifenbang.net/getAccessToken.do");

		this.setAccessTokenUrl("http://open.t.qq.com/cgi-bin/access_token");
		this.setAuthorizeUrl("http://open.t.qq.com/cgi-bin/authorize");
		this.setRequestTokenUrl("http://open.t.qq.com/cgi-bin/request_token");

		this.setOAuthParameterStyle(OAuthParameterStyle.QUERY_STRING.toString());
	}

}
