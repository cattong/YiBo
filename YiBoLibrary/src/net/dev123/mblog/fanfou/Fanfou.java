package net.dev123.mblog.fanfou;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.Paging;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.mblog.entity.Group;
import net.dev123.mblog.entity.RateLimitStatus;
import net.dev123.mblog.entity.Relationship;
import net.dev123.mblog.entity.ResponseCount;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.Trends;
import net.dev123.mblog.entity.UnreadCount;
import net.dev123.mblog.entity.UnreadType;
import net.dev123.mblog.entity.User;

import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 饭否微博API实现。
 * @author Weiping Ye
 * @version Jul 19, 2011 9:56:11 PM
 * identifyName = userId;
 * displayName = userId;
 */
public class Fanfou extends MicroBlog {

	private static final long serialVersionUID = -5340460378724312533L;
	private static final Logger logger = LoggerFactory.getLogger(Fanfou.class.getSimpleName());

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
		return getTimeline(null, paging, conf.getFriendTimelineURL());
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging)
			throws LibException {
		return getTimeline(identityName, paging, conf.getUserTimelineURL());
	}

	private List<Status> getTimeline(String identityName, Paging<Status> paging, String url)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, url, auth);
		if (StringUtil.isNotEmpty(identityName)) {
			httpRequestMessage.addParameter("id", identityName);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		if (paging.getAttribute("format") != null) {
			httpRequestMessage.addParameter("format", paging.getAttribute("format"));
		}
		if (paging.getAttribute("callback") != null) {
			httpRequestMessage.addParameter("callback", paging.getAttribute("callback"));
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getPublicTimelineURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<Status> statusList = FanfouStatusAdaptor.createStatusList(response);
		return statusList;
	}

	@Override
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getMetionsTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		if (paging.getAttribute("format") != null) {
			httpRequestMessage.addParameter("format", paging.getAttribute("format"));
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<Status> statusList = FanfouStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getDestroyStatusURL(), auth);
		httpRequestMessage.addParameter("id", statusId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getRetweetStatusURL(), auth);
		httpRequestMessage.addParameter("repost_status_id", statusId);
		httpRequestMessage.addParameter("status", status);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging)
			throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getSearchStatusURL(), auth);
		httpRequestMessage.addParameter("q", keyword);
		if (paging.getMax() != null && paging.getMax().getId() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusesList = FanfouStatusAdaptor.createStatusList(response);

		setNextPageMax(paging, statusesList.get(statusesList.size()-1));

		updatePaging(statusesList, paging);
		return statusesList;
	}

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, String.format(conf.getShowOfStatusURL(), statusId), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
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

		String requestUrl = conf.getUpdateStatusURL();
		if (isUpload) {
			requestUrl = conf.getUploadStatusURL();
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, requestUrl, auth);
		httpRequestMessage.addParameters(parameters);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getFollowsURL(), auth);
		if(StringUtil.isNotEmpty(id)){
			httpRequestMessage.addParameter("id", id);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> userList = FanfouUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public List<User> getUserFriends(String id, Paging<User> paging) throws LibException {

		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getFriendsURL(), auth);
		if (StringUtil.isNotEmpty(id)) {
			httpRequestMessage.addParameter("id", id);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> userList = FanfouUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;

	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getShowOfUserURL(), auth);
		httpRequestMessage.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getDestroyDirectMessageURL(), auth);
		httpRequestMessage.addParameter("id", directMessageId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getInboxTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<DirectMessage> messagesList = FanfouDirectMessageAdaptor
				                               .createDirectMessageList(response);
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, conf.getOutboxTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<DirectMessage> messagesList = FanfouDirectMessageAdaptor
				                               .createDirectMessageList(response);
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String message)
			throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(message)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getSendDirectMessageURL(), auth);
		httpRequestMessage.addParameter("user", displayName);
		httpRequestMessage.addParameter("text", message);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public User createFriendship(String identityName) throws LibException {
		return managerFriendship(identityName, conf.getCreateFriendshipURL());
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		return managerFriendship(identityName, conf.getDestroyFriendshipURL());
	}

	private User managerFriendship(String identityName, String url) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("id", identityName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		relationship.setFollowed(isFollowed);
		relationship.setFollowing(isFollowing);
		relationship.setSourceNotificationsEnabled(false);//没有这个信息
		relationship.setSourceScreenName(sourceUser.getScreenName());
		relationship.setSourceUserId(sourceUser.getId());
		relationship.setTargetScreenName(targetUser.getScreenName());
		relationship.setTargetUserId(targetUser.getId());
		relationship.setBlocking(false);//没有这个信息

		return relationship;
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, conf.getExistFriendshipURL(), auth);
		httpRequestMessage.addParameter("user_a", sourceIdentifyName);
		httpRequestMessage.addParameter("user_b", targetIdentifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return Boolean.valueOf(response).booleanValue();
	}

	@Override
	public List<String> getFollowersIDs(Paging<String> paging) throws LibException {
		return getFollowersIDs(getUserId(), paging);
	}

	@Override
	public List<String> getFollowersIDs(String id, Paging<String> paging) throws LibException {
		return getIDs(id, paging, conf.getFollowersIDsURL());
	}

	@Override
	public List<String> getFriendsIDs(Paging<String> paging) throws LibException {
		return getFriendsIDs(getUserId(), paging);
	}

	@Override
	public List<String> getFriendsIDs(String id, Paging<String> paging) throws LibException {
		return getIDs(id, paging, conf.getFriendsIDsURL());
	}

	private List<String> getIDs(String id, Paging<String> paging, String url)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, url, auth);
		if (StringUtil.isNotEmpty(id)) {
			httpRequestMessage.addParameter("id", id);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = FanfouIDsAdaptor.createIdsList(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User updateProfile(String screenName, String email, String url, String location,
			        String description) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, conf.getVerifyCredentialsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);

		if (null != user) {
			this.screenName = user.getScreenName();
			this.userId = user.getId();
		}

		return user;
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		String url = String.format(conf.getCreateFavoriteURL(), statusId);
		return manageFavorite(statusId, url);
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		String url = String.format(conf.getDestroyFavoriteURL(), statusId);
		return manageFavorite(statusId, url);
	}

	private Status manageFavorite(String statusId, String url) throws LibException {
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FanfouStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		return getFavorites(null, paging);
	}

	@Override
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET,  conf.getFavoritesTimelineURL(), auth);
		if (!StringUtil.isEmpty(identifyName)) {
			httpRequestMessage.addParameter("id", identifyName);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList =  FanfouStatusAdaptor.createStatusList(response);
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {;
	    if (StringUtil.isEmpty(statusId) || StringUtil.isEmpty(comment)) {
	    	throw new LibException(ExceptionCode.PARAMETER_NULL);
	    }

		StatusUpdate update = new StatusUpdate(comment);
		update.setInReplyToStatusId(statusId);
		Status status = updateStatus(update);
		return FanfouCommentAdaptor.createCommentFromStatus(status);
	}

	@Override
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<ResponseCount> getResponseCountList(List<Status> statusList) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public UnreadCount getUnreadCount() throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public boolean resetUnreadCount(UnreadType unreadType) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Trends getCurrentTrends() throws LibException {
		Trends trends = null;
		List<Trends> trendsList = getDailyTrends();
		if (trendsList != null && trendsList.size() > 0) {
			trends = trendsList.get(0);
		}
		return trends;
	}

	@Override
	public List<Trends> getDailyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getCurrentTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Trends> trendsList = FanfouTrendsAdapter.createTrends(response);

		return trendsList;
	}

	@Override
	public List<Trends> getWeeklyTrends() throws LibException {
		return getDailyTrends();
	}

	@Override
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getCreateBlockURL(), auth);
		httpRequestMessage.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);
		user.setBlocking(true);
		return user;
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getDestroyBlockURL(), auth);
		httpRequestMessage.addParameter("id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = FanfouUserAdaptor.createUser(response);
		user.setBlocking(false);
		return user;
	}

	@Override
	public boolean existsBlock(String identifyName) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getWeeklyHotRetweets(Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getDailyHotComments(Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getWeeklyHotComments(Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group createGroup(String listName, boolean isPublicList,
			String description) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group updateGroup(String listId, String newListName,
			boolean isPublicList, String newDescription) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Group> getGroups(String listOwnerIdentifyName,
			Paging<Group> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group showGroup(String listId) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group destroyGroup(String listId) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> getGroupStatuses(String listId, Paging<Status> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Group> getGroupMemberships(String listMemberIdentifyName,
			Paging<Group> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Group> getGroupSubscriptions(
			String listOwnerIdentifyName, Paging<Group> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Group> getAllGroups(String identifyName)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<User> getGroupMembers(String listId, Paging<User> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group createGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group createGroupMembers(String listId, String[] identifyNames)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group destroyGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User showGroupMember(String listId, String identifyName)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<User> getGroupSubscribers(String listId, Paging<User> paging)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group createGroupSubscriber(String listId)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Group destroyGroupSubscriber(String listId)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User showGroupSubscriber(String listId, String identifyName)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

}
