package com.cattong.weibo.impl.sina;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class SinaOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -4059369499822415321L;

	public SinaOAuthConfig() {		
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
		this.setConsumerKey("834484950");//YiBo微博客户端
		this.setConsumerSecret("ff6bb46717f98d7d360459abd0a654f9");
		this.setCallbackUrl("http://www.yibo.me/authorize/getAccessToken.do");
//		this.setConsumerKey("2849184197");//ipad
//		this.setConsumerSecret("7338acf99a00412983f255767c7643d0");
//		this.setCallbackUrl("https://api.weibo.com/oauth2/default.html");
		
		this.setRequestTokenUrl("https://api.weibo.com/oauth2/authorize");
		this.setAuthorizeUrl("https://api.weibo.com/oauth2/authorize");
		this.setAccessTokenUrl("https://api.weibo.com/oauth2/access_token");
	}

}
