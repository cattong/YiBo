package net.dev123.mblog.tencent;

import net.dev123.commons.oauth.OAuthParameterStyle;
import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class TencentOAuthConfiguration extends OAuthConfigurationBase {

	public TencentOAuthConfiguration() {
		this.setOAuthConsumerKey("d793a554c70746e5bf2c8c18f91845d5");
		this.setOAuthConsumerSecret("e7d311a073b653c0ef4bdd63183820ea");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");

		this.setOAuthAccessTokenURL("http://open.t.qq.com/cgi-bin/access_token");
		this.setOAuthAuthorizeURL("http://open.t.qq.com/cgi-bin/authorize");
		this.setOAuthRequestTokenURL("http://open.t.qq.com/cgi-bin/request_token");

		this.setOAuthParameterStyle(OAuthParameterStyle.QUERY_STRING.toString());
	}

}
