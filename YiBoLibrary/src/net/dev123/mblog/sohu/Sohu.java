package net.dev123.mblog.sohu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dev123.commons.PagableList;
import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.UrlUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.FeaturePatternUtils;
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
import net.dev123.mblog.sina.SinaEmotions;

import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sohu微博API实现
 *
 * @version
 * @author 敏升工作室
 * 搜狐平台属性声明:
 * identifyName = userId
 * displayName = screenName
 */
public class Sohu extends MicroBlog {
	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Sohu.class.getSimpleName());

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
		HttpRequestMessage request = null;
		request = new HttpRequestMessage(HttpMethod.GET, conf.getPublicTimelineURL(), auth);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Status> listStatus = SohuStatusAdaptor.createStatusList(response);
		return listStatus;
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getHomeTimelineURL(), paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getFriendTimelineURL(), paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(identityName) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}
		String url = String.format(conf.getUserTimelineURL(), identityName);
		List<Status> listStatus = getStatusList(url, paging, null);
		return listStatus;
	}

	@Override
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
		    this.initPagePaging(paging);
		}

		List<Status> listStatus = getStatusList(conf.getMetionsTimelineURL(), paging, null);
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/* StatusMethod */

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String url = String.format(conf.getShowOfStatusURL(), statusId);
		Status status = getStatus(url, null);
		return status;
	}

	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		boolean isRetweet = false;
		String url = conf.getUpdateStatusURL();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sohu, latestStatus.getStatus());
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
			url = conf.getUploadStatusURL();
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getDestroyStatusURL(), statusId);
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		if (StringUtil.isEmpty(status)) {
			status = "";
		}
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sohu, status);
		params.put("status", specializeText);		

		String url = String.format(conf.getRetweetStatusURL(), statusId);
		Status newStatus = updateStatus(url, params);
		if (isComment) {
			//TODO:调用评论接口;
		}

		return newStatus;
	}

	@Override
	@Deprecated
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
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

		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, conf.getSearchStatusURL(),
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String url = String.format(conf.getShowOfUserURL(), identifyName);
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String url = String.format(conf.getShowOfUserURL(), displayName);
		User user = getUser(url, null);
		//获取最新的一条微博
		Paging<Status> paging = new Paging<Status>();
		paging.setPageSize(1);
		paging.moveToNext();
		List<Status> statusList = getUserTimeline(user.getId(), paging);
		if (ListUtil.isNotEmpty(statusList)) {
			user.setStatus(statusList.get(0));
		}

		return user;
	}

	@Override
	public List<User> searchUsers(String keyword, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", keyword);
		List<User> listUser = getUserList(conf.getSearchUserURL(), paging, params);
		return listUser;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFriendsURL(), "");
		List<User> listUser = getUserList(url, paging, null);

		return listUser;
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFriendsURL(), "/" + identifyName);
		List<User> listUser = getUserList(url, paging, null);
		return listUser;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFollowsURL(), "");
		List<User> listUser = getUserList(url, paging, null);

		return listUser;
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		String url = String.format(conf.getFollowsURL(), "/" + identifyName);
		List<User> listUser = getUserList(url, paging, null);
		return listUser;
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

		return getDirectMessageList(conf.getInboxTimelineURL(), paging, null);
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		return getDirectMessageList(conf.getOutboxTimelineURL(), paging, null);
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		//用了统一display的接口，做一次转换
		User user = showUser(displayName);
		displayName = user.getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", displayName);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sohu, text);
		params.put("text", specializeText);

		DirectMessage message = updateDirectMessage(conf.getSendDirectMessageURL(), params);
		return message;
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String messageId) throws LibException {
		if (StringUtil.isEmpty(messageId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		return destroyDirectMessage(messageId, "in");
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String messageId) throws LibException {
		if (StringUtil.isEmpty(messageId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		return destroyDirectMessage(messageId, "out");
	}

	/* Friendship methods */

	@Override
	public User createFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getCreateFriendshipURL(), identityName);
		User user = updateUser(url, null);
		return user;
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getDestroyFriendshipURL(), identityName);
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		return SohuUserAdaptor.createUser(response);
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName)
			|| StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source_id", sourceIdentifyName);
		params.put("target_id", targetIdentifyName);
        Relationship relation = getRelationship(conf.getShowOfFriendshipURL(), params);
		return relation;
	}

	/* Social Graph Methods */

	/**
	 * <Strong>Sohu不提供此接口</Strong><BR>
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
	public List<String> getFriendsIDs(String userId, Paging<String> pagfng) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFollowersIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFollowersIDs(String userId, Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/* Account Methods */

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
		return SohuRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		if (image == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		checkFileValidity(image);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("image", image);

		User user = updateUser(conf.getUpdateProfileImageURL(), params);
		return user;
	}

	@Override
	public User updateProfile(String screenName, String email, String url, String location, String description)
			throws LibException {
		if (StringUtil.isEmpty(screenName)
			&& StringUtil.isEmpty(email)
			&& StringUtil.isEmpty(description)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
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

		User user = updateUser(conf.getUpdateProfileURL(), params);
		return user;
	}

	/* Favorite Methods */

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getCreateFavoriteURL(), statusId);
		Status status = updateStatus(url, null);
		return status;
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getDestroyFavoriteURL(), statusId);
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
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

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", paging.getPageIndex());
		List<Status> listStatus = getStatusList(conf.getFavoritesTimelineURL(), paging, params);
		return listStatus;
	}

	@Deprecated
	@Override
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

    /* Comment Methods */

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		if (StringUtil.isEmpty(comment) || StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sohu, comment);
		params.put("comment", specializeText);

		Comment newComment = updateComment(conf.getCommentStatusURL(), params);
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		Pattern p = FeaturePatternUtils.getMentionPattern(ServiceProvider.Sohu);
		Matcher m = p.matcher(comment);
        if (!m.find()) {
        	throw new LibException(ExceptionCode.PARAMETER_ERROR);
        }

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", statusId);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sohu, comment);
		params.put("comment", specializeText);

		Comment newComment = updateComment(conf.getCommentStatusURL(), params);
		return newComment;
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = String.format(conf.getDestroyCommentURL(), commentId);
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Comment comment = SohuCommentAdaptor.createComment(response);
		return comment;
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
        String url = String.format(conf.getCommentsOfStatusURL(), statusId);
        Map<String, Object> params = new HashMap<String, Object>();
		params.put("count", paging.getPageSize());
		params.put("page", paging.getPageIndex());

        List<Comment> listComment = getCommentList(url, paging, params);
		return listComment;
	}

	@Deprecated
	@Override
	public List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		Comment since = paging.getSince();
		Comment max = paging.getMax();
		String url = conf.getCommentsToMeURL();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("count", paging.getPageSize());
		params.put("page", paging.getPageIndex());
		if (since != null) {
			params.put("since_id", since.getId());
		}
		if (max != null) {
			params.put("max_id", max.getId());
		}

		List<Comment> listComment = getCommentList(url, paging, params);
		return listComment;
	}

    /*  Count Methods */

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null || StringUtil.isEmpty(status.getId())) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		List<ResponseCount> listCount = null;
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < listStatus.size(); i++) {
			if (i == 0) {
				sb.append(listStatus.get(i).getId());
			} else {
				sb.append("," + listStatus.get(i).getId());
			}
		}
		params.put("ids", sb.toString());

		HttpRequestMessage request = new HttpRequestMessage(
			HttpMethod.GET, conf.getCountsOfCommentAndRetweetURL(),
			auth, params);
		String response = HttpRequestHelper.execute(request, responseHandler);

		listCount = SohuCountAdaptor.createCountList(response);

		if (listCount != null && listCount.size() > 0) {
			for (ResponseCount count : listCount) {
				for (Status status : listStatus) {
					if (status.getId() != null &&
						status.getId().equals(count.getStatusId())
					) {
						status.setRetweetCount(count.getRetweetCount());
						status.setCommentCount(count.getCommentsCount());
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public boolean resetUnreadCount(UnreadType type) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	private List<Status> getStatusList(String url, Paging<Status> paging, Map<String, Object> params)
	    throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		Status max = paging.getMax();
		Status since = paging.getSince();
		List<Status> listStatus = null;

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);
		if (max != null) {
			request.addParameter("max_id", max.getId());
		}
		if (since != null) {
			request.addParameter("since_id", since.getId());
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	private Status updateStatus(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Status status = SohuStatusAdaptor.createStatus(response);
		return status;
	}

	private User getUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = SohuUserAdaptor.createUser(response);
		return user;
	}

	private List<User> getUserList(String url, Paging<User> paging, Map<String, Object> params)
		throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
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
					&& StringUtil.isNotEmpty(retweet.getId())
					&& StringUtil.isEmpty(retweet.getText())) {
					retweet = showStatus(retweet.getId());
					status.setRetweetedStatus(retweet);
				}
			}
		}

		updatePaging(listUser, paging);
		return listUser;
	}

	private User updateUser(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		User user = SohuUserAdaptor.createUser(response);
		return user;
	}

	private List<DirectMessage> getDirectMessageList(String url, Paging<DirectMessage> paging,
		Map<String, Object> params)	throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		DirectMessage max = paging.getMax();
		DirectMessage since = paging.getSince();
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		DirectMessage message = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", type);
			String url = String.format(conf.getDestroyDirectMessageURL(), messageId);
			HttpRequestMessage request = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}

		return message;
	}

	private List<Comment> getCommentList(String url, Paging<Comment> paging, Map<String, Object> params)
			throws LibException {
		if (StringUtil.isEmpty(url) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);

		String response = HttpRequestHelper.execute(request, responseHandler);
		List<Comment> listComment = SohuCommentAdaptor.createCommentList(response);
		updatePaging(listComment, paging);
		return listComment;
	}

	private Comment updateComment(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.POST, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);
		Comment comment = SohuCommentAdaptor.createComment(response);
		return comment;
	}

	private Relationship getRelationship(String url, Map<String, Object> params) throws LibException {
		if (StringUtil.isEmpty(url)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		HttpRequestMessage request = new HttpRequestMessage(HttpMethod.GET, url, auth);
		request.addParameters(params);
		String response = HttpRequestHelper.execute(request, responseHandler);

		Relationship ship = SohuRelationshipAdaptor.createRelationship(response);
		return ship;
	}

	@Override
	@Deprecated
	public Trends getCurrentTrends() throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<Trends> getDailyTrends() throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<Trends> getWeeklyTrends() throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public User createBlock(String identifyName) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public User destroyBlock(String identifyName) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public boolean existsBlock(String identifyName) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Deprecated
	@Override
	public List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public String toString() {
		return "Sohu{" + "auth='" + auth + '\'' + '}';
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

}
