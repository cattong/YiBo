package net.dev123.mblog.tencent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tencent微博API实现
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午11:38:20
 * identifyName = id
 * displayName = name
 */
public class Tencent extends MicroBlog {

	private static final long serialVersionUID = -1486360080128882436L;
	private static final Logger logger = LoggerFactory.getLogger(Tencent.class.getSimpleName());
	private static final String RESPONSE_FORMAT = "json";

	private transient ResponseHandler<String> responseHandler;
	private transient User user = null;

	public Tencent(Authorization auth) {
		super(auth);
		responseHandler = new TencentResponseHandler();
	}

	/**
	 * 返回认证用户的昵称<br>
	 *
	 * @return 认证用户昵称
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getScreenName() throws LibException, IllegalStateException {
		return getUser().getScreenName();
	}

	/**
	 * 返回认证用户的用户ID。<br>
	 *
	 * @return 认证用户的用户ID
	 * @throws LibException
	 * @throws IllegalStateException
	 */
	public String getUserId() throws LibException, IllegalStateException {
		return getUser().getId();
	}

	private User getUser() throws LibException, IllegalStateException{
		if (null == user) {
			user = verifyCredentials();
		}

		return user;
	}

	/* Status Methods */

	@Override
	public List<Status> getPublicTimeline() throws LibException {
		return getStatusList(conf.getPublicTimelineURL(), null, null);
	}

	@Override
	public List<Status> getHomeTimeline(Paging<Status> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		return getStatusList(conf.getFriendTimelineURL(), paging, null);
	}

	@Override
	public List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}
		return getStatusList(conf.getFriendTimelineURL(), paging, null);
	}

	/**
	 * @param identityName
	 * 			用户唯一标识，本接口只支持使用用户UserId
	 * @param paging
	 * 			分页控制参数，不能为空
	 */
	@Override
	public List<Status> getUserTimeline(String identityName, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(identityName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", identityName); //userId
		return getStatusList(conf.getUserTimelineURL(), paging, parameters);
	}

	@Override
	public List<Status> getMentions(Paging<Status> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		return getStatusList(conf.getMetionsTimelineURL(), paging, null);
	}

	/**
	 * {@inheritDoc}
	 * <p><Strong>Tencent不提供此接口</Strong></p>
	 */
	@Override
	@Deprecated
	public List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Status showStatus(String id) throws LibException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		return getStatus(conf.getShowOfStatusURL(), parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	public Status updateStatus(StatusUpdate latestStatus) throws LibException {
		if (latestStatus == null || StringUtil.isEmpty(latestStatus.getStatus())){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		boolean isRetweet = false;

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("content", Emotions.specializeEmotion(
				ServiceProvider.Tencent, latestStatus.getStatus()));

		if (latestStatus.getInReplyToStatusId()!= null){
			parameters.put("reid", latestStatus.getInReplyToStatusId());
			isRetweet = true;
		}

		if (latestStatus.getLocation() != null) {
			parameters.put("wei", latestStatus.getLocation().getLatitude());
			parameters.put("jing", latestStatus.getLocation().getLongitude());
		}
		//不同步到QQ空间
        parameters.put("syncflag", 1);
        
		boolean isUpload = false;
		if (latestStatus.getImage() != null){
			if (!isRetweet) {
				//图片上传只有在发原创微博的时候可用，转发时不允许上传图片
				verifyImageFile(latestStatus.getImage());
				parameters.put("pic", latestStatus.getImage());
				isUpload = true;
			} else {
				logger.debug("Image file {} is ignored in retweet", latestStatus.getImage().getName());
			}
		}

		String requestUrl = conf.getUpdateStatusURL();
		if (isUpload){
			requestUrl = conf.getUploadStatusURL();
		}

		try {
			Status status = null;
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, requestUrl, auth);
			httpRequestMessage.addParameters(parameters);
			httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
			String resopnse = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(resopnse);
			String statusId = ParseUtil.getRawString("id", json);
			long timestamp = ParseUtil.getLong("time", json);
			status = new Status();
			status.setId(statusId);
			status.setUser(getUser());
			status.setText(latestStatus.getStatus());
			status.setCreatedAt(new Date(timestamp * 1000L));
			status.setSource(conf.getSource());
			status.setServiceProvider(ServiceProvider.Tencent);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}

	}

	@Override
	public Status destroyStatus(String statusId) throws LibException {
		try {
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyStatusURL(), auth);
			httpRequestMessage.addParameter("id", statusId);
			httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
			String resopnse = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(resopnse);
			String id = ParseUtil.getRawString("id", json);
			Status deleted = new Status();
			deleted.setId(id);
			deleted.setServiceProvider(ServiceProvider.Tencent);
			return deleted;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Status retweetStatus(String sourceId, String rtStatus, boolean isComment) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getRetweetStatusURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("reid", sourceId);
		if (StringUtil.isNotEmpty(rtStatus)) {
			httpRequestMessage.addParameter("content",
					Emotions.specializeEmotion(ServiceProvider.Tencent, rtStatus));
		}

		try {
			Status status = null;
			String resopnse = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(resopnse);
			String statusId = ParseUtil.getRawString("id", json);
			long timestamp = ParseUtil.getLong("time", json);

			status = new Status();
			status.setId(statusId);
			status.setUser(getUser());
			status.setText(rtStatus);
			status.setCreatedAt(new Date(timestamp * 1000L));
			status.setSource(conf.getSource());
			Status retweetedStatus = new Status();
			retweetedStatus.setId(sourceId);
			status.setRetweetedStatus(retweetedStatus);
			status.setServiceProvider(ServiceProvider.Tencent);

			if (isComment) {
				createComment(rtStatus, sourceId);
			}

			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException {
		if (StringUtil.isEmpty(statusId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		Map<String, Object> parameters = getPagingParamsMap(paging);
		parameters.put("rootid", statusId);
		parameters.put("flag", 0); //获取微博转发列表
		parameters.put("format", RESPONSE_FORMAT);
		List<Status> statusList = getStatusList(conf.getRetweetsOfStatusURL(), paging, parameters);
		
		return statusList;
	}

	/* User Methods */

	@Override
	public User showUser(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", identifyName);

		User user = getUser(conf.getShowOfUserURL(), params);

		//获取最新的一条微博
		Paging<Status> paging = new Paging<Status>();
		paging.setPageSize(1);
		paging.moveToNext();
		List<Status> listStatus = getUserTimeline(identifyName, paging);
		if (ListUtil.isNotEmpty(listStatus)) {
			user.setStatus(listStatus.get(0));
		}

		return user;
	}

	@Override
	public User showUserByDisplayName(String displayName) throws LibException {
		User user = showUser(displayName);
		return user;
	}

	@Override
	public List<User> searchUsers(String query, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(query) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isCursorPaging()){
			initPagePaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getSearchUserURL(), auth);
		httpRequestMessage.addParameter("keyword", query); //关键字
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("pagesize", paging.getPageSize());
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<User> userList = TencentUserAdaptor.createUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return getUserFriends(this.getUserId(), paging);
	}

	@Override
	public List<User> getUserFriends(String identifyName, Paging<User> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFriendsURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		if (paging.getPageIndex() > 0) {
			httpRequestMessage.addParameter("startindex", (paging.getPageIndex() - 1) * paging.getPageSize());
		}
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<User> userList = TencentUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);

		return userList;
	}

	@Override
	public List<User> getFollowers(Paging<User> paging) throws LibException {
		return getUserFollowers(this.getUserId(), paging);
	}

	@Override
	public List<User> getUserFollowers(String identifyName, Paging<User> paging) throws LibException {
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getFollowsURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		if (paging.getPageIndex() > 0) {
			httpRequestMessage.addParameter("startindex", (paging.getPageIndex() - 1) * paging.getPageSize());
		}
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<User> userList = TencentUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	/* Direct Message Methods */
	@Override
	public List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getInboxTimelineURL(), auth);
		httpRequestMessage.addParameters(getPagingParamsMap(paging));
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<DirectMessage> directMessageList = TencentDirectMessageAdaptor.createPagableDirectMessageList(response, 1);
		clearDirectMessage(directMessageList, paging);
		for (DirectMessage dmsg : directMessageList) {
			dmsg.setRecipient(getUser());
			dmsg.setRecipientId(getUserId());
			dmsg.setRecipientScreenName(getScreenName());
			dmsg.setServiceProvider(ServiceProvider.Tencent);
		}
		updatePaging(directMessageList, paging);

		if (!paging.isLastPage() && directMessageList.size() > 0) {
			setNextPageMax(paging, directMessageList.get(directMessageList.size() - 1));
		}
		return directMessageList;
	}

	@Override
	public List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getOutboxTimelineURL(), auth);
		httpRequestMessage.addParameters(getPagingParamsMap(paging));
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<DirectMessage> directMessageList = TencentDirectMessageAdaptor.createPagableDirectMessageList(response, 0);
		clearDirectMessage(directMessageList, paging);
		updatePaging(directMessageList, paging);
		if (!paging.isLastPage() && directMessageList.size() > 0) {
			setNextPageMax(paging, directMessageList.get(directMessageList.size() - 1));
		}
		return directMessageList;
	}

	private void clearDirectMessage(List<DirectMessage> dmsgList, Paging<DirectMessage> paging){
		if (paging == null || dmsgList == null) {
			return;
		}

		DirectMessage since = paging.getSince();
		long sinceTime = (since == null) ? 0L : getUnixTimestamp(since.getCreatedAt());
		Iterator<DirectMessage> iterator = dmsgList.iterator();
		DirectMessage tmpMsg = null;
		boolean isExist = false;
		while (iterator.hasNext()) {
			tmpMsg = iterator.next();
			long timestamp = tmpMsg.getCreatedAt().getTime() / 1000L;
			isExist = (isExist || (sinceTime > 0L && sinceTime >= timestamp));
			if (isExist) {
				iterator.remove();
			}
		}
	}

	@Override
	public DirectMessage sendDirectMessage(String displayName, String text) throws LibException {
		if (StringUtil.isEmpty(displayName) || StringUtil.isEmpty(text)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getSendDirectMessageURL(), auth);
		httpRequestMessage.addParameter("name", displayName);
		httpRequestMessage.addParameter("content", Emotions.specializeEmotion(ServiceProvider.Tencent, text));
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			String id = ParseUtil.getRawString("id", json);
			long time = ParseUtil.getLong("timestamp", json);
			DirectMessage msg = new DirectMessage();
			msg.setSenderId(getUserId());
			msg.setRecipientId(displayName);
			msg.setId(id);
			msg.setCreatedAt(new Date(time * 1000L));
			msg.setText(text);
			msg.setServiceProvider(ServiceProvider.Tencent);
			return msg;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (LibException e) {
			if (e.getExceptionCode() == ExceptionCode.MicroBlog.API_PERMISSION_ACCESS_DENIED) {
				e.setExceptionCode(ExceptionCode.MicroBlog.API_MESSAGE_RECEIVER_NOT_FOLLOWER);
			}
			throw e;
		}
	}

	private DirectMessage destroyDirectMessage(String directMessageid) throws LibException {
		assert (directMessageid != null);
		Status status = destroyStatus(directMessageid);
		DirectMessage msg = new DirectMessage();
		msg.setId(status.getId());
		msg.setServiceProvider(ServiceProvider.Tencent);
		return msg;
	}

	@Override
	public DirectMessage destroyInboxDirectMessage(String directMessageid) throws LibException {
		return destroyDirectMessage(directMessageid);
	}

	@Override
	public DirectMessage destroyOutboxDirectMessage(String directMessageid) throws LibException {
		return destroyDirectMessage(directMessageid);
	}

	@Override
	public User createFriendship(String identifyName) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateFriendshipURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = new User();
		user.setId(identifyName);
		user.setName(identifyName);
		user.setServiceProvider(ServiceProvider.Tencent);
		return user;
	}


	@Override
	public User destroyFriendship(String identifyName) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyFriendshipURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = new User();
		user.setId(identifyName);
		user.setName(identifyName);
		user.setServiceProvider(ServiceProvider.Tencent);
		return user;
	}

	/*
	 * 只能检测当前用户与target之间的关系
	 */
	@Override
	@Deprecated
	public Relationship showRelationship(String sourceId, String targetId) throws LibException {
		/*
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getShowOfFriendshipURL(), auth);
		httpRequestMessage.addParameter("names", targetId);
		httpRequestMessage.addParameter("flag", 2); // 0 检测粉丝，1检测偶像，2两种都检测
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		JSONObject responseJson = new JSONObject(response);
		JSONObject relationJson = responseJson.getJSONObject(targetId);
		Relationship relationship = new Relationship();
		relationship.setFollowing(relationJson.getBoolean("isidol"));
		relationship.setFollowed(relationJson.getBoolean("isfans"));
		relationship.setBlocking(relationJson.getBoolean("ismyblack"));
		relationship.setSourceUserId(getUserId());
		relationship.setTargetUserId(targetId);
		*/
		User user = showUser(targetId);
		Relationship relationship = new Relationship();
		relationship.setFollowing(user.isFollowing());
		relationship.setFollowed(user.isFollowedBy());
		relationship.setBlocking(user.isBlocking());
		relationship.setSourceUserId(getUserId());
		relationship.setTargetUserId(targetId);
		return relationship;
	}

	/* Social Graph Methods */

	@Override
	@Deprecated
	public List<String> getFriendsIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	@Deprecated
	public List<String> getFriendsIDs(String userId, Paging<String> paging) throws LibException {
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

	@Override
	public User verifyCredentials() throws LibException {
		User user = getUser(conf.getVerifyCredentialsURL(), null);
		if (null != user) {
			this.user = user;
		}

		return user;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() throws LibException {
		return null;
	}

	@Override
	public User updateProfile(String screenname, String email, String url, String location, String description)
			throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileURL(), auth);
		httpRequestMessage.addParameter("nick", screenname);
		httpRequestMessage.addParameter("introduction", description);
		httpRequestMessage.addParameter("sex", 0);
		httpRequestMessage.addParameter("year", "2010");
		httpRequestMessage.addParameter("month", "10");
		httpRequestMessage.addParameter("day", "10");
		httpRequestMessage.addParameter("countrycode", "1");
		httpRequestMessage.addParameter("provincecode", "44");
		httpRequestMessage.addParameter("citycode", "3");
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return verifyCredentials();
	}

	@Override
	public User updateProfileImage(File image) throws LibException {
		verifyImageFile(image);
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getUpdateProfileImageURL(), auth);
		httpRequestMessage.addParameter("pic", image);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return verifyCredentials();
	}

	/**
	 * 判断文件合法性
	 *
	 * @param image
	 *            将被上传的图片文件
	 * @throws LibException
	 */
	private void verifyImageFile(File image) throws LibException {
		if (!image.exists()) {
			throw new LibException(ExceptionCode.FILE_NOT_FOUND);
		}

		if (!image.isFile()) {
			throw new LibException(ExceptionCode.NOT_A_FILE);
		}
	}

	@Override
	public List<Status> getFavorites(Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		return getStatusList(conf.getFavoritesTimelineURL(), paging, null);
	}

	@Override
	@Deprecated
	public List<Status> getFavorites(String identifyName, Paging<Status> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public Status createFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		try {
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateFavoriteURL(), auth);
			httpRequestMessage.addParameter("id", statusId);
			httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			Status status = new Status();
			status.setId(ParseUtil.getRawString("id", json));
			status.setServiceProvider(ServiceProvider.Tencent);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Status destroyFavorite(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		try {
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyFavoriteURL(), auth);
			httpRequestMessage.addParameter("id", statusId);
			httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			Status status = new Status();
			status.setId(ParseUtil.getRawString("id", json));
			status.setServiceProvider(ServiceProvider.Tencent);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(keyword)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getSearchStatusURL(), auth);
		httpRequestMessage.addParameter("page", paging.getPageIndex());
		httpRequestMessage.addParameter("pagesize", paging.getPageSize());
		httpRequestMessage.addParameter("keyword", keyword);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<Status> statusList = TencentStatusAdaptor.createStatusSearchResult(response);
		updatePaging(statusList, paging);

		return statusList;
	}

	@Override
	public Comment createComment(String comment, String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId) || StringUtil.isEmpty(comment)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCommentStatusURL(), auth);
		httpRequestMessage.addParameter("reid", statusId);
		httpRequestMessage.addParameter("content", Emotions.specializeEmotion(ServiceProvider.Tencent, comment));
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			String id = ParseUtil.getRawString("id", json);
			long timestamp = ParseUtil.getLong("time", json);
			Status status = new Status();
			status.setId(id);
			status.setUser(getUser());
			status.setText(comment);
			status.setCreatedAt(new Date(timestamp * 1000L));
			status.setSource(conf.getSource());
			Status retweetedStatus = new Status();
			retweetedStatus.setId(statusId);
			status.setServiceProvider(ServiceProvider.Tencent);
			return TencentCommentAdaptor.createCommentFromStatus(status);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	@Deprecated
	public Comment createComment(String comment, String statusId, String commentId) throws LibException {
		return createComment(comment, statusId);
	}

	@Override
	public Comment destroyComment(String commentId) throws LibException {
		Status status = destroyStatus(commentId);
		return TencentCommentAdaptor.createCommentFromStatus(status);
	}

	@Override
	public List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException {
		if (paging == null || StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()) {
			initCursorPaging(paging);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getCommentsOfStatusURL(), auth);
		httpRequestMessage.addParameter("rootid", statusId);
		httpRequestMessage.addParameter("flag", 1); //获取微博点评列表
		httpRequestMessage.addParameters(getPagingParamsMap(paging));
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		ArrayList<Status> statusList = TencentStatusAdaptor.createPagableStatusList(response);
		updatePaging(statusList, paging);
		ArrayList<Comment> commentsList = new ArrayList<Comment>(statusList.size());
		for (int i = 0; i < statusList.size(); i++) {
			if (statusList.get(i).getRetweetedStatus() != null) {
				commentsList.add(TencentCommentAdaptor.createCommentFromStatus(statusList.get(i)));
			}
		}
		if (!paging.isLastPage() && statusList.size() > 0) {
			setNextPageMax(paging, commentsList.get(commentsList.size() - 1));
		}
		return commentsList;
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
		return new ArrayList<Comment>();
	}

	@Override
	public ResponseCount getResponseCount(Status status) throws LibException {
		if (status == null) {
			return null;
		}

		Status updated = showStatus(status.getId());
		ResponseCount count = new ResponseCount();
		count.setStatusId(updated.getId());
		count.setCommentsCount(updated.getCommentCount());
		count.setRetweetCount(updated.getRetweetCount());
		count.setServiceProvider(ServiceProvider.Tencent);

		return count;
	}

	@Override
	@Deprecated
	public List<ResponseCount> getResponseCountList(List<Status> listStatus) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}

	@Override
	public UnreadCount getUnreadCount() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getUnreadCountURL(), auth);
		httpRequestMessage.addParameter("op", 0); //只请求更新数，不清除更新数
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TencentCountAdaptor.createRemindCount(response);
	}

	@Override
	public boolean resetUnreadCount(UnreadType remindType) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getResetUnreadCountURL(), auth);
		httpRequestMessage.addParameter("op", 1); // 请求更新数，并对更新数清零
		int type = 5; //默认清除首页未读微博计数
		switch (remindType) {
		case METION:
			type = 6;
			break;
		case DIRECT_MESSAGE:
			type = 7;
			break;
		case FOLLOWER:
			type = 8;
		case COMMENT:
		default:
			break;
		}
		httpRequestMessage.addParameter("type", type);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		boolean isSuccess = false;
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		isSuccess = TencentCountAdaptor.createResetRemindCount("{\"result\":true}");

		return isSuccess;
	}

	private List<Status> getStatusList(String url, Paging<Status> paging, Map<String, Object> params)
		throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameters(getPagingParamsMap(paging));
		httpRequestMessage.addParameters(params);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);

		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList = TencentStatusAdaptor.createPagableStatusList(response);

		clearStatus(statusList, paging);
		updatePaging(statusList, paging);

		if (paging!= null && !paging.isLastPage() && statusList.size() > 0) {
			setNextPageMax(paging, statusList.get(statusList.size() - 1));
		}
		return statusList;
	}

	private void clearStatus(List<Status> statusList, Paging<Status> paging){
		if (paging == null || statusList == null) {
			return;
		}
		Status since = paging.getSince();
		long sinceTime = (since == null) ? 0L : getUnixTimestamp(since.getCreatedAt());
		Iterator<Status> iterator = statusList.iterator();
		Status tmpStatus = null;
		boolean isExist = false;
		while (iterator.hasNext()) {
			tmpStatus = iterator.next();
			long timestamp = getUnixTimestamp(tmpStatus.getCreatedAt());
			isExist = (isExist || (sinceTime > 0L && sinceTime >= timestamp));
			if (isExist) {
				iterator.remove();
			}
		}
	}

	private Status getStatus(String url, Map<String, Object> params) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameters(params);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TencentStatusAdaptor.createStatus(response);
	}

	private User getUser(String url, Map<String, Object> params) throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameters(params);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TencentUserAdaptor.createUser(response);
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
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return TencentTrendsAdapter.createTrends(response);
	}

	@Override
	public List<Trends> getWeeklyTrends() throws LibException {
		return getDailyTrends();
	}

	private Map<String, Object> getPagingParamsMap(Paging<?> paging) {
		if (paging == null) {
			return null;
		}

		Map<String, Object> pagingParameters = new HashMap<String, Object>();
		pagingParameters.put("reqnum", paging.getPageSize());

		if (paging.getMax() != null) {
			pagingParameters.put("pagetime", extractBoundaryValue(paging.getMax()));
			pagingParameters.put("pageflag", 1); //下翻
		} else if (paging.getSince() != null) {
			pagingParameters.put("pagetime", extractBoundaryValue(paging.getSince()));
			pagingParameters.put("pageflag", 0); //取第一页
		} else {
			pagingParameters.put("pageflag", 0);
			pagingParameters.put("pagetime", 0);
		}
		return pagingParameters;
	}

	private <T> String extractBoundaryValue(T entity) {
		String boundaryValue = null;
		if (entity instanceof Status) {
			Status status = (Status) entity;
			boundaryValue = String.valueOf(getUnixTimestamp(status.getCreatedAt()));
		} else if (entity instanceof Comment) {
			Comment comment = (Comment) entity;
			boundaryValue = String.valueOf(getUnixTimestamp(comment.getCreatedAt()));
		} else if (entity instanceof DirectMessage) {
			DirectMessage msg = (DirectMessage) entity;
			boundaryValue = String.valueOf(getUnixTimestamp(msg.getCreatedAt()));
		} else if (entity instanceof String) {
			boundaryValue = String.valueOf(entity);
		}

		return boundaryValue;
	}

	private static long getUnixTimestamp(Date date){
		if (date == null) {
			return 0;
		}

		return date.getTime() / 1000L;
	}

	@Override
	public User createBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getCreateBlockURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = new User();
		user.setServiceProvider(ServiceProvider.Tencent);
		user.setName(identifyName);
		user.setId(identifyName);
		user.setBlocking(true);
		return user;
	}

	@Override
	public User destroyBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, conf.getDestroyBlockURL(), auth);
		httpRequestMessage.addParameter("name", identifyName);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		User user = new User();
		user.setServiceProvider(ServiceProvider.Tencent);
		user.setName(identifyName);
		user.setId(identifyName);
		user.setBlocking(false);
		return user;
	}

	@Override
	public boolean existsBlock(String identifyName) throws LibException {
		if (StringUtil.isEmpty(identifyName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		User user = showUser(identifyName);
		return user.isBlocking();
	}

	@Override
	public List<User> getBlockingUsers(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, conf.getBlockingUsersURL(), auth);
		httpRequestMessage.addParameter("reqnum", paging.getPageSize());
		httpRequestMessage.addParameter("startindex", (paging.getPageIndex() - 1) * paging.getPageSize());
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);

		List<User> userList = TencentUserAdaptor.createPagableUserList(response);
		updatePaging(userList, paging);

		return userList;
	}

	@Override
	public List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException {
		throw new LibException(ExceptionCode.UNSUPPORTED_API);
	}


	@Override
	public Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException {
		if (StringUtil.isEmpty(groupName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getCreateGroupURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("name", groupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("access", "0");
		} else {
			httpRequestMessage.addParameter("access", "1");
		}
		if (StringUtil.isNotEmpty(description)) {
			httpRequestMessage.addParameter("description", description);
		}
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Group group = TencentGroupAdaptor.createGroup(response);
		group = showGroup(group.getId());
		
		return group;		
	}

	@Override
	public Group updateGroup(String groupId, String newGroupName,
		boolean isPublicList, String newDescription) throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(newGroupName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getUpdateGroupURL(), auth);
		httpRequestMessage.addParameter("name", newGroupName);
		if (isPublicList) {
			httpRequestMessage.addParameter("access", "0");
		} else {
			httpRequestMessage.addParameter("access", "1");
		}
		if (StringUtil.isNotEmpty(newDescription)) {
			httpRequestMessage.addParameter("description", newDescription);
		}
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Group group = TencentGroupAdaptor.createGroup(response);
		
		return group;
	}

	@Override
	public List<Group> getGroups(String listOwnerIdentifyName,
			Paging<Group> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getGroupListURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> groupList = TencentGroupAdaptor.createPagableGroupList(response);
		paging.setLastPage(true);//只能取一页
		
		return groupList;
	}

	@Override
	public Group showGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getShowOfGroupURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("listids", groupId);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Group> groupList = TencentGroupAdaptor.createPagableGroupList(response);
		Group group = null;
		if (ListUtil.isNotEmpty(groupList)) {
			group = groupList.get(0);
		}
		
		return group;
	}

	@Override
	public Group destroyGroup(String groupId) throws LibException {
		if (StringUtil.isEmpty(groupId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getDestroyGroupURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("listid", groupId);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Group group = null;
		if (StringUtil.isNotEmpty(response)) {
			group = new Group();
		    group.setId(groupId);
		}
		
		return group;
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
	public List<User> getGroupMembers(String groupId, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (paging.getPageSize() != 15) {
			paging.setPageSize(15);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getGroupMembersURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("listid", groupId);
		httpRequestMessage.addParameter("pageflag", paging.getPageIndex());
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> userList = TencentGroupAdaptor.createGroupMemberList(response);
		//updatePaging(userList, paging);
		if (ListUtil.isEmpty(userList)
			|| userList.size() <= paging.getPageIndex() - 1) {
			paging.setLastPage(true);
		}
		
		return userList;
	}

	@Override
	public Group createGroupMember(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getCreateGroupMemberURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("names", userId);
		httpRequestMessage.addParameter("listid", groupId);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Group group = null;
		if (StringUtil.isNotEmpty(response)) {
			group = new Group();
		    group.setId(groupId);
		}
		
		return group;
	}

	@Override
	public Group createGroupMembers(String groupId, String[] userIds)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || userIds == null ||userIds.length == 0) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		StringBuffer idSb = new StringBuffer();
		for (String userId : userIds) {
			idSb.append(userId).append(",");
		}
		idSb.deleteCharAt(idSb.length() - 1);
		
		Group group = createGroupMember(groupId, idSb.toString());
		return group;
	}

	@Override
	public Group destroyGroupMember(String groupId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
			HttpMethod.POST, conf.getDestroyGroupMemberURL(), auth);
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
		httpRequestMessage.addParameter("names", userId);
		httpRequestMessage.addParameter("listId", groupId);
		
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Group group = null;
		if (StringUtil.isNotEmpty(response)) {
			group = new Group();
		    group.setId(groupId);
		}
		
		return group;
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
		if (paging == null){
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (paging.isPagePaging()){
			initCursorPaging(paging);
		}

		return getStatusList(conf.getDailyHotRetweetsURL(), paging, null);
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

	protected void updatePaging(List<?> list, Paging<?> paging) {
		if (list == null || paging == null) {
			return;
		}
		if (paging.isCursorPaging() && list instanceof PagableList<?>) {
			long nextCursor = Paging.CURSOR_START;
			if (list.size() <= paging.getPageSize() / 2) {
				nextCursor = Paging.CURSOR_END;
			} else {
				nextCursor = ((PagableList<?>) list).getNextCursor();
			}
			setNextPageCursor(paging, nextCursor);
			if (nextCursor == Paging.CURSOR_END) {
				paging.setLastPage(true);
			}
		} else {
			if (list.size() <= paging.getPageSize() / 2) {
			    paging.setLastPage(true);
			}
		}
	}

	@Override
	public String toString() {
		return "Tencent{" + "auth='" + auth + '\'' + '}';
	}
}
