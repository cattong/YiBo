package net.dev123.mblog.sohu;

import net.dev123.mblog.conf.ApiConfigurationBase;

public class SohuApiConfiguration extends ApiConfigurationBase {

	public SohuApiConfiguration() {
		this.setRestBaseURL("http://api.t.sohu.com/");

		initRestURL();
	}

	private void initRestURL() {
		this.setPublicTimelineURL(this.getRestBaseURL() + "statuses/public_timeline.json");
		this.setHomeTimelineURL(this.getRestBaseURL() + "statuses/friends_timeline.json");
		this.setFriendTimelineURL(this.getRestBaseURL() + "statuses/friends_timeline.json");
		this.setUserTimelineURL(this.getRestBaseURL() + "statuses/user_timeline/%1$s.json");
		this.setMetionsTimelineURL(this.getRestBaseURL() + "statuses/mentions_timeline.json");

		this.setShowOfStatusURL(this.getRestBaseURL() + "statuses/show/%1$s.json");
		this.setUpdateStatusURL(this.getRestBaseURL() + "statuses/update.json");
		this.setUploadStatusURL(this.getRestBaseURL() + "statuses/upload.json");
		this.setDestroyStatusURL(this.getRestBaseURL() + "statuses/destroy/%1$s.json");
		this.setRetweetStatusURL(this.getRestBaseURL() + "statuses/transmit/%1$s.json");

		this.setCountsOfCommentAndRetweetURL(this.getRestBaseURL() + "statuses/counts.json");

		this.setShowOfUserURL(this.getRestBaseURL() + "users/show/%1$s.json");
		this.setFriendsURL(this.getRestBaseURL() + "statuses/friends%1$s.json"); // 以兼容id.json
		this.setFollowsURL(this.getRestBaseURL() + "statuses/followers%1$s.json");

		this.setInboxTimelineURL(this.getRestBaseURL() + "direct_messages.json");
		this.setOutboxTimelineURL(this.getRestBaseURL() + "direct_messages/sent.json");
		this.setSendDirectMessageURL(this.getRestBaseURL() + "direct_messages/new.json");
		this.setDestroyDirectMessageURL(this.getRestBaseURL() + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipURL(this.getRestBaseURL() + "friendship/create/%1$s.json");
		this.setDestroyFriendshipURL(this.getRestBaseURL() + "friendship/destroy/%1$s.json");
		this.setExistFriendshipURL(this.getRestBaseURL() + "friendship/exists.json");
		this.setShowOfFriendshipURL(this.getRestBaseURL() + "friendships/show.json");

		// this.setFriendsIDsURL(this.getRestBaseURL() + "friends/ids.json");
		// this.setFollowersIDsURL(this.getRestBaseURL() + "followers/ids.json");

		this.setVerifyCredentialsURL(this.getRestBaseURL() + "account/verify_credentials.json");
		this.setRateLimitStatusURL(this.getRestBaseURL() + "account/rate_limit_status.json");
		this.setUpdateProfileURL(this.getRestBaseURL() + "account/update_profile.json");
		this.setUpdateProfileImageURL(this.getRestBaseURL() + "account/update_profile_image.json");

		this.setFavoritesTimelineURL(this.getRestBaseURL() + "favourites.json");
		// this.setFavoritesOfUserURL(this.getRestBaseURL() + "favourites/%1$s.json");
		this.setCreateFavoriteURL(this.getRestBaseURL() + "favourites/create/%1$s.json");
		this.setDestroyFavoriteURL(this.getRestBaseURL() + "favourites/destroy/%1$s.json");

		// this.setCommentsTimelineURL(this.getRestBaseURL() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusURL(this.getRestBaseURL() + "statuses/comments/%1$s.json");
		// this.setCommentsByMeURL(this.getRestBaseURL() + "statuses/comments_by_me.json");
		this.setCommentsToMeURL(this.getRestBaseURL() + "statuses/comments_timeline.json");
		this.setCommentStatusURL(this.getRestBaseURL() + "statuses/comment.json");
		this.setDestroyCommentURL(this.getRestBaseURL() + "statuses/comment_destroy/%1$s.json");

		this.setSearchUserURL(this.getRestBaseURL() + "users/search.json");
		this.setSearchStatusURL(this.getRestBaseURL() + "statuses/search.json");
	}
}
