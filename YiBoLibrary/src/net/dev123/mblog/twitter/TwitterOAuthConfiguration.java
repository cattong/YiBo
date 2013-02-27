package net.dev123.mblog.twitter;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class TwitterOAuthConfiguration extends OAuthConfigurationBase {

	public TwitterOAuthConfiguration() {
		this.setOAuthConsumerKey("ZOsdd8r8cJYbdjQn05LA");
		this.setOAuthConsumerSecret("11rw00tM5hTwce2mlSXXKhnScFIG90brdZSwwVO2E");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");

		this.setOAuthAccessTokenURL("https://api.twitter.com/oauth/access_token");
		this.setOAuthAuthorizeURL("https://api.twitter.com/oauth/authorize");
		this.setOAuthRequestTokenURL("https://api.twitter.com/oauth/request_token");
	}

}
