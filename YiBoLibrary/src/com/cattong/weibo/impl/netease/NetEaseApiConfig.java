package com.cattong.weibo.impl.netease;

import com.cattong.weibo.conf.ApiConfigBase;


public class NetEaseApiConfig extends ApiConfigBase {

	public NetEaseApiConfig() {
		this.setRestBaseUrl("http://api.t.163.com/");

		initRestUrl();
	}

	private void initRestUrl() {
		this.setPublicTimelineUrl(this.getRestBaseUrl() + "statuses/public_timeline.json");
		this.setHomeTimelineUrl(this.getRestBaseUrl() + "statuses/home_timeline.json");
		this.setFriendTimelineUrl(this.getRestBaseUrl() + "statuses/home_timeline.json");
		this.setUserTimelineUrl(this.getRestBaseUrl() + "statuses/user_timeline.json");
		this.setMetionsTimelineUrl(this.getRestBaseUrl() + "statuses/mentions.json");
		//this.setRetweetsTimelineUrl(this.getRetweetsTimelineUrl() + "statuses/retweets_of_me.json");

		this.setShowOfStatusUrl(this.getRestBaseUrl() + "statuses/show/%1$s.json");
		this.setUpdateStatusUrl(this.getRestBaseUrl() + "statuses/update.json");
		this.setUploadStatusUrl(this.getRestBaseUrl() + "statuses/upload.json");
		this.setDestroyStatusUrl(this.getRestBaseUrl() + "statuses/destroy/%1$s.json");
		this.setRetweetStatusUrl(this.getRestBaseUrl() + "statuses/retweet/%1$s.json");
		this.setResponseCountOfStatusUrl(this.getRestBaseUrl() + "statuses/counts/%1$s.json");

		this.setUnreadCountUrl(this.getRestBaseUrl() + "reminds/message/latest.json");

		this.setShowOfUserUrl(this.getRestBaseUrl() + "users/show.json");
		this.setFriendsUrl(this.getRestBaseUrl() + "statuses/friends.json");
		this.setFollowsUrl(this.getRestBaseUrl() + "statuses/followers.json");

		this.setInboxTimelineUrl(this.getRestBaseUrl() + "direct_messages.json");
		this.setOutboxTimelineUrl(this.getRestBaseUrl() + "direct_messages/sent.json");
		this.setSendDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/new.json");
		this.setDestroyDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipUrl(this.getRestBaseUrl() + "friendships/create.json");
		this.setDestroyFriendshipUrl(this.getRestBaseUrl() + "friendships/destroy.json");
		this.setShowOfFriendshipUrl(this.getRestBaseUrl() + "friendships/show.json");

		this.setVerifyCredentialsUrl(this.getRestBaseUrl() + "account/verify_credentials.json");
		this.setRateLimitStatusUrl(this.getRestBaseUrl() + "account/rate_limit_status.json");
		this.setUpdateProfileUrl(this.getRestBaseUrl() + "account/update_profile.json");
		this.setUpdateProfileImageUrl(this.getRestBaseUrl() + "account/update_profile_image.json");

		this.setFavoritesOfUserUrl(this.getRestBaseUrl() + "favorites/%1$s.json");
		this.setCreateFavoriteUrl(this.getRestBaseUrl() + "favorites/create/%1$s.json");
		this.setDestroyFavoriteUrl(this.getRestBaseUrl() + "favorites/destroy/%1$s.json");

		//this.setCommentsTimelineUrl(this.getRestBaseUrl() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusUrl(this.getRestBaseUrl() + "statuses/comments/%1$s.json");
		this.setCommentStatusUrl(this.getRestBaseUrl() + "statuses/reply.json");
		//this.setDestroyCommentUrl(this.getRestBaseUrl() + "statuses/comment_destroy/%1$d.json");
		this.setCommentsByMeUrl(this.getRestBaseUrl() + "statuses/comments_by_me.json");
		this.setCommentsToMeUrl(this.getRestBaseUrl() + "statuses/comments_to_me.json");

		this.setSearchUserUrl(this.getRestBaseUrl() + "users/search.json");
		this.setSearchStatusUrl(this.getRestBaseUrl() + "search.json");

		this.setDailyTrendsUrl(this.getRestBaseUrl() + "trends/recommended.json");

		this.setCreateBlockUrl(this.getRestBaseUrl() + "blocks/create.json");
		this.setDestroyBlockUrl(this.getRestBaseUrl() + "blocks/destroy.json");
		this.setBlockingUsersUrl(this.getRestBaseUrl() + "blocks/blocking.json");
		this.setBlockingUsersIdsUrl(this.getRestBaseUrl() + "blocks/blocking/ids.json");
		this.setExistsBlockUrl(this.getRestBaseUrl() + "blocks/exists.json");

		this.setDailyHotRetweetsUrl(this.getRestBaseUrl() + "statuses/topRetweets/oneDay.json");
		this.setWeeklyHotRetweetsUrl(this.getRestBaseUrl() + "statuses/topRetweets/oneWeek.json");
		
		this.setGroupListUrl(this.getRestBaseUrl() + "users/groups.json");
		this.setGroupStatusesUrl(this.getRestBaseUrl() + "statuses/group_timeline.json");

	}
}
