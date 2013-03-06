package com.cattong.weibo.impl.sohu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.UrlUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;
import com.cattong.entity.User;
import com.cattong.weibo.FeaturePatternUtils;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.weibo.entity.Group;
import com.cattong.weibo.entity.RateLimitStatus;
import com.cattong.weibo.entity.ResponseCount;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;

/**
 * Sohu微博API实现
 *
 * @version
 * @author
 * 搜狐平台属性声明:
 * identifyName = userId
 * displayName = screenName
 */
public class Sohu extends Weibo {
	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Sohu.class);

	private transient ResponseHandler<String> responseHandler;
	private transient String screenName = null;
	private transient String userId = null;

	public Sohu(Authorization auth) {
		super(auth);
		responseHandler = new SohuResponseHandler();
	}

	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			verifyCredentials();
		}
		return screenName;
	}

	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(userId)) {
			verifyCredentials();
		}
		return userId;
	}

	/* Timeline Methods */

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestWrapper request = null;
		request = new HttpRequestWrapper(HttpMethod.GET, conf.getPublicTimelineUrl(), auth);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> listStatus = SohuStatusAdaptor.createStatusList(response);
		return listStatus;
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getHomeTimelineUrl(), paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getFriendTimelineUrl(), paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(identityName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}
		String url = String.format(conf.getUserTimelineUrl(), identityName);
		List<Status> listStatus = getStatusList(url, paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getMentionTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getMentionTimelineUrl(), paging, null);
		return listStatus;
	}

	/**
	 * <Strong>Sohu不提供此接口</Strong><BR>
	 * <BR>
	 *
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	/* StatusMethod */

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = String.format(conf.getShowStatusUrl(), statusId);
		Status status = getStatus(url, null);
		return status;
	}

	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		boolean isRetweet = false;
		String url = conf.getUpdateStatusUrl();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String specializeText = SohuEmotions.specializeEmotion(ServiceProvider.Sohu, latestStatus.getStatus());
		parameters.put("status", specializeText);

		if (latestStatus.getInReplyToStatusId() != null) {
			parameters.put("in_reply_to_status_id", latestStatus.getInReplyToStatusId());
			isRetweet = true;
		}

		if (latestStatus.getImage() != null && !isRetweet) {
			//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
			checkFileValidity(latestStatus.getImage());
			parameters.put("pic", latestStatus.getImage());
			parameters.put("status", UrlUtil.encode(latestStatus.getStatus()));
			url = conf.getUploadStatusUrl();
		}
        if (latestStatus.getImage() != null && isRetweet) {
        	logger.debug("Image file {} is ignored in retweet", latestStatus.getImage().getName());
        }

		Status status = updateStatus(url, parameters);
		return status;
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getDestroyStatusUrl(), statusId);
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		if (StringUtil.isEmpty(status)) {
			status = "";
		}
		String specializeText = SohuEmotions.specializeEmotion(ServiceProvider.Sohu, status);
		params.put("status", specializeText);		

		String url = String.format(conf.getRetweetStatusUrl(), statusId);
		Status newStatus = updateStatus(url, params);
		if (isComment) {
			//TODO:调用评论接口;
		}

		return newStatus;
	}

	@Override
	@Deprecated
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
        if (!paging.isPagePaging()) {
            initCursorPaging(paging);
        }

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", keyword);
		if (paging.getPageSize() > 0) {
			params.put("rpp", paging.getPageSize());
		}
		if (paging.getSince() != null) {
			params.put("since_id", paging.getSince());
		}
		if (paging.getPageIndex() > 0) {
			params.put("page", paging.getPageIndex());
		}

		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, conf.getSearchStatusUrl(),
			auth, params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> listStatus = null;
		listStatus = SohuStatusAdaptor.createStatusSearchResultList(response);

		updatePaging(listStatus, paging);
		return listStatus;
	}

	/* User Methods */

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = String.format(conf.getShowUserUrl(), identifyName);
		User user = getUser(url, null);
		//获取最新的一条微博
		Paging<Status> paging = new Paging<Status>();
		paging.setPageSize(1);
		paging.moveToNext();
		List<Status> statusList = getUserTimeline(identifyName, paging);
		if (ListUtil.isNotEmpty(statusList)) {
			user.setStatus(statusList.get(0));
		}

		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		if (StringUtil.isEmpty(displayName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = String.format(conf.getShowUserUrl(), displayName);
		User user = getUser(url, null);
		//获取最新的一条微博
		Paging<Status> paging = new Paging<Status>();
		paging.setPageSize(1);
		paging.moveToNext();
		List<Status> statusList = getUserTimeline(user.getUserId(), paging);
		if (ListUtil.isNotEmpty(statusList)) {
			user.setStatus(statusList.get(0));
		}

		return user;
	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", keyword);
		List<User> listUser = getUserList(conf.getSearchUserUrl(), paging, params);
		return listUser;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFriendsUrl(), "");
		List<User> listUser = getUserList(url, paging, null);

		return listUser;
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFriendsUrl(), "/" + identifyName);
		List<User> listUser = getUserList(url, paging, null);
		return listUser;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFollowsUrl(), "");
		List<User> listUser = getUserList(url, paging, null);

		return listUser;
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFollowsUrl(), "/" + identifyName);
		List<User> listUser = getUserList(url, paging, null);
		return listUser;
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

		return getDirectMessageList(conf.getInboxTimelineUrl(), paging, null);
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getDirectMessageList(conf.getOutboxTimelineUrl(), paging, null);
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		//用了统一display的接口，做一次转换
		User user = showUser(displayName);
		displayName = user.getUserId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", displayName);
		String specializeText = SohuEmotions.specializeEmotion(ServiceProvider.Sohu, text);
		params.put("text", specializeText);

		DirectMessage message = updateDirectMessage(conf.getSendDirectMessageUrl(), params);
		return message;
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String messageId) throws LibException {
		if (StringUtil.isEmpty(messageId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		return destroyDirectMessage(messageId, "in");
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String messageId) throws LibException {
		if (StringUtil.isEmpty(messageId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		return destroyDirectMessage(messageId, "out");
	}

	/* Friendship methods */

	@Override
	public User createFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getCreateFriendshipUrl(), identityName);
		User user = updateUser(url, null);
		return user;
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getDestroyFriendshipUrl(), identityName);
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		return SohuUserAdaptor.createUser(response);
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName)
			|| StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
        
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, conf.getShowFriendshipUrl(), auth);
		httpRequestWrapper.addParameter("source_id", sourceIdentifyName);
		httpRequestWrapper.addParameter("target_id", targetIdentifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);

		Relationship relationship = SohuRelationshipAdaptor.createRelationship(response);
		if (StringUtil.isEquals(sourceIdentifyName, userId) && relationship != null) {
			relationship.setSourceBlockingTarget(existsBlock(targetIdentifyName));
		}
		
		return relationship;
	}

	private boolean existsBlock(String identifyName) throws LibException {
		//throw new LibException(LibResultCode.API_UNSUPPORTED);
		return false;
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
		return SohuRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		if (image == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		checkFileValidity(image);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("image", image);

		User user = updateUser(conf.getUpdateProfileImageUrl(), params);
		return user;
	}

	@Override
	public User updateProfile(String screenName, String email, String url, String location, String description)
			throws LibException {
		if (StringUtil.isEmpty(screenName)
			&& StringUtil.isEmpty(email)
			&& StringUtil.isEmpty(description)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtil.isNotEmpty(screenName)) {
			params.put("nick_name", screenName);
		}
		if (StringUtil.isNotEmpty(email)) {
		    params.put("email", email);
		}
		if (StringUtil.isNotEmpty(description)) {
			params.put("description", description);
		}

		User user = updateUser(conf.getUpdateProfileUrl(), params);
		return user;
	}

	/* Favorite Methods */

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getCreateFavoriteUrl(), statusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getDestroyFavoriteUrl(), statusId);
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
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

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", paging.getPageIndex());
		List<Status> listStatus = getStatusList(conf.getFavoritesTimelineUrl(), paging, params);
		return listStatus;
	}

	@Deprecated
	@Override
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

    /* Comment Methods */

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		if (StringUtil.isEmpty(comment) || StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		String specializeText = SohuEmotions.specializeEmotion(ServiceProvider.Sohu, comment);
		params.put("comment", specializeText);

		Comment newComment = updateComment(conf.getCommentStatusUrl(), params);
		return newComment;
	}

	/*
	 * 搜狐api不存直接回复评论的接口，
	 * 官网回复评论的方式，在评论内容前加@用户
	 * 这个由外部接口，在comment文本内进行确保
	 *
	 */
	@Override
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		if (StringUtil.isEmpty(comment) 
			|| StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		Pattern p = FeaturePatternUtils.getMentionPattern(ServiceProvider.Sohu);
		Matcher m = p.matcher(comment);
        if (!m.find()) {
        	throw new LibException(LibResultCode.E_PARAM_ERROR);
        }

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		String specializeText = SohuEmotions.specializeEmotion(ServiceProvider.Sohu, comment);
		params.put("comment", specializeText);

		Comment newComment = updateComment(conf.getCommentStatusUrl(), params);
		return newComment;
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = String.format(conf.getDestroyCommentUrl(), commentId);
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Comment comment = SohuCommentAdaptor.createComment(response);
		return comment;
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
        String url = String.format(conf.getCommentTimelineOfStatusUrl(), statusId);
        Map<String, Object> params = new HashMap<String, Object>();
		params.put("count", paging.getPageSize());
		params.put("page", paging.getPageIndex());

        List<Comment> listComment = getCommentList(url, paging, params);
		return listComment;
	}

	@Deprecated
	@Override
	public List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Deprecated
	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Comment since = paging.getSince();
		Comment max = paging.getMax();
		String url = conf.getCommentsToMeUrl();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("count", paging.getPageSize());
		params.put("page", paging.getPageIndex());
		if (since != null) {
			params.put("since_id", since.getCommentId());
		}
		if (max != null) {
			params.put("max_id", max.getCommentId());
		}

		List<Comment> listComment = getCommentList(url, paging, params);
		return listComment;
	}

    /*  Count Methods */

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null || StringUtil.isEmpty(status.getStatusId())) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		List<Status> listStatus = new ArrayList<Status>();
		listStatus.add(status);
		List<ResponseCount> listCount = getResponseCountList(listStatus);
		if (listCount != null && listCount.size() > 0) {
			return listCount.get(0);
		}

		return null;
	}

	@Override
	public List<ResponseCount> getResponseCountList(List<Status> listStatus) throws LibException {
		if (listStatus == null || listStatus.size() == 0) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		List<ResponseCount> listCount = null;
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < listStatus.size(); i++) {
			if (i == 0) {
				sb.append(listStatus.get(i).getStatusId());
			} else {
				sb.append("," + listStatus.get(i).getStatusId());
			}
		}
		params.put("ids", sb.toString());

		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, conf.getResponseCountOfStatusUrl(),
			auth, params);
		String response = HttpRequestHelper.execute(request, responseHandler);

		listCount = SohuCountAdaptor.createCountList(response);

		if (listCount != null && listCount.size() > 0) {
			for (ResponseCount count : listCount) {
				for (Status status : listStatus) {
					if (status.getStatusId() != null 
					    && status.getStatusId().equals(count.getStatusId())) {
						status.setRetweetCount(count.getRetweetCount());
						status.setCommentCount(count.getCommentCount());
						break;
					}
				}
			}
		}

		return listCount;
	}

	@Override
	@Deprecated
	public UnreadCount getUnreadCount() throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	@Deprecated
	public boolean resetUnreadCount(UnreadType type) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	private List<Status> getStatusList(String url, Paging<Status> paging, Map<String, Object> params)
	    throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		Status max = paging.getMax();
		Status since = paging.getSince();
		List<Status> listStatus = null;

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);
		if (max != null) {
			request.addParameter("max_id", max.getStatusId());
		}
		if (since != null) {
			request.addParameter("since_id", since.getStatusId());
		}
		request.addParameter("count", paging.getPageSize());

		String response = HttpRequestHelper.execute(request, responseHandler);
		if (paging.isCursorPaging()) {
			try {
			    listStatus = SohuStatusAdaptor.createPagableStatusList(response);
			    if (listStatus != null && listStatus.size() <= paging.getPageSize() / 2) {
				    ((PagableList<?>) listStatus).setNextCursor(Paging.CURSOR_END);
			    }
			} catch (Exception e) {
				listStatus = SohuStatusAdaptor.createStatusList(response);
			}
		} else {
			listStatus = SohuStatusAdaptor.createStatusList(response);
		}

		listStatus = ListUtil.truncate(listStatus, max, since);

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
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	private Status updateStatus(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	private User getUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = SohuUserAdaptor.createUser(response);
		return user;
	}

	private List<User> getUserList(String url, Paging<User> paging, Map<String, Object> params)
		throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);
		request.addParameter("page", paging.getPageIndex());
		int count = paging.getPageSize() > 20 ? 20 : paging.getPageSize();
		request.addParameter("count", count); //cout <= 20;

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<User> listUser = null;
		if (paging.isCursorPaging()) {
			listUser = SohuUserAdaptor.createPagableUserList(response);
		} else {
			listUser = SohuUserAdaptor.createUserList(response);
		}

		if (ListUtil.isNotEmpty(listUser)) {
			for (User user : listUser) {
				Status status = user.getStatus();
				Status retweet = null;
				if (status != null) {
					retweet = status.getRetweetedStatus();
				}
				if (retweet != null
					&& StringUtil.isNotEmpty(retweet.getStatusId())
					&& StringUtil.isEmpty(retweet.getText())) {
					retweet = showStatus(retweet.getStatusId());
					status.setRetweetedStatus(retweet);
				}
			}
		}

		updatePaging(listUser, paging);
		return listUser;
	}

	private User updateUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = SohuUserAdaptor.createUser(response);
		return user;
	}

	private List<DirectMessage> getDirectMessageList(String url, Paging<DirectMessage> paging,
		Map<String, Object> params)	throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		DirectMessage max = paging.getMax();
		DirectMessage since = paging.getSince();
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);
		if (since != null) {
			request.addParameter("since_id", since.getId());
		}
		request.addParameter("page", paging.getPageIndex());
		request.addParameter("count", paging.getPageSize());
		//由于私信接口不支持max,取更多数据，再根据max截取
		if (max != null) {
			request.addParameter("count", 60);
		}

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<DirectMessage> listMessage = new ArrayList<DirectMessage>();
		listMessage.addAll(SohuDirectMessageAdaptor.createDirectMessageList(response));

		if (max != null && !listMessage.contains(max)) {
			listMessage.clear();
			if (paging.moveToNext()) {
				request.addParameter("page", paging.getPageIndex());
				response = HttpRequestHelper.execute(request, responseHandler);
				listMessage.addAll(SohuDirectMessageAdaptor.createDirectMessageList(response));
			}
		}

		ListUtil.truncate(listMessage, max, since);
		updatePaging(listMessage, paging);
		return listMessage;
	}

	private DirectMessage updateDirectMessage(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		DirectMessage message = SohuDirectMessageAdaptor.createDirectMessage(response);
		return message;
	}

	private DirectMessage destroyDirectMessage(String messageId, String type) throws LibException {
		if (StringUtil.isEmpty(messageId) ||
			StringUtil.isEmpty(type) ||
			(!type.equals("in") && !type.equals("out"))
		) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		DirectMessage message = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", type);
			String url = String.format(conf.getDestroyDirectMessageUrl(), messageId);
			HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
			request.addParameters(params);
			String response = HttpRequestHelper.execute(request, responseHandler);

			JSONObject json = new JSONObject(response);
			// 删除成功的响应：{"code":200,"error":"Delete direct-message successfully.","request":"/direct_messages/destroy/5600.json"}
			if (json.getInt("code") == HttpStatus.SC_OK) {
				message = new DirectMessage();
				message.setId(messageId);
				message.setServiceProvider(ServiceProvider.Sohu);
			}
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}

		return message;
	}

	private List<Comment> getCommentList(String url, Paging<Comment> paging, Map<String, Object> params)
			throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Comment> listComment = SohuCommentAdaptor.createCommentList(response);
		updatePaging(listComment, paging);
		return listComment;
	}

	private Comment updateComment(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Comment comment = SohuCommentAdaptor.createComment(response);
		return comment;
	}

    /* Block methods */
	
	@Deprecated
	@Override
	public User createBlock(String identifyName) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Deprecated
	@Override
	public User destroyBlock(String identifyName) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Deprecated
	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
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
	public String toString() {
		return "Sohu{" + "auth='" + auth + '\'' + '}';
	}
}
