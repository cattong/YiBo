package com.cattong.weibo.impl.netease;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
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
import com.cattong.weibo.Emotions;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.weibo.entity.Group;
import com.cattong.weibo.entity.RateLimitStatus;
import com.cattong.weibo.entity.ResponseCount;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;

/**
 * NetEase微博API实现
 * @version
 * @author
 * 网易平台属性声明(网易api平台声明screen_name\name与我们库的定义相反)：
 * identifyName = userId;
 * displayName = screenName;
 */
public class NetEase extends Weibo {
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
		HttpRequestWrapper request = null;
		request = new HttpRequestWrapper(HttpMethod.GET, conf.getPublicTimelineUrl(), auth);
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
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getHomeTimelineUrl(), paging, null);
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getFriendTimelineUrl(), paging, null);
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		return getStatusList(conf.getUserTimelineUrl(), paging, params);
	}

	@Override
	public List<Status> getMentionTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getStatusList(conf.getMentionTimelineUrl(), paging, null);
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
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	/* StatusMethods */

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getShowStatusUrl(), realStatusId);
		return getStatus(url, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
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

		Status newStatus = updateStatus(conf.getUpdateStatusUrl(), parameters);
		return newStatus;
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getDestroyStatusUrl(), realStatusId);
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
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
		String url = String.format(conf.getRetweetStatusUrl(), realStatusId);

		Status newStatus = updateStatus(url, parameters);
		return newStatus;
	}

	@Override
	@Deprecated
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}


	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
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

		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, conf.getSearchStatusUrl(), auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identifyName);

		User user = getUser(conf.getShowUserUrl(), params);
		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		if (StringUtil.isEmpty(displayName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", displayName);

		User user = getUser(conf.getShowUserUrl(), params);
		return user;
	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
        if (!paging.isPagePaging()) {
        	this.initPagePaging(paging);
        }

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, conf.getSearchUserUrl(), auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		verifyCredentials();
		return getUserFriends(userId, paging);
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, conf.getFriendsUrl(), auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		return getUserFollowers(getScreenName(), paging);
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
        if (!paging.isCursorPaging()) {
        	initCursorPaging(paging);
        }

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, conf.getFollowsUrl(), auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
		    initPagePaging(paging);
		}
		List<DirectMessage> listMessage = getDirectMessageList(conf.getInboxTimelineUrl(), paging, null);
		return listMessage;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
		    initPagePaging(paging);
		}
		List<DirectMessage> listMessage = getDirectMessageList(conf.getOutboxTimelineUrl(), paging, null);
		return listMessage;
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
        //diplayName 为screenName
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", displayName);
		params.put("text", Emotions.specializeEmotion(ServiceProvider.NetEase, text));

		DirectMessage message = updateDirectMessage(conf.getSendDirectMessageUrl(), params);
		return message;
	}

	private DirectMessage destroyDirectMessage(String id) throws LibException {
		if (StringUtil.isEmpty(id)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getDestroyDirectMessageUrl(), id);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		User user = updateUser(conf.getCreateFriendshipUrl(), params);
		return user;
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", identityName);
		User user = updateUser(conf.getDestroyFriendshipUrl(), params);
		return user;
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName) || StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source_id", sourceIdentifyName);
		params.put("target_id", targetIdentifyName);

		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, conf.getShowFriendshipUrl(), auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		Relationship relationship = NetEaseRelationshipAdaptor.createRelationship(response);
		if (StringUtil.isEquals(sourceIdentifyName, userId) && relationship != null) {
			relationship.setSourceBlockingTarget(existsBlock(targetIdentifyName));
		}
		
		return relationship;
	}

	private boolean existsBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getExistsBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		try {
			String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
			NetEaseUserAdaptor.createUser(response);
			return true;
        } catch (LibException e) {
            if (e.getErrorCode() == 404) {
                return false;
            }
            throw e;
        }
	}
	
	/* Account Methods */

	@Override
	public User verifyCredentials() throws LibException {
		User user = getUser(conf.getVerifyCredentialsUrl(), null);
		if (user != null) {
			this.screenName = user.getScreenName();
			this.userId = user.getUserId();
		}

		return user;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		HttpRequestWrapper request = null;
		request = new HttpRequestWrapper(HttpMethod.GET, conf.getRateLimitStatusUrl(), auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		return NetEaseRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		checkFileValidity(image);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileImageUrl(), auth);
		httpRequestWrapper.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public User updateProfile(String screenname, String email,
		String url, String location, String description) throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileUrl(), auth);
		if (StringUtil.isNotEmpty(screenname) && !getScreenName().equals(screenname)) {
			httpRequestWrapper.addParameter("nick_name", screenname);
		}
		if (StringUtil.isNotEmpty(description)){
			httpRequestWrapper.addParameter("description", description);
		}

		if (httpRequestWrapper.getParameters().size() > 0) {
			String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
			return NetEaseUserAdaptor.createUser(response);
		} else {
			return verifyCredentials();
		}
	}

	/* FavoriteMethods */


	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getCreateFavoriteUrl(), realStatusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getDestroyFavoriteUrl(), realStatusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Status max = paging.getMax();
		Status since = paging.getSince();
		Map<String, Object> params = new HashMap<String, Object>();
		if (max != null) {
			params.put("since_id", max.getStatusId());
		}
		params.put("count", paging.getPageSize());

		String url = String.format(conf.getFavoritesOfUserUrl(), identifyName);
		HttpRequestWrapper request = new HttpRequestWrapper(
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
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

		Status status = updateStatus(conf.getCommentStatusUrl(), params);
		Comment newComment = NetEaseCommentAdaptor.createCommentFromStatus(status);
		return newComment;
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String realCommentId = extractRealStatusId(commentId);
		Status status = destroyStatus(realCommentId);
		Comment comment = NetEaseCommentAdaptor.createCommentFromStatus(status);
		return comment;
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Comment max = paging.getMax();
		Comment since = paging.getSince();
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (since != null) {
			params.put("max_id", since.getCommentId());
		}
		if (max != null) {
			params.put("since_id", max.getCommentId());
		}
		params.put("count", paging.getPageSize());

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getCommentTimelineOfStatusUrl(), realStatusId);
		HttpRequestWrapper request = new HttpRequestWrapper(
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
	public List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		List<Comment> listComment = getCommentList(conf.getCommentsByMeUrl(), paging, null);
		return listComment;
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		List<Comment> listComment = getCommentList(conf.getCommentsToMeUrl(), paging, null);
		return listComment;
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null || StringUtil.isEmpty(status.getStatusId())) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		ResponseCount count = new ResponseCount();
		String statusId = status.getStatusId();
		count.setServiceProvider(ServiceProvider.NetEase);
		count.setStatusId(statusId);

		String realStatusId = extractRealStatusId(statusId);
		String url = String.format(conf.getResponseCountOfStatusUrl(), realStatusId);
		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);

		status = NetEaseStatusAdaptor.createStatus(response);
		count.setCommentCount(status.getCommentCount());
		count.setRetweetCount(status.getRetweetCount());
		return count;
	}

	@Override
	@Deprecated
	public List<ResponseCount> getResponseCountList(List<Status> listStatus) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public UnreadCount getUnreadCount() throws LibException {
		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, conf.getUnreadCountUrl(), auth);

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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Status since = paging.getSince();
		Status max = paging.getMax();
		List<Status> listStatus = null;

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameter("trim_user", false);
		request.addParameters(params);

		//网易的since_id，max_id与其他平台刚好相反（符合传统概念)
		if (since != null && StringUtil.isNotEmpty(since.getStatusId())) {
			request.addParameter("max_id", since.getStatusId());
		}
		if (max != null && StringUtil.isNotEmpty(max.getStatusId())) {
			request.addParameter("since_id", max.getStatusId());
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	private Status updateStatus(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = NetEaseStatusAdaptor.createStatus(response);
		return status;
	}

	private User getUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = NetEaseUserAdaptor.createUser(response);
		return user;
	}

	private List<Comment> getCommentList(String url, Paging<Comment> paging, Map<String, Object> params)
        throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Comment since = paging.getSince();
		Comment max = paging.getMax();
		List<Comment> listComment = null;

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameter("trim_user", false);
		request.addParameters(params);

		//网易的since_id，max_id与其他平台刚好相反（符合传统概念)
		if (since != null && StringUtil.isNotEmpty(since.getCommentId())) {
			request.addParameter("max_id", since.getCommentId());
		}
		if (max != null && StringUtil.isNotEmpty(max.getCommentId())) {
			request.addParameter("since_id", max.getCommentId());
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
        	throw new LibException(LibResultCode.E_PARAM_ERROR);
        }

        DirectMessage max = paging.getMax();
        DirectMessage since = paging.getSince();
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
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
			HttpRequestWrapper request = new HttpRequestWrapper(
				HttpMethod.POST, conf.getUploadStatusUrl(),
				auth, params);
			String uploadResponse = HttpRequestHelper.execute(request, responseHandler);
			JSONObject json = new JSONObject(uploadResponse);
			imageUrl = json.getString("upload_image_url");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getCreateBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return NetEaseUserAdaptor.createUser(response);
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getBlockingUsersUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		List<User> userList = NetEaseUserAdaptor.createUserList(response);
		updatePaging(userList, paging);		
		return userList;
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
	public List<Group> getGroups(String groupOwnerUserId,
			Paging<Group> paging) throws LibException {
		if (!getUserId().equals(groupOwnerUserId)) {
			throw new LibException(LibResultCode.API_UNSUPPORTED);
		}
		
		paging.setLastPage(true);
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getGroupListUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		return NetEaseGroupAdaptor.createGroupList(response);
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
		if (StringUtil.isEmpty(listId)
			|| paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", listId);
		
		return getStatusList(conf.getGroupStatusesUrl(), paging, params);
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

	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, conf.getDailyHotRetweetsUrl(), auth);
		httpRequestWrapper.addParameter("size", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return NetEaseStatusAdaptor.createStatusListFromTopRetweets(response);
	}

	@Override
	public List<Status> getWeeklyHotRetweets(Paging<Status> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getWeeklyHotRetweetsUrl(), auth);
		httpRequestWrapper.addParameter("size", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return NetEaseStatusAdaptor.createStatusListFromTopRetweets(response);
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

}
