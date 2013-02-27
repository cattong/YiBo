package net.dev123.yibome;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class YiBoMeOAuthConfiguration extends OAuthConfigurationBase {

	public YiBoMeOAuthConfiguration() {
		this.setOAuthConsumerKey("yibo.me");
		this.setOAuthConsumerSecret("dev123.net_yibo.me");
	}

}
