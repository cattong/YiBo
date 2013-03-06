package com.cattong.weibo.impl.twitter;

import com.cattong.weibo.conf.ApiConfigBase;

public class TwitterApiConfig extends ApiConfigBase {

	public TwitterApiConfig() {
		this.setRestBaseUrl("https://api.twitter.com/1/");
		this.setSearchBaseUrl("https://search.twitter.com/");

		initRestUrl();
	}

	public void updateRestApiUrl(){
		initRestUrl();
	}

	private void initRestUrl() {
		this.setPublicTimelineUrl(this.getRestBaseUrl()  + "statuses/public_timeline.json");
		this.setFriendTimelineUrl(this.getRestBaseUrl()  + "statuses/friends_timeline.json"); // statuses/home_timeline.json
		this.setUserTimelineUrl(this.getRestBaseUrl()  + "statuses/user_timeline/%1$s.json");
		this.setMetionsTimelineUrl(this.getRestBaseUrl()  + "statuses/mentions.json");
		this.setHomeTimelineUrl(this.getRestBaseUrl()  + "statuses/home_timeline.json");

		this.setShowOfStatusUrl(this.getRestBaseUrl()  + "statuses/show/%1$s.json");
		this.setUpdateStatusUrl(this.getRestBaseUrl()  + "statuses/update.json");
		this.setUploadStatusUrl("https://upload.twitter.com/1/statuses/update_with_media.json");
		this.setDestroyStatusUrl(this.getRestBaseUrl()  + "statuses/destroy/%1$s.json");
		this.setRetweetStatusUrl(this.getRestBaseUrl()  + "statuses/retweet/%1$s.json");

//		this.setCountsOfCommentAndRetweetUrl(this.getRestBaseUrl() + "statuses/counts.json");
//      this.setRemindCountUrl(this.getRestBaseUrl() + "statuses/unread.json");
//      this.setResetRemindCountUrl(this.getRestBaseUrl() + "statuses/reset_count.json");

		this.setShowOfUserUrl(this.getRestBaseUrl()  + "users/show/%1$s.json");
		this.setFriendsUrl(this.getRestBaseUrl()  + "statuses/friends/%1$s.json");
		this.setFollowsUrl(this.getRestBaseUrl()  + "statuses/followers/%1$s.json");

		this.setInboxTimelineUrl(this.getRestBaseUrl()  + "direct_messages.json");
		this.setOutboxTimelineUrl(this.getRestBaseUrl()  + "direct_messages/sent.json");
		this.setSendDirectMessageUrl(this.getRestBaseUrl()   + "direct_messages/new.json");
		this.setDestroyDirectMessageUrl(this.getRestBaseUrl()  + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipUrl(this.getRestBaseUrl()  + "friendships/create/%1$s.json");
		this.setDestroyFriendshipUrl(this.getRestBaseUrl()  + "friendships/destroy/%1$s.json");
		this.setShowOfFriendshipUrl(this.getRestBaseUrl()  + "friendships/show.json");

		this.setVerifyCredentialsUrl(this.getRestBaseUrl()  + "account/verify_credentials.json");
		this.setRateLimitStatusUrl(this.getRestBaseUrl()  + "account/rate_limit_status.json");
		this.setUpdateProfileUrl(this.getRestBaseUrl()  + "account/update_profile.json");
		this.setUpdateProfileImageUrl(this.getRestBaseUrl()  + "account/update_profile_image.json");

		this.setFavoritesTimelineUrl(this.getRestBaseUrl()  + "favorites.json");
		this.setFavoritesOfUserUrl(this.getRestBaseUrl()  + "favorites/%1$s.json");
		this.setCreateFavoriteUrl(this.getRestBaseUrl()  + "favorites/create/%1$s.json");
		this.setDestroyFavoriteUrl(this.getRestBaseUrl()  + "favorites/destroy/%1$s.json");

		this.setSearchUserUrl(this.getRestBaseUrl()  + "users/search.json");
		this.setSearchStatusUrl(this.getSearchBaseUrl() + "search.json");

		this.setCurrentTrendsUrl(this.getRestBaseUrl() + "trends/current.json");
		this.setDailyTrendsUrl(this.getRestBaseUrl() + "trends/daily.json");
		this.setWeeklyTrendsUrl(this.getRestBaseUrl() + "trends/weekly.json");

		this.setCreateBlockUrl(this.getRestBaseUrl() + "blocks/create.json");
		this.setDestroyBlockUrl(this.getRestBaseUrl() + "blocks/destroy.json");
		this.setBlockingUsersUrl(this.getRestBaseUrl() + "blocks/blocking.json");
		this.setBlockingUsersIdsUrl(this.getRestBaseUrl() + "blocks/blocking/ids.json");
		this.setExistsBlockUrl(this.getRestBaseUrl() + "blocks/exists.json");

		this.setCreateGroupUrl(this.getRestBaseUrl() + "lists/create.json");
		this.setUpdateGroupUrl(this.getRestBaseUrl() + "lists/update.json");
		this.setDestroyGroupUrl(this.getRestBaseUrl() + "lists/destroy.json");
		this.setShowOfGroupUrl(this.getRestBaseUrl() + "lists/show.json");
		this.setGroupListUrl(this.getRestBaseUrl() + "lists.json");
		this.setGroupStatusesUrl(this.getRestBaseUrl() + "lists/statuses.json");
		this.setGroupMembersUrl(this.getRestBaseUrl() + "lists/members.json");
		this.setGroupMembershipsUrl(this.getRestBaseUrl() + "lists/memberships.json");
		this.setCreateGroupMemberUrl(this.getRestBaseUrl() + "lists/members/create.json");
		this.setCreateGroupMembersUrl(this.getRestBaseUrl() + "lists/members/create_all.json");
		this.setDestroyGroupMemberUrl(this.getRestBaseUrl() + "lists/members/destroy.json");
		this.setShowGroupMemberUrl(this.getRestBaseUrl() + "lists/members/show.json");
		this.setGroupSubscribersUrl(this.getRestBaseUrl() + "lists/subscribers.json");
		this.setCreateGroupSubscriberUrl(this.getRestBaseUrl() + "lists/subscribers/create.json");
		this.setDestroyGroupSubscriberUrl(this.getRestBaseUrl() + "lists/subscribers/destroy.json");
		this.setShowGroupSubscriberUrl(this.getRestBaseUrl() + "lists/subscribers/show.json");
		this.setAllGroupsUrl(this.getRestBaseUrl() + "lists/all.json");
	}
}
