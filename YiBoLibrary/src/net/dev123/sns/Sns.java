package net.dev123.sns;

import net.dev123.commons.PagingSupport;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.exception.LibException;
import net.dev123.sns.api.AdminMethods;
import net.dev123.sns.api.CommentMethods;
import net.dev123.sns.api.FriendshipMethods;
import net.dev123.sns.api.LikeMethods;
import net.dev123.sns.api.NoteMethods;
import net.dev123.sns.api.PageMethods;
import net.dev123.sns.api.MediaMethods;
import net.dev123.sns.api.FeedMethods;
import net.dev123.sns.api.StatusMethods;
import net.dev123.sns.api.UserMethods;

public abstract class Sns extends PagingSupport implements
		AdminMethods, FriendshipMethods, PageMethods, UserMethods,
		StatusMethods, NoteMethods, MediaMethods, LikeMethods,
		CommentMethods, FeedMethods {

	protected final OAuthConfiguration oauthConf;
	protected Authorization auth;

	public Sns(Authorization auth) {
		this.auth = auth;
		this.oauthConf = OAuthConfigurationFactory.getOAuthConfiguration(auth
				.getServiceProvider());
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
