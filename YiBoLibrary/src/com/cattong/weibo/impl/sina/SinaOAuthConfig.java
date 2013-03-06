package com.cattong.weibo.impl.sina;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class SinaOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -4059369499822415321L;

	public SinaOAuthConfig() {		
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
		this.setConsumerKey("1455835882");//社交猫
		this.setConsumerSecret("5307a4391715cfe9532758c15e6845f7");
		this.setCallbackUrl("http://www.cattong.com/getAccessToken.do");
		//this.setConsumerKey("3748945754");//积分邦
		//this.setConsumerSecret("1c2e6c8b0105e03cba380f90e9ac7a89");
		//this.setCallbackUrl("http://www.jifenbang.net/getAccessToken.do");
		
		this.setRequestTokenUrl("https://api.weibo.com/oauth2/authorize");
		this.setAuthorizeUrl("https://api.weibo.com/oauth2/authorize");
		this.setAccessTokenUrl("https://api.weibo.com/oauth2/access_token");
	}

}
