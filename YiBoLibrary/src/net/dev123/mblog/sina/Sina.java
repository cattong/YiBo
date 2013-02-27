package net.dev123.mblog.sina;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.PagableList;
import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.Location;
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
import net.dev123.mblog.entity.Trend;
import net.dev123.mblog.entity.Trends;
import net.dev123.mblog.entity.UnreadCount;
import net.dev123.mblog.entity.UnreadType;
import net.dev123.mblog.entity.User;

import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sina微博API实现
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午11:38:20
 * displayName = screenName
 */
public class Sina extends MicroBlog {

	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Sina.class.getSimpleName());

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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getPublicTimelineURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFriendTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}


		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getUserTimelineURL(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestMessage.addParameter("user_id", userId);
		}
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getMetionsTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getRetweetedByMeURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		String url = String.format(conf.getShowOfStatusURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		boolean isRetweet = false;

		Map<String, Object> parameters = new HashMap<String, Object>();
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, latestStatus.getStatus());
		parameters.put("status", specializeText);

		if (latestStatus.getInReplyToStatusId() != null) {
			parameters.put("in_reply_to_status_id", latestStatus.getInReplyToStatusId());
			isRetweet = true;
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("lat", latestStatus.getLocation().getLatitude());
			parameters.put("long", latestStatus.getLocation().getLongitude());
		}

		boolean isUpload = false;
		if (latestStatus.getImage() != null) {
			if (!isRetweet) {
				//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
				checkFileValidity(latestStatus.getImage());
				parameters.put("pic", latestStatus.getImage());
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
		return SinaStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/destroy">statuses/destroy/:id</a>
	 * </p>
	 */
	@Override
	public Status destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		String url = String.format(conf.getDestroyStatusURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/repost">statuses/repost</a>
	 * </p>
	 */
	@Override
	public Status retweetStatus(String statusId, String status, boolean isComment) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getRetweetStatusURL(), auth);
		httpRequestMessage.addParameter("id", statusId);
		if (StringUtil.isNotEmpty(status)) {
			String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, status);
			httpRequestMessage.addParameter("status", specializeText);
		}
		if (isComment) {
			httpRequestMessage.addParameter("is_comment", 1);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/repost_timeline">statuses/repost_timeline</a>
	 * </p>
	 */
	@Override
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getRetweetsOfStatusURL(), auth);
		httpRequestMessage.addParameter("id", statusId);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<Status> statusList = SinaStatusAdaptor.createStatusList(response);
		ListUtil.truncateFromHead(statusList, paging.getMax());
		updatePaging(statusList, paging);
		return statusList;
	}

	/* User Methods */

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Users/show">users/show</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，此处仅使用用户昵称ScreenName
	 */
	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfUserURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfUserURL(), auth);
		httpRequestMessage.addParameter("screen_name", displayName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = SinaUserAdaptor.createUser(response);
		Status status = user.getStatus();
		if (status != null
			&& status.getUser() == null) {
			status.setUser(user);
		}
		return user;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Users/search">users/search</a>
	 * </p>
	 */
	@Override
	public List<User> searchUsers(String query, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(query) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getSearchUserURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("q", query); //关键字
		httpRequestMessage.addParameter("snick", 1); //搜索范围包含昵称 (0是不包含，1为包含)
		httpRequestMessage.addParameter("sdomain", 1); //搜索范围包含个性域名 (0是不包含，1为包含)
		httpRequestMessage.addParameter("sort", 1); //排序方式(1为按更新时间，2为按粉丝数)

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> usersList = SinaUserAdaptor.createUserList(response);
		updatePaging(usersList, paging);
		return usersList;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return getUserFriends(null, paging);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/friends">statuses/friends</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，这里仅使用用户昵称ScreenName，若为空，则为获取用户本人的关注用户列表
	 * @param paging
	 * 			分页控制参数，此接口使用Cursor分页
	 */
	@Override
	public List<User> getUserFriends(String userId, Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFriendsURL(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestMessage.addParameter("user_id", userId);
		}
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<User> userList = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		return getUserFollowers(null, paging);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/followers">statuses/followers</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，这里仅使用用户昵称ScreenName，若为空，则为获取用户本人的粉丝列表
	 * @param paging
	 * 			分页控制参数，此接口使用Cursor分页
	 */
	@Override
	public List<User> getUserFollowers(String userId, Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isCursorPaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFollowsURL(), auth);
		if (StringUtil.isNotEmpty(userId)) {
			httpRequestMessage.addParameter("user_id", userId);
		}
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<User> userList = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	/* Direct Message Methods */

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Direct_messages">direct_messages</a>
	 * </p>
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，支持since、max、count、page参数
	 */
	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getInboxTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<DirectMessage> messagesList = SinaDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(messagesList, paging.getMax());
		updatePaging(messagesList, paging);

		return messagesList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Direct_messages/sent">direct_messages/sent</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，支持since、max、count、page参数
	 */
	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getOutboxTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<DirectMessage> messagesList = SinaDirectMessageAdaptor.createDirectMessageList(response);
		ListUtil.truncateFromHead(messagesList, paging.getMax());
		updatePaging(messagesList, paging);

		return messagesList;
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Direct_messages/new">direct_messages/new</a>
	 * </p>
	 *
	 * @param displayName
	 * 			用户唯一标示，可以是用户ID或者微博昵称ScreenName
	 */
	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getSendDirectMessageURL(), auth);
		httpRequestMessage.addParameter("id", displayName);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, text);
		httpRequestMessage.addParameter("text", specializeText);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaDirectMessageAdaptor.createDirectMessage(response);
	}

	/**
	 * 删除私信
	 *
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Direct_messages/new">direct_messages/new</a>
	 * </p>
	 *
	 * @param directMessageId
	 * 			要删除的私信ID，不能为空
	 * @return 删除的私信对象
	 * @throws LibException
	 */
	private DirectMessage destroyDirectMessage(String directMessageId) throws LibException {
		if (StringUtil.isEmpty(directMessageId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String restApi = String.format(conf.getDestroyDirectMessageURL(), directMessageId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, restApi, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Friendships/create">friendships/create</a>
	 * </p>
	 * @param userId
	 * 			用户唯一标识，可以是用户Id或者微博昵称ScreenName，不能为空
	 */
	@Override
	public User createFriendship(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateFriendshipURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Friendships/destroy">friendships/destroy</a>
	 * </p>
	 * @param userId
	 * 			用户唯一标识，此处使用微博昵称ScreenName，不能为空
	 */
	@Override
	public User destroyFriendship(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyFriendshipURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Friendships/show">friendships/show</a>
	 * </p>
	 * @param sourceUserId
	 * 			源用户唯一标识，此处仅使用昵称，可为空，为空时则表示判断当前认证用户与目标用户的关系
	 * @param targetUserId
	 * 			目标用户唯一标识。此处仅使用昵称，不能为空
	 */
	@Override
	public Relationship showRelationship(String sourceUserId, String targetUserId) throws LibException {
		if (StringUtil.isEmpty(targetUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfFriendshipURL(), auth);
		if (StringUtil.isNotEmpty(sourceUserId)) {
			httpRequestMessage.addParameter("source_id", sourceUserId);
		}
		httpRequestMessage.addParameter("target_id", targetUserId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaRelationshipAdaptor.createRelationship(response);
	}

	/* Social Graph Methods */

	@Override
	public List<String> getFriendsIDs(Paging<String> paging) throws LibException {
		return getFriendsIDs(getUserId(), paging);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Friends/ids">friends/ids</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，这里仅使用微博昵称ScreenName
	 * @param paging
	 * 			分页控制参数，不能为空，本接口实现只使用其Cursor参数，Count使用接口默认值500
	 */
	@Override
	public List<String> getFriendsIDs(String userId, Paging<String> paging) throws LibException {
		if (StringUtil.isEmpty(userId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFriendsIDsURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<String> idsList = SinaIDsAdaptor.createPagableIdsList(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public List<String> getFollowersIDs(Paging<String> paging) throws LibException {
		return getFollowersIDs(getScreenName(), paging);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Followers/ids">followers/ids</a>
	 * </p>
	 *
	 * @param userId
	 * 			用户唯一标识，可以是用户Id或微博昵称ScreenName
	 * @param paging
	 * 			分页控制参数，不能为空，本接口实现只使用其Cursor参数，Count使用接口默认值500
	 */
	@Override
	public List<String> getFollowersIDs(String userId, Paging<String> paging) throws LibException {
		if (StringUtil.isEmpty(userId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFollowersIDsURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<String> idsList = SinaIDsAdaptor.createPagableIdsList(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Account/verify_credentials">account/verify_credentials</a>
	 * </p>
	 */
	@Override
	public User verifyCredentials() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getVerifyCredentialsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = SinaUserAdaptor.createUser(response);

		if (user != null) {
			this.screenName = user.getScreenName();
			this.userId = user.getId();
			if (user.getStatus() != null
				&& user.getStatus().getUser() == null) {
				user.getStatus().setUser(user);
			}
		}

		return user;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Account/rate_limit_status">account/rate_limit_status</a>
	 * </p>
	 */
	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET,
			    conf.getRateLimitStatusURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaRateLimitStatusAdaptor.createRateLimitStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Account/update_profile">account/update_profile</a>
	 * </p>
	 */
	@Override
	public User updateProfile(String name, String email, String url, String location, String description)
			throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileURL(), auth);
		if (StringUtil.isNotEmpty(name)){
			httpRequestMessage.addParameter("name", name);
		}
		if (StringUtil.isNotEmpty(description)){
			httpRequestMessage.addParameter("description", description);
		}

		if (httpRequestMessage.getParameters().size() > 0){
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			return SinaUserAdaptor.createUser(response);
		} else {
			return verifyCredentials();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Account/update_profile_image">account/update_profile_image</a>
	 * </p>
	 */
	@Override
	public User updateProfileImage(File image) throws LibException {
		checkFileValidity(image);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileImageURL(), auth);
		httpRequestMessage.addParameter("image", image);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaUserAdaptor.createUser(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Favorites">favorites</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，但只支持页码参数，不知道Since、Max
	 */
	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFavoritesTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList =  SinaStatusAdaptor.createStatusList(response);
		updatePaging(statusList, paging);
		return statusList;
	}

	@Deprecated
	@Override
	public List<Status> getFavorites(String userId, Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Favorites/create">favorites/create</a>
	 * </p>
	 */
	@Override
	public Status createFavorite(String statusId) throws LibException {
		String restApi = String.format(conf.getCreateFavoriteURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, restApi, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Favorites/destroy">favorites/destroy</a>
	 * </p>
	 */
	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		String restApi = String.format(conf.getDestroyFavoriteURL(), statusId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, restApi, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaStatusAdaptor.createStatus(response);
	}

	@Override
	public String toString() {
		return "Sina{" + "auth='" + auth + '\'' + '}';
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/search">statuses/search</a>
	 * </p>
	 *
	 * @param paging
	 * 			分页控制参数，本接口使用Page分页，不支持Since、Max参数
	 */
	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getSearchStatusURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		httpRequestMessage.addParameter("q", keyword);
		httpRequestMessage.addParameter("filter_ori", 0); //是否原创 (0为全部，5为原创，4为转发，默认0)
		httpRequestMessage.addParameter("filter_pic", 0); //是否含图 (0为全部，1为含图，2为不含图)

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusesList = SinaStatusAdaptor.createStatusList(response);
		updatePaging(statusesList, paging);
		return statusesList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comment">statuses/comment</a>
	 * </p>
	 */
	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		return createComment(comment, statusId, null);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCommentStatusURL(), auth);
		httpRequestMessage.addParameter("id", statusId);
		String specializeText = SinaEmotions.specializeEmotion(ServiceProvider.Sina, comment);
		httpRequestMessage.addParameter("comment", specializeText);
		if (StringUtil.isNotEmpty(commentId)) {
			httpRequestMessage.addParameter("cid", commentId);
			httpRequestMessage.addParameter("without_mention", 1); //1：回复中不自动加入“回复@用户名”，0：回复中自动加入“回复@用户名”.默认为0.
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaCommentAdaptor.createComment(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Statuses/comment_destroy">statuses/comment_destroy/:id </a>
	 * </p>
	 */
	@Override
	public Comment destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String restApi = String.format(conf.getDestroyCommentURL(), commentId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, restApi, auth);

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCommentsOfStatusURL(), auth);
		httpRequestMessage.addParameter("id", statusId);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCommentsToMeURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCommentsByMeURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
	public List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()) {
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCommentsTimelineURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("max_id", paging.getMax().getId());
		}
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since_id", paging.getSince().getId());
		}

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		if (statusList == null || statusList.size() == 0) {
			return null;
		}

		List<ResponseCount> countList = null;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCountsOfCommentAndRetweetURL(), auth);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < statusList.size(); i++) {
			if (i > 99) {
				break;
			}

			if (i == 0) {
				sb.append(statusList.get(i).getId());
			} else {
				sb.append("," + statusList.get(i).getId());
			}
		}
		httpRequestMessage.addParameter("ids", sb.toString());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);

		countList = SinaCountAdaptor.createCountList(response);

		if (countList != null && countList.size() > 0) {
			for (ResponseCount count : countList) {
				for (Status status : statusList) {
					if (status.getId().equals(count.getStatusId())) {
						status.setRetweetCount(count.getRetweetCount());
						status.setCommentCount(count.getCommentsCount());
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
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getUnreadCountURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getResetUnreadCountURL(), auth);
		httpRequestMessage.addParameter("type", type.getType());
		boolean isSuccess = false;
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		isSuccess = SinaCountAdaptor.createResetRemindCount(response);
		return isSuccess;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Trends/hourly">trends/hourly</a>
	 * </p>
	 */
	@Override
	public Trends getCurrentTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCurrentTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Trends> trendsList = SinaTrendsAdapter.createTrends(response);
		Trends trends = null;
		if (trendsList != null && trendsList.size() > 0) {
			trends = trendsList.get(0);
		}
		return trends;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Trends/daily">trends/daily</a>
	 * </p>
	 */
	@Override
	public List<Trends> getDailyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaTrendsAdapter.createTrends(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Trends/weekly">trends/weekly</a>
	 * </p>
	 */
	@Override
	public List<Trends> getWeeklyTrends() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getWeeklyTrendsURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaTrendsAdapter.createTrends(response);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = SinaUserAdaptor.createUser(response);
		user.setBlocking(true);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = SinaUserAdaptor.createUser(response);
		user.setBlocking(false);
		return user;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Blocks/exists">blocks/exists</a>
	 * </p>
	 */
	@Override
	public boolean existsBlock(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getExistsBlockURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getBoolean("result", json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Blocks/blocking">blocks/blocking</a>
	 * </p>
	 */
	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getBlockingUsersURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);

		List<User> userList = SinaUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.t.sina.com.cn/wiki/index.php/Blocks/blocking/ids">blocks/blocking/ids</a>
	 * </p>
	 */
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

		List<String> idsList = SinaIDsAdaptor.createIdsList(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/POST/:user/lists">:user/lists</a>
	 * </p>
	 */
	@Override
	public Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException {
		if (StringUtil.isEmpty(groupName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getCreateGroupURL(), getUserId());
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("name", groupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("mode", "public");
		}
		if (StringUtil.isNotEmpty(description)) {
			httpRequestMessage.addParameter("description", description);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getUpdateGroupURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("name", newGroupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("mode", "public");
		}
		if (StringUtil.isNotEmpty(newDescription)) {
			httpRequestMessage.addParameter("description", newDescription);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		if (paging == null || StringUtil.isEmpty(groupOwnerUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		//目前每人最多创建20个分组，此接口分页每页20个，一次性取完
		String url = String.format(conf.getGroupListURL(), getUserId());
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", Paging.CURSOR_START);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		//默认获取的是公开的分组
		List<Group> resultList = SinaGroupAdaptor.createPagableGroupList(response);
		if (getUserId().equals(groupOwnerUserId)
			|| getScreenName().equals(groupOwnerUserId)) {
			httpRequestMessage.addParameter("listType", 1); //获取私有列表
			httpRequestMessage.addParameter("cursor", Paging.CURSOR_START);
			response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			PagableList<Group> privateList = SinaGroupAdaptor.createPagableGroupList(response);
			resultList.addAll(privateList);
		}
		paging.setLastPage(true); //一次性取完，直接设为true;
		return resultList;
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getShowOfGroupURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyGroupURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getGroupStatusesURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
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
		List<Status> statusList =  SinaStatusAdaptor.createStatusList(response);
		if (ListUtil.isNotEmpty(statusList)) {
			ListUtil.truncate(statusList, max, since);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		String url = String.format(conf.getGroupMembershipsURL(), groupMemberUserId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> userListList =  SinaGroupAdaptor.createPagableGroupList(response);
		updatePaging(userListList, paging);
		return userListList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/User/lists/subscriptions">:user/lists/subscriptions</a>
	 * </p>
	 */
	@Override
	public List<Group> getGroupSubscriptions(
			String groupOwnerUserId, Paging<Group> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupOwnerUserId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		String url = String.format(conf.getGroupSubscriptionsURL(), groupOwnerUserId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> userListList =  SinaGroupAdaptor.createPagableGroupList(response);
		updatePaging(userListList, paging);
		return userListList;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * </p>
	 */
	@Override
	public List<Group> getAllGroups(String userId)
			throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/GET/:user/:list_id/members">:user/:list_id/members</a>
	 * </p>
	 */
	@Override
	public List<User> getGroupMembers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		String url = String.format(conf.getGroupMembersURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getCreateGroupMemberURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyGroupMemberURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		httpRequestMessage.addParameter("id", userId);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getShowGroupMemberURL(), getUserId(), groupId, userId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			boolean result = json.getBoolean("lists");
			if (result) {
				return showUser(userId);
			}
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/GET/:user/:list_id/subscribers">:user/:list_id/subscribers</a>
	 * </p>
	 */
	@Override
	public List<User> getGroupSubscribers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		String url = String.format(conf.getGroupSubscribersURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("cursor", paging.getCursor());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		PagableList<User> users = SinaUserAdaptor.createPagableUserList(response);
		updatePaging(users, paging);
		return users;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/POST/:user/:list_id/subscribers">:user/:list_id/subscribers</a>
	 * </p>
	 */
	@Override
	public Group createGroupSubscriber(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getCreateGroupSubscriberURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("id", getUserId());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/DELETE/:user/:list_id/subscribers">:user/:id/subscribers</a>
	 * </p>
	 */
	@Override
	public Group destroyGroupSubscriber(String groupId)
			throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getDestroyGroupSubscriberURL(), getUserId(), groupId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.DELETE, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return SinaGroupAdaptor.createGroup(response);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/User/:list_id/subscribers/:id">:user/:list_id/subscribers/:id</a>
	 * </p>
	 */
	@Override
	public User showGroupSubscriber(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = String.format(conf.getShowGroupSubscriberURL(), getUserId(), groupId, userId);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			boolean result = json.getBoolean("lists");
			if (result) {
				return showUser(userId);
			}
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 官方接口文档：<a href="http://open.weibo.com/wiki/index.php/Statuses/hot/repost_daily">statuses/hot/repost_daily</a>
	 * </p>
	 */
	@Override
	public List<Status> getDailyHotRetweets(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyHotRetweetsURL(), auth);
		httpRequestMessage.addParameter("count", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getDailyHotCommentsURL(), auth);
		httpRequestMessage.addParameter("count", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getWeeklyHotRetweetsURL(), auth);
		httpRequestMessage.addParameter("count", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
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
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getWeeklyHotCommentsURL(), auth);
		httpRequestMessage.addParameter("count", paging.getPageSize() > 50 ? 50 :  paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		paging.setLastPage(true);
		return SinaStatusAdaptor.createStatusList(response);
	}

	@Override
	public Location getLocationByCoordinate(double latitude, double longitude)
			throws LibException {
		if ((latitude < -90.0 || latitude > 90.0)
			|| (longitude < -180.0 || longitude > 180.0)) {
			throw new LibException(ExceptionCode.MicroBlog.API_PARAMS_ERROR);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET,
				conf.getGeoLocationByCoordinateURL(), auth);
		httpRequestMessage.addParameter("coordinate", longitude + "," + latitude);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);

		Location location = SinaGeoAdaptor.createLocationFromJson(response);
		return location;
	}

	@Override
	public List<Trend> getUserTrends(String userId, Paging<Trend> paging)
			throws LibException {
		if (StringUtil.isEmpty(userId)) {
			userId = getUserId();
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.GET, conf.getUserTrendsURL(), auth);
		httpRequestMessage.addParameter("user_id", userId);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("count", paging.getPageSize());

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Trend> trendList = SinaTrendsAdapter.createUserTrends(response);
		ListUtil.truncateFromHead(trendList, paging.getMax());
		updatePaging(trendList, paging);
		return trendList;
	}

	@Override
	public List<Status> getUserTrendsStatus(String trendName, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(trendName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.GET, conf.getUserTrendsStatusURL(), auth);
		httpRequestMessage.addParameter("trend_name", trendName);

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statsList = SinaStatusAdaptor.createStatusList(response);
		paging.setLastPage(true);
		return statsList;
	}
}
