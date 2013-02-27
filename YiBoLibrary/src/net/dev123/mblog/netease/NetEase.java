package net.dev123.mblog.netease;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.Emotions;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * NetEase微博API实现
 * @version
 * @author 敏升工作室
 * 网易平台属性声明(网易api平台声明screen_name\name与我们库的定义相反)：
 * identifyName = userId;
 * displayName = screenName;
 */
public class NetEase extends MicroBlog {
	private static final long serialVersionUID = 222763618354073280L;

	private transient ResponseHandler<String> responseHandler;
	public NetEase(Authorization auth) {
		super(auth);
		responseHandler = new NetEaseResponseHandler();
	}

	private transient String screenName = null;
	private transient String userId = null;

	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			verifyCredentials();
		}
		return screenName;
	}

	public String getUserId() throws LibException  {
		if (StringUtil.isEmpty(userId)) {
			verifyCredentials();
		}
		return userId;
	}

	/* Status Methods */

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestMessage request = null;
		request = new HttpRequestMessage(HttpMethod.GET, conf.getPublicTimelineURL(), auth);
		request.addParameter("trim_user", false);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> statusList = NetEaseStatusAdaptor.createStatusList(response);
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.163.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%99%BB%E5%BD%95%E7%94%A8%E6%88%B7%E5%85%B3%E6%B3%A8%E7%94%A8%E6%88%B7%E7%9A%84%E6%9C%80%E6%96%B0%E5%BE%AE%E5%8D%9A%E5%88%97%E8%A1%A8(statuses/home_timeline)">statuses/friends_timeline</a>
	 * </p>
	 *
	 * @param paging
	 * 		分页参数，本接口使用page分页，支持since、max参数
	 */
	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getHomeTimelineURL(), paging, null);
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getFriendTimelineURL(), paging, null);
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		return getStatusList(conf.getUserTimelineURL(), paging, params);
	}

	@Override
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getMetionsTimelineURL(), paging, null);
	}

	/**
	 * <Strong>NetEase不提供此接口</Strong><BR>
	 * <BR>
	 *
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/* StatusMethods */

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getShowOfStatusURL(), realStatusId);
		return getStatus(url, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		String status = Emotions.specializeEmotion(ServiceProvider.NetEase, latestStatus.getStatus());
		if (latestStatus.getImage() != null) {
			status += (" " + uploadImage(latestStatus.getImage()));
		}
		parameters.put("status", status);

		if (latestStatus.getInReplyToStatusId() != null) {
			parameters.put("in_reply_to_status_id", latestStatus.getInReplyToStatusId());
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("lat", latestStatus.getLocation().getLatitude());
			parameters.put("long", latestStatus.getLocation().getLongitude());
		}

		Status newStatus = updateStatus(conf.getUpdateStatusURL(), parameters);
		return newStatus;
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getDestroyStatusURL(), realStatusId);
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		if (StringUtil.isNotEmpty(status)) {
			parameters.put("status",
					Emotions.specializeEmotion(ServiceProvider.NetEase, status));
		} else {
			parameters.put("status", "");
		}
		if (isComment) {
			parameters.put("is_comment", 1);
		}
		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getRetweetStatusURL(), realStatusId);

		Status newStatus = updateStatus(url, parameters);
		return newStatus;
	}

	@Override
	@Deprecated
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}


	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", keyword);
		if (paging.getPageSize() > 0) {
			params.put("per_page", paging.getPageSize());
		}
		if (paging.getPageIndex() > 0) {
			params.put("page", paging.getPageIndex());
		}

		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, conf.getSearchStatusURL(), auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> listStatus = NetEaseStatusAdaptor.createStatusSearchResultList(response);

		updatePaging(listStatus, paging);
		return listStatus;
	}

	/* User Methods */

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identifyName);

		User user = getUser(conf.getShowOfUserURL(), params);
		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		if (StringUtil.isEmpty(displayName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", displayName);

		User user = getUser(conf.getShowOfUserURL(), params);
		return user;
	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
        if (!paging.isPagePaging()) {
        	this.initPagePaging(paging);
        }

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, conf.getSearchUserURL(), auth);
		request.addParameter("q", keyword);
		int perPage = paging.getPageSize() > 20 ? 20 : paging.getPageSize();
		request.addParameter("per_page", perPage);
		request.addParameter("page", paging.getPageIndex());

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<User> listUser = NetEaseUserAdaptor.createUserList(response);
		updatePaging(listUser, paging);

		return listUser;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		verifyCredentials();
		return getUserFriends(userId, paging);
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, conf.getFriendsURL(), auth);
		request.addParameter("user_id", identifyName);
		request.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<User> listFollowing = NetEaseUserAdaptor.createPagableUserList(response);
		updatePaging(listFollowing, paging);

		return listFollowing;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		return getUserFollowers(getScreenName(), paging);
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
        if (!paging.isCursorPaging()) {
        	initCursorPaging(paging);
        }

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, conf.getFollowsURL(), auth);
		request.addParameter("user_id", identifyName);
		request.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<User> listFollower = NetEaseUserAdaptor.createPagableUserList(response);
		updatePaging(listFollower, paging);

		return listFollower;
	}

	/* Direct Message Methods */

	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
		    initPagePaging(paging);
		}
		List<DirectMessage> listMessage = getDirectMessageList(conf.getInboxTimelineURL(), paging, null);
		return listMessage;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
		    initPagePaging(paging);
		}
		List<DirectMessage> listMessage = getDirectMessageList(conf.getOutboxTimelineURL(), paging, null);
		return listMessage;
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
        //diplayName 为screenName
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", displayName);
		params.put("text", Emotions.specializeEmotion(ServiceProvider.NetEase, text));

		DirectMessage message = updateDirectMessage(conf.getSendDirectMessageURL(), params);
		return message;
	}

	private DirectMessage destroyDirectMessage(String id) throws LibException {
		if (StringUtil.isEmpty(id)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getDestroyDirectMessageURL(), id);
		DirectMessage message = updateDirectMessage(url, null);
		return message;
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String id) throws LibException {
		return destroyDirectMessage(id);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String id) throws LibException {
		return destroyDirectMessage(id);
	}

	/* FriendshpMehods */

	@Override
	public User createFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		User user = updateUser(conf.getCreateFriendshipURL(), params);
		return user;
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		User user = updateUser(conf.getDestroyFriendshipURL(), params);
		return user;
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName) || StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source_id", sourceIdentifyName);
		params.put("target_id", targetIdentifyName);

		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, conf.getShowOfFriendshipURL(), auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		Relationship relationship = NetEaseRelationshipAdaptor.createRelationship(response);
		return relationship;
	}

	/* Social Graph Methods */

	/**
	 * <Strong>NetEase不提供此接口</Strong><BR>
	 *
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public List<String> getFriendsIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFriendsIDs(String identifyName, Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFollowersIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFollowersIDs(String identifyName, Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public User verifyCredentials() throws LibException {
		User user = getUser(conf.getVerifyCredentialsURL(), null);
		if (user != null) {
			this.screenName = user.getScreenName();
			this.userId = user.getId();
		}

		return user;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		HttpRequestMessage request = null;
		request = new HttpRequestMessage(HttpMethod.GET, conf.getRateLimitStatusURL(), auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		return NetEaseRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		checkFileValidity(image);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileImageURL(), auth);
		httpRequestMessage.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public User updateProfile(String screenname, String email,
		String url, String location, String description) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileURL(), auth);
		if (StringUtil.isNotEmpty(screenname) && !getScreenName().equals(screenname)) {
			httpRequestMessage.addParameter("nick_name", screenname);
		}
		if (StringUtil.isNotEmpty(description)){
			httpRequestMessage.addParameter("description", description);
		}

		if (httpRequestMessage.getParameters().size() > 0) {
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			return NetEaseUserAdaptor.createUser(response);
		} else {
			return verifyCredentials();
		}
	}

	/* FavoriteMethods */


	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getCreateFavoriteURL(), realStatusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getDestroyFavoriteURL(), realStatusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		List<Status> listFavorite = getFavorites(getUserId(), paging);
		return listFavorite;
	}

	@Override
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Status max = paging.getMax();
		Status since = paging.getSince();
		Map<String, Object> params = new HashMap<String, Object>();
		if (max != null) {
			params.put("since_id", max.getId());
		}
		params.put("count", paging.getPageSize());

		String url = String.format(conf.getFavoritesOfUserURL(), identifyName);
		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> listFavorite = NetEaseStatusAdaptor.createStatusList(response);

		ListUtil.truncate(listFavorite, max, since);
		updatePaging(listFavorite, paging);
		if (!paging.isLastPage() && listFavorite.size() > 0) {
			setNextPageMax(paging, listFavorite.get(listFavorite.size() - 1));
		}

		return listFavorite;
	}

	/* CommentMethods */

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		return createComment(comment, statusId, null);
	}

	@Override
	public Comment createComment(String comment, String statusId, String commentId)
			throws LibException {
		if (StringUtil.isEmpty(comment)
			|| StringUtil.isEmpty(statusId) ) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		String realId = null;
		if (StringUtil.isEmpty(commentId)) {
			realId = statusId;
		} else {
			realId = commentId;
		}
				
		String realCommentId = extractRealStatusId(realId);
		params.put("id", realCommentId);
		params.put("status", Emotions.specializeEmotion(ServiceProvider.NetEase, comment));

		Status status = updateStatus(conf.getCommentStatusURL(), params);
		Comment newComment = NetEaseCommentAdaptor.createCommentFromStatus(status);
		return newComment;
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String realCommentId = extractRealStatusId(commentId);
		Status status = destroyStatus(realCommentId);
		Comment comment = NetEaseCommentAdaptor.createCommentFromStatus(status);
		return comment;
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Comment max = paging.getMax();
		Comment since = paging.getSince();
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (since != null) {
			params.put("max_id", since.getId());
		}
		if (max != null) {
			params.put("since_id", max.getId());
		}
		params.put("count", paging.getPageSize());

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getCommentsOfStatusURL(), realStatusId);
		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Comment> listComment = NetEaseCommentAdaptor.createCommentsList(response);
		if (listComment != null && listComment.size() > 0) {
			setNextPageSince(paging, listComment.get(listComment.size() - 1));
		}

		updatePaging(listComment, paging);
		return listComment;
	}

	@Deprecated
	@Override
	public List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		List<Comment> listComment = getCommentList(conf.getCommentsByMeURL(), paging, null);
		return listComment;
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		List<Comment> listComment = getCommentList(conf.getCommentsToMeURL(), paging, null);
		return listComment;
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null || StringUtil.isEmpty(status.getId())) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		ResponseCount count = new ResponseCount();
		String statusId = status.getId();
		count.setServiceProvider(ServiceProvider.NetEase);
		count.setStatusId(statusId);

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getShowOfStatusURL(), realStatusId);
		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);

		status = NetEaseStatusAdaptor.createStatus(response);
		count.setCommentsCount(status.getCommentCount());
		count.setRetweetCount(status.getRetweetCount());
		return count;
	}

	@Override
	@Deprecated
	public List<ResponseCount> getResponseCountList(List<Status> listStatus) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public UnreadCount getUnreadCount() throws LibException {
		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, conf.getUnreadCountURL(), auth);

		String response = HttpRequestHelper.execute(request, responseHandler);
		UnreadCount count = NetEaseCountAdaptor.createRemindCount(response);
		return count;
	}

	@Deprecated
	@Override
	public boolean resetUnreadCount(UnreadType type) throws LibException {
		return true;
	}

	private List<Status> getStatusList(String url, Paging<Status> paging, Map<String, Object> params)
	    throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Status since = paging.getSince();
		Status max = paging.getMax();
		List<Status> listStatus = null;

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameter("trim_user", false);
		request.addParameters(params);

		//网易的since_id，max_id与其他平台刚好相反（符合传统概念)
		if (since != null && StringUtil.isNotEmpty(since.getId())) {
			request.addParameter("max_id", since.getId());
		}
		if (max != null && StringUtil.isNotEmpty(max.getId())) {
			request.addParameter("since_id", max.getId());
		}
		request.addParameter("count", paging.getPageSize());

		String response = HttpRequestHelper.execute(request, responseHandler);
		if (paging.isCursorPaging()) {
			listStatus = NetEaseStatusAdaptor.createPagableStatusList(response);
		} else {
			listStatus = NetEaseStatusAdaptor.createStatusList(response);
		}

		listStatus = ListUtil.truncate(listStatus, max, since);

		//回设paging信息;
		updatePaging(listStatus, paging);
		if (!paging.isLastPage() && listStatus.size() > 0) {
			setNextPageMax(paging, listStatus.get(listStatus.size() - 1));
		}

		return listStatus;
	}

	private Status getStatus(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	private Status updateStatus(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	private User getUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = NetEaseUserAdaptor.createUser(response);
		
		if (user.getStatus() != null) {
			if (user.getStatus().getInReplyToStatusId() != null) {
				Status retweetedStatus = showStatus(user.getStatus().getInReplyToStatusId());
				user.getStatus().setRetweetedStatus(retweetedStatus);
			}
			if (user.getStatus().getUser() == null) {
				user.getStatus().setUser(user);
			}
		}
		return user;
	}

	private User updateUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = NetEaseUserAdaptor.createUser(response);
		return user;
	}

	private List<Comment> getCommentList(String url, Paging<Comment> paging, Map<String, Object> params)
        throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Comment since = paging.getSince();
		Comment max = paging.getMax();
		List<Comment> listComment = null;

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameter("trim_user", false);
		request.addParameters(params);

		//网易的since_id，max_id与其他平台刚好相反（符合传统概念)
		if (since != null && StringUtil.isNotEmpty(since.getId())) {
			request.addParameter("max_id", since.getId());
		}
		if (max != null && StringUtil.isNotEmpty(max.getId())) {
			request.addParameter("since_id", max.getId());
		}
		request.addParameter("count", paging.getPageSize());

		String response = HttpRequestHelper.execute(request, responseHandler);
		if (paging.isCursorPaging()) {
			listComment = NetEaseCommentAdaptor.createPagableCommentsList(response);
		} else {
			listComment = NetEaseCommentAdaptor.createCommentsList(response);
		}

		listComment = ListUtil.truncate(listComment, max, since);

		//回设paging信息;
		updatePaging(listComment, paging);
		if (!paging.isLastPage() && listComment.size() > 0) {
			setNextPageMax(paging, listComment.get(listComment.size() - 1));
		}

		return listComment;
    }

	private List<DirectMessage> getDirectMessageList(
		String url, Paging<DirectMessage> paging, Map<String, Object> params)
		throws LibException {
        if (StringUtil.isEmpty(url) || paging == null) {
        	throw new LibException(ExceptionCode.PARAMETER_ERROR);
        }

        DirectMessage max = paging.getMax();
        DirectMessage since = paging.getSince();
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);

		if (max != null) {
			request.addParameter("since_id", max.getId());
		}
		request.addParameter("count", paging.getPageSize());

		String response = HttpRequestHelper.execute(request, responseHandler);

		ArrayList<DirectMessage> listMessage = NetEaseDirectMessageAdaptor.createDirectMessageList(response);
        ListUtil.truncate(listMessage, max, since);

		updatePaging(listMessage, paging);
		if (!paging.isLastPage() && listMessage.size() > 0) {
			setNextPageMax(paging, listMessage.get(listMessage.size() - 1));
		}

		return listMessage;

	}

	private DirectMessage updateDirectMessage(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		DirectMessage message = NetEaseDirectMessageAdaptor.createDirectMessage(response);
		return message;
	}

	private String uploadImage(File image) throws LibException {
		String imageUrl = "";
		checkFileValidity(image);

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("pic", image);
			HttpRequestMessage request = new HttpRequestMessage(
				HttpMethod.POST, conf.getUploadStatusURL(),
				auth, params);
			String uploadResponse = HttpRequestHelper.execute(request, responseHandler);
			JSONObject json = new JSONObject(uploadResponse);
			imageUrl = json.getString("upload_image_url");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}

		return imageUrl;
	}

	/**
	 * 网易的Status，我们用cursor_id替换了真正的statusId，
	 * 所以需要用到statusId的时候需要调用此方法
	 *
	 * @param statusId
	 * @return
	 */
	private String extractRealStatusId(String statusId){
		if (StringUtil.isEmpty(statusId)) {
			return null;
		}
		return statusId.split(":")[0];
	}

	@Override
	public String toString() {
		return "NetEase{" + "auth='" + auth + '\'' + '}';
	}

	@Override
	public Trends getCurrentTrends() throws LibException {
		List<Trends> trendsList = getDailyTrends();
		Trends trends = null;
		if (trendsList.size() > 0) {
			trends = trendsList.get(0);
		}
		return trends;
	}

	@Override
	public List<Trends> getDailyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return NetEaseTrendsAdapter.createTrends(response);
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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public boolean existsBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getExistsBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			NetEaseUserAdaptor.createUser(response);
			return true;
        } catch (LibException e) {
            if (e.getExceptionCode() == 404) {
                return false;
            }
            throw e;
        }
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, conf.getBlockingUsersURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		List<User> userList = NetEaseUserAdaptor.createUserList(response);
		updatePaging(userList, paging);		
		return userList;
	}

	@Override
	public List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, conf.getBlockingUsersIdsURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		List<String> idList = NetEaseIDsAdaptor.createIdsList(response);
		updatePaging(idList, paging);
		return idList;
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
	public List<Group> getGroups(String groupOwnerUserId,
			Paging<Group> paging) throws LibException {
		if (!getUserId().equals(groupOwnerUserId)) {
			throw new LibException(ExceptionCode.UNSUPPORTED_API);
		}
		
		paging.setLastPage(true);
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getGroupListURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		return NetEaseGroupAdaptor.createGroupList(response);
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
		if (StringUtil.isEmpty(listId)
			|| paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", listId);
		
		return getStatusList(conf.getGroupStatusesURL(), paging, params);
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

	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyHotRetweetsURL(), auth);
		httpRequestMessage.addParameter("size", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		paging.setLastPage(true);
		return NetEaseStatusAdaptor.createStatusListFromTopRetweets(response);
	}

	@Override
	public List<Status> getWeeklyHotRetweets(Paging<Status> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getWeeklyHotRetweetsURL(), auth);
		httpRequestMessage.addParameter("size", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		paging.setLastPage(true);
		return NetEaseStatusAdaptor.createStatusListFromTopRetweets(response);
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

}
