package net.dev123.sns.kaixin;

import java.io.File;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
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

public class KaiXin extends Sns {

	private static final String REST_BASE = "https://api.kaixin001.com/";
	private static final String RESPONSE_FORMAT = "json";

	private ResponseHandler<String> responseHandler;
	private String userId;
	private String screenName;

	public KaiXin(Authorization auth) {
		super(auth);
		this.responseHandler = new KaiXinResponseHandler();
	}

	@Override
	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			User user = showUser(getUserId());
			this.screenName = user.getScreenName();
		}
		return this.screenName;
	}

	@Override
	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(userId)) {
			String url = REST_BASE + "users/me." + RESPONSE_FORMAT;
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
					HttpMethod.GET, url, auth);
			httpRequestMessage.addParameter("fields", "uid");
			String response = HttpRequestHelper.execute(httpRequestMessage,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				this.userId = ParseUtil.getRawString("uid", json);
			} catch (JSONException e) {
				throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
			}
		}
		return this.userId;
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
		String url = REST_BASE + "users/me." + RESPONSE_FORMAT;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage,
				responseHandler);
		User user = KaixinUserAdapter.createSimpleUser(response);
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

		String url = REST_BASE + "records/add." + RESPONSE_FORMAT;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, url, auth);
		String status = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, statusMessage);
		httpRequestMessage.addParameter("content", status);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("rid");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
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
	public List<Status> getStatuses(String userId, Paging<Status> paging)
			throws LibException {
		return null;
	}

	@Override
	public Status showStatus(String statusId, String ownerId)
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
		return false;
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
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = REST_BASE + "records/add." + RESPONSE_FORMAT;
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("pic", photo);
		String specCaption = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, caption);
		httpRequestMessage.addParameter("content", specCaption);
		httpRequestMessage.addParameter("save_to_album", true);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("rid");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}

		String url = REST_BASE + "photo/upload." + RESPONSE_FORMAT;
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("albumId", albumId);
		String specCaption = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, caption);
		httpRequestMessage.addParameter("title", specCaption);
		httpRequestMessage.addParameter("pic", photo);
		httpRequestMessage.addParameter("send_news", true);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("pid");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
		return false;
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		return false;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException {
		return null;
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
