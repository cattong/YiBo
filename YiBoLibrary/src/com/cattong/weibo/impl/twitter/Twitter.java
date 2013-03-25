package com.cattong.weibo.impl.twitter;

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
 * TwitterAPI实现
 *
 * @version
 * @author 
 * identifyName = id
 * displayName = name
 */
public class Twitter extends Weibo {

	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Twitter.class);

	private transient ResponseHandler<String> responseHandler;
	private transient User user;

	public Twitter(Authorization auth) {
		super(auth);
		if (this.auth instanceof ProxyBasicAuth) {
			ProxyBasicAuth proxyAuth = (ProxyBasicAuth) auth;
			TwitterApiConfig apiConfig = (TwitterApiConfig) this.conf;
			if (StringUtil.isNotEmpty(proxyAuth.getRestApiServer())) {
				apiConfig.setRestBaseUrl(proxyAuth.getRestApiServer());
				if (StringUtil.isNotEmpty(proxyAuth.getSearchApiServer())) {
					apiConfig.setSearchBaseUrl(proxyAuth.getSearchApiServer());
				} else {
					apiConfig.setSearchBaseUrl(proxyAuth.getRestApiServer());
				}
			}
			apiConfig.updateRestApiUrl();
			auth.setAuthVersion(Authorization.AUTH_VERSION_BASIC);
		}
		responseHandler = new TwitterResponseHandler();
	}

	/**
	 * 返回认证用户的昵称<br>
	 *
	 * @return 认证用户昵称
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getScreenName() throws LibException, IllegalStateException {
		if (null == user) {
			verifyCredentials();
		}

		return user.getScreenName();
	}

	private String getName() throws LibException, IllegalStateException {
		if (null == user) {
			verifyCredentials();
		}

		return user.getName();
	}

	/**
	 * 返回认证用户的用户ID<br>
	 *
	 * @return 认证用户的用户ID
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getUserId() throws LibException, IllegalStateException {
		if (null == user) {
			verifyCredentials();
		}
		return user.getUserId();
	}

	/* Status Methods */

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getPublicTimelineUrl(), auth);
		httpRequestWrapper.addParameter("include_entities", true);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createStatusList(response);
		return statusList;
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("include_rts", true);
		return getStatusList(conf.getHomeTimelineUrl(), paging, extraParameters);
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		return getStatusList(conf.getFriendTimelineUrl(), paging, null);
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		verifyPagePaging(paging);

		String url = String.format(conf.getUserTimelineUrl(), identityName);
		return getStatusList(url, paging, null);
	}

	@Override
	public List<Status> getMentionTimeline(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("include_rts", true);
		return getStatusList(conf.getMentionTimelineUrl(), paging, extraParameters);
	}

	/**
	 * <Strong>Twitter不提供此接口</Strong><BR>
	 * <BR>
	 *
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String url = String.format(conf.getShowStatusUrl(), statusId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("include_entities ", true);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {

		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		boolean isReply = false;

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", latestStatus.getStatus());

		if (latestStatus.getInReplyToStatusId()!= null) {
			parameters.put("in_reply_to_status_id", latestStatus.getInReplyToStatusId());
			isReply = true;
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("lat", latestStatus.getLocation().getLatitude());
			parameters.put("long", latestStatus.getLocation().getLongitude());
		}

		boolean isUpload = false;
		if (auth.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_1 
			&& latestStatus.getImage() != null) {
			//在使用代理的情况下不处理图片
			if (!isReply) {
				//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
				checkFileValidity(latestStatus.getImage());
				parameters.put("media", latestStatus.getImage());
				isUpload = true;
			} else {
				logger.debug("Image file {} is ignored in retweet", latestStatus.getImage().getName());
			}
		}

		String requestUrl = conf.getUpdateStatusUrl();
		if (isUpload) {
			requestUrl = conf.getUploadStatusUrl();
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, requestUrl, auth);
		httpRequestWrapper.addParameters(parameters);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);

	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getDestroyStatusUrl(), statusId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (isComment) {
			createComment(status, statusId);
		}

		String url = String.format(conf.getRetweetStatusUrl(), statusId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	/**
	 * <Strong>Twitter不提供此接口</Strong><BR>
	 *
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	/* User Methods */

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new  LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getShowUserUrl(), identifyName);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null && user.getStatus().getRetweetedStatus() != null) {
				//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
				//因为此时的原微博中是没有作者信息的
				user.setStatus(showStatus(user.getStatus().getStatusId()));
			}
		}
		Status status = user.getStatus();
		if (status != null
			&& status.getUser() == null) {
			status.setUser(user);
		}
		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		if (StringUtil.isEmpty(displayName)){
			throw new  LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getShowUserUrl(), displayName);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null && user.getStatus().getRetweetedStatus() != null) {
				//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
				//因为此时的原微博中是没有作者信息的
				user.setStatus(showStatus(user.getStatus().getStatusId()));
			}
		}
		Status status = user.getStatus();
		if (status != null
			&& status.getUser() == null) {
			status.setUser(user);
		}
		return user;
	}

	@Override
	public List<User> searchUsers(String query, Paging<User> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(query)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getSearchUserUrl(), auth);

		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("per_page", paging.getPageSize());
		httpRequestWrapper.addParameter("q", query); //关键字

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> userList = TwitterUserAdaptor.createUserList(response);
		updatePaging(userList, paging);

		return userList;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return getUserFriends(getName(), paging);
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		verifyCursorPaging(paging);
		String url = String.format(conf.getFriendsUrl(), identifyName);
		return getPagableUserList(url, paging, null);
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		return getUserFollowers(getName(), paging);
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		verifyCursorPaging(paging);
		String url = String.format(conf.getFollowsUrl(), identifyName);
		return getPagableUserList(url, paging, null);
	}

	private List<User> lookupUsers(String... ids) throws LibException {
		if (ids == null || ids.length == 0) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = conf.getRestBaseUrl() + "users/lookup.json";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("user_id", StringUtil.join(ids, ","));
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUserList(response);
	}

	/* Direct Message Methods */
	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		verifyPagePaging(paging);
		return getDirectMessageList(conf.getInboxTimelineUrl(), paging, null);
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		verifyPagePaging(paging);
		return getDirectMessageList(conf.getOutboxTimelineUrl(), paging, null);
	}

	@Override
	public DirectMessage sendDirectMessage(String diplayName, String text) throws LibException {
		if (StringUtil.isEmpty(text) || StringUtil.isEmpty(text)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getSendDirectMessageUrl(), auth);
		httpRequestWrapper.addParameter("screen_name", diplayName);
		httpRequestWrapper.addParameter("text", text);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterDirectMessageAdaptor.createDirectMessage(response);
	}

	private DirectMessage destroyDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getDestroyDirectMessageUrl(), directMessageId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		return destroyDirectMessage(directMessageId);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		return destroyDirectMessage(directMessageId);
	}

	@Override
	public User createFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getCreateFriendshipUrl(), identityName);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getDestroyFriendshipUrl(), identityName);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param sourceIdentifyName
	 * 			源用户唯一标识，此处仅使用昵称，可为空，为空时则表示判断当前认证用户与目标用户的关系
	 * @param targetIdentifyName
	 * 			目标用户唯一标识。此处仅使用昵称，不能为空
	 */
	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(sourceIdentifyName) || StringUtil.isEmpty(targetIdentifyName)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, conf.getShowFriendshipUrl(), auth);
		httpRequestWrapper.addParameter("source_id", sourceIdentifyName);
		httpRequestWrapper.addParameter("target_id", targetIdentifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		Relationship relationship =  TwitterRelationshipAdaptor.createRelationship(response);
		if (user != null
			&& StringUtil.isEquals(sourceIdentifyName, user.getUserId()) 
			&& relationship != null) {
			relationship.setSourceBlockingTarget(existsBlock(targetIdentifyName));
		}
		
		return relationship;
	}

	private boolean existsBlock(String identifyName) throws LibException {		
		boolean isBlock = false;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getExistsBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		try {
			String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
			isBlock = (-1 == response.indexOf("You are not blocking this user."));
        } catch (LibException e) {
        }
        
        return isBlock;
	}

	/* Account Methods */
	
	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getVerifyCredentialsUrl(), auth);

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null) {
				if (user.getStatus().getRetweetedStatus() != null) {
					//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
					//因为此时的原微博中是没有作者信息的
					user.setStatus(showStatus(user.getStatus().getStatusId()));
				}
				if (user.getStatus().getUser() == null) {
					user.getStatus().setUser(user);
				}
			}
		}

		return user;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getRateLimitStatusUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfile(String name, String email, String url, String location, String description)
			throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileUrl(), auth);
		if (StringUtil.isNotEmpty(name)) {
			httpRequestWrapper.addParameter("name", name);
		}
		if (StringUtil.isNotEmpty(email)) {
			httpRequestWrapper.addParameter("email", email);
		}
		if (StringUtil.isNotEmpty(url)) {
			httpRequestWrapper.addParameter("url", url);
		}
		httpRequestWrapper.addParameter("description", description);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		verifyImageFile(image);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileImageUrl(), auth);
		httpRequestWrapper.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	/**
	 * 判断图片文件合法性
	 *
	 * @param image
	 *            将被上传的图片文件
	 * @throws LibException
	 */
	private void verifyImageFile(File image) throws LibException {
		if (!image.exists()) {
			throw new LibException(LibResultCode.FILE_NOT_FOUND);
		}

		if (!image.isFile()) {
			throw new LibException(LibResultCode.FILE_TYPE_INVALID);
		}
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);

		return getStatusList(conf.getFavoritesTimelineUrl(), paging, null);
	}

	@Override
	public List<Status> getFavorites(String userId, Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
        String url = String.format(conf.getFavoritesOfUserUrl(), userId);
		return getStatusList(url, paging, null);
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String url = String.format(conf.getCreateFavoriteUrl(), statusId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String url = String.format(conf.getDestroyFavoriteUrl(), statusId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public String toString() {
		return "Twitter{" + "auth='" + auth + '\'' + '}';
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getSearchStatusUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("rpp", paging.getPageSize());
		httpRequestWrapper.addParameter("q", keyword);
		httpRequestWrapper.addParameter("with_twitter_user_id", true);
		httpRequestWrapper.addParameter("include_entities", true);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createSearchResultList(response);
		updatePaging(statusList, paging);
		int listSize = statusList.size();
		if (listSize > 0) {
			String[] userIds = new String[listSize];
			for (int i = 0; i < listSize ; i ++) {
				userIds[i] = statusList.get(i).getUser().getUserId();
			}
			List<User> users = lookupUsers(userIds);
			if (users != null && users.size() > 0) {
				Map<String, User> userMap = new HashMap<String, User>();
				for (User user : users) {
					userMap.put(user.getUserId(), user);
				}
				String userKey = null;
				for (Status status : statusList) {
					userKey = status.getUser().getUserId();
					status.setUser(userMap.get(userKey));
				}
			}
		}
		return statusList;
	}

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		StatusUpdate update = new StatusUpdate(comment);
		update.setInReplyToStatusId(statusId);
		Status status = updateStatus(update);
		return TwitterCommentAdaptor.createCommentFromStatus(status);
	}

	@Override
	@Deprecated
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		Status status = destroyStatus(commentId);
		return TwitterCommentAdaptor.createCommentFromStatus(status);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		return new ArrayList<Comment>(0);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	@Deprecated
	public ResponseCount getResponseCount(Status status) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	@Deprecated
	public List<ResponseCount> getResponseCountList(List<Status> statuses) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
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
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		if (paging.getSince() != null){
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		if (paging.getMax() != null){
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		httpRequestWrapper.addParameter("include_entities", true);
		httpRequestWrapper.addParameters(params);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	private List<User> getPagableUserList(String url, Paging<User> paging, Map<String, Object> params)
		throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		if (paging != null){
			if (paging.isPagePaging()){
				initCursorPaging(paging);
			}
			httpRequestWrapper.addParameter("count", paging.getPageSize());
			httpRequestWrapper.addParameter("cursor", paging.getCursor());
		}
		httpRequestWrapper.addParameters(params);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<User> userList = TwitterUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	private List<DirectMessage> getDirectMessageList(String url, Paging<DirectMessage> paging, Map<String, Object> params)
		throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		if (paging.getSince() != null){
			httpRequestWrapper.addParameter("since_id", paging.getSince().getId());
		}
		if (paging.getMax() != null){
			httpRequestWrapper.addParameter("max_id", paging.getMax().getId());
		}
		httpRequestWrapper.addParameters(params);

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<DirectMessage> directMessageList = TwitterDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(directMessageList, paging.getMax());
		updatePaging(directMessageList, paging);
		return directMessageList;
	}

	private void verifyPagePaging(Paging<?> paging) throws LibException{
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		if (paging.getPageIndex() == 0) {
			paging.moveToFirst();
		}
	}

	private void verifyCursorPaging(Paging<?> paging) throws LibException{
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		if (paging.getPageIndex() == 0) {
			paging.moveToFirst();
		}
	}

	@Override
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, conf.getCreateBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);

		return user;
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, conf.getDestroyBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		
		return user;
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, conf.getBlockingUsersUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> users = TwitterUserAdaptor.createUserList(response);
		updatePaging(users, paging);
		return users;
	}

	@Override
	public Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException {
		if (StringUtil.isEmpty(groupName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getCreateGroupUrl(), auth);
		httpRequestWrapper.addParameter("name", groupName);
		if (isPublicList) {
			httpRequestWrapper.addParameter("mode", "public");
		} else {
			httpRequestWrapper.addParameter("mode", "private");
		}
		if (StringUtil.isNotEmpty(description)) {
			httpRequestWrapper.addParameter("description", description);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group updateGroup(String groupId, String newGroupName,
			boolean isPublicList, String newDescription) throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(newGroupName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateGroupUrl(), auth);
		httpRequestWrapper.addParameter("name", newGroupName);
		if (isPublicList) {
			httpRequestWrapper.addParameter("mode", "public");
		} else {
			httpRequestWrapper.addParameter("mode", "private");
		}
		if (StringUtil.isNotEmpty(newDescription)) {
			httpRequestWrapper.addParameter("description", newDescription);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public List<Group> getGroups(String groupOwnerIdentifyName,
			Paging<Group> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(groupOwnerIdentifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		//目前每人最多创建20个分组，此接口分页每页20个，一次性取完
		String url = String.format(conf.getGroupListUrl(), getUserId());
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Group> resultList = TwitterGroupAdaptor.createPagableGroupList(response);
		updatePaging(resultList, paging);
		return resultList;
	}

	@Override
	public Group showGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getShowGroupUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group destroyGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyGroupUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public List<Status> getGroupStatuses(String groupId, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getGroupStatusesUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("per_page", paging.getPageSize());
		Status max = paging.getMax();
		Status since = paging.getSince();
		if (max != null) {
			httpRequestWrapper.addParameter("max_id", max.getStatusId());
		}
		if (since != null) {
			httpRequestWrapper.addParameter("since_id", since.getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList =  TwitterStatusAdaptor.createStatusList(response);
		if (ListUtil.isNotEmpty(statusList)) {
			ListUtil.truncate(statusList, max, since);
		}

		updatePaging(statusList, paging);
		setLastPage(paging, statusList.size() == 0);
		return statusList;
	}

	@Override
	public List<Group> getGroupMemberships(String groupMemberIdentifyName,
			Paging<Group> paging) throws LibException {
		if (StringUtil.isEmpty(groupMemberIdentifyName) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getGroupMembershipsUrl(), auth);
		httpRequestWrapper.addParameter("user_id", groupMemberIdentifyName);
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Group> userListList =  TwitterGroupAdaptor.createPagableGroupList(response);
		updatePaging(userListList, paging);
		return userListList;
	}

	@Override
	public List<Group> getAllGroups(String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getAllGroupsUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Group> userListList =  TwitterGroupAdaptor.createPagableGroupList(response);
		return userListList;
	}

	@Override
	public List<User> getGroupMembers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getGroupMembersUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> users = TwitterUserAdaptor.createPagableUserList(response);
		updatePaging(users, paging);
		return users;
	}

	@Override
	public Group createGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, conf.getCreateGroupMemberUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		httpRequestWrapper.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group createGroupMembers(String groupId, String[] identifyNames)
			throws LibException {
		if (StringUtil.isEmpty(groupId)
			|| identifyNames == null
			|| identifyNames.length == 0) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, conf.getCreateGroupMembersUrl(), auth);
		httpRequestWrapper.addParameter("user_id", StringUtil.join(identifyNames, ","));
		httpRequestWrapper.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group destroyGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyGroupMemberUrl(), auth);
		httpRequestWrapper.addParameter("user_id", identifyName);
		httpRequestWrapper.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public User showGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.GET, conf.getShowGroupMemberUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		httpRequestWrapper.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return TwitterUserAdaptor.createUser(response);
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

}
