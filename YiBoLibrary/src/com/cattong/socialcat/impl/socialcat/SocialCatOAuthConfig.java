package com.cattong.socialcat.impl.socialcat;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class SocialCatOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -8748187144105387330L;

	public SocialCatOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
		this.setConsumerKey("shejiaomao.com");
		this.setConsumerSecret("shejiaomao.com_cattong");
	}

}
