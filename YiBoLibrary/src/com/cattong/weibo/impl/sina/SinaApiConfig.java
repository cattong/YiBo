package com.cattong.weibo.impl.sina;

import com.cattong.weibo.conf.ApiConfigBase;

public class SinaApiConfig extends ApiConfigBase {

	public SinaApiConfig() {
		this.setRestBaseUrl("https://api.weibo.com/2/");

		initRestUrl();
	}

	private void initRestUrl() {
		this.setPublicTimelineUrl(this.getRestBaseUrl() + "statuses/public_timeline.json");
		this.setFriendTimelineUrl(this.getRestBaseUrl() + "statuses/friends_timeline.json");
		this.setHomeTimelineUrl(this.getRestBaseUrl() + "statuses/friends_timeline.json");
		this.setUserTimelineUrl(this.getRestBaseUrl() + "statuses/user_timeline.json");
		this.setMetionsTimelineUrl(this.getRestBaseUrl() + "statuses/mentions.json");
		this.setRetweetedByMeUrl(this.getRestBaseUrl() + "statuses/repost_by_me.json");
		this.setRetweetsOfStatusUrl(this.getRestBaseUrl() + "statuses/repost_timeline.json");
		
		this.setShowOfStatusUrl(this.getRestBaseUrl() + "statuses/show.json");
		this.setUpdateStatusUrl(this.getRestBaseUrl() + "statuses/update.json");
		this.setUploadStatusUrl(this.getRestBaseUrl() + "statuses/upload.json");
		this.setDestroyStatusUrl(this.getRestBaseUrl() + "statuses/destroy.json");
		this.setRetweetStatusUrl(this.getRestBaseUrl() + "statuses/repost.json");
		this.setResponseCountOfStatusUrl(this.getRestBaseUrl() + "statuses/count.json");
        
		this.setUnreadCountUrl("https://rm.api.weibo.com/2/remind/unread_count.json");
        this.setResetUnreadCountUrl("https://rm.api.weibo.com/2/remind/set_count.json");

		this.setShowOfUserUrl(this.getRestBaseUrl() + "users/show.json");
		this.setFriendsUrl(this.getRestBaseUrl() + "friendships/friends.json");
		this.setFollowsUrl(this.getRestBaseUrl() + "friendships/followers.json");
		
		this.setInboxTimelineUrl(this.getRestBaseUrl() + "direct_messages.json");
		this.setOutboxTimelineUrl(this.getRestBaseUrl() + "direct_messages/sent.json");
		this.setSendDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/new.json");
		this.setDestroyDirectMessageUrl(this.getRestBaseUrl() + "direct_messages/destroy/%1$s.json");

		this.setShowOfFriendshipUrl(this.getRestBaseUrl() + "friendships/show.json");
		this.setCreateFriendshipUrl(this.getRestBaseUrl() + "friendships/create.json");
		this.setDestroyFriendshipUrl(this.getRestBaseUrl() + "friendships/destroy.json");

		this.setVerifyCredentialsUrl(this.getRestBaseUrl() + "account/get_uid.json");
		this.setRateLimitStatusUrl(this.getRestBaseUrl() + "account/rate_limit_status.json");
		this.setUpdateProfileUrl(this.getRestBaseUrl() + "account/update_profile.json");
		this.setUpdateProfileImageUrl(this.getRestBaseUrl() + "account/update_profile_image.json");

		this.setFavoritesTimelineUrl(this.getRestBaseUrl() + "favorites.json");
		//this.setFavoritesOfUserUrl(this.getRestBaseUrl() + "favorites.json");
		this.setCreateFavoriteUrl(this.getRestBaseUrl() + "favorites/create.json");
		this.setDestroyFavoriteUrl(this.getRestBaseUrl() + "favorites/destroy.json");

		this.setCommentsTimelineUrl(this.getRestBaseUrl() + "comments/timeline.json");
		this.setCommentsOfStatusUrl(this.getRestBaseUrl() + "comments/show.json");
		this.setCommentsByMeUrl(this.getRestBaseUrl() + "comments/by_me.json");
		this.setCommentsToMeUrl(this.getRestBaseUrl() + "comments/to_me.json");
		//this.setShowOfCommentUrl(this.getRestBaseUrl() + "comments/show_batch.json");
		this.setCommentStatusUrl(this.getRestBaseUrl() + "comments/create.json");
		this.setReplyCommentUrl(this.getRestBaseUrl() + "comments/reply.json");
		this.setDestroyCommentUrl(this.getRestBaseUrl() + "comments/destroy.json");
        /** comments/mentions 获取@到我的评论**/
		
		this.setSearchUserUrl(this.getRestBaseUrl() + "search/users.json");
		this.setSearchStatusUrl(this.getRestBaseUrl() + "search/statuses.json");

		this.setCurrentTrendsUrl(this.getRestBaseUrl() + "trends/hourly.json");
		this.setDailyTrendsUrl(this.getRestBaseUrl() + "trends/daily.json");
		this.setWeeklyTrendsUrl(this.getRestBaseUrl() + "trends/weekly.json");

		this.setCreateBlockUrl(this.getRestBaseUrl() + "blocks/create.json");
		this.setDestroyBlockUrl(this.getRestBaseUrl() + "blocks/destroy.json");
		this.setBlockingUsersUrl(this.getRestBaseUrl() + "blocks/blocking.json");
		this.setBlockingUsersIdsUrl(this.getRestBaseUrl() + "blocks/blocking/ids.json");
		this.setExistsBlockUrl(this.getRestBaseUrl() + "blocks/exists.json");

		this.setCreateGroupUrl(this.getRestBaseUrl() + "friendships/groups/create.json");
		this.setUpdateGroupUrl(this.getRestBaseUrl() + "friendships/groups/update.json");
		this.setGroupListUrl(this.getRestBaseUrl() + "friendships/groups.json");
		this.setShowOfGroupUrl(this.getRestBaseUrl() + "%1$s/lists/%2$s.json");
		this.setDestroyGroupUrl(this.getRestBaseUrl() + "%1$s/lists/%2$s.json");
		this.setGroupStatusesUrl(this.getRestBaseUrl() + "%1$s/lists/%2$s/statuses.json");
		this.setGroupMembershipsUrl(this.getRestBaseUrl() + "%1$s/lists/memberships.json");
		this.setGroupSubscriptionsUrl(this.getRestBaseUrl() + "%1$s/lists/subscriptions.json");

		this.setGroupMembersUrl(this.getRestBaseUrl() + "%1$s/%2$s/members.json");
		this.setCreateGroupMemberUrl(this.getRestBaseUrl() + "%1$s/%2$s/members.json");
		this.setDestroyGroupMemberUrl(this.getRestBaseUrl() + "%1$s/%2$s/members.json");
		this.setShowGroupMemberUrl(this.getRestBaseUrl() + "%1$s/%2$s/members/%3$s.json");

		this.setGroupSubscribersUrl(this.getRestBaseUrl() + "friendships/groups/timeline.json");
		this.setCreateGroupSubscriberUrl(this.getRestBaseUrl() + "%1$s/%2$s/subscribers.json");
		this.setDestroyGroupSubscriberUrl(this.getRestBaseUrl() + "%1$s/%2$s/subscribers.json");
		this.setShowGroupSubscriberUrl(this.getRestBaseUrl() + "%1$s/%2$s/subscribers/%3$s.json");

		this.setDailyHotRetweetsUrl(this.getRestBaseUrl() + "statuses/hot/repost_daily.json");
		this.setDailyHotCommentsUrl(this.getRestBaseUrl() + "statuses/hot/comments_daily.json");
		this.setWeeklyHotRetweetsUrl(this.getRestBaseUrl() + "statuses/hot/repost_weekly.json");
		this.setWeeklyHotCommentsUrl(this.getRestBaseUrl() + "statuses/hot/comments_weekly.json");
		
		this.setGeoLocationByCoordinateUrl(this.getRestBaseUrl() + "location/geo/geo_to_address.json");

	}
}
