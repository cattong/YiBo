package com.cattong.sns;

import com.cattong.commons.LibException;
import com.cattong.commons.PagingSupport;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.sns.api.AlbumMethods;
import com.cattong.sns.api.FriendshipMethods;
import com.cattong.sns.api.NoteMethods;
import com.cattong.sns.api.StatusMethods;
import com.cattong.sns.api.UserMethods;

public abstract class Sns extends PagingSupport implements
		FriendshipMethods, UserMethods,
		StatusMethods, NoteMethods, AlbumMethods {

	protected final OAuthConfig oauthConfig;
	protected Authorization auth;
	public Sns(Authorization auth) {
		this.auth = auth;
		this.oauthConfig = auth.getoAuthConfig();
	}

	public abstract String getScreenName() throws LibException;

	public abstract String getUserId() throws LibException;

	public void setAuthorization(Authorization auth) {
		this.auth = auth;
	}

	public Authorization getAuthorization() {
		return auth;
	}

	@Override
	public String toString() {
		return "SNS {" + "auth=" + auth + '}';
	}
}
