package net.dev123.mblog.twitter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.PagableList;
import net.dev123.commons.Paging;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
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
 * TwitterAPI实现
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午11:38:20
 * identifyName = id
 * displayName = name
 */
public class Twitter extends MicroBlog {

	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Twitter.class.getSimpleName());

	private transient ResponseHandler<String> responseHandler;
	private transient User user;

	public Twitter(Authorization auth) {
		super(auth);
		if (this.auth instanceof ProxyBasicAuth) {
			ProxyBasicAuth proxyAuth = (ProxyBasicAuth) auth;
			TwitterApiConfiguration apiConfig = (TwitterApiConfiguration) this.conf;
			if (StringUtil.isNotEmpty(proxyAuth.getRestApiServer())) {
				apiConfig.setRestBaseURL(proxyAuth.getRestApiServer());
				if (StringUtil.isNotEmpty(proxyAuth.getSearchApiServer())) {
					apiConfig.setSearchBaseURL(proxyAuth.getSearchApiServer());
				} else {
					apiConfig.setSearchBaseURL(proxyAuth.getRestApiServer());
				}
			}
			apiConfig.updateRestApiURL();
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
		return user.getId();
	}

	/* Status Methods */

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getPublicTimelineURL(), auth);
		httpRequestMessage.addParameter("include_entities", true);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createStatusList(response);
		return statusList;
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("include_rts", true);
		return getStatusList(conf.getHomeTimelineURL(), paging, extraParameters);
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		return getStatusList(conf.getFriendTimelineURL(), paging, null);
	}

	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(identityName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		verifyPagePaging(paging);

		String url = String.format(conf.getUserTimelineURL(), identityName);
		return getStatusList(url, paging, null);
	}

	@Override
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("include_rts", true);
		return getStatusList(conf.getMetionsTimelineURL(), paging, extraParameters);
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String url = String.format(conf.getShowOfStatusURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("include_entities ", true);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {

		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
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
		if (auth instanceof OAuthAuthorization && latestStatus.getImage() != null) {
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

		String requestUrl = conf.getUpdateStatusURL();
		if (isUpload) {
			requestUrl = conf.getUploadStatusURL();
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, requestUrl, auth);
		httpRequestMessage.addParameters(parameters);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);

	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyStatusURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		if (isComment) {
			createComment(status, statusId);
		}

		String url = String.format(conf.getRetweetStatusURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/* User Methods */

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new  LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getShowOfUserURL(), identifyName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null && user.getStatus().getRetweetedStatus() != null) {
				//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
				//因为此时的原微博中是没有作者信息的
				user.setStatus(showStatus(user.getStatus().getId()));
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
			throw new  LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getShowOfUserURL(), displayName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null && user.getStatus().getRetweetedStatus() != null) {
				//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
				//因为此时的原微博中是没有作者信息的
				user.setStatus(showStatus(user.getStatus().getId()));
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getSearchUserURL(), auth);

		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("per_page", paging.getPageSize());
		httpRequestMessage.addParameter("q", query); //关键字

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		verifyCursorPaging(paging);
		String url = String.format(conf.getFriendsURL(), identifyName);
		return getPagableUserList(url, paging, null);
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		return getUserFollowers(getName(), paging);
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		verifyCursorPaging(paging);
		String url = String.format(conf.getFollowsURL(), identifyName);
		return getPagableUserList(url, paging, null);
	}

	private List<User> lookupUsers(String... ids) throws LibException {
		if (ids == null || ids.length == 0) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = conf.getRestBaseURL() + "users/lookup.json";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("user_id", StringUtil.join(ids, ","));
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterUserAdaptor.createUserList(response);
	}

	/* Direct Message Methods */
	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		verifyPagePaging(paging);
		return getDirectMessageList(conf.getInboxTimelineURL(), paging, null);
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		verifyPagePaging(paging);
		return getDirectMessageList(conf.getOutboxTimelineURL(), paging, null);
	}

	@Override
	public DirectMessage sendDirectMessage(String diplayName, String text) throws LibException {
		if (StringUtil.isEmpty(text) || StringUtil.isEmpty(text)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getSendDirectMessageURL(), auth);
		httpRequestMessage.addParameter("screen_name", diplayName);
		httpRequestMessage.addParameter("text", text);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterDirectMessageAdaptor.createDirectMessage(response);
	}

	private DirectMessage destroyDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyDirectMessageURL(), directMessageId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		return destroyDirectMessage(directMessageId);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		return destroyDirectMessage(directMessageId);
	}

	@Override
	public User createFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getCreateFriendshipURL(), identityName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	@Override
	public User destroyFriendship(String identityName) throws LibException {
		if (StringUtil.isEmpty(identityName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyFriendshipURL(), identityName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfFriendshipURL(), auth);
		httpRequestMessage.addParameter("source_id", sourceIdentifyName);
		httpRequestMessage.addParameter("target_id", targetIdentifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterRelationshipAdaptor.createRelationship(response);
	}

	/* Social Graph Methods */

	@Override
	public List<String> getFriendsIDs(Paging<String> paging) throws LibException {
		verifyCursorPaging(paging);

		String url = String.format(conf.getFriendsIDsURL(), getUserId());
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = TwitterIDsAdaptor.createIDs(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public List<String> getFriendsIDs(String identifyName, Paging<String> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		verifyCursorPaging(paging);

		String url = String.format(conf.getFriendsIDsURL(), identifyName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = TwitterIDsAdaptor.createIDs(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public List<String> getFollowersIDs(Paging<String> paging) throws LibException {
		verifyCursorPaging(paging);

		String url = String.format(conf.getFollowersIDsURL(), getUserId());
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = TwitterIDsAdaptor.createIDs(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public List<String> getFollowersIDs(String identifyName, Paging<String> paging) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		verifyCursorPaging(paging);

		String url = String.format(conf.getFollowersIDsURL(), identifyName);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("cursor", paging.getCursor());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = TwitterIDsAdaptor.createIDs(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getVerifyCredentialsURL(), auth);

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		if (null != user) {
			this.user = user;
			if (user.getStatus() != null) {
				if (user.getStatus().getRetweetedStatus() != null) {
					//如果用户的最新微博中有转发的原微博，则重新调用showStatus方法获取该微博
					//因为此时的原微博中是没有作者信息的
					user.setStatus(showStatus(user.getStatus().getId()));
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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getRateLimitStatusURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfile(String name, String email, String url, String location, String description)
			throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileURL(), auth);
		if (StringUtil.isNotEmpty(name)) {
			httpRequestMessage.addParameter("name", name);
		}
		if (StringUtil.isNotEmpty(email)) {
			httpRequestMessage.addParameter("email", email);
		}
		if (StringUtil.isNotEmpty(url)) {
			httpRequestMessage.addParameter("url", url);
		}
		httpRequestMessage.addParameter("description", description);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		verifyImageFile(image);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileImageURL(), auth);
		httpRequestMessage.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.FILE_NOT_FOUND, image.getName() + " do not exist.");
		}

		if (!image.isFile()) {
			throw new LibException(ExceptionCode.NOT_A_FILE, image.getName() + " is not a file.");
		}
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);

		return getStatusList(conf.getFavoritesTimelineURL(), paging, null);
	}

	@Override
	public List<Status> getFavorites(String userId, Paging<Status> paging) throws LibException {
		verifyPagePaging(paging);
        String url = String.format(conf.getFavoritesOfUserURL(), userId);
		return getStatusList(url, paging, null);
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String url = String.format(conf.getCreateFavoriteURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String url = String.format(conf.getDestroyFavoriteURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterStatusAdaptor.createStatus(response);
	}

	@Override
	public String toString() {
		return "Twitter{" + "auth='" + auth + '\'' + '}';
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(keyword) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getSearchStatusURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("rpp", paging.getPageSize());
		httpRequestMessage.addParameter("q", keyword);
		httpRequestMessage.addParameter("with_twitter_user_id", true);
		httpRequestMessage.addParameter("include_entities", true);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createSearchResultList(response);
		updatePaging(statusList, paging);
		int listSize = statusList.size();
		if (listSize > 0) {
			String[] userIds = new String[listSize];
			for (int i = 0; i < listSize ; i ++) {
				userIds[i] = statusList.get(i).getUser().getId();
			}
			List<User> users = lookupUsers(userIds);
			if (users != null && users.size() > 0) {
				Map<String, User> userMap = new HashMap<String, User>();
				for (User user : users) {
					userMap.put(user.getId(), user);
				}
				String userKey = null;
				for (Status status : statusList) {
					userKey = status.getUser().getId();
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public ResponseCount getResponseCount(Status status) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<ResponseCount> getResponseCountList(List<Status> statuses) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		if (paging.getSince() != null){
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		if (paging.getMax() != null){
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		httpRequestMessage.addParameter("include_entities", true);
		httpRequestMessage.addParameters(params);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList = TwitterStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	private List<User> getPagableUserList(String url, Paging<User> paging, Map<String, Object> params)
		throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		if (paging != null){
			if (paging.isPagePaging()){
				initCursorPaging(paging);
			}
			httpRequestMessage.addParameter("count", paging.getPageSize());
			httpRequestMessage.addParameter("cursor", paging.getCursor());
		}
		httpRequestMessage.addParameters(params);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<User> userList = TwitterUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	private List<DirectMessage> getDirectMessageList(String url, Paging<DirectMessage> paging, Map<String, Object> params)
		throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		if (paging.getSince() != null){
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		if (paging.getMax() != null){
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		httpRequestMessage.addParameters(params);

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<DirectMessage> directMessageList = TwitterDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(directMessageList, paging.getMax());
		updatePaging(directMessageList, paging);
		return directMessageList;
	}

	private void verifyPagePaging(Paging<?> paging) throws LibException{
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		if (paging.getPageIndex() == 0) {
			paging.moveToFirst();
		}
	}

	@Override
	public Trends getCurrentTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCurrentTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Trends> trendsList = TwitterTrendsAdapter.createTrends(response);
		Trends trends = null;
		if (trendsList != null && trendsList.size() > 0) {
			trends = trendsList.get(0);
		}
		return trends;
	}

	@Override
	public List<Trends> getDailyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyTrendsURL(), auth);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		httpRequestMessage.addParameter("date", sdf.format(new Date()));
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterTrendsAdapter.createTrends(response);
	}

	@Override
	public List<Trends> getWeeklyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getWeeklyTrendsURL(), auth);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		httpRequestMessage.addParameter("date", sdf.format(new Date()));
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterTrendsAdapter.createTrends(response);
	}

	@Override
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		user.setBlocking(true);
		return user;
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = TwitterUserAdaptor.createUser(response);
		user.setBlocking(false);
		return user;
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
			return -1 == response.indexOf("You are not blocking this user.");
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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getBlockingUsersURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> users = TwitterUserAdaptor.createUserList(response);
		updatePaging(users, paging);
		return users;
	}

	@Override
	public List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getBlockingUsersIdsURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> ids = TwitterIDsAdaptor.createIdsList(response);
		updatePaging(ids, paging);
		return ids;
	}


	@Override
	public Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException {
		if (StringUtil.isEmpty(groupName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateGroupURL(), auth);
		httpRequestMessage.addParameter("name", groupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("mode", "public");
		} else {
			httpRequestMessage.addParameter("mode", "private");
		}
		if (StringUtil.isNotEmpty(description)) {
			httpRequestMessage.addParameter("description", description);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group updateGroup(String groupId, String newGroupName,
			boolean isPublicList, String newDescription) throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(newGroupName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateGroupURL(), auth);
		httpRequestMessage.addParameter("name", newGroupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("mode", "public");
		} else {
			httpRequestMessage.addParameter("mode", "private");
		}
		if (StringUtil.isNotEmpty(newDescription)) {
			httpRequestMessage.addParameter("description", newDescription);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public List<Group> getGroups(String groupOwnerIdentifyName,
			Paging<Group> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(groupOwnerIdentifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		//目前每人最多创建20个分组，此接口分页每页20个，一次性取完
		String url = String.format(conf.getGroupListURL(), getUserId());
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> resultList = TwitterGroupAdaptor.createPagableGroupList(response);
		updatePaging(resultList, paging);
		return resultList;
	}

	@Override
	public Group showGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfGroupURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group destroyGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyGroupURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public List<Status> getGroupStatuses(String groupId, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getGroupStatusesURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("per_page", paging.getPageSize());
		Status max = paging.getMax();
		Status since = paging.getSince();
		if (max != null) {
			httpRequestMessage.addParameter("max_id", max.getId());
		}
		if (since != null) {
			httpRequestMessage.addParameter("since_id", since.getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getGroupMembershipsURL(), auth);
		httpRequestMessage.addParameter("user_id", groupMemberIdentifyName);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> userListList =  TwitterGroupAdaptor.createPagableGroupList(response);
		updatePaging(userListList, paging);
		return userListList;
	}

	@Override
	public List<Group> getGroupSubscriptions(
			String groupOwnerIdentifyName, Paging<Group> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public List<Group> getAllGroups(String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getAllGroupsURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> userListList =  TwitterGroupAdaptor.createPagableGroupList(response);
		return userListList;
	}

	@Override
	public List<User> getGroupMembers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getGroupMembersURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> users = TwitterUserAdaptor.createPagableUserList(response);
		updatePaging(users, paging);
		return users;
	}

	@Override
	public Group createGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getCreateGroupMemberURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group createGroupMembers(String groupId, String[] identifyNames)
			throws LibException {
		if (StringUtil.isEmpty(groupId)
			|| identifyNames == null
			|| identifyNames.length == 0) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getCreateGroupMembersURL(), auth);
		httpRequestMessage.addParameter("user_id", StringUtil.join(identifyNames, ","));
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group destroyGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getDestroyGroupMemberURL(), auth);
		httpRequestMessage.addParameter("user_id", identifyName);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public User showGroupMember(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getShowGroupMemberURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		httpRequestMessage.addParameter("user_id", identifyName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterUserAdaptor.createUser(response);
	}

	@Override
	public List<User> getGroupSubscribers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getGroupSubscribersURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<User> users = TwitterUserAdaptor.createPagableUserList(response);
		updatePaging(users, paging);
		return users;
	}

	@Override
	public Group createGroupSubscriber(String groupId)
			throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getCreateGroupSubscriberURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public Group destroyGroupSubscriber(String groupId)
			throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, conf.getDestroyGroupSubscriberURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterGroupAdaptor.createGroup(response);
	}

	@Override
	public User showGroupSubscriber(String groupId, String identifyName)
			throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.GET, conf.getShowGroupSubscriberURL(), auth);
		httpRequestMessage.addParameter("list_id", groupId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TwitterUserAdaptor.createUser(response);
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
