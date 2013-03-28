package com.cattong.weibo.impl.sina;

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
import com.cattong.commons.Logger;
import com.cattong.commons.PagableList;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Location;
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
 * Sina微博API实现
 *
 * @version
 * @author 
 * identifyName = userId //不定义，实际默认定义
 * displayName = screenName
 */
public class Sina extends Weibo {
	private static final long serialVersionUID = 211701802736632658L;
	
	private transient ResponseHandler<String> responseHandler;
	private transient String screenName = null;
	private transient String userId = null;

	public Sina(Authorization auth) {
		super(auth);
		responseHandler = new SinaResponseHandler();
	}

	/**
	 * 返回认证用户的昵称<br>
	 *
	 * @return 认证用户昵称
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getScreenName() throws LibException {
		if (null == screenName) {
			verifyCredentials();
		}

		return screenName;
	}

	/**
	 * 返回认证用户的用户ID。<br>
	 *
	 * @return 认证用户的用户ID
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getUserId() throws LibException {
		if (null == userId) {
			verifyCredentials();
		}
		return userId;
	}

	/* Status Methods */

	/**
	 * {@inheritDoc}
	 * <p>新浪默认数量为20条，返回结果非完全实时，最长会缓存60秒 </p>
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/public_timeline">statuses/public_timeline</a>
	 * </p>
	 */
	@Override
	public List<Status> getPublicTimeline() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getPublicTimelineUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>新浪微博此接口等同于statuses/friends_timeline接口</p>
	 */
	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		return getFriendsTimeline(paging);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/friends_timeline">statuses/friends_timeline</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页参数，本接口使用page分页，支持since、max参数
	 */
	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getFriendTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 默认返回最近15天以内的微博信息，由于分页限制，暂时最多只能返回用户最新的200条微博信息
	 * </p>
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/user_timeline">statuses/user_timeline</a>
	 * </p>
	 *
	 * @param identityName
	 * 			用户唯一标识，此处仅适用微博昵称(ScreenName)，可以为空，若为空则表示为获得当前用户的微博消息
	 * @param paging
	 * 			分页控制参数，本接口使用page分页，支持since、max参数
	 */
	@Override
	public List<Status> getUserTimeline(String userId, Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, 
			conf.getUserTimelineUrl(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestWrapper.addParameter("uid", userId);
		}
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/mentions">statuses/mentions</a>
	 * </p>
	 * @param paging
	 * 			分页控制参数，本接口使用page分页，支持since、max参数
	 */
	@Override
	public List<Status> getMentionTimeline(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getMentionTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/repost_by_me">statuses/repost_by_me</a>
	 * </p>
	 * @param paging
	 * 			分页控制参数，本接口使用page分页，支持since、max参数
	 */
	@Override
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getRetweetedByMeUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/show">statuses/show</a>
	 * </p>
	 */
	@Override
	public Status showStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getShowStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/update">statuses/update</a>
	 * </p>
	 * <p>
	 * 更新的参数封装为{@link StatusUpdate}对象，包含微博内容、图片、地理位置信息和被转发微博ID
	 * <p>
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		boolean isRetweet = false;
		String requestUrl = conf.getUpdateStatusUrl();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, latestStatus.getStatus());
		parameters.put("status", specializeText);

		if (latestStatus.getInReplyToStatusId() != null) {
			parameters.put("id", latestStatus.getInReplyToStatusId());
			requestUrl = conf.getRestBaseUrl() + "statuses/repost.json";
			isRetweet = true;
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("lat", latestStatus.getLocation().getLatitude());
			parameters.put("long", latestStatus.getLocation().getLongitude());
		}

		
		if (latestStatus.getImage() != null) {
			if (!isRetweet) {
				//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
				checkFileValidity(latestStatus.getImage());
				parameters.put("pic", latestStatus.getImage());
				requestUrl = conf.getUploadStatusUrl();
			} else {
				Logger.debug("Image file {} is ignored in retweet", latestStatus.getImage().getName());
			}
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, requestUrl, auth);
		httpRequestWrapper.addParameters(parameters);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		
		return SinaStatusAdaptor.createStatus(response);
	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		String url = conf.getDestroyStatusUrl();
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("id", statusId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getRetweetStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		if (StringUtil.isNotEmpty(status)) {
			String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, status);
			httpRequestWrapper.addParameter("status", specializeText);
		}
		if (isComment) {
			httpRequestWrapper.addParameter("is_comment", 1);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	@Override
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getRetweetsOfStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	/* User Methods */

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getShowUserUrl(), auth);
		httpRequestWrapper.addParameter("uid", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = SinaUserAdaptor.createUser(response);
		Status status = user.getStatus();
		if (status != null
			&& status.getUser() == null) {
			status.setUser(user);
		}
		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		if (StringUtil.isEmpty(displayName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getShowUserUrl(), auth);
		httpRequestWrapper.addParameter("screen_name", displayName);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = SinaUserAdaptor.createUser(response);
		Status status = user.getStatus();
		if (status != null
			&& status.getUser() == null) {
			status.setUser(user);
		}
		return user;
	}

	@Override
	public List<User> searchUsers(String query, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(query) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getSearchUserUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		httpRequestWrapper.addParameter("q", query); //关键字
		httpRequestWrapper.addParameter("snick", 1); //搜索范围包含昵称 (0是不包含，1为包含)
		httpRequestWrapper.addParameter("sdomain", 1); //搜索范围包含个性域名 (0是不包含，1为包含)
		httpRequestWrapper.addParameter("sort", 1); //排序方式(1为按更新时间，2为按粉丝数)

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> usersList = SinaUserAdaptor.createUserList(response);
		updatePaging(usersList, paging);
		return usersList;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			verifyCredentials();
		}
		
		return getUserFriends(userId, paging);
	}

	@Override
	public List<User> getUserFriends(String userId, Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getFriendsUrl(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestWrapper.addParameter("uid", userId);
		}
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		PagableList<User> userList = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		
		return userList;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			verifyCredentials();
		}
		return getUserFollowers(userId, paging);
	}

	@Override
	public List<User> getUserFollowers(String userId, Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getFollowsUrl(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestWrapper.addParameter("uid", userId);
		}
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		PagableList<User> userList = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		
		return userList;
	}

	/* Direct Message Methods */

	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getInboxTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<DirectMessage> messagesList = SinaDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(messagesList, paging.getMax());
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getOutboxTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<DirectMessage> messagesList = SinaDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(messagesList, paging.getMax());
		updatePaging(messagesList, paging);

		return messagesList;
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getSendDirectMessageUrl(), auth);
		httpRequestWrapper.addParameter("id", displayName);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, text);
		httpRequestWrapper.addParameter("text", specializeText);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaDirectMessageAdaptor.createDirectMessage(response);
	}

	private DirectMessage destroyDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String restApi = String.format(conf.getDestroyDirectMessageUrl(), directMessageId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, restApi, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaDirectMessageAdaptor.createDirectMessage(response);
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String id) throws LibException {
		return destroyDirectMessage(id);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String id) throws LibException {
		return destroyDirectMessage(id);
	}

	@Override
	public User createFriendship(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getCreateFriendshipUrl(), auth);
		httpRequestWrapper.addParameter("uid", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	@Override
	public User destroyFriendship(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyFriendshipUrl(), auth);
		httpRequestWrapper.addParameter("uid", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	@Override
	public Relationship showRelationship(String sourceIdentifyName, String targetIdentifyName) throws LibException {
		if (StringUtil.isEmpty(targetIdentifyName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getShowFriendshipUrl(), auth);
		if (StringUtil.isNotEmpty(sourceIdentifyName)) {
			httpRequestWrapper.addParameter("source_id", sourceIdentifyName);
		}
		httpRequestWrapper.addParameter("target_id", targetIdentifyName);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		Relationship relationship = SinaRelationshipAdaptor.createRelationship(response);
		if (StringUtil.isEquals(sourceIdentifyName, userId) && relationship != null) {
			relationship.setSourceBlockingTarget(existsBlock(targetIdentifyName));
		}
		
		return relationship;
	}

	private boolean existsBlock(String userId) throws LibException {
//		if (StringUtil.isEmpty(userId)) {
//			throw new LibException(LibResultCode.E_PARAM_NULL);
//		}
//		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
//				HttpMethod.GET, conf.getExistsBlockURL(), auth);
//		httpRequestWrapper.addParameter("user_id", userId);
//		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
//		try {
//			JSONObject json = new JSONObject(response);
//			return ParseUtil.getBoolean("result", json);
//		} catch (JSONException e) {
//			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
//		}
		return false;
	}
	
	/* Account Methods */

	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getVerifyCredentialsUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		String userId = null;
		try {
			JSONObject json = new JSONObject(response);
			userId = json.getString("uid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		
		User user = null;
        if (StringUtil.isEmpty(userId)) {
        	return user;
        }
        
        user = this.showUser(userId);
		if (user != null) {
			this.screenName = user.getScreenName();
			this.userId = user.getUserId();
			if (user.getStatus() != null
				&& user.getStatus().getUser() == null) {
				user.getStatus().setUser(user);
			}
		}

		return user;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET,
			    conf.getRateLimitStatusUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	@Override
	public User updateProfile(String name, String email, String url, String location, String description)
			throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileUrl(), auth);
		if (StringUtil.isNotEmpty(name)){
			httpRequestWrapper.addParameter("name", name);
		}
		if (StringUtil.isNotEmpty(description)){
			httpRequestWrapper.addParameter("description", description);
		}

		if (httpRequestWrapper.getParameters().size() > 0){
			String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
			return SinaUserAdaptor.createUser(response);
		} else {
			return verifyCredentials();
		}
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		checkFileValidity(image);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getUpdateProfileImageUrl(), auth);
		httpRequestWrapper.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getFavoritesTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList =  SinaFavoritesAdaptor.createStatusList(response);
		updatePaging(statusList, paging);
		return statusList;
	}

	@Deprecated
	@Override
	public List<Status> getFavorites(String userId, Paging<Status> paging) throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getCreateFavoriteUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaFavoritesAdaptor.createStatus(response);
	}
	
	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getDestroyFavoriteUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaFavoritesAdaptor.createStatus(response);
	}

	@Override
	public String toString() {
		return "Sina{" + "auth2='" + auth + '\'' + '}';
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getSearchStatusUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		httpRequestWrapper.addParameter("q", keyword);
		httpRequestWrapper.addParameter("filter_ori", 0); //是否原创 (0为全部，5为原创，4为转发，默认0)
		httpRequestWrapper.addParameter("filter_pic", 0); //是否含图 (0为全部，1为含图，2为不含图)

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusesList = SinaStatusAdaptor.createStatusList(response);
		updatePaging(statusesList, paging);
		return statusesList;
	}

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		if (StringUtil.isEmpty(comment) || StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getCommentStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, comment);
		httpRequestWrapper.addParameter("comment", specializeText);

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaCommentAdaptor.createComment(response);
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comment">statuses/comment</a>
	 * </p>
	 */
	@Override
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		if (StringUtil.isEmpty(comment) || StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getReplyCommentUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, comment);
		httpRequestWrapper.addParameter("comment", specializeText);
		if (StringUtil.isNotEmpty(commentId)) {
			httpRequestWrapper.addParameter("cid", commentId);
			httpRequestWrapper.addParameter("without_mention", 1); //1：回复中不自动加入“回复@用户名”，0：回复中自动加入“回复@用户名”.默认为0.
		}
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaCommentAdaptor.createComment(response);
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.POST, conf.getDestroyCommentUrl(), auth);
		httpRequestWrapper.addParameter("cid", commentId);
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaCommentAdaptor.createComment(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comments">statuses/comments</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，不支持Since、Max参数
	 */
	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getCommentTimelineOfStatusUrl(), auth);
		httpRequestWrapper.addParameter("id", statusId);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getCommentId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getCommentId());
		}
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Comment> commentsList = SinaCommentAdaptor.createCommentList(response);
		updatePaging(commentsList, paging);
		return commentsList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comments_to_me">statuses/comments_to_me</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，支持Since、Max参数
	 */
	@Override
	public List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getCommentsToMeUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getCommentId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getCommentId());
		}

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Comment> commentsList = SinaCommentAdaptor.createCommentList(response);
		ListUtil.truncateFromHead(commentsList, paging.getMax());
		updatePaging(commentsList, paging);
		return commentsList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comments_by_me">statuses/comments_by_me</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，支持Since、Max参数
	 */
	@Override
	public List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getCommentsByMeUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getCommentId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getCommentId());
		}

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Comment> commentsList = SinaCommentAdaptor.createCommentList(response);
		ListUtil.truncateFromHead(commentsList, paging.getMax());
		updatePaging(commentsList, paging);
		return commentsList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comments_timeline">statuses/comments_timeline</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，支持Since、Max参数
	 */
	@Override
	public List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException {
		if (paging == null){
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getCommentTimelineUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getCommentId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getCommentId());
		}

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Comment> commentsList = SinaCommentAdaptor.createCommentList(response);
		ListUtil.truncateFromHead(commentsList, paging.getMax());
		updatePaging(commentsList, paging);
		return commentsList;
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null) {
			return null;
		}
		List<Status> listStatus = new ArrayList<Status>();
		listStatus.add(status);
		List<ResponseCount> countList = getResponseCountList(listStatus);
		if (null != countList && countList.size() > 0) {
			return countList.get(0);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 一次请求最多可以获取100条微博消息的评论数和转发数
	 * </p>
	 *
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/counts">statuses/counts</a>
	 * </p>
	 */
	@Override
	public List<ResponseCount> getResponseCountList(List<Status> statusList) throws LibException {
		if (ListUtil.isEmpty(statusList)) {
			return null;
		}

		List<ResponseCount> countList = null;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, 
			conf.getResponseCountOfStatusUrl(), auth);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < statusList.size(); i++) {
			if (i > 99) {
				break;
			}

			if (i == 0) {
				sb.append(statusList.get(i).getStatusId());
			} else {
				sb.append("," + statusList.get(i).getStatusId());
			}
		}
		httpRequestWrapper.addParameter("ids", sb.toString());

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);

		countList = SinaCountAdaptor.createCountList(response);

		if (countList != null && countList.size() > 0) {
			for (ResponseCount count : countList) {
				for (Status status : statusList) {
					if (status.getStatusId().equals(count.getStatusId())) {
						status.setRetweetCount(count.getRetweetCount());
						status.setCommentCount(count.getCommentCount());
						break;
					}
				}
			}
		}

		return countList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/unread">statuses/unread</a>
	 * </p>
	 */
	@Override
	public UnreadCount getUnreadCount() throws LibException {
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getUnreadCountUrl(), auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaCountAdaptor.createRemindCount(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/reset_count">statuses/reset_count</a>
	 * </p>
	 */
	@Override
	public boolean resetUnreadCount(UnreadType type) throws LibException {
		if (type == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getResetUnreadCountUrl(), auth);
		String typeStr = "";
		switch (type) {
		case COMMENT:
			typeStr = "cmt";
			break;
		case MENTION:
			typeStr = "mention_status";
			break;
		case DIRECT_MESSAGE:
			typeStr = "dm";
			break;
		case FOLLOWER:
			typeStr = "follower";
			break;
		default:
			break;
		}
		httpRequestWrapper.addParameter("type", typeStr);
		boolean isSuccess = false;
		
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		isSuccess = SinaCountAdaptor.createResetRemindCount(response);
		return isSuccess;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Blocks/create">blocks/create</a>
	 * </p>
	 */
	@Override
	public User createBlock(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getCreateBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = SinaUserAdaptor.createUser(response);

		return user;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Blocks/destroy">blocks/destroy</a>
	 * </p>
	 */
	@Override
	public User destroyBlock(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, conf.getDestroyBlockUrl(), auth);
		httpRequestWrapper.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		User user = SinaUserAdaptor.createUser(response);

		return user;
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, conf.getBlockingUsersUrl(), auth);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);

		List<User> userList = SinaUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException {
		if (StringUtil.isEmpty(groupName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getCreateGroupUrl(), getUserId());
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("name", groupName);
		if (isPublicList) {
			httpRequestWrapper.addParameter("mode", "public");
		}
		if (StringUtil.isNotEmpty(description)) {
			httpRequestWrapper.addParameter("description", description);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/POST/:user/lists/:list_id">:user/lists/:list_id</a>
	 * </p>
	 */
	@Override
	public Group updateGroup(String groupId, String newGroupName,
			boolean isPublicList, String newDescription) throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(newGroupName)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getUpdateGroupUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("name", newGroupName);
		if (isPublicList) {
			httpRequestWrapper.addParameter("mode", "public");
		}
		if (StringUtil.isNotEmpty(newDescription)) {
			httpRequestWrapper.addParameter("description", newDescription);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/GET/:user/lists">:user/lists</a>
	 * </p>
	 */
	@Override
	public List<Group> getGroups(String groupOwnerUserId,
			Paging<Group> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		
		//目前每人最多创建20个分组，此接口分页每页20个，一次性取完
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getGroupListUrl(), auth);
	
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Group> groupList = SinaGroupAdaptor.createGroupList(response);
		paging.setLastPage(true); //一次性取完，直接设为true;
		
		return groupList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/GET/:user/lists/:list_id">:user/lists/:list_id</a>
	 * </p>
	 */
	@Override
	public Group showGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getShowGroupUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/DELETE/:user/lists/:list_id">:user/lists/:list_id</a>
	 * </p>
	 */
	@Override
	public Group destroyGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getDestroyGroupUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/User/lists/:list_id/statuses">:user/lists/:list_id/statuses</a>
	 * </p>
	 */
	@Override
	public List<Status> getGroupStatuses(String groupId, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getGroupStatusesUrl(), auth);
		httpRequestWrapper.addParameter("list_id", groupId);
		httpRequestWrapper.addParameter("page", paging.getPageIndex());
		httpRequestWrapper.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("max_id", paging.getMax().getStatusId());
		}
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since_id", paging.getSince().getStatusId());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		if (ListUtil.isNotEmpty(statusList)) {
			ListUtil.truncate(statusList, paging.getMax(), paging.getSince());
		}

		updatePaging(statusList, paging);
		return statusList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/User/lists/memberships">:user/lists/memberships</a>
	 * </p>
	 */
	@Override
	public List<Group> getGroupMemberships(String groupMemberUserId,
			Paging<Group> paging) throws LibException {
		if (StringUtil.isEmpty(groupMemberUserId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		String url = String.format(conf.getGroupMembershipsUrl(), groupMemberUserId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Group> userListList =  SinaGroupAdaptor.createGroupList(response);
		updatePaging(userListList, paging);
		return userListList;
	}

	@Override
	public List<Group> getAllGroups(String userId)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
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
		String url = String.format(conf.getGroupMembersUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> users = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(users, paging);
		return users;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/POST/:user/:list_id/members">:user/:list_id/members</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，此接口仅支持UserId
	 */
	@Override
	public Group createGroupMember(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getCreateGroupMemberUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("id", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * </p>
	 */
	@Override
	public Group createGroupMembers(String groupId, String[] userIds)
			throws LibException {
		throw new LibException(LibResultCode.API_UNSUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/DELETE/:user/:list_id/members">:user/:list_id/members</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，此接口仅支持UserId
	 */
	@Override
	public Group destroyGroupMember(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getDestroyGroupMemberUrl(), getUserId(), groupId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.DELETE, url, auth);
		httpRequestWrapper.addParameter("id", userId);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/User/:list_id/members/:id">:user/:list_id/members/:id</a>
	 * </p>
	 */
	@Override
	public User showGroupMember(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = String.format(conf.getShowGroupMemberUrl(), getUserId(), groupId, userId);
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			boolean result = json.getBoolean("lists");
			if (result) {
				return showUser(userId);
			}
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		return null;
	}

	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getDailyHotRetweetsUrl(), auth);
		int pageSize = paging.getPageSize() > 50 ? 50 : paging.getPageSize();
		httpRequestWrapper.addParameter("count", pageSize);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return SinaStatusAdaptor.createStatusList(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/Statuses/hot/comments_daily">statuses/hot/comments_daily</a>
	 * </p>
	 */
	@Override
	public List<Status> getDailyHotComments(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getDailyHotCommentsUrl(), auth);
		int pageSize = paging.getPageSize() > 50 ? 50 : paging.getPageSize();
		httpRequestWrapper.addParameter("count", pageSize);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return SinaStatusAdaptor.createStatusList(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/Statuses/hot/repost_weekly">statuses/hot/repost_weekly</a>
	 * </p>
	 */
	@Override
	public List<Status> getWeeklyHotRetweets(Paging<Status> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getWeeklyHotRetweetsUrl(), auth);
		int pageSize = paging.getPageSize() > 50 ? 50 : paging.getPageSize();
		httpRequestWrapper.addParameter("count", pageSize);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return SinaStatusAdaptor.createStatusList(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/Statuses/hot/comments_weekly">statuses/hot/comments_weekly</a>
	 * </p>
	 */
	@Override
	public List<Status> getWeeklyHotComments(Paging<Status> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
			HttpMethod.GET, conf.getWeeklyHotCommentsUrl(), auth);
		int pageSize = paging.getPageSize() > 50 ? 50 : paging.getPageSize();
		httpRequestWrapper.addParameter("count", pageSize);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		paging.setLastPage(true);
		return SinaStatusAdaptor.createStatusList(response);
	}

	@Override
	public Location getLocationByCoordinate(double latitude, double longitude)
			throws LibException {
		if ((latitude < -90.0 || latitude > 90.0)
			|| (longitude < -180.0 || longitude > 180.0)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET,
				conf.getGeoLocationByCoordinateUrl(), auth);
		httpRequestWrapper.addParameter("coordinate", longitude + "," + latitude);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);

		Location location = SinaGeoAdaptor.createLocation(response);
		return location;
	}
}
