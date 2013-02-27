package net.dev123.mblog.netease;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class NetEaseOAuthConfiguration extends OAuthConfigurationBase {

	public NetEaseOAuthConfiguration() {
		this.setOAuthConsumerKey("dXz9uUKWRZ9hZhNe");
		this.setOAuthConsumerSecret("uapBMTnUJw00YE3f4N9FoAorTnok2gXS");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");

		this.setOAuthAccessTokenURL("http://api.t.163.com/oauth/access_token");
		this.setOAuthAuthorizeURL("http://api.t.163.com/oauth/authenticate");
		this.setOAuthRequestTokenURL("http://api.t.163.com/oauth/request_token");
	}

}
