package net.dev123.mblog.fanfou;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class FanfouOAuthConfiguration extends OAuthConfigurationBase {

	public FanfouOAuthConfiguration() {
		this.setOAuthConsumerKey("942fbd3fcc8d4574a0e7d4659b63d17c");
		this.setOAuthConsumerSecret("f2165e725b32c32a94c6f6fa2282252c");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");

		this.setOAuthAccessTokenURL("http://fanfou.com/oauth/access_token");
		this.setOAuthAuthorizeURL("http://fanfou.com/oauth/authenticate");
		this.setOAuthRequestTokenURL("http://fanfou.com/oauth/request_token");
	}

}
