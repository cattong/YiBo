package net.dev123.mblog.tencent;

import net.dev123.mblog.conf.ApiConfigurationBase;

public class TencentApiConfiguration extends ApiConfigurationBase {

	public TencentApiConfiguration() {
		this.setRestBaseURL("http://open.t.qq.com/api/");

		initRestURL();
	}

	private void initRestURL() {
		this.setPublicTimelineURL(this.getRestBaseURL() + "statuses/public_timeline");
		this.setFriendTimelineURL(this.getRestBaseURL() + "statuses/home_timeline");
		this.setHomeTimelineURL(this.getRestBaseURL() + "statuses/home_timeline");
		this.setUserTimelineURL(this.getRestBaseURL() + "statuses/user_timeline");
		this.setMetionsTimelineURL(this.getRestBaseURL() + "statuses/mentions_timeline");
		this.setRetweetsOfStatusURL(this.getRestBaseURL() + "t/re_list");
		
		this.setShowOfStatusURL(this.getRestBaseURL() + "t/show");
		this.setUpdateStatusURL(this.getRestBaseURL() + "t/add");
		this.setUploadStatusURL(this.getRestBaseURL() + "t/add_pic");
		this.setDestroyStatusURL(this.getRestBaseURL() + "t/del");
		this.setRetweetStatusURL(this.getRestBaseURL() + "t/re_add");

		this.setCountsOfCommentAndRetweetURL(this.getRestBaseURL() + "t/re_count");
		this.setUnreadCountURL(this.getRestBaseURL() + "info/update");
		this.setResetUnreadCountURL(this.getRestBaseURL() + "info/update");

		this.setShowOfUserURL(this.getRestBaseURL() + "user/other_info");
		this.setFriendsURL(this.getRestBaseURL() + "friends/user_idollist");
		this.setFollowsURL(this.getRestBaseURL() + "friends/user_fanslist");

		this.setInboxTimelineURL(this.getRestBaseURL() + "private/recv");
		this.setOutboxTimelineURL(this.getRestBaseURL() + "private/send");
		this.setSendDirectMessageURL(this.getRestBaseURL() + "private/add");
		this.setDestroyDirectMessageURL(this.getRestBaseURL() + "private/del");

		this.setCreateFriendshipURL(this.getRestBaseURL() + "friends/add");
		this.setDestroyFriendshipURL(this.getRestBaseURL() + "friends/del");
//		this.setExistFriendshipURL(this.getRestBaseURL() + "friendships/exists.json");
		this.setShowOfFriendshipURL(this.getRestBaseURL() + "friends/check");

//		this.setFriendsIDsURL(this.getRestBaseURL() + "friends/ids.json");
//		this.setFollowersIDsURL(this.getRestBaseURL() + "followers/ids.json");

		this.setVerifyCredentialsURL(this.getRestBaseURL() + "user/info");
		this.setUpdateProfileURL(this.getRestBaseURL() + "user/update");
		this.setUpdateProfileImageURL(this.getRestBaseURL() + "user/update_head");

		this.setFavoritesTimelineURL(this.getRestBaseURL() + "fav/list_t");
//		this.setFavoritesOfUserURL(this.getRestBaseURL() + "fav/list_t");
		this.setCreateFavoriteURL(this.getRestBaseURL() + "fav/addt");
		this.setDestroyFavoriteURL(this.getRestBaseURL() + "fav/delt");

//		this.setCommentsTimelineURL(this.getRestBaseURL() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusURL(this.getRestBaseURL() + "t/re_list");
//		this.setCommentsByMeURL(this.getRestBaseURL() + "statuses/comments_by_me.json");
//		this.setCommentsToMeURL(this.getRestBaseURL() + "statuses/comments_to_me.json");
		this.setCommentStatusURL(this.getRestBaseURL() + "t/comment");
		this.setDestroyCommentURL(this.getRestBaseURL() + "t/del");

		this.setSearchUserURL(this.getRestBaseURL() + "search/user");
		this.setSearchStatusURL(this.getRestBaseURL() + "search/t");

		this.setDailyTrendsURL(this.getRestBaseURL() + "trends/ht");
        this.setDailyHotRetweetsURL(this.getRestBaseURL() + "trends/t");
        
		this.setCreateBlockURL(this.getRestBaseURL() + "friends/addblacklist");
		this.setDestroyBlockURL(this.getRestBaseURL() + "friends/delblacklist");
		this.setBlockingUsersURL(this.getRestBaseURL() + "friends/blacklist");
		
		this.setCreateGroupURL(this.getRestBaseURL() + "list/create");
		this.setUpdateGroupURL(this.getRestBaseURL() + "list/edit");
		this.setGroupListURL(this.getRestBaseURL() + "list/get_list");
		this.setShowOfGroupURL(this.getRestBaseURL() + "list/list_attr");
		this.setDestroyGroupURL(this.getRestBaseURL() + "list/delete");
		this.setGroupStatusesURL(this.getRestBaseURL() + "%1$s/lists/%2$s/statuses.json");
		this.setGroupMembershipsURL(this.getRestBaseURL() + "list/listusers");
		this.setGroupSubscriptionsURL(this.getRestBaseURL() + "%1$s/lists/subscriptions.json");

		this.setGroupMembersURL(this.getRestBaseURL() + "list/listusers");
		this.setCreateGroupMemberURL(this.getRestBaseURL() + "list/add_to_list");
		this.setDestroyGroupMemberURL(this.getRestBaseURL() + "list/del_from_list");
		this.setShowGroupMemberURL(this.getRestBaseURL() + "%1$s/%2$s/members/%3$s.json");
	}
}
