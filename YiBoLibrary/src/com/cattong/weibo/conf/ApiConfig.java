package com.cattong.weibo.conf;

public interface ApiConfig {

	String getSource();

	String getRestBaseUrl();

	String getSearchBaseUrl();

	/* Timeline Methods url */

	String getPublicTimelineUrl();

	String getFriendTimelineUrl();

	String getHomeTimelineUrl();

	String getUserTimelineUrl();

	String getMentionTimelineUrl();

	String getRetweetedByMeUrl();

	String getCommentTimelineUrl();

	String getCommentsByMeUrl();

	String getCommentsToMeUrl();
	
	/* Status Methods url */
	String getShowStatusUrl();

	String getResponseCountOfStatusUrl();
	
	String getUpdateStatusUrl();

	String getUploadStatusUrl();

	String getDestroyStatusUrl();

	String getRetweetStatusUrl();

	String getSearchStatusUrl();
	
	String getDailyHotRetweetsUrl();

	String getDailyHotCommentsUrl();

	String getWeeklyHotRetweetsUrl();

	String getWeeklyHotCommentsUrl();
	
	/* Comment Methods url */
	String getCommentStatusUrl();

	String getDestroyCommentUrl();

	String getReplyCommentUrl();

	String getCommentTimelineOfStatusUrl();

	String getSearchUserUrl();

	/* Favorite Methods url */
	String getFavoritesTimelineUrl();

	String getFavoritesOfUserUrl();

	String getCreateFavoriteUrl();

	String getDestroyFavoriteUrl();

	/* DirectMessage Methods url */
	String getInboxTimelineUrl();

	String getOutboxTimelineUrl();

	String getSendDirectMessageUrl();

	String getDestroyDirectMessageUrl();

	/* User Methods url */
	String getShowUserUrl();

	String getFriendsUrl();

	String getFollowsUrl();

	/* Friendship Methods url */
	String getCreateFriendshipUrl();

	String getDestroyFriendshipUrl();

	String getShowFriendshipUrl();
	
	/* AccountMethods URL */

	String getVerifyCredentialsUrl();

	String getRateLimitStatusUrl();

	String getUpdateProfileImageUrl();

	String getUpdateProfileUrl();

	String getEndSessionUrl();

	/* Count Methods url */
	String getUnreadCountUrl();

	String getResetUnreadCountUrl();

	String getRetweetsOfStatusUrl();

	/* Block Methods url */
	String getCreateBlockUrl();

	String getDestroyBlockUrl();

	String getExistsBlockUrl();

	String getBlockingUsersUrl();

	/* Group Methods url */
	String getCreateGroupUrl();

	String getUpdateGroupUrl();

	String getDestroyGroupUrl();

	String getShowGroupUrl();

	String getGroupListUrl();
    
	String getAllGroupsUrl();
	
	String getGroupStatusesUrl();

	String getGroupMembershipsUrl();

	/* GroupMembers Methods url */
	String getGroupMembersUrl();

	String getCreateGroupMemberUrl();

	String getCreateGroupMembersUrl();

	String getDestroyGroupMemberUrl();

	String getShowGroupMemberUrl();


//	String getGeoLocationKeywordURL();
	
	String getGeoLocationByCoordinateUrl();
}
