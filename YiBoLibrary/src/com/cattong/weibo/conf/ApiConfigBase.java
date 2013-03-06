package com.cattong.weibo.conf;

import com.cattong.commons.util.StringUtil;

public class ApiConfigBase implements ApiConfig {

    private String source;

    /**** micro blog base Url ****/
    private String restBaseUrl;
    private String searchBaseUrl;

    /**** micro blog rest address ****/
    // Query rest Url;
    private String publicTimelineUrl;
    private String friendTimelineUrl; // same as homeTimelineUrl
    private String homeTimelineUrl;
    private String userTimelineUrl;
    private String metionsTimelineUrl;
    private String retweetedByMeUrl;
    private String retweetsOfStatusUrl;
    private String commentsTimelineUrl;
    private String commentsOfStatusUrl;
    private String commentsByMeUrl;
    private String commentsToMeUrl;
    private String unreadCountUrl;
    private String resetUnreadCountUrl;
    // A Status operate url;
    private String showStatusUrl;
    private String responseCountOfStatusUrl;
    private String updateStatusUrl;
    private String uploadStatusUrl;
    private String destroyStatusUrl;
    private String retweetStatusUrl;
    private String commentStatusUrl;
    private String destroyCommentUrl;
    private String replyCommentUrl;
    private String searchStatusUrl;
    // favorite operate url;
    private String favoritesTimelineUrl;
    private String favoritesOfUserUrl;
    private String createFavoriteUrl;
    private String destroyFavoriteUrl;
    // Direct Message Url;
    private String inboxTimelineUrl; // direct messages received;
    private String outboxTimelineUrl; // direct messages sended;
    private String sendDirectMessageUrl;
    private String destroyDirectMessageUrl;
    // User Information mehtod url;
    private String searchUserUrl;
    private String showOfUserUrl;
    private String friendsUrl;
    private String followsUrl;
    // friendship
    private String createFriendshipUrl;
    private String destroyFriendshipUrl;
    private String showOfFriendshipUrl;
    // User operate Url
    private String verifyCredentialsUrl;
    private String rateLimitStatusUrl;
    private String updateProfileImageUrl;
    private String updateProfileUrl;
    private String endSessionUrl;
    // Trends Url
    private String currentTrendsUrl;
    private String dailyTrendsUrl;
    private String weeklyTrendsUrl;
    // Block Url
    private String createBlockUrl;
    private String destroyBlockUrl;
    private String existsBlockUrl;
    private String blockingUsersUrl;
    private String blockingUsersIdsUrl;

    private String createGroupUrl;
    private String updateGroupUrl;
    private String destroyGroupUrl;
    private String showOfGroupUrl;
    private String groupListUrl;
    private String groupStatusesUrl;
    private String groupMembershipsUrl; // 获取用户被加入的列表
    private String groupSubscriptionsUrl; // 获取用户订阅的组列表
    private String createGroupSubscriberUrl;
    private String groupSubscribersUrl;
    private String destroyGroupSubscriberUrl;
    private String showGroupSubscriberUrl;
    private String groupMembersUrl;
    private String createGroupMembersUrl;
    private String createGroupMemberUrl;
    private String destroyGroupMemberUrl;
    private String showGroupMemberUrl;
    private String allGroupsUrl;

    private String dailyHotRetweetsUrl;
    private String dailyHotCommentsUrl;
    private String weeklyHotRetweetsUrl;
    private String weeklyHotCommentsUrl;

	//GeoLocation Url
//    private String geoLocationKeywordUrl;
    private String geoLocationByCoordinateUrl;

	@Override
    public final String getSource() {
        return source;
    }

    protected final void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getRestBaseUrl() {
        return restBaseUrl;
    }

    public final void setRestBaseUrl(String restBaseUrl) {
        if (StringUtil.isEmpty(restBaseUrl)) {
            throw new NullPointerException("RestBaseUrl is null.");
        }
        if (!restBaseUrl.endsWith("/")) {
            restBaseUrl += "/";
        }
        this.restBaseUrl = restBaseUrl;
    }

    @Override
    public String getSearchBaseUrl() {
        return searchBaseUrl;
    }

    public final void setSearchBaseUrl(String searchBaseUrl) {
        if (searchBaseUrl == null) {
            throw new NullPointerException("SearchBaseUrl is null.");
        }
        if (!searchBaseUrl.endsWith("/")) {
            searchBaseUrl += "/";
        }
        this.searchBaseUrl = searchBaseUrl;
    }

    @Override
    public String getPublicTimelineUrl() {
        return publicTimelineUrl;
    }

    @Override
    public String getFriendTimelineUrl() {
        return friendTimelineUrl;
    }

    @Override
    public String getUserTimelineUrl() {
        return userTimelineUrl;
    }

    @Override
    public String getMentionTimelineUrl() {
        return metionsTimelineUrl;
    }

    @Override
    public String getCommentTimelineUrl() {
        return commentsTimelineUrl;
    }

    @Override
    public String getCommentTimelineOfStatusUrl() {
        return commentsOfStatusUrl;
    }

    @Override
    public String getCommentsByMeUrl() {
        return commentsByMeUrl;
    }

    @Override
    public String getResponseCountOfStatusUrl() {
        return responseCountOfStatusUrl;
    }

    @Override
    public String getShowStatusUrl() {
        return showStatusUrl;
    }

    @Override
    public String getUpdateStatusUrl() {
        return updateStatusUrl;
    }

    @Override
    public String getUploadStatusUrl() {
        return uploadStatusUrl;
    }

    @Override
    public String getDestroyStatusUrl() {
        return destroyStatusUrl;
    }

    @Override
    public String getRetweetStatusUrl() {
        return retweetStatusUrl;
    }

    @Override
    public String getCommentStatusUrl() {
        return commentStatusUrl;
    }

    @Override
    public String getDestroyCommentUrl() {
        return destroyCommentUrl;
    }

    @Override
    public String getReplyCommentUrl() {
        return replyCommentUrl;
    }

    @Override
    public String getFavoritesTimelineUrl() {
        return favoritesTimelineUrl;
    }

    @Override
    public String getCreateFavoriteUrl() {
        return createFavoriteUrl;
    }

    @Override
    public String getDestroyFavoriteUrl() {
        return destroyFavoriteUrl;
    }

    @Override
    public String getInboxTimelineUrl() {
        return inboxTimelineUrl;
    }

    @Override
    public String getOutboxTimelineUrl() {
        return outboxTimelineUrl;
    }

    @Override
    public String getSendDirectMessageUrl() {
        return sendDirectMessageUrl;
    }

    @Override
    public String getDestroyDirectMessageUrl() {
        return destroyDirectMessageUrl;
    }

    @Override
    public String getShowUserUrl() {
        return showOfUserUrl;
    }

    @Override
    public String getFriendsUrl() {
        return friendsUrl;
    }

    @Override
    public String getFollowsUrl() {
        return followsUrl;
    }

    @Override
    public String getCreateFriendshipUrl() {
        return createFriendshipUrl;
    }

    @Override
    public String getDestroyFriendshipUrl() {
        return destroyFriendshipUrl;
    }

    @Override
    public String getShowFriendshipUrl() {
        return showOfFriendshipUrl;
    }

    @Override
    public String getVerifyCredentialsUrl() {
        return verifyCredentialsUrl;
    }

    @Override
    public String getRateLimitStatusUrl() {
        return rateLimitStatusUrl;
    }

    @Override
    public String getUpdateProfileImageUrl() {
        return updateProfileImageUrl;
    }

    @Override
    public String getUpdateProfileUrl() {
        return updateProfileUrl;
    }

    @Override
    public String getEndSessionUrl() {
        return endSessionUrl;
    }

    public void setPublicTimelineUrl(String publicTimelineUrl) {
        this.publicTimelineUrl = publicTimelineUrl;
    }

    public void setFriendTimelineUrl(String friendTimelineUrl) {
        this.friendTimelineUrl = friendTimelineUrl;
    }

    public void setUserTimelineUrl(String userTimelineUrl) {
        this.userTimelineUrl = userTimelineUrl;
    }

    public void setMetionsTimelineUrl(String metionsTimelineUrl) {
        this.metionsTimelineUrl = metionsTimelineUrl;
    }

    public void setCommentsTimelineUrl(String commentsTimelineUrl) {
        this.commentsTimelineUrl = commentsTimelineUrl;
    }

    public void setCommentsOfStatusUrl(String commentsOfStatusUrl) {
        this.commentsOfStatusUrl = commentsOfStatusUrl;
    }

    public void setCommentsByMeUrl(String commentsByMeUrl) {
        this.commentsByMeUrl = commentsByMeUrl;
    }

    public void setResponseCountOfStatusUrl(String responseCountOfStatusUrl) {
        this.responseCountOfStatusUrl = responseCountOfStatusUrl;
    }

    public void setShowOfStatusUrl(String showStatusUrl) {
        this.showStatusUrl = showStatusUrl;
    }

    public void setUpdateStatusUrl(String updateStatusUrl) {
        this.updateStatusUrl = updateStatusUrl;
    }

    public void setUploadStatusUrl(String uploadStatusUrl) {
        this.uploadStatusUrl = uploadStatusUrl;
    }

    public void setDestroyStatusUrl(String destroyStatusUrl) {
        this.destroyStatusUrl = destroyStatusUrl;
    }

    public void setRetweetStatusUrl(String retweetStatusUrl) {
        this.retweetStatusUrl = retweetStatusUrl;
    }

    public void setCommentStatusUrl(String commentStatusUrl) {
        this.commentStatusUrl = commentStatusUrl;
    }

    public void setDestroyCommentUrl(String destroyCommentUrl) {
        this.destroyCommentUrl = destroyCommentUrl;
    }

    public void setReplyCommentUrl(String replyCommentUrl) {
        this.replyCommentUrl = replyCommentUrl;
    }

    public void setFavoritesTimelineUrl(String favoritesTimelineUrl) {
        this.favoritesTimelineUrl = favoritesTimelineUrl;
    }

    public void setCreateFavoriteUrl(String createFavoriteUrl) {
        this.createFavoriteUrl = createFavoriteUrl;
    }

    public void setDestroyFavoriteUrl(String destroyFavoriteUrl) {
        this.destroyFavoriteUrl = destroyFavoriteUrl;
    }

    public void setInboxTimelineUrl(String inboxTimelineUrl) {
        this.inboxTimelineUrl = inboxTimelineUrl;
    }

    public void setOutboxTimelineUrl(String outboxTimelineUrl) {
        this.outboxTimelineUrl = outboxTimelineUrl;
    }

    public void setSendDirectMessageUrl(String sendDirectMessageUrl) {
        this.sendDirectMessageUrl = sendDirectMessageUrl;
    }

    public void setDestroyDirectMessageUrl(String destroyDirectMessageUrl) {
        this.destroyDirectMessageUrl = destroyDirectMessageUrl;
    }

    public void setShowOfUserUrl(String showOfUserUrl) {
        this.showOfUserUrl = showOfUserUrl;
    }

    public void setFriendsUrl(String friendsUrl) {
        this.friendsUrl = friendsUrl;
    }

    public void setFollowsUrl(String followsUrl) {
        this.followsUrl = followsUrl;
    }

    public void setCreateFriendshipUrl(String createFriendshipUrl) {
        this.createFriendshipUrl = createFriendshipUrl;
    }

    public void setDestroyFriendshipUrl(String destroyFriendshipUrl) {
        this.destroyFriendshipUrl = destroyFriendshipUrl;
    }

    public void setShowOfFriendshipUrl(String showOfFriendshipUrl) {
        this.showOfFriendshipUrl = showOfFriendshipUrl;
    }

    public void setVerifyCredentialsUrl(String verifyCredentialsUrl) {
        this.verifyCredentialsUrl = verifyCredentialsUrl;
    }

    public void setRateLimitStatusUrl(String rateLimitStatusUrl) {
        this.rateLimitStatusUrl = rateLimitStatusUrl;
    }

    public void setUpdateProfileImageUrl(String updateProfileImageUrl) {
        this.updateProfileImageUrl = updateProfileImageUrl;
    }

    public void setUpdateProfileUrl(String updateProfileUrl) {
        this.updateProfileUrl = updateProfileUrl;
    }

    public void setEndSessionUrl(String endSessionUrl) {
        this.endSessionUrl = endSessionUrl;
    }

    @Override
    public String getFavoritesOfUserUrl() {
        return favoritesOfUserUrl;
    }

    public void setFavoritesOfUserUrl(String favoritesOfUserUrl) {
        this.favoritesOfUserUrl = favoritesOfUserUrl;
    }

    @Override
    public String getSearchStatusUrl() {
        return searchStatusUrl;
    }

    public void setSearchStatusUrl(String searchStatusUrl) {
        this.searchStatusUrl = searchStatusUrl;
    }

    @Override
    public String getRetweetedByMeUrl() {
        return retweetedByMeUrl;
    }

    public void setRetweetedByMeUrl(String retweetedByMeUrl) {
        this.retweetedByMeUrl = retweetedByMeUrl;
    }

    @Override
    public String getCommentsToMeUrl() {
        return commentsToMeUrl;
    }

    public void setCommentsToMeUrl(String commentsToMeUrl) {
        this.commentsToMeUrl = commentsToMeUrl;
    }

    @Override
    public String getSearchUserUrl() {
        return searchUserUrl;
    }

    public void setSearchUserUrl(String searchUserUrl) {
        this.searchUserUrl = searchUserUrl;
    }

    @Override
    public String getHomeTimelineUrl() {
        return homeTimelineUrl;
    }

    public void setHomeTimelineUrl(String homeTimelineUrl) {
        this.homeTimelineUrl = homeTimelineUrl;
    }

    @Override
    public String getRetweetsOfStatusUrl() {
        return retweetsOfStatusUrl;
    }

    public void setRetweetsOfStatusUrl(String retweetsOfStatusUrl) {
        this.retweetsOfStatusUrl = retweetsOfStatusUrl;
    }

    public String getCurrentTrendsUrl() {
        return currentTrendsUrl;
    }

    public void setCurrentTrendsUrl(String currentTrendsUrl) {
        this.currentTrendsUrl = currentTrendsUrl;
    }

    public String getDailyTrendsUrl() {
        return dailyTrendsUrl;
    }

    public void setDailyTrendsUrl(String dailyTrendsUrl) {
        this.dailyTrendsUrl = dailyTrendsUrl;
    }

    public String getWeeklyTrendsUrl() {
        return weeklyTrendsUrl;
    }

    public void setWeeklyTrendsUrl(String weeklyTrendsUrl) {
        this.weeklyTrendsUrl = weeklyTrendsUrl;
    }

    public String getUnreadCountUrl() {
        return unreadCountUrl;
    }

    public void setUnreadCountUrl(String unreadCountUrl) {
        this.unreadCountUrl = unreadCountUrl;
    }

    public String getResetUnreadCountUrl() {
        return resetUnreadCountUrl;
    }

    public void setResetUnreadCountUrl(String resetUnreadCountUrl) {
        this.resetUnreadCountUrl = resetUnreadCountUrl;
    }

    public String getCreateBlockUrl() {
        return createBlockUrl;
    }

    public void setCreateBlockUrl(String createBlockUrl) {
        this.createBlockUrl = createBlockUrl;
    }

    public String getDestroyBlockUrl() {
        return destroyBlockUrl;
    }

    public void setDestroyBlockUrl(String destroyBlockUrl) {
        this.destroyBlockUrl = destroyBlockUrl;
    }

    public String getExistsBlockUrl() {
        return existsBlockUrl;
    }

    public void setExistsBlockUrl(String existsBlockUrl) {
        this.existsBlockUrl = existsBlockUrl;
    }

    public String getBlockingUsersUrl() {
        return blockingUsersUrl;
    }

    public void setBlockingUsersUrl(String blockingUsersUrl) {
        this.blockingUsersUrl = blockingUsersUrl;
    }

    public String getBlockingUsersIdsUrl() {
        return blockingUsersIdsUrl;
    }

    public void setBlockingUsersIdsUrl(String blockingUsersIdsUrl) {
        this.blockingUsersIdsUrl = blockingUsersIdsUrl;
    }

    public String getCreateGroupUrl() {
        return createGroupUrl;
    }

    public String getUpdateGroupUrl() {
        return updateGroupUrl;
    }

    public String getDestroyGroupUrl() {
        return destroyGroupUrl;
    }

    public String getShowGroupUrl() {
        return showOfGroupUrl;
    }

    public String getGroupListUrl() {
        return groupListUrl;
    }

    public String getGroupStatusesUrl() {
        return groupStatusesUrl;
    }

    public String getGroupMembershipsUrl() {
        return groupMembershipsUrl;
    }

    public String getGroupSubscriptionsUrl() {
        return groupSubscriptionsUrl;
    }

    public String getCreateGroupSubscriberUrl() {
        return createGroupSubscriberUrl;
    }

    public String getGroupSubscribersUrl() {
        return groupSubscribersUrl;
    }

    public String getDestroyGroupSubscriberUrl() {
        return destroyGroupSubscriberUrl;
    }

    public String getShowGroupSubscriberUrl() {
        return showGroupSubscriberUrl;
    }

    public String getGroupMembersUrl() {
        return groupMembersUrl;
    }

    public String getCreateGroupMembersUrl() {
        return createGroupMembersUrl;
    }

    public String getDestroyGroupMemberUrl() {
        return destroyGroupMemberUrl;
    }

    public String getShowGroupMemberUrl() {
        return showGroupMemberUrl;
    }

    public String getDailyHotRetweetsUrl() {
        return dailyHotRetweetsUrl;
    }

    public String getDailyHotCommentsUrl() {
        return dailyHotCommentsUrl;
    }

    public void setDailyHotCommentsUrl(String dailyHotCommentsUrl) {
        this.dailyHotCommentsUrl = dailyHotCommentsUrl;
    }

    public String getWeeklyHotRetweetsUrl() {
        return weeklyHotRetweetsUrl;
    }

    public void setWeeklyHotRetweetsUrl(String weeklyHotRetweetsUrl) {
        this.weeklyHotRetweetsUrl = weeklyHotRetweetsUrl;
    }

    public String getWeeklyHotCommentsUrl() {
        return weeklyHotCommentsUrl;
    }

    public void setWeeklyHotCommentsUrl(String weeklyHotCommentsUrl) {
        this.weeklyHotCommentsUrl = weeklyHotCommentsUrl;
    }

    public String getCreateGroupMemberUrl() {
        return createGroupMemberUrl;
    }

    public void setCreateGroupMemberUrl(String createGroupMemberUrl) {
        this.createGroupMemberUrl = createGroupMemberUrl;
    }

    public String getAllGroupsUrl() {
        return allGroupsUrl;
    }

    public void setAllGroupsUrl(String allGroupsUrl) {
        this.allGroupsUrl = allGroupsUrl;
    }

    public void setCreateGroupUrl(String createGroupUrl) {
        this.createGroupUrl = createGroupUrl;
    }

    public void setUpdateGroupUrl(String updateGroupUrl) {
        this.updateGroupUrl = updateGroupUrl;
    }

    public void setDestroyGroupUrl(String destroyGroupUrl) {
        this.destroyGroupUrl = destroyGroupUrl;
    }

    public void setShowOfGroupUrl(String showOfGroupUrl) {
        this.showOfGroupUrl = showOfGroupUrl;
    }

    public void setGroupListUrl(String groupListUrl) {
        this.groupListUrl = groupListUrl;
    }

    public void setGroupStatusesUrl(String groupStatusesUrl) {
        this.groupStatusesUrl = groupStatusesUrl;
    }

    public void setGroupMembershipsUrl(String groupMembershipsUrl) {
        this.groupMembershipsUrl = groupMembershipsUrl;
    }

    public void setGroupSubscriptionsUrl(String groupSubscriptionsUrl) {
        this.groupSubscriptionsUrl = groupSubscriptionsUrl;
    }

    public void setCreateGroupSubscriberUrl(String createGroupSubscriberUrl) {
        this.createGroupSubscriberUrl = createGroupSubscriberUrl;
    }

    public void setGroupSubscribersUrl(String groupSubscribersUrl) {
        this.groupSubscribersUrl = groupSubscribersUrl;
    }

    public void setDestroyGroupSubscriberUrl(String destroyGroupSubscriberUrl) {
        this.destroyGroupSubscriberUrl = destroyGroupSubscriberUrl;
    }

    public void setShowGroupSubscriberUrl(String showGroupSubscriberUrl) {
        this.showGroupSubscriberUrl = showGroupSubscriberUrl;
    }

    public void setGroupMembersUrl(String groupMembersUrl) {
        this.groupMembersUrl = groupMembersUrl;
    }

    public void setCreateGroupMembersUrl(String createGroupMembersUrl) {
        this.createGroupMembersUrl = createGroupMembersUrl;
    }

    public void setDestroyGroupMemberUrl(String destroyGroupMemberUrl) {
        this.destroyGroupMemberUrl = destroyGroupMemberUrl;
    }

    public void setShowGroupMemberUrl(String showGroupMemberUrl) {
        this.showGroupMemberUrl = showGroupMemberUrl;
    }

    public void setDailyHotRetweetsUrl(String dailyHotRetweetsUrl) {
        this.dailyHotRetweetsUrl = dailyHotRetweetsUrl;
    }

	@Override
	public String getGeoLocationByCoordinateUrl() {
		return geoLocationByCoordinateUrl;
	}

	public void setGeoLocationByCoordinateUrl(String geoLocationByCoordinateUrl) {
		this.geoLocationByCoordinateUrl = geoLocationByCoordinateUrl;
	}

}
