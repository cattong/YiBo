package com.cattong.weibo.impl.fanfou;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.weibo.entity.Group;
import com.cattong.weibo.entity.RateLimitStatus;
import com.cattong.weibo.entity.ResponseCount;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;

/**
 * 饭否微博API实现。
 * @author 
 * @version
 * identifyName = userId;
 * displayName = userId;
 */
public class Fanfou extends Weibo {

	private static final long serialVersionUID = -5340460378724312533L;
	private static final Logger logger = LoggerFactory.getLogger(Fanfou.class);

	private transient String screenName = null;
	private transient String userId = null;

	private transient ResponseHandler<String> responseHandler;

	public Fanfou(Authorization auth) {
		super(auth);
		responseHandler = new FanfouResponseHandler();
	}

	@Override
	public String getScreenName() throws LibException {
		if (null == screenName) {
			verifyCredentials();
		}

		return screenName;
	}

	@Override
	public String getUserId() throws LibException {
		if (null == userId) {
			verifyCredentials();
		}
		return userId;
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		return getTimeline(null, paging, conf.getFriendTimelineUrl());
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging)
			throws LibException {
		return getTimeline(identityName, paging, conf.getUserTimelineUrl());
	}

	private List<Status> getTimeline(String identityName, Paging<Status> paging, String url)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, url, auth);
		if (StringUtil.isNotEmpty(identityName)) {
			httpRequestWrapper.addParameter("id", identityName);
		}
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		if (paging.getAttribute("format") != null) {
			httpRequestWrapper.addParameter("format", paging.getAttribute("format"));
		}
		if (paging.getAttribute("callback") != null) {
			httpRequestWrapper.addParameter("callback", paging.getAttribute("callback"));
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = FanfouStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		// 饭否没有homeTimeline接口。这里调用friendsTimeline接口（获得的数据是一样的）
		return getFriendsTimeline(paging);
	}

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getPublicTimelineUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = FanfouStatusAdaptor.createStatusList(response);
		return statusList;
	}

	@Override
	public List<Status> getMentionTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getMentionTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		if (paging.getAttribute("format") != null) {
			httpRequestWrapper.addParameter("format", paging.getAttribute("format"));
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = FanfouStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	/**
	 * 转发微博，支持添加转发附注，以及同时发布为评论
	 *
	 * @param statusId
	 *            转发的微博消息的ID，不能为空
	 * @param status
	 *            转发附注，可以为空
	 * @param isComment
	 *            饭否没有评论功能，改值无意义
	 * @return 转发后的微博消息对象
	 * @throws LibException
	 */
	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment)
			throws LibException {
		if (StringUtil.isEmpty(statusId) || StringUtil.isEmpty(status)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, conf.getRetweetStatusUrl(), auth);
		httpRequestWrapper.addParameter("repost_status_id", statusId);
		httpRequestWrapper.addParameter("status", status);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging)
			throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getSearchStatusUrl(), auth);
		httpRequestWrapper.addParameter("q", keyword);
		if (paging.getMax() != null && paging.getMax().getStatusId() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusesList = FanfouStatusAdaptor.createStatusList(response);

		setNextPageMax(paging, statusesList.get(statusesList.size()-1));

		updatePaging(statusesList, paging);
		return statusesList;
	}

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, String.format(conf.getShowStatusUrl(), statusId), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		boolean isRetweet = false;

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", latestStatus.getStatus());

		if (latestStatus.getInReplyToStatusId()!= null) {
			parameters.put("in_reply_to_status_id", latestStatus.getInReplyToStatusId());
			isRetweet = true;
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("location", latestStatus.getLocation().getLatitude() + ","
		                   + latestStatus.getLocation().getLongitude());
		}

		boolean isUpload = false;
		if (latestStatus.getImage() != null) {
			if (!isRetweet) {
				//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
				checkFileValidity(latestStatus.getImage());
				parameters.put("photo", latestStatus.getImage());
				isUpload = true;
			} else {
				logger.debug("Image file {} is ignored in retweet", latestStatus.getImage().getName());
			}
		}

		String requestUrl = conf.getUpdateStatusUrl();
		if (isUpload) {
			requestUrl = conf.getUploadStatusUrl();
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, requestUrl, auth);
		httpRequestWrapper.addParameters(parameters);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		return getUserFollowers(null, paging);
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return getUserFriends(null, paging);
	}

	@Override
	public List<User> getUserFollowers(String id, Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getFollowsUrl(), auth);
		if(StringUtil.isNotEmpty(id)){
			httpRequestWrapper.addParameter("id", id);
		}
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> userList = FanfouUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public List<User> getUserFriends(String id, Paging<User> paging) throws LibException {

		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getFriendsUrl(), auth);
		if (StringUtil.isNotEmpty(id)) {
			httpRequestWrapper.addParameter("id", id);
		}
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> userList = FanfouUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;

	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getShowUserUrl(), auth);
		httpRequestWrapper.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);
		if (user.getStatus() != null && user.getStatus().getUser() == null) {
			user.getStatus().setUser(user);
		}
		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
//		throw new LibException(ExceptionCode.UNSUPPORTED_API);
		return showUser(displayName);
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String messageId) throws LibException {
		return destroyDirectMessage(messageId);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String messageId) throws LibException {
		return destroyDirectMessage(messageId);
	}

	private DirectMessage destroyDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getDestroyDirectMessageUrl(), auth);
		httpRequestWrapper.addParameter("id", directMessageId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getInboxTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<DirectMessage> messagesList = FanfouDirectMessageAdaptor
				                               .createDirectMessageList(response);
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getOutboxTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<DirectMessage> messagesList = FanfouDirectMessageAdaptor
				                               .createDirectMessageList(response);
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String message)
			throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(message)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getSendDirectMessageUrl(), auth);
		httpRequestWrapper.addParameter("user", displayName);
		httpRequestWrapper.addParameter("text", message);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public User createFriendship(String identityName) throws LibException {
		return managerFriendship(identityName, conf.getCreateFriendshipUrl());
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		return managerFriendship(identityName, conf.getDestroyFriendshipUrl());
	}

	private User managerFriendship(String identityName, String url) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("id", identityName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouUserAdaptor.createUser(response);
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName)
			throws LibException {
		boolean isFollowing = isFollowing(sourceIdentifyName, targetIdentifyName);
		boolean isFollowed = isFollowing(targetIdentifyName, sourceIdentifyName);
		User sourceUser = showUser(sourceIdentifyName);
		User targetUser = showUser(targetIdentifyName);

		Relationship relationship = new Relationship();
		relationship.setSourceFollowedByTarget(isFollowed);
		relationship.setSourceFollowingTarget(isFollowing);
		relationship.setSourceScreenName(sourceUser.getScreenName());
		relationship.setSourceUserId(sourceUser.getUserId());
		relationship.setTargetScreenName(targetUser.getScreenName());
		relationship.setTargetUserId(targetUser.getUserId());
		relationship.setSourceBlockingTarget(false);
		if (StringUtil.isEquals(sourceIdentifyName, userId)) {
		    relationship.setSourceBlockingTarget(existsBlock(targetIdentifyName));
		}
		
		return relationship;
	}

	private boolean existsBlock(String identifyName) throws LibException {
		//throw new LibException(LibResultCode.API_UNSUPPORTED);
		return false;
	}
	
	/**
	 * sourceIdentifyName用户是否follow了targetIdentifyName用户。
	 * @param sourceIdentifyName
	 * @param targetIdentifyName
	 * @return true or false
	 * @throws LibException
	 */
	private boolean isFollowing(String sourceIdentifyName, String targetIdentifyName)
			throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName) || StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getShowFriendshipUrl(), auth);
		httpRequestWrapper.addParameter("user_a", sourceIdentifyName);
		httpRequestWrapper.addParameter("user_b", targetIdentifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return Boolean.valueOf(response).booleanValue();
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User updateProfile(String screenName, String email, String url, String location,
			        String description) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getVerifyCredentialsUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);

		if (null != user) {
			this.screenName = user.getScreenName();
			this.userId = user.getUserId();
		}

		return user;
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		String url = String.format(conf.getCreateFavoriteUrl(), statusId);
		return manageFavorite(statusId, url);
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		String url = String.format(conf.getDestroyFavoriteUrl(), statusId);
		return manageFavorite(statusId, url);
	}

	private Status manageFavorite(String statusId, String url) throws LibException {
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		return getFavorites(null, paging);
	}

	@Override
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET,  conf.getFavoritesTimelineUrl(), auth);
		if (!StringUtil.isEmpty(identifyName)) {
			httpRequestWrapper.addParameter("id", identifyName);
		}
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList =  FanfouStatusAdaptor.createStatusList(response);
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {;
	    if (StringUtil.isEmpty(statusId) || StringUtil.isEmpty(comment)) {
	    	throw new LibException(LibResultCode.E_PARAM_NULL);
	    }

		StatusUpdate update = new StatusUpdate(comment);
		update.setInReplyToStatusId(statusId);
		Status status = updateStatus(update);
		return FanfouCommentAdaptor.createCommentFromStatus(status);
	}

	@Override
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<ResponseCount> getResponseCountList(List<Status> statusList) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public UnreadCount getUnreadCount() throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public boolean resetUnreadCount(UnreadType unreadType) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getCreateBlockUrl(), auth);
		httpRequestWrapper.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);

		return user;
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getDestroyBlockUrl(), auth);
		httpRequestWrapper.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);

		return user;
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> getWeeklyHotRetweets(Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> getDailyHotComments(Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> getWeeklyHotComments(Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group createGroup(String listName, boolean isPublicList,
			String description) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group updateGroup(String listId, String newListName,
			boolean isPublicList, String newDescription) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Group> getGroups(String listOwnerIdentifyName,
			Paging<Group> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group showGroup(String listId) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group destroyGroup(String listId) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> getGroupStatuses(String listId, Paging<Status> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Group> getGroupMemberships(String listMemberIdentifyName,
			Paging<Group> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Group> getAllGroups(String identifyName)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<User> getGroupMembers(String listId, Paging<User> paging)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group createGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group createGroupMembers(String listId, String[] identifyNames)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Group destroyGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public User showGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}
}
