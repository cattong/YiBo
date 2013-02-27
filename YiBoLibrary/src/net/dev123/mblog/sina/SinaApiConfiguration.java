package net.dev123.mblog.sina;

import net.dev123.mblog.conf.ApiConfigurationBase;

public class SinaApiConfiguration extends ApiConfigurationBase {

	public SinaApiConfiguration() {
		this.setRestBaseURL("http://api.t.sina.com.cn/");

		initRestURL();
	}

	private void initRestURL() {
		this.setPublicTimelineURL(this.getRestBaseURL() + "statuses/public_timeline.json");
		this.setFriendTimelineURL(this.getRestBaseURL() + "statuses/friends_timeline.json");
		this.setHomeTimelineURL(this.getRestBaseURL() + "statuses/friends_timeline.json");
		this.setUserTimelineURL(this.getRestBaseURL() + "statuses/user_timeline.json");
		this.setMetionsTimelineURL(this.getRestBaseURL() + "statuses/mentions.json");
		this.setRetweetedByMeURL(this.getRestBaseURL() + "statuses/repost_by_me.json");
		this.setRetweetsOfStatusURL(this.getRestBaseURL() + "statuses/repost_timeline.json");
		
		this.setShowOfStatusURL(this.getRestBaseURL() + "statuses/show/%1$s.json");
		this.setUpdateStatusURL(this.getRestBaseURL() + "statuses/update.json");
		this.setUploadStatusURL(this.getRestBaseURL() + "statuses/upload.json");
		this.setDestroyStatusURL(this.getRestBaseURL() + "statuses/destroy/%1$s.json");
		this.setRetweetStatusURL(this.getRestBaseURL() + "statuses/repost.json");
		this.setCountsOfCommentAndRetweetURL(this.getRestBaseURL() + "statuses/counts.json");
        
		this.setUnreadCountURL(this.getRestBaseURL() + "statuses/unread.json");
        this.setResetUnreadCountURL(this.getRestBaseURL() + "statuses/reset_count.json");

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

		this.setFriendsIDsURL(this.getRestBaseURL() + "friends/ids.json");
		this.setFollowersIDsURL(this.getRestBaseURL() + "followers/ids.json");

		this.setVerifyCredentialsURL(this.getRestBaseURL() + "account/verify_credentials.json");
		this.setRateLimitStatusURL(this.getRestBaseURL() + "account/rate_limit_status.json");
		this.setUpdateProfileURL(this.getRestBaseURL() + "account/update_profile.json");
		this.setUpdateProfileImageURL(this.getRestBaseURL() + "account/update_profile_image.json");

		this.setFavoritesTimelineURL(this.getRestBaseURL() + "favorites.json");
		this.setFavoritesOfUserURL(this.getRestBaseURL() + "favorites/%1$s.json");
		this.setCreateFavoriteURL(this.getRestBaseURL() + "favorites/create/%1$s.json");
		this.setDestroyFavoriteURL(this.getRestBaseURL() + "favorites/destroy/%1$s.json");

		this.setCommentsTimelineURL(this.getRestBaseURL() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusURL(this.getRestBaseURL() + "statuses/comments.json");
		this.setCommentsByMeURL(this.getRestBaseURL() + "statuses/comments_by_me.json");
		this.setCommentsToMeURL(this.getRestBaseURL() + "statuses/comments_to_me.json");
		this.setCommentStatusURL(this.getRestBaseURL() + "statuses/comment.json");
		this.setDestroyCommentURL(this.getRestBaseURL() + "statuses/comment_destroy/%1$s.json");

		this.setSearchUserURL(this.getRestBaseURL() + "users/search.json");
		this.setSearchStatusURL(this.getRestBaseURL() + "statuses/search.json");

		this.setCurrentTrendsURL(this.getRestBaseURL() + "trends/hourly.json");
		this.setDailyTrendsURL(this.getRestBaseURL() + "trends/daily.json");
		this.setWeeklyTrendsURL(this.getRestBaseURL() + "trends/weekly.json");
		this.setUserTrendsURL(this.getRestBaseURL() + "trends.json");
		this.setUserTrendsStatusURL(this.getRestBaseURL() + "trends/statuses.json");

		this.setCreateBlockURL(this.getRestBaseURL() + "blocks/create.json");
		this.setDestroyBlockURL(this.getRestBaseURL() + "blocks/destroy.json");
		this.setBlockingUsersURL(this.getRestBaseURL() + "blocks/blocking.json");
		this.setBlockingUsersIdsURL(this.getRestBaseURL() + "blocks/blocking/ids.json");
		this.setExistsBlockURL(this.getRestBaseURL() + "blocks/exists.json");

		this.setCreateGroupURL(this.getRestBaseURL() + "%1$s/lists.json");
		this.setUpdateGroupURL(this.getRestBaseURL() + "%1$s/lists/%2$s.json");
		this.setGroupListURL(this.getRestBaseURL() + "%1$s/lists.json");
		this.setShowOfGroupURL(this.getRestBaseURL() + "%1$s/lists/%2$s.json");
		this.setDestroyGroupURL(this.getRestBaseURL() + "%1$s/lists/%2$s.json");
		this.setGroupStatusesURL(this.getRestBaseURL() + "%1$s/lists/%2$s/statuses.json");
		this.setGroupMembershipsURL(this.getRestBaseURL() + "%1$s/lists/memberships.json");
		this.setGroupSubscriptionsURL(this.getRestBaseURL() + "%1$s/lists/subscriptions.json");

		this.setGroupMembersURL(this.getRestBaseURL() + "%1$s/%2$s/members.json");
		this.setCreateGroupMemberURL(this.getRestBaseURL() + "%1$s/%2$s/members.json");
		this.setDestroyGroupMemberURL(this.getRestBaseURL() + "%1$s/%2$s/members.json");
		this.setShowGroupMemberURL(this.getRestBaseURL() + "%1$s/%2$s/members/%3$s.json");

		this.setGroupSubscribersURL(this.getRestBaseURL() + "%1$s/%2$s/subscribers.json");
		this.setCreateGroupSubscriberURL(this.getRestBaseURL() + "%1$s/%2$s/subscribers.json");
		this.setDestroyGroupSubscriberURL(this.getRestBaseURL() + "%1$s/%2$s/subscribers.json");
		this.setShowGroupSubscriberURL(this.getRestBaseURL() + "%1$s/%2$s/subscribers/%3$s.json");

		this.setDailyHotRetweetsURL(this.getRestBaseURL() + "statuses/hot/repost_daily.json");
		this.setDailyHotCommentsURL(this.getRestBaseURL() + "statuses/hot/comments_daily.json");
		this.setWeeklyHotRetweetsURL(this.getRestBaseURL() + "statuses/hot/repost_weekly.json");
		this.setWeeklyHotCommentsURL(this.getRestBaseURL() + "statuses/hot/comments_weekly.json");
		
//		this.setGeoLocationKeywordURL(this.getRestBaseURL() + "location/pois/keyword.json");
		this.setGeoLocationByCoordinateURL(this.getRestBaseURL() + "location/geocode/geo_to_address.json");

	}
}
