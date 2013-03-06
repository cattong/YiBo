package com.cattong.weibo.impl.sohu;

import com.cattong.weibo.conf.ApiConfigBase;

public class SohuApiConfig extends ApiConfigBase {

	public SohuApiConfig() {
		this.setRestBaseUrl("http://api.t.sohu.com/");

		initRestUrl();
	}

	private void initRestUrl() {
		this.setPublicTimelineUrl(this.getRestBaseUrl() + "statuses/public_timeline.json");
		this.setHomeTimelineUrl(this.getRestBaseUrl() + "statuses/friends_timeline.json");
		this.setFriendTimelineUrl(this.getRestBaseUrl() + "statuses/friends_timeline.json");
		this.setUserTimelineUrl(this.getRestBaseUrl() + "statuses/user_timeline/%1$s.json");
		this.setMetionsTimelineUrl(this.getRestBaseUrl() + "statuses/mentions_timeline.json");

		this.setShowOfStatusUrl(this.getRestBaseUrl() + "statuses/show/%1$s.json");
		this.setUpdateStatusUrl(this.getRestBaseUrl() + "statuses/update.json");
		this.setUploadStatusUrl(this.getRestBaseUrl() + "statuses/upload.json");
		this.setDestroyStatusUrl(this.getRestBaseUrl() + "statuses/destroy/%1$s.json");
		this.setRetweetStatusUrl(this.getRestBaseUrl() + "statuses/transmit/%1$s.json");

		this.setResponseCountOfStatusUrl(this.getRestBaseUrl() + "statuses/counts.json");

		this.setShowOfUserUrl(this.getRestBaseUrl() + "users/show/%1$s.json");
		this.setFriendsUrl(this.getRestBaseUrl() + "statuses/friends%1$s.json"); // 以兼容id.json
		this.setFollowsUrl(this.getRestBaseUrl() + "statuses/followers%1$s.json");

		this.setInboxTimelineUrl(this.getRestBaseUrl() + "direct_messages.json");
		this.setOutboxTimelineUrl(this.getRestBaseUrl() + "direct_messages/sent.json");
		this.setSendDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/new.json");
		this.setDestroyDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipUrl(this.getRestBaseUrl() + "friendship/create/%1$s.json");
		this.setDestroyFriendshipUrl(this.getRestBaseUrl() + "friendship/destroy/%1$s.json");
		this.setShowOfFriendshipUrl(this.getRestBaseUrl() + "friendships/show.json");

		// this.setFriendsIDsUrl(this.getRestBaseUrl() + "friends/ids.json");
		// this.setFollowersIDsUrl(this.getRestBaseUrl() + "followers/ids.json");

		this.setVerifyCredentialsUrl(this.getRestBaseUrl() + "account/verify_credentials.json");
		this.setRateLimitStatusUrl(this.getRestBaseUrl() + "account/rate_limit_status.json");
		this.setUpdateProfileUrl(this.getRestBaseUrl() + "account/update_profile.json");
		this.setUpdateProfileImageUrl(this.getRestBaseUrl() + "account/update_profile_image.json");

		this.setFavoritesTimelineUrl(this.getRestBaseUrl() + "favourites.json");
		// this.setFavoritesOfUserUrl(this.getRestBaseUrl() + "favourites/%1$s.json");
		this.setCreateFavoriteUrl(this.getRestBaseUrl() + "favourites/create/%1$s.json");
		this.setDestroyFavoriteUrl(this.getRestBaseUrl() + "favourites/destroy/%1$s.json");

		// this.setCommentsTimelineUrl(this.getRestBaseUrl() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusUrl(this.getRestBaseUrl() + "statuses/comments/%1$s.json");
		// this.setCommentsByMeUrl(this.getRestBaseUrl() + "statuses/comments_by_me.json");
		this.setCommentsToMeUrl(this.getRestBaseUrl() + "statuses/comments_timeline.json");
		this.setCommentStatusUrl(this.getRestBaseUrl() + "statuses/comment.json");
		this.setDestroyCommentUrl(this.getRestBaseUrl() + "statuses/comment_destroy/%1$s.json");

		this.setSearchUserUrl(this.getRestBaseUrl() + "users/search.json");
		this.setSearchStatusUrl(this.getRestBaseUrl() + "statuses/search.json");
	}
}
