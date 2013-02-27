package net.dev123.mblog.netease;

import net.dev123.mblog.conf.ApiConfigurationBase;


public class NetEaseApiConfiguration extends ApiConfigurationBase {

	public NetEaseApiConfiguration() {
		this.setRestBaseURL("http://api.t.163.com/");

		initRestURL();
	}

	private void initRestURL() {
		this.setPublicTimelineURL(this.getRestBaseURL() + "statuses/public_timeline.json");
		this.setHomeTimelineURL(this.getRestBaseURL() + "statuses/home_timeline.json");
		this.setFriendTimelineURL(this.getRestBaseURL() + "statuses/home_timeline.json");
		this.setUserTimelineURL(this.getRestBaseURL() + "statuses/user_timeline.json");
		this.setMetionsTimelineURL(this.getRestBaseURL() + "statuses/mentions.json");
		//this.setRetweetsTimelineURL(this.getRetweetsTimelineURL() + "statuses/retweets_of_me.json");

		this.setShowOfStatusURL(this.getRestBaseURL() + "statuses/show/%1$s.json");
		this.setUpdateStatusURL(this.getRestBaseURL() + "statuses/update.json");
		this.setUploadStatusURL(this.getRestBaseURL() + "statuses/upload.json");
		this.setDestroyStatusURL(this.getRestBaseURL() + "statuses/destroy/%1$s.json");
		this.setRetweetStatusURL(this.getRestBaseURL() + "statuses/retweet/%1$s.json");
		this.setCountsOfCommentAndRetweetURL(this.getRestBaseURL() + "statuses/counts/%1$s.json");

		this.setUnreadCountURL(this.getRestBaseURL() + "reminds/message/latest.json");

		this.setShowOfUserURL(this.getRestBaseURL() + "users/show.json");
		this.setFriendsURL(this.getRestBaseURL() + "statuses/friends.json");
		this.setFollowsURL(this.getRestBaseURL() + "statuses/followers.json");

		this.setInboxTimelineURL(this.getRestBaseURL() + "direct_messages.json");
		this.setOutboxTimelineURL(this.getRestBaseURL() + "direct_messages/sent.json");
		this.setSendDirectMessageURL(this.getRestBaseURL() + "direct_messages/new.json");
		this.setDestroyDirectMessageURL(this.getRestBaseURL() + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipURL(this.getRestBaseURL() + "friendships/create.json");
		this.setDestroyFriendshipURL(this.getRestBaseURL() + "friendships/destroy.json");
		this.setExistFriendshipURL(this.getRestBaseURL() + "friendships/exists.json");
		this.setShowOfFriendshipURL(this.getRestBaseURL() + "friendships/show.json");

		this.setVerifyCredentialsURL(this.getRestBaseURL() + "account/verify_credentials.json");
		this.setRateLimitStatusURL(this.getRestBaseURL() + "account/rate_limit_status.json");
		this.setUpdateProfileURL(this.getRestBaseURL() + "account/update_profile.json");
		this.setUpdateProfileImageURL(this.getRestBaseURL() + "account/update_profile_image.json");

		this.setFavoritesOfUserURL(this.getRestBaseURL() + "favorites/%1$s.json");
		this.setCreateFavoriteURL(this.getRestBaseURL() + "favorites/create/%1$s.json");
		this.setDestroyFavoriteURL(this.getRestBaseURL() + "favorites/destroy/%1$s.json");

		//this.setCommentsTimelineURL(this.getRestBaseURL() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusURL(this.getRestBaseURL() + "statuses/comments/%1$s.json");
		this.setCommentStatusURL(this.getRestBaseURL() + "statuses/reply.json");
		//this.setDestroyCommentURL(this.getRestBaseURL() + "statuses/comment_destroy/%1$d.json");
		this.setCommentsByMeURL(this.getRestBaseURL() + "statuses/comments_by_me.json");
		this.setCommentsToMeURL(this.getRestBaseURL() + "statuses/comments_to_me.json");

		this.setSearchUserURL(this.getRestBaseURL() + "users/search.json");
		this.setSearchStatusURL(this.getRestBaseURL() + "search.json");

		this.setDailyTrendsURL(this.getRestBaseURL() + "trends/recommended.json");

		this.setCreateBlockURL(this.getRestBaseURL() + "blocks/create.json");
		this.setDestroyBlockURL(this.getRestBaseURL() + "blocks/destroy.json");
		this.setBlockingUsersURL(this.getRestBaseURL() + "blocks/blocking.json");
		this.setBlockingUsersIdsURL(this.getRestBaseURL() + "blocks/blocking/ids.json");
		this.setExistsBlockURL(this.getRestBaseURL() + "blocks/exists.json");

		this.setDailyHotRetweetsURL(this.getRestBaseURL() + "statuses/topRetweets/oneDay.json");
		this.setWeeklyHotRetweetsURL(this.getRestBaseURL() + "statuses/topRetweets/oneWeek.json");
		
		this.setGroupListURL(this.getRestBaseURL() + "users/groups.json");
		this.setGroupStatusesURL(this.getRestBaseURL() + "statuses/group_timeline.json");

	}
}
