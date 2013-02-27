package net.dev123.sns.qqzone;

import java.io.File;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.sns.Sns;
import net.dev123.sns.entity.Album;
import net.dev123.sns.entity.Comment;
import net.dev123.sns.entity.FriendList;
import net.dev123.sns.entity.Note;
import net.dev123.sns.entity.Page;
import net.dev123.sns.entity.Photo;
import net.dev123.sns.entity.Post;
import net.dev123.sns.entity.Post.PostType;
import net.dev123.sns.entity.Privacy;
import net.dev123.sns.entity.Status;
import net.dev123.sns.entity.User;

import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class QQZone extends Sns {

	private static final String REST_BASE = "https://graph.qq.com/";
	private static final String RESPONSE_FORMAT = "json";

	private String openId; // 腾讯针对应用生成的用户在应用中的唯一标识
	private String screenName;
	private ResponseHandler<String> responseHandler;
	private OAuthConfiguration oauthAuthConfiguration;

	public QQZone(Authorization auth) {
		super(auth);
		this.responseHandler = new QQZoneResponseHandler();
		this.oauthAuthConfiguration = OAuthConfigurationFactory
				.getOAuthConfiguration(ServiceProvider.QQZone);
	}

	@Override
	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			User user = showUser(getUserId());
			this.screenName = user.getScreenName();
		}
		return null;
	}

	@Override
	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(openId)) {
			String url = REST_BASE + "oauth2.0/me";
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.GET, url, auth);
			String response = HttpRequestHelper.execute(httpRequestMessage,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				if (json.has("openid")) {
					openId = ParseUtil.getRawString("openid", json);
				}
			} catch (JSONException e) {
				throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
			}

		}
		return openId;
	}

	@Override
	public List<Boolean> areFriends(List<String> listSourceUserId,
			List<String> listTargetUserId) throws LibException {
		return null;
	}

	@Override
	public List<String> getFriendsIds(Paging<String> paging)
			throws LibException {
		return null;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return null;
	}

	@Override
	public boolean isPageFollower(String userId, String pageId)
			throws LibException {
		return false;
	}

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		User user = null;
		if (StringUtil.isEquals(openId, userId)) {
			String url = REST_BASE + "user/get_user_info";
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.POST, url, auth);
			appendAddtionalParameters(httpRequestMessage);
			String response = HttpRequestHelper.execute(httpRequestMessage,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				user = new User();
				user.setServiceProvider(ServiceProvider.QQZone);
				user.setId(openId);
				user.setScreenName(ParseUtil.getRawString("nickname", json));
				user.setName(user.getScreenName());
				user.setHeadUrl(ParseUtil.getRawString("figureurl_2", json));
			} catch (Exception e) {
				throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
			}
		}
		return user;
	}

	@Override
	public List<User> showUsers(List<String> listUserId) throws LibException {
		return null;
	}

	@Override
	public boolean createStatus(String statusMessage) throws LibException {
		if (StringUtil.isEmpty(statusMessage)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String url = REST_BASE + "shuoshuo/add_topic";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, url, auth);
		String status = QQZoneEmotions.specializeEmotion(ServiceProvider.QQZone, statusMessage);
		httpRequestMessage.addParameter("con", status);
		appendAddtionalParameters(httpRequestMessage);
		String response = HttpRequestHelper.execute(httpRequestMessage,
				responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			// {"data":{"msg":"","ret":0},"err":{"code":0},"richinfo":null}
			if (json.has("data")) {
				json = json.getJSONObject("data");
			}
			return ParseUtil.getInt("ret", json) == 0;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public List<Status> getStatuses(String userId, Paging<Status> paging)
			throws LibException {
		return null;
	}

	@Override
	public Status showStatus(String statusId, String ownerId)
			throws LibException {
		return null;
	}

	private void appendAddtionalParameters(HttpRequestMessage httpRequestMessage)
			throws LibException {
		httpRequestMessage.addParameter("oauth_consumer_key",
				oauthAuthConfiguration.getOAuthConsumerKey());
		httpRequestMessage.addParameter("openid", getUserId());
		httpRequestMessage.addParameter("format", RESPONSE_FORMAT);
	}

	@Override
	public boolean areFriends(String listSourceUserId, String listTargetUserId)
			throws LibException {

		return false;
	}

	@Override
	public boolean createFriendList(String listName) throws LibException {

		return false;
	}

	@Override
	public boolean destroyFriendList(String listName) throws LibException {

		return false;
	}

	@Override
	public List<FriendList> getFriendLists(Paging<FriendList> paging)
			throws LibException {

		return null;
	}

	@Override
	public boolean isPageAdmin(String pageId) throws LibException {

		return false;
	}

	@Override
	public Page showPage(String pageId) throws LibException {

		return null;
	}

	@Override
	public List<User> getMutualFriends(String userIdA, String userIdB, Paging<User> paging)
			throws LibException {

		return null;
	}

	@Override
	public boolean followPage(String pageId) throws LibException {

		return false;
	}

	@Override
	public boolean unfollowPage(String pageId) throws LibException {

		return false;
	}

	@Override
	public List<Page> getFollowingPages(String userId, Paging<Page> paging)
			throws LibException {

		return null;
	}

	@Override
	public boolean createNote(String subject, String content, Privacy privacy,
			String... tags) throws LibException {
		if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(content)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = REST_BASE + "blog/add_one_blog";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("title", subject);
		httpRequestMessage.addParameter("content", content);
		appendAddtionalParameters(httpRequestMessage);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getInt("ret", json) == 0;
		} catch (Exception e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Note showNote(String noteId, String ownerId) throws LibException {
		return null;
	}

	@Override
	public List<Note> getNotes(String ownerId, Paging<Note> paging)
			throws LibException {
		return null;
	}

	@Override
	public boolean uploadPhoto(File photo, String caption) throws LibException {
		if (photo == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String topicUrl = REST_BASE + "shuoshuo/add_topic";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, topicUrl, auth);
		// 先向默认相册传照片
		String uploadUrl = REST_BASE + "photo/upload_pic";
		HttpRequestMessage uploadRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, uploadUrl, auth);
		String specCaption = QQZoneEmotions.specializeEmotion(ServiceProvider.QQZone, caption);
		uploadRequestMessage.addParameter("photodesc", specCaption);
		uploadRequestMessage.addParameter("picture", photo);
		appendAddtionalParameters(uploadRequestMessage);
		String uploadResponse = HttpRequestHelper.execute(uploadRequestMessage, responseHandler);
		try {
			JSONObject photoJson = new JSONObject(uploadResponse);
			String albumid = ParseUtil.getRawString("albumid", photoJson);
			String lloc = ParseUtil.getRawString("lloc", photoJson); // 大图Id
			String sloc = ParseUtil.getRawString("sloc", photoJson); //小图Id
			String richval = String.format("%1$s,%2$s,%3$s,", albumid, lloc, sloc);

			httpRequestMessage.addParameter("richtype", 1); // 1表示图片
			httpRequestMessage.addParameter("richval", richval);

			httpRequestMessage.addParameter("con", caption);
			appendAddtionalParameters(httpRequestMessage);
			String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
			JSONObject json = new JSONObject(response);
			// {"data":{"msg":"","ret":0},"err":{"code":0},"richinfo":null}
			if (json.has("data")) {
				json = json.getJSONObject("data");
			}
			int ret = ParseUtil.getInt("ret", json);
			return ret == 0;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String uploadUrl = REST_BASE + "photo/upload_pic";
		HttpRequestMessage uploadRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, uploadUrl, auth);
		uploadRequestMessage.addParameter("albumid", albumId);
		caption = QQZoneEmotions.specializeEmotion(ServiceProvider.QQZone, caption);
		uploadRequestMessage.addParameter("photodesc", caption);
		uploadRequestMessage.addParameter("picture", photo);
		appendAddtionalParameters(uploadRequestMessage);
		String response = HttpRequestHelper.execute(uploadRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getInt("ret", json) == 0;
		} catch (Exception e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Photo showPhoto(String photoId, String ownerId) throws LibException {

		return null;
	}

	@Override
	public boolean destroyPhoto(String photoId) throws LibException {

		return false;
	}

	@Override
	public boolean createAlbum(String name, String description, String ownerId, Privacy privacy)
			throws LibException {
		if (StringUtil.isEmpty(name)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = REST_BASE + "photo/add_album";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("albumname", name);
		if (StringUtil.isNotEmpty(description)) {
			httpRequestMessage.addParameter("albumdesc", description);
		}
		if (privacy != null) {
			int privacyInt = 1; // 1=公开；3=只主人可见； 4=QQ好友可见； 5=问答加密
			switch (privacy.getValue()) {
			case ALL_FRIENDS:
				privacyInt = 4;
				break;
			case EVERYONE:
				privacyInt = 1;
				break;
			case SELF:
				privacyInt = 3;
				break;
			default:
				privacyInt = 4;
				break;
			}
			httpRequestMessage.addParameter("priv", privacyInt);
		}
		appendAddtionalParameters(httpRequestMessage);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getInt("ret", json) == 0;
		} catch (Exception e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		return false;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = REST_BASE + "photo/list_album";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		appendAddtionalParameters(httpRequestMessage);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Album> albums = QQZoneAlbumAdapter.createAlbumList(response);
		updatePaging(albums, paging);
		return albums;
	}

	@Override
	public List<Photo> getAlbumPhotos(String albumId, String ownerId,
			Paging<Photo> paging) throws LibException {

		return null;
	}

	@Override
	public boolean createLike(String objectId, String ownerId, PostType type)
			throws LibException {

		return false;
	}

	@Override
	public boolean destroyLike(String objectId, String ownerId, PostType type)
			throws LibException {

		return false;
	}

	@Override
	public long getLikeCount(String objectId, String ownerId, PostType type)
			throws LibException {
		return 0;
	}

	@Override
	public boolean createComment(String commentText, String objectId,
			String ownerId, PostType type) throws LibException {
		return false;
	}

	@Override
	public Comment showComment(String commentId) throws LibException {
		return null;
	}

	@Override
	public boolean destroyComment(String commentId) throws LibException {
		return false;
	}

	@Override
	public List<Comment> getComments(String objectId, String ownerId,
			PostType type, Paging<Comment> paging) throws LibException {
		return null;
	}

	@Override
	public boolean share(Post post) throws LibException {
		return false;
	}

	@Override
	public List<Post> getNewsFeed(Paging<Post> paging) throws LibException {
		return null;
	}

	@Override
	public List<Post> getProfileFeed(String profileId, Paging<Post> paging)
			throws LibException {
		return null;
	}

	@Override
	public boolean createFriendListMember(String listId, String userId)
			throws LibException {
		return false;
	}

	@Override
	public boolean destroyFriendListMember(String listId, String userId)
			throws LibException {
		return false;
	}

	@Override
	public List<User> getFriendListMember(String listId, Paging<User> paging)
			throws LibException {
		return null;
	}

	@Override
	public boolean destroyStatus(String statusId) throws LibException {
		return false;
	}

	@Override
	public boolean destroyNote(String noteId) throws LibException {
		return false;
	}

	@Override
	public Album showAlbum(String albumId, String ownerId) throws LibException {
		return null;
	}

}
