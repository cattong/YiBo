package net.dev123.mblog.conf;

public interface ApiConfiguration {

	String getSource();

	String getRestBaseURL();

	String getSearchBaseURL();

	String getStreamBaseURL();

	/*** Rest API URL ***/

	String getPublicTimelineURL();

	String getFriendTimelineURL();

	String getHomeTimelineURL();

	String getUserTimelineURL();

	String getMetionsTimelineURL();

	String getRetweetedByMeURL();

	String getCommentsTimelineURL();

	String getCommentsOfStatusURL();

	String getCommentsByMeURL();

	String getCommentsToMeURL();

	String getCountsOfCommentAndRetweetURL();

	String getShowOfStatusURL();

	String getUpdateStatusURL();

	String getUploadStatusURL();

	String getDestroyStatusURL();

	String getRetweetStatusURL();

	String getCommentStatusURL();

	String getDestroyCommentURL();

	String getReplyCommentURL();

	String getSearchStatusURL();

	String getSearchUserURL();

	String getFavoritesTimelineURL();

	String getFavoritesOfUserURL();

	String getCreateFavoriteURL();

	String getDestroyFavoriteURL();

	String getInboxTimelineURL();

	String getOutboxTimelineURL();

	String getSendDirectMessageURL();

	String getDestroyDirectMessageURL();

	String getShowOfUserURL();

	String getFriendsURL();

	String getFollowsURL();

	String getCreateFriendshipURL();

	String getExistFriendshipURL();

	String getDestroyFriendshipURL();

	String getShowOfFriendshipURL();

	String getFriendsIDsURL();

	String getFollowersIDsURL();

	String getVerifyCredentialsURL();

	String getRateLimitStatusURL();

	String getUpdateProfileImageURL();

	String getUpdateProfileURL();

	String getEndSessionURL();

	String getUnreadCountURL();

	String getResetUnreadCountURL();

	String getRetweetsOfStatusURL();

	String getCurrentTrendsURL();
	
	String getUserTrendsURL();
	
	String getUserTrendsStatusURL();

	String getDailyTrendsURL();

	String getWeeklyTrendsURL();

	String getCreateBlockURL();

	String getDestroyBlockURL();

	String getExistsBlockURL();

	String getBlockingUsersURL();

	String getBlockingUsersIdsURL();

	String getCreateGroupURL();

	String getUpdateGroupURL();

	String getDestroyGroupURL();

	String getShowOfGroupURL();

	String getGroupListURL();

	String getGroupStatusesURL();

	String getGroupMembershipsURL();

	String getGroupSubscriptionsURL();

	String getCreateGroupSubscriberURL();

	String getGroupSubscribersURL();

	String getDestroyGroupSubscriberURL();

	String getShowGroupSubscriberURL();

	String getGroupMembersURL();

	String getCreateGroupMemberURL();

	String getCreateGroupMembersURL();

	String getDestroyGroupMemberURL();

	String getShowGroupMemberURL();

	String getAllGroupsURL();

	String getDailyHotRetweetsURL();

	String getDailyHotCommentsURL();

	String getWeeklyHotRetweetsURL();

	String getWeeklyHotCommentsURL();

//	String getGeoLocationKeywordURL();
	
	String getGeoLocationByCoordinateURL();
}
