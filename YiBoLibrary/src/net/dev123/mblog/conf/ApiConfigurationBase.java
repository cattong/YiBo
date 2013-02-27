package net.dev123.mblog.conf;

import net.dev123.commons.util.StringUtil;

public class ApiConfigurationBase implements ApiConfiguration {

    private String source;

    /**** micro blog base URL ****/
    private String restBaseURL;
    private String searchBaseURL;
    private String streamBaseURL;

    /**** micro blog rest address ****/
    // Query rest url;
    private String publicTimelineURL;
    private String friendTimelineURL; // same as homeTimelineURL
    private String homeTimelineURL;
    private String userTimelineURL;
    private String metionsTimelineURL;
    private String retweetedByMeURL;
    private String retweetsOfStatusURL;
    private String commentsTimelineURL;
    private String commentsOfStatusURL;
    private String commentsByMeURL;
    private String commentsToMeURL;
    private String countsOfCommentAndRetweetURL;
    private String unreadCountURL;
    private String resetUnreadCountURL;
    // A Status operate url;
    private String showOfStatusURL;
    private String updateStatusURL;
    private String uploadStatusURL;
    private String destroyStatusURL;
    private String retweetStatusURL;
    private String commentStatusURL;
    private String destroyCommentURL;
    private String replyCommentURL;
    private String searchStatusURL;
    // favorite operate url;
    private String favoritesTimelineURL;
    private String favoritesOfUserURL;
    private String createFavoriteURL;
    private String destroyFavoriteURL;
    // Direct Message Url;
    private String inboxTimelineURL; // direct messages received;
    private String outboxTimelineURL; // direct messages sended;
    private String sendDirectMessageURL;
    private String destroyDirectMessageURL;
    // User Information mehtod url;
    private String searchUserURL;
    private String showOfUserURL;
    private String friendsURL;
    private String followsURL;
    // friendship
    private String createFriendshipURL;
    private String existFriendshipURL;
    private String destroyFriendshipURL;
    private String showOfFriendshipURL;
    // social graph 社交网络
    private String friendsIDsURL;
    private String followersIDsURL;
    // User operate Url
    private String verifyCredentialsURL;
    private String rateLimitStatusURL;
    private String updateProfileImageURL;
    private String updateProfileURL;
    private String endSessionURL;
    // Trends URL
    private String currentTrendsURL;
    private String dailyTrendsURL;
    private String weeklyTrendsURL;
    // Block URL
    private String createBlockURL;
    private String destroyBlockURL;
    private String existsBlockURL;
    private String blockingUsersURL;
    private String blockingUsersIdsURL;

    private String createGroupURL;
    private String updateGroupURL;
    private String destroyGroupURL;
    private String showOfGroupURL;
    private String groupListURL;
    private String groupStatusesURL;
    private String groupMembershipsURL; // 获取用户被加入的列表
    private String groupSubscriptionsURL; // 获取用户订阅的组列表
    private String createGroupSubscriberURL;
    private String groupSubscribersURL;
    private String destroyGroupSubscriberURL;
    private String showGroupSubscriberURL;
    private String groupMembersURL;
    private String createGroupMembersURL;
    private String createGroupMemberURL;
    private String destroyGroupMemberURL;
    private String showGroupMemberURL;
    private String allGroupsURL;

    private String dailyHotRetweetsURL;
    private String dailyHotCommentsURL;
    private String weeklyHotRetweetsURL;
    private String weeklyHotCommentsURL;
    private String userTrendsURL;
    private String userTrendsStatusURL;

	//GeoLocation URL
//    private String geoLocationKeywordURL;
    private String geoLocationByCoordinateURL;

	@Override
    public final String getSource() {
        return source;
    }

    protected final void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getRestBaseURL() {
        return restBaseURL;
    }

    public final void setRestBaseURL(String restBaseURL) {
        if (StringUtil.isEmpty(restBaseURL)) {
            throw new NullPointerException("RestBaseURL is null.");
        }
        if (!restBaseURL.endsWith("/")) {
            restBaseURL += "/";
        }
        this.restBaseURL = restBaseURL;
    }

    @Override
    public String getSearchBaseURL() {
        return searchBaseURL;
    }

    public final void setSearchBaseURL(String searchBaseURL) {
        if (searchBaseURL == null) {
            throw new NullPointerException("SearchBaseURL is null.");
        }
        if (!searchBaseURL.endsWith("/")) {
            searchBaseURL += "/";
        }
        this.searchBaseURL = searchBaseURL;
    }

    @Override
    public String getStreamBaseURL() {
        return streamBaseURL;
    }

    public final void setStreamBaseURL(String streamBaseURL) {
        if (StringUtil.isEmpty(streamBaseURL)) {
            throw new NullPointerException("StreamBaseURL is null.");
        }
        if (!streamBaseURL.endsWith("/")) {
            streamBaseURL += "/";
        }
        this.streamBaseURL = streamBaseURL;
    }

    @Override
    public String getPublicTimelineURL() {
        return publicTimelineURL;
    }

    @Override
    public String getFriendTimelineURL() {
        return friendTimelineURL;
    }

    @Override
    public String getUserTimelineURL() {
        return userTimelineURL;
    }

    @Override
    public String getMetionsTimelineURL() {
        return metionsTimelineURL;
    }

    @Override
    public String getCommentsTimelineURL() {
        return commentsTimelineURL;
    }

    @Override
    public String getCommentsOfStatusURL() {
        return commentsOfStatusURL;
    }

    @Override
    public String getCommentsByMeURL() {
        return commentsByMeURL;
    }

    @Override
    public String getCountsOfCommentAndRetweetURL() {
        return countsOfCommentAndRetweetURL;
    }

    @Override
    public String getShowOfStatusURL() {
        return showOfStatusURL;
    }

    @Override
    public String getUpdateStatusURL() {
        return updateStatusURL;
    }

    @Override
    public String getUploadStatusURL() {
        return uploadStatusURL;
    }

    @Override
    public String getDestroyStatusURL() {
        return destroyStatusURL;
    }

    @Override
    public String getRetweetStatusURL() {
        return retweetStatusURL;
    }

    @Override
    public String getCommentStatusURL() {
        return commentStatusURL;
    }

    @Override
    public String getDestroyCommentURL() {
        return destroyCommentURL;
    }

    @Override
    public String getReplyCommentURL() {
        return replyCommentURL;
    }

    @Override
    public String getFavoritesTimelineURL() {
        return favoritesTimelineURL;
    }

    @Override
    public String getCreateFavoriteURL() {
        return createFavoriteURL;
    }

    @Override
    public String getDestroyFavoriteURL() {
        return destroyFavoriteURL;
    }

    @Override
    public String getInboxTimelineURL() {
        return inboxTimelineURL;
    }

    @Override
    public String getOutboxTimelineURL() {
        return outboxTimelineURL;
    }

    @Override
    public String getSendDirectMessageURL() {
        return sendDirectMessageURL;
    }

    @Override
    public String getDestroyDirectMessageURL() {
        return destroyDirectMessageURL;
    }

    @Override
    public String getShowOfUserURL() {
        return showOfUserURL;
    }

    @Override
    public String getFriendsURL() {
        return friendsURL;
    }

    @Override
    public String getFollowsURL() {
        return followsURL;
    }

    @Override
    public String getCreateFriendshipURL() {
        return createFriendshipURL;
    }

    @Override
    public String getExistFriendshipURL() {
        return existFriendshipURL;
    }

    @Override
    public String getDestroyFriendshipURL() {
        return destroyFriendshipURL;
    }

    @Override
    public String getShowOfFriendshipURL() {
        return showOfFriendshipURL;
    }

    @Override
    public String getFriendsIDsURL() {
        return friendsIDsURL;
    }

    @Override
    public String getFollowersIDsURL() {
        return followersIDsURL;
    }

    @Override
    public String getVerifyCredentialsURL() {
        return verifyCredentialsURL;
    }

    @Override
    public String getRateLimitStatusURL() {
        return rateLimitStatusURL;
    }

    @Override
    public String getUpdateProfileImageURL() {
        return updateProfileImageURL;
    }

    @Override
    public String getUpdateProfileURL() {
        return updateProfileURL;
    }

    @Override
    public String getEndSessionURL() {
        return endSessionURL;
    }

    public void setPublicTimelineURL(String publicTimelineURL) {
        this.publicTimelineURL = publicTimelineURL;
    }

    public void setFriendTimelineURL(String friendTimelineURL) {
        this.friendTimelineURL = friendTimelineURL;
    }

    public void setUserTimelineURL(String userTimelineURL) {
        this.userTimelineURL = userTimelineURL;
    }

    public void setMetionsTimelineURL(String metionsTimelineURL) {
        this.metionsTimelineURL = metionsTimelineURL;
    }

    public void setCommentsTimelineURL(String commentsTimelineURL) {
        this.commentsTimelineURL = commentsTimelineURL;
    }

    public void setCommentsOfStatusURL(String commentsOfStatusURL) {
        this.commentsOfStatusURL = commentsOfStatusURL;
    }

    public void setCommentsByMeURL(String commentsByMeURL) {
        this.commentsByMeURL = commentsByMeURL;
    }

    public void setCountsOfCommentAndRetweetURL(
            String countsOfCommentAndRetweetURL) {
        this.countsOfCommentAndRetweetURL = countsOfCommentAndRetweetURL;
    }

    public void setShowOfStatusURL(String showOfStatusURL) {
        this.showOfStatusURL = showOfStatusURL;
    }

    public void setUpdateStatusURL(String updateStatusURL) {
        this.updateStatusURL = updateStatusURL;
    }

    public void setUploadStatusURL(String uploadStatusURL) {
        this.uploadStatusURL = uploadStatusURL;
    }

    public void setDestroyStatusURL(String destroyStatusURL) {
        this.destroyStatusURL = destroyStatusURL;
    }

    public void setRetweetStatusURL(String retweetStatusURL) {
        this.retweetStatusURL = retweetStatusURL;
    }

    public void setCommentStatusURL(String commentStatusURL) {
        this.commentStatusURL = commentStatusURL;
    }

    public void setDestroyCommentURL(String destroyCommentURL) {
        this.destroyCommentURL = destroyCommentURL;
    }

    public void setReplyCommentURL(String replyCommentURL) {
        this.replyCommentURL = replyCommentURL;
    }

    public void setFavoritesTimelineURL(String favoritesTimelineURL) {
        this.favoritesTimelineURL = favoritesTimelineURL;
    }

    public void setCreateFavoriteURL(String createFavoriteURL) {
        this.createFavoriteURL = createFavoriteURL;
    }

    public void setDestroyFavoriteURL(String destroyFavoriteURL) {
        this.destroyFavoriteURL = destroyFavoriteURL;
    }

    public void setInboxTimelineURL(String inboxTimelineURL) {
        this.inboxTimelineURL = inboxTimelineURL;
    }

    public void setOutboxTimelineURL(String outboxTimelineURL) {
        this.outboxTimelineURL = outboxTimelineURL;
    }

    public void setSendDirectMessageURL(String sendDirectMessageURL) {
        this.sendDirectMessageURL = sendDirectMessageURL;
    }

    public void setDestroyDirectMessageURL(String destroyDirectMessageURL) {
        this.destroyDirectMessageURL = destroyDirectMessageURL;
    }

    public void setShowOfUserURL(String showOfUserURL) {
        this.showOfUserURL = showOfUserURL;
    }

    public void setFriendsURL(String friendsURL) {
        this.friendsURL = friendsURL;
    }

    public void setFollowsURL(String followsURL) {
        this.followsURL = followsURL;
    }

    public void setCreateFriendshipURL(String createFriendshipURL) {
        this.createFriendshipURL = createFriendshipURL;
    }

    public void setExistFriendshipURL(String existFriendshipURL) {
        this.existFriendshipURL = existFriendshipURL;
    }

    public void setDestroyFriendshipURL(String destroyFriendshipURL) {
        this.destroyFriendshipURL = destroyFriendshipURL;
    }

    public void setShowOfFriendshipURL(String showOfFriendshipURL) {
        this.showOfFriendshipURL = showOfFriendshipURL;
    }

    public void setFriendsIDsURL(String friendsIDsURL) {
        this.friendsIDsURL = friendsIDsURL;
    }

    public void setFollowersIDsURL(String followersIDsURL) {
        this.followersIDsURL = followersIDsURL;
    }

    public void setVerifyCredentialsURL(String verifyCredentialsURL) {
        this.verifyCredentialsURL = verifyCredentialsURL;
    }

    public void setRateLimitStatusURL(String rateLimitStatusURL) {
        this.rateLimitStatusURL = rateLimitStatusURL;
    }

    public void setUpdateProfileImageURL(String updateProfileImageURL) {
        this.updateProfileImageURL = updateProfileImageURL;
    }

    public void setUpdateProfileURL(String updateProfileURL) {
        this.updateProfileURL = updateProfileURL;
    }

    public void setEndSessionURL(String endSessionURL) {
        this.endSessionURL = endSessionURL;
    }

    @Override
    public String getFavoritesOfUserURL() {
        return favoritesOfUserURL;
    }

    public void setFavoritesOfUserURL(String favoritesOfUserURL) {
        this.favoritesOfUserURL = favoritesOfUserURL;
    }

    @Override
    public String getSearchStatusURL() {
        return searchStatusURL;
    }

    public void setSearchStatusURL(String searchStatusURL) {
        this.searchStatusURL = searchStatusURL;
    }

    @Override
    public String getRetweetedByMeURL() {
        return retweetedByMeURL;
    }

    public void setRetweetedByMeURL(String retweetedByMeURL) {
        this.retweetedByMeURL = retweetedByMeURL;
    }

    @Override
    public String getCommentsToMeURL() {
        return commentsToMeURL;
    }

    public void setCommentsToMeURL(String commentsToMeURL) {
        this.commentsToMeURL = commentsToMeURL;
    }

    @Override
    public String getSearchUserURL() {
        return searchUserURL;
    }

    public void setSearchUserURL(String searchUserURL) {
        this.searchUserURL = searchUserURL;
    }

    @Override
    public String getHomeTimelineURL() {
        return homeTimelineURL;
    }

    public void setHomeTimelineURL(String homeTimelineURL) {
        this.homeTimelineURL = homeTimelineURL;
    }

    @Override
    public String getRetweetsOfStatusURL() {
        return retweetsOfStatusURL;
    }

    public void setRetweetsOfStatusURL(String retweetsOfStatusURL) {
        this.retweetsOfStatusURL = retweetsOfStatusURL;
    }

    public String getCurrentTrendsURL() {
        return currentTrendsURL;
    }

    public void setCurrentTrendsURL(String currentTrendsURL) {
        this.currentTrendsURL = currentTrendsURL;
    }

    public String getDailyTrendsURL() {
        return dailyTrendsURL;
    }

    public void setDailyTrendsURL(String dailyTrendsURL) {
        this.dailyTrendsURL = dailyTrendsURL;
    }

    public String getWeeklyTrendsURL() {
        return weeklyTrendsURL;
    }

    public void setWeeklyTrendsURL(String weeklyTrendsURL) {
        this.weeklyTrendsURL = weeklyTrendsURL;
    }

    public String getUnreadCountURL() {
        return unreadCountURL;
    }

    public void setUnreadCountURL(String unreadCountURL) {
        this.unreadCountURL = unreadCountURL;
    }

    public String getResetUnreadCountURL() {
        return resetUnreadCountURL;
    }

    public void setResetUnreadCountURL(String resetUnreadCountURL) {
        this.resetUnreadCountURL = resetUnreadCountURL;
    }

    public String getCreateBlockURL() {
        return createBlockURL;
    }

    public void setCreateBlockURL(String createBlockURL) {
        this.createBlockURL = createBlockURL;
    }

    public String getDestroyBlockURL() {
        return destroyBlockURL;
    }

    public void setDestroyBlockURL(String destroyBlockURL) {
        this.destroyBlockURL = destroyBlockURL;
    }

    public String getExistsBlockURL() {
        return existsBlockURL;
    }

    public void setExistsBlockURL(String existsBlockURL) {
        this.existsBlockURL = existsBlockURL;
    }

    public String getBlockingUsersURL() {
        return blockingUsersURL;
    }

    public void setBlockingUsersURL(String blockingUsersURL) {
        this.blockingUsersURL = blockingUsersURL;
    }

    public String getBlockingUsersIdsURL() {
        return blockingUsersIdsURL;
    }

    public void setBlockingUsersIdsURL(String blockingUsersIdsURL) {
        this.blockingUsersIdsURL = blockingUsersIdsURL;
    }

    public String getCreateGroupURL() {
        return createGroupURL;
    }

    public String getUpdateGroupURL() {
        return updateGroupURL;
    }

    public String getDestroyGroupURL() {
        return destroyGroupURL;
    }

    public String getShowOfGroupURL() {
        return showOfGroupURL;
    }

    public String getGroupListURL() {
        return groupListURL;
    }

    public String getGroupStatusesURL() {
        return groupStatusesURL;
    }

    public String getGroupMembershipsURL() {
        return groupMembershipsURL;
    }

    public String getGroupSubscriptionsURL() {
        return groupSubscriptionsURL;
    }

    public String getCreateGroupSubscriberURL() {
        return createGroupSubscriberURL;
    }

    public String getGroupSubscribersURL() {
        return groupSubscribersURL;
    }

    public String getDestroyGroupSubscriberURL() {
        return destroyGroupSubscriberURL;
    }

    public String getShowGroupSubscriberURL() {
        return showGroupSubscriberURL;
    }

    public String getGroupMembersURL() {
        return groupMembersURL;
    }

    public String getCreateGroupMembersURL() {
        return createGroupMembersURL;
    }

    public String getDestroyGroupMemberURL() {
        return destroyGroupMemberURL;
    }

    public String getShowGroupMemberURL() {
        return showGroupMemberURL;
    }

    public String getDailyHotRetweetsURL() {
        return dailyHotRetweetsURL;
    }

    public String getDailyHotCommentsURL() {
        return dailyHotCommentsURL;
    }

    public void setDailyHotCommentsURL(String dailyHotCommentsURL) {
        this.dailyHotCommentsURL = dailyHotCommentsURL;
    }

    public String getWeeklyHotRetweetsURL() {
        return weeklyHotRetweetsURL;
    }

    public void setWeeklyHotRetweetsURL(String weeklyHotRetweetsURL) {
        this.weeklyHotRetweetsURL = weeklyHotRetweetsURL;
    }

    public String getWeeklyHotCommentsURL() {
        return weeklyHotCommentsURL;
    }

    public void setWeeklyHotCommentsURL(String weeklyHotCommentsURL) {
        this.weeklyHotCommentsURL = weeklyHotCommentsURL;
    }

    public String getCreateGroupMemberURL() {
        return createGroupMemberURL;
    }

    public void setCreateGroupMemberURL(String createGroupMemberURL) {
        this.createGroupMemberURL = createGroupMemberURL;
    }

    public String getAllGroupsURL() {
        return allGroupsURL;
    }

    public void setAllGroupsURL(String allGroupsURL) {
        this.allGroupsURL = allGroupsURL;
    }

    public void setCreateGroupURL(String createGroupURL) {
        this.createGroupURL = createGroupURL;
    }

    public void setUpdateGroupURL(String updateGroupURL) {
        this.updateGroupURL = updateGroupURL;
    }

    public void setDestroyGroupURL(String destroyGroupURL) {
        this.destroyGroupURL = destroyGroupURL;
    }

    public void setShowOfGroupURL(String showOfGroupURL) {
        this.showOfGroupURL = showOfGroupURL;
    }

    public void setGroupListURL(String groupListURL) {
        this.groupListURL = groupListURL;
    }

    public void setGroupStatusesURL(String groupStatusesURL) {
        this.groupStatusesURL = groupStatusesURL;
    }

    public void setGroupMembershipsURL(String groupMembershipsURL) {
        this.groupMembershipsURL = groupMembershipsURL;
    }

    public void setGroupSubscriptionsURL(String groupSubscriptionsURL) {
        this.groupSubscriptionsURL = groupSubscriptionsURL;
    }

    public void setCreateGroupSubscriberURL(String createGroupSubscriberURL) {
        this.createGroupSubscriberURL = createGroupSubscriberURL;
    }

    public void setGroupSubscribersURL(String groupSubscribersURL) {
        this.groupSubscribersURL = groupSubscribersURL;
    }

    public void setDestroyGroupSubscriberURL(String destroyGroupSubscriberURL) {
        this.destroyGroupSubscriberURL = destroyGroupSubscriberURL;
    }

    public void setShowGroupSubscriberURL(String showGroupSubscriberURL) {
        this.showGroupSubscriberURL = showGroupSubscriberURL;
    }

    public void setGroupMembersURL(String groupMembersURL) {
        this.groupMembersURL = groupMembersURL;
    }

    public void setCreateGroupMembersURL(String createGroupMembersURL) {
        this.createGroupMembersURL = createGroupMembersURL;
    }

    public void setDestroyGroupMemberURL(String destroyGroupMemberURL) {
        this.destroyGroupMemberURL = destroyGroupMemberURL;
    }

    public void setShowGroupMemberURL(String showGroupMemberURL) {
        this.showGroupMemberURL = showGroupMemberURL;
    }

    public void setDailyHotRetweetsURL(String dailyHotRetweetsURL) {
        this.dailyHotRetweetsURL = dailyHotRetweetsURL;
    }

//    @Override
//    public String getGeoLocationKeywordURL() {
//		return geoLocationKeywordURL;
//	}
//
//	public void setGeoLocationKeywordURL(String geoLocationKeywordURL) {
//		this.geoLocationKeywordURL = geoLocationKeywordURL;
//	}

	@Override
	public String getGeoLocationByCoordinateURL() {
		return geoLocationByCoordinateURL;
	}

	public void setGeoLocationByCoordinateURL(String geoLocationByCoordinateURL) {
		this.geoLocationByCoordinateURL = geoLocationByCoordinateURL;
	}

	@Override
	public String getUserTrendsURL() {
		return userTrendsURL;
	}

	@Override
	public String getUserTrendsStatusURL() {
		return userTrendsStatusURL;
	}

	public void setUserTrendsURL(String userTrendsURL) {
		this.userTrendsURL = userTrendsURL;
	}

	public void setUserTrendsStatusURL(String userTrendsStatusURL) {
		this.userTrendsStatusURL = userTrendsStatusURL;
	}

}
