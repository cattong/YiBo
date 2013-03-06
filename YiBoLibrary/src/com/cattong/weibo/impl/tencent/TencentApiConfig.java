package com.cattong.weibo.impl.tencent;

import com.cattong.weibo.conf.ApiConfigBase;

public class TencentApiConfig extends ApiConfigBase {

	public TencentApiConfig() {
		this.setRestBaseUrl("http://open.t.qq.com/api/");

		initRestUrl();
	}

	private void initRestUrl() {
		this.setPublicTimelineUrl(this.getRestBaseUrl() + "statuses/public_timeline");
		this.setFriendTimelineUrl(this.getRestBaseUrl() + "statuses/home_timeline");
		this.setHomeTimelineUrl(this.getRestBaseUrl() + "statuses/home_timeline");
		this.setUserTimelineUrl(this.getRestBaseUrl() + "statuses/user_timeline");
		this.setMetionsTimelineUrl(this.getRestBaseUrl() + "statuses/mentions_timeline");
		this.setRetweetsOfStatusUrl(this.getRestBaseUrl() + "t/re_list");
		
		this.setShowOfStatusUrl(this.getRestBaseUrl() + "t/show");
		this.setUpdateStatusUrl(this.getRestBaseUrl() + "t/add");
		this.setUploadStatusUrl(this.getRestBaseUrl() + "t/add_pic");
		this.setDestroyStatusUrl(this.getRestBaseUrl() + "t/del");
		this.setRetweetStatusUrl(this.getRestBaseUrl() + "t/re_add");

		this.setResponseCountOfStatusUrl(this.getRestBaseUrl() + "t/re_count");
		this.setUnreadCountUrl(this.getRestBaseUrl() + "info/update");
		this.setResetUnreadCountUrl(this.getRestBaseUrl() + "info/update");

		this.setShowOfUserUrl(this.getRestBaseUrl() + "user/other_info");
		this.setFriendsUrl(this.getRestBaseUrl() + "friends/user_idollist");
		this.setFollowsUrl(this.getRestBaseUrl() + "friends/user_fanslist");

		this.setInboxTimelineUrl(this.getRestBaseUrl() + "private/recv");
		this.setOutboxTimelineUrl(this.getRestBaseUrl() + "private/send");
		this.setSendDirectMessageUrl(this.getRestBaseUrl() + "private/add");
		this.setDestroyDirectMessageUrl(this.getRestBaseUrl() + "private/del");

		this.setCreateFriendshipUrl(this.getRestBaseUrl() + "friends/add");
		this.setDestroyFriendshipUrl(this.getRestBaseUrl() + "friends/del");
//		this.setExistFriendshipUrl(this.getRestBaseUrl() + "friendships/exists.json");
		this.setShowOfFriendshipUrl(this.getRestBaseUrl() + "friends/check");

//		this.setFriendsIDsUrl(this.getRestBaseUrl() + "friends/ids.json");
//		this.setFollowersIDsUrl(this.getRestBaseUrl() + "followers/ids.json");

		this.setVerifyCredentialsUrl(this.getRestBaseUrl() + "user/info");
		this.setUpdateProfileUrl(this.getRestBaseUrl() + "user/update");
		this.setUpdateProfileImageUrl(this.getRestBaseUrl() + "user/update_head");

		this.setFavoritesTimelineUrl(this.getRestBaseUrl() + "fav/list_t");
//		this.setFavoritesOfUserUrl(this.getRestBaseUrl() + "fav/list_t");
		this.setCreateFavoriteUrl(this.getRestBaseUrl() + "fav/addt");
		this.setDestroyFavoriteUrl(this.getRestBaseUrl() + "fav/delt");

//		this.setCommentsTimelineUrl(this.getRestBaseUrl() + "statuses/comments_timeline.json");
		this.setCommentsOfStatusUrl(this.getRestBaseUrl() + "t/re_list");
//		this.setCommentsByMeUrl(this.getRestBaseUrl() + "statuses/comments_by_me.json");
//		this.setCommentsToMeUrl(this.getRestBaseUrl() + "statuses/comments_to_me.json");
		this.setCommentStatusUrl(this.getRestBaseUrl() + "t/comment");
		this.setDestroyCommentUrl(this.getRestBaseUrl() + "t/del");

		this.setSearchUserUrl(this.getRestBaseUrl() + "search/user");
		this.setSearchStatusUrl(this.getRestBaseUrl() + "search/t");

		this.setDailyTrendsUrl(this.getRestBaseUrl() + "trends/ht");
        this.setDailyHotRetweetsUrl(this.getRestBaseUrl() + "trends/t");
        
		this.setCreateBlockUrl(this.getRestBaseUrl() + "friends/addblacklist");
		this.setDestroyBlockUrl(this.getRestBaseUrl() + "friends/delblacklist");
		this.setBlockingUsersUrl(this.getRestBaseUrl() + "friends/blacklist");
		
		this.setCreateGroupUrl(this.getRestBaseUrl() + "list/create");
		this.setUpdateGroupUrl(this.getRestBaseUrl() + "list/edit");
		this.setGroupListUrl(this.getRestBaseUrl() + "list/get_list");
		this.setShowOfGroupUrl(this.getRestBaseUrl() + "list/list_attr");
		this.setDestroyGroupUrl(this.getRestBaseUrl() + "list/delete");
		this.setGroupStatusesUrl(this.getRestBaseUrl() + "%1$s/lists/%2$s/statuses.json");
		this.setGroupMembershipsUrl(this.getRestBaseUrl() + "list/listusers");
		this.setGroupSubscriptionsUrl(this.getRestBaseUrl() + "%1$s/lists/subscriptions.json");

		this.setGroupMembersUrl(this.getRestBaseUrl() + "list/listusers");
		this.setCreateGroupMemberUrl(this.getRestBaseUrl() + "list/add_to_list");
		this.setDestroyGroupMemberUrl(this.getRestBaseUrl() + "list/del_from_list");
		this.setShowGroupMemberUrl(this.getRestBaseUrl() + "%1$s/%2$s/members/%3$s.json");
	}
}
