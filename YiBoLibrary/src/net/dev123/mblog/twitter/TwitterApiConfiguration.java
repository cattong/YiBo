package net.dev123.mblog.twitter;

import net.dev123.mblog.conf.ApiConfigurationBase;

public class TwitterApiConfiguration extends ApiConfigurationBase {

	public TwitterApiConfiguration() {
		this.setRestBaseURL("https://api.twitter.com/1/");
		this.setSearchBaseURL("https://search.twitter.com/");

		initRestURL();
	}

	public void updateRestApiURL(){
		initRestURL();
	}

	private void initRestURL() {
		this.setPublicTimelineURL(this.getRestBaseURL()  + "statuses/public_timeline.json");
		this.setFriendTimelineURL(this.getRestBaseURL()  + "statuses/friends_timeline.json"); // statuses/home_timeline.json
		this.setUserTimelineURL(this.getRestBaseURL()  + "statuses/user_timeline/%1$s.json");
		this.setMetionsTimelineURL(this.getRestBaseURL()  + "statuses/mentions.json");
		this.setHomeTimelineURL(this.getRestBaseURL()  + "statuses/home_timeline.json");

		this.setShowOfStatusURL(this.getRestBaseURL()  + "statuses/show/%1$s.json");
		this.setUpdateStatusURL(this.getRestBaseURL()  + "statuses/update.json");
		this.setUploadStatusURL("https://upload.twitter.com/1/statuses/update_with_media.json");
		this.setDestroyStatusURL(this.getRestBaseURL()  + "statuses/destroy/%1$s.json");
		this.setRetweetStatusURL(this.getRestBaseURL()  + "statuses/retweet/%1$s.json");

//		this.setCountsOfCommentAndRetweetURL(this.getRestBaseURL() + "statuses/counts.json");
//      this.setRemindCountURL(this.getRestBaseURL() + "statuses/unread.json");
//      this.setResetRemindCountURL(this.getRestBaseURL() + "statuses/reset_count.json");

		this.setShowOfUserURL(this.getRestBaseURL()  + "users/show/%1$s.json");
		this.setFriendsURL(this.getRestBaseURL()  + "statuses/friends/%1$s.json");
		this.setFollowsURL(this.getRestBaseURL()  + "statuses/followers/%1$s.json");

		this.setInboxTimelineURL(this.getRestBaseURL()  + "direct_messages.json");
		this.setOutboxTimelineURL(this.getRestBaseURL()  + "direct_messages/sent.json");
		this.setSendDirectMessageURL(this.getRestBaseURL()   + "direct_messages/new.json");
		this.setDestroyDirectMessageURL(this.getRestBaseURL()  + "direct_messages/destroy/%1$s.json");

		this.setCreateFriendshipURL(this.getRestBaseURL()  + "friendships/create/%1$s.json");
		this.setDestroyFriendshipURL(this.getRestBaseURL()  + "friendships/destroy/%1$s.json");
		this.setExistFriendshipURL(this.getRestBaseURL()  + "friendships/exists.json");
		this.setShowOfFriendshipURL(this.getRestBaseURL()  + "friendships/show.json");

		this.setFriendsIDsURL(this.getRestBaseURL()  + "friends/ids/%1$s.json");
		this.setFollowersIDsURL(this.getRestBaseURL()  + "followers/ids/%1$s.json");

		this.setVerifyCredentialsURL(this.getRestBaseURL()  + "account/verify_credentials.json");
		this.setRateLimitStatusURL(this.getRestBaseURL()  + "account/rate_limit_status.json");
		this.setUpdateProfileURL(this.getRestBaseURL()  + "account/update_profile.json");
		this.setUpdateProfileImageURL(this.getRestBaseURL()  + "account/update_profile_image.json");

		this.setFavoritesTimelineURL(this.getRestBaseURL()  + "favorites.json");
		this.setFavoritesOfUserURL(this.getRestBaseURL()  + "favorites/%1$s.json");
		this.setCreateFavoriteURL(this.getRestBaseURL()  + "favorites/create/%1$s.json");
		this.setDestroyFavoriteURL(this.getRestBaseURL()  + "favorites/destroy/%1$s.json");

		this.setSearchUserURL(this.getRestBaseURL()  + "users/search.json");
		this.setSearchStatusURL(this.getSearchBaseURL() + "search.json");

		this.setCurrentTrendsURL(this.getRestBaseURL() + "trends/current.json");
		this.setDailyTrendsURL(this.getRestBaseURL() + "trends/daily.json");
		this.setWeeklyTrendsURL(this.getRestBaseURL() + "trends/weekly.json");

		this.setCreateBlockURL(this.getRestBaseURL() + "blocks/create.json");
		this.setDestroyBlockURL(this.getRestBaseURL() + "blocks/destroy.json");
		this.setBlockingUsersURL(this.getRestBaseURL() + "blocks/blocking.json");
		this.setBlockingUsersIdsURL(this.getRestBaseURL() + "blocks/blocking/ids.json");
		this.setExistsBlockURL(this.getRestBaseURL() + "blocks/exists.json");

		this.setCreateGroupURL(this.getRestBaseURL() + "lists/create.json");
		this.setUpdateGroupURL(this.getRestBaseURL() + "lists/update.json");
		this.setDestroyGroupURL(this.getRestBaseURL() + "lists/destroy.json");
		this.setShowOfGroupURL(this.getRestBaseURL() + "lists/show.json");
		this.setGroupListURL(this.getRestBaseURL() + "lists.json");
		this.setGroupStatusesURL(this.getRestBaseURL() + "lists/statuses.json");
		this.setGroupMembersURL(this.getRestBaseURL() + "lists/members.json");
		this.setGroupMembershipsURL(this.getRestBaseURL() + "lists/memberships.json");
		this.setCreateGroupMemberURL(this.getRestBaseURL() + "lists/members/create.json");
		this.setCreateGroupMembersURL(this.getRestBaseURL() + "lists/members/create_all.json");
		this.setDestroyGroupMemberURL(this.getRestBaseURL() + "lists/members/destroy.json");
		this.setShowGroupMemberURL(this.getRestBaseURL() + "lists/members/show.json");
		this.setGroupSubscribersURL(this.getRestBaseURL() + "lists/subscribers.json");
		this.setCreateGroupSubscriberURL(this.getRestBaseURL() + "lists/subscribers/create.json");
		this.setDestroyGroupSubscriberURL(this.getRestBaseURL() + "lists/subscribers/destroy.json");
		this.setShowGroupSubscriberURL(this.getRestBaseURL() + "lists/subscribers/show.json");
		this.setAllGroupsURL(this.getRestBaseURL() + "lists/all.json");
	}
}
