package net.dev123.mblog.sina;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class SinaOAuthConfiguration extends OAuthConfigurationBase {

	public SinaOAuthConfiguration() {
		this.setOAuthConsumerKey("2849184197");
		this.setOAuthConsumerSecret("7338acf99a00412983f255767c7643d0");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");

		this.setOAuthRequestTokenURL("http://api.t.sina.com.cn/oauth/request_token");
		this.setOAuthAuthorizeURL("http://api.t.sina.com.cn/oauth/authorize");
		this.setOAuthAccessTokenURL("http://api.t.sina.com.cn/oauth/access_token");
	}

}
