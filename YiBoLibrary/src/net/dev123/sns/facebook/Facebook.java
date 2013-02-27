package net.dev123.sns.facebook;

import java.io.File;
import java.util.List;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.dev123.commons.Paging;
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
import net.dev123.sns.entity.Privacy;
import net.dev123.sns.entity.Privacy.Value;
import net.dev123.sns.entity.Profile;
import net.dev123.sns.entity.Status;
import net.dev123.sns.entity.User;
import net.dev123.sns.entity.Post.PostType;

public class Facebook extends Sns {

	private static final String GRAPH_BASE = "https://graph.facebook.com/";
	public static final String PICTURE_URL_FORMAT = "http://graph.facebook.com/%1$s/picture";
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	private String userId;
	private String screenName;
	private ResponseHandler<String> responseHandler;

	public Facebook(Authorization auth) {
		super(auth);
		this.responseHandler = new FacebookResponseHandler();
	}

	@Override
	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			initSelfData();
		}
		return screenName;
	}

	@Override
	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(userId)) {
			initSelfData();
		}
		return userId;
	}

	private void initSelfData() throws LibException {
		String url = GRAPH_BASE + "me";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			this.userId = ParseUtil.getRawString("id", json);
			this.screenName = ParseUtil.getRawString("name", json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException {
		if (StringUtil.isEmpty(targetUserId) || StringUtil.isEmpty(sourceUserId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}

		String url = GRAPH_BASE + sourceUserId + "/friends/" + targetUserId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONArray jsonArray = new JSONArray(response);
			return jsonArray.length() == 1; // 若不是好友则data为[]
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public List<Boolean> areFriends(List<String> listSourceUserId,
			List<String> listTargetUserId) throws LibException {
		return null;
	}

	@Override
	public List<String> getFriendsIds(Paging<String> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + "me/friends";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<String> idsList = FacebookUserAdapter.createUserIdList(response);
		updatePaging(idsList, paging);
		return idsList;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + "me/friends";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> userList = FacebookUserAdapter.createSimpleUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public List<User> getMutualFriends(String userIdA, String userIdB, Paging<User> paging)
			throws LibException {
		if (StringUtil.isEmpty(userIdA) || StringUtil.isEmpty(userIdB) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + userIdA + "/mutualfriends/" + userIdB;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> friends = FacebookUserAdapter.createSimpleUserList(response);
		updatePaging(friends, paging);
		return friends;
	}

	@Override
	public boolean createFriendList(String listName) throws LibException {
		if (StringUtil.isEmpty(listName)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/friendlists";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("name", listName);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public boolean destroyFriendList(String listId) throws LibException {
		if (StringUtil.isEmpty(listId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + "/" + listId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public List<FriendList> getFriendLists(Paging<FriendList> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + getUserId() + "/friendlists";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<FriendList> friendListLists = FacebookUserAdapter.createFriendListList(response);
		updatePaging(friendListLists, paging);
		return friendListLists;
	}

	@Override
	public boolean createFriendListMember(String listId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(listId) || StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + listId + "/members/" + userId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean destroyFriendListMember(String listId, String userId)
			throws LibException {
		if (StringUtil.isEmpty(listId) || StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + listId + "/members/" + userId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public List<User> getFriendListMember(String listId, Paging<User> paging) throws LibException {
		if (StringUtil.isEmpty(listId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + listId + "/members";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<User> userList = FacebookUserAdapter.createSimpleUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public boolean followPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + pageId + "/likes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean unfollowPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + pageId + "/likes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean isPageFollower(String userId, String pageId)
			throws LibException {
		if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + userId + "/likes/" + pageId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONArray jsonArray = new JSONArray(response);
			return jsonArray.length() == 1;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public List<Page> getFollowingPages(String userId, Paging<Page> paging)
			throws LibException {
		if (StringUtil.isEmpty(userId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + userId + "/likes" ;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Page> pageList = FacebookPageAdapter.createPageList(response);
		updatePaging(pageList, paging);
		return pageList;
	}

	@Override
	public boolean isPageAdmin(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + pageId + "/admins/" + getUserId();
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public Page showPage(String pageId) throws LibException {
		if (StringUtil.isEmpty(pageId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + pageId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FacebookPageAdapter.createPage(response);
	}

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + userId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return FacebookUserAdapter.createUser(response);
	}

	@Override
	public List<User> showUsers(List<String> listUserId) throws LibException {
		return null;
	}

	@Override
	public boolean createStatus(String status) throws LibException {
		if (StringUtil.isEmpty(status)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + "feed";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("message", status);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject jsonObject = new JSONObject(response);
			return jsonObject.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	private Profile showProfile(String profileId) throws LibException {
		if (StringUtil.isEmpty(profileId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + profileId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("metadata", 1);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			Profile profile = null;
			JSONObject json = new JSONObject(response);
			String type = ParseUtil.getRawString("type", json);
			if ("user".equals(type)) {
				profile = FacebookUserAdapter.createUser(json);
			} else if ("page".equals(type)) {
				profile = FacebookPageAdapter.createPage(json);
			}
			return profile;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public List<Status> getStatuses(String profileId, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(profileId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + userId + "/statuses";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Status> statusList = FacebookStatusAdapter.createStatusList(response);
		Profile profile = showProfile(profileId);
		for (Status status : statusList) {
			status.setFrom(profile);
		}
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Status showStatus(String statusId, String ownerId)
			throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + statusId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Status status = FacebookStatusAdapter.createStatus(response);
		status.setFrom(showProfile(ownerId));
		return status;
	}


	@Override
	public boolean destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + statusId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean createNote(String subject, String content, Privacy privacy,
			String... tags) throws LibException {
		if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(content)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/notes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("subject", subject);
		httpRequestMessage.addParameter("message", content);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Note showNote(String noteId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(noteId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + noteId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Note note = FacebookNoteAdapter.createNote(response);
		note.setFrom(showProfile(ownerId));
		return note;
	}

	@Override
	public boolean destroyNote(String noteId)
			throws LibException {
		if (StringUtil.isEmpty(noteId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + noteId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}


	@Override
	public List<Note> getNotes(String ownerId, Paging<Note> paging)
			throws LibException {
		if (StringUtil.isEmpty(ownerId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/notes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Note> noteList = FacebookNoteAdapter.createNoteList(response);
		Profile profile = showProfile(ownerId);
		for (Note note : noteList) {
			note.setFrom(profile);
		}
		updatePaging(noteList, paging);
		return noteList;
	}

	@Override
	public boolean uploadPhoto(File photo, String caption) throws LibException {
		if (photo == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/photos";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("source", photo);
		if (StringUtil.isNotEmpty(caption)) {
			httpRequestMessage.addParameter("message", photo);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}


	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + albumId + "/photos";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("source", photo);
		if (StringUtil.isNotEmpty(caption)) {
			httpRequestMessage.addParameter("message", photo);
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Photo showPhoto(String photoId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(photoId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + photoId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Photo photo = FacebookPhotoAdapter.createPhoto(response);
		photo.setFrom(showProfile(ownerId));
		return photo;
	}

	@Override
	public boolean destroyPhoto(String photoId) throws LibException {
		if (StringUtil.isEmpty(photoId)) {
			throw new LibException(ExceptionCode.PARAMETER_ERROR);
		}
		String url = GRAPH_BASE + photoId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
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
		String url = GRAPH_BASE + ownerId + "/albums";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("name", name);
		if (StringUtil.isNotEmpty(description)) {
			httpRequestMessage.addParameter("message", description);
		}
		if (privacy != null && privacy.getValue() != Value.CUSTOM) {
			httpRequestMessage.addParameter("privacy",
				String.format("{'value':'%1$s'}", privacy.getValue().toString()));
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		if (StringUtil.isEmpty(albumId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + albumId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public Album showAlbum(String albumId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(albumId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + albumId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Album album = FacebookAlbumAdapter.createAlbum(response);
		album.setFrom(showProfile(ownerId));
		return album;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + ownerId + "/albums";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since", paging.getSince().getCreatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("until", paging.getMax().getCreatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Album> albumList = FacebookAlbumAdapter.createAlbumList(response);
		updatePaging(albumList, paging);
		Profile profile = showProfile(ownerId);
		for (Album album : albumList) {
			album.setFrom(profile);
		}
		return albumList;
	}

	@Override
	public List<Photo> getAlbumPhotos(String albumId, String ownerId,
			Paging<Photo> paging) throws LibException {
		if (StringUtil.isEmpty(albumId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + albumId + "/photos";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Photo> photos = FacebookPhotoAdapter.createPhotoList(response);
		updatePaging(photos, paging);
		Profile profile = showProfile(ownerId);
		for (Photo photo : photos) {
			photo.setFrom(profile);
		}
		return photos;
	}

	@Override
	public boolean createLike(String objectId, String ownerId, PostType type)
			throws LibException {
		if (StringUtil.isEmpty(objectId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + objectId + "/likes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean destroyLike(String objectId, String ownerId, PostType type)
			throws LibException {
		if (StringUtil.isEmpty(objectId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + objectId + "/likes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public long getLikeCount(String objectId, String ownerId, PostType type)
			throws LibException {
		if (StringUtil.isEmpty(objectId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + objectId + "/likes";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONArray jsonArray = new JSONArray(response);
			return jsonArray.length();
		} catch (Exception e) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
	}

	@Override
	public boolean createComment(String commentText, String objectId,
			String ownerId, PostType type) throws LibException {
		if (StringUtil.isEmpty(objectId) || StringUtil.isEmpty(commentText)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + objectId + "/comments";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.POST, url, auth);
		httpRequestMessage.addParameter("message", commentText);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Comment showComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + commentId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		Comment comment = FacebookCommentAdapter.createComment(response);
		return comment;
	}

	@Override
	public boolean destroyComment(String commentId) throws LibException {
		if (StringUtil.isEmpty(commentId)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + commentId;
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public List<Comment> getComments(String objectId, String ownerId,
			PostType type, Paging<Comment> paging) throws LibException {
		if (StringUtil.isEmpty(objectId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + objectId + "/comments";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		httpRequestMessage.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Comment> commentList = FacebookCommentAdapter.createCommentsList(response);
		updatePaging(commentList, paging);
		return commentList;
	}

	@Override
	public boolean share(Post post) throws LibException {
		return false;
	}

	@Override
	public List<Post> getNewsFeed(Paging<Post> paging) throws LibException {
		if (paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		String url = GRAPH_BASE + "me/home";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Post> postList = FacebookPostAdapter.createPostList(response);
		updatePaging(postList, paging);
		return postList;
	}

	@Override
	public List<Post> getProfileFeed(String profileId, Paging<Post> paging)
			throws LibException {
		if (StringUtil.isEmpty(profileId) || paging == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + profileId + "/feed";
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(HttpMethod.GET, url, auth);
		httpRequestMessage.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestMessage.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestMessage.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		List<Post> postList = FacebookPostAdapter.createPostList(response);
		updatePaging(postList, paging);
		return postList;
	}

}
