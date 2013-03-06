package com.cattong.sns.impl.facebook;

import java.io.File;
import java.util.List;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.sns.Sns;
import com.cattong.sns.entity.Album;
import com.cattong.sns.entity.Note;
import com.cattong.sns.entity.Photo;
import com.cattong.sns.entity.Status;
import com.cattong.sns.entity.User;


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
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			this.userId = ParseUtil.getRawString("id", json);
			this.screenName = ParseUtil.getRawString("name", json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException {
		if (StringUtil.isEmpty(targetUserId) || StringUtil.isEmpty(sourceUserId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String url = GRAPH_BASE + sourceUserId + "/friends/" + targetUserId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONArray jsonArray = new JSONArray(response);
			return jsonArray.length() == 1; // 若不是好友则data为[]
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + "me/friends";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("limit", paging.getPageSize());
		httpRequestWrapper.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<User> userList = FacebookUserAdapter.createSimpleUserList(response);
		updatePaging(userList, paging);
		return userList;
	}

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + userId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return FacebookUserAdapter.createUser(response);
	}

	@Override
	public List<User> showUsers(List<String> listUserId) throws LibException {
		return null;
	}

	@Override
	public boolean createStatus(String status) throws LibException {
		if (StringUtil.isEmpty(status)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + "feed";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("message", status);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject jsonObject = new JSONObject(response);
			return jsonObject.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public List<Status> getStatuses(String profileId, Paging<Status> paging)
			throws LibException {
		if (StringUtil.isEmpty(profileId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		String url = GRAPH_BASE + userId + "/statuses";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Status> statusList = FacebookStatusAdapter.createStatusList(response);

		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Status showStatus(String statusId, String ownerId)
			throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + statusId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		Status status = FacebookStatusAdapter.createStatus(response);
		status.setUserId(ownerId);
		return status;
	}


	@Override
	public boolean destroyStatus(String statusId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + statusId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean createNote(String subject, String content,
			String... tags) throws LibException {
		if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(content)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/notes";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("subject", subject);
		httpRequestWrapper.addParameter("message", content);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Note showNote(String noteId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(noteId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + noteId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		Note note = FacebookNoteAdapter.createNote(response);
		//note.setFrom(showProfile(ownerId));
		return note;
	}

	@Override
	public boolean destroyNote(String noteId)
			throws LibException {
		if (StringUtil.isEmpty(noteId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + noteId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return "true".equalsIgnoreCase(response);
	}


	@Override
	public List<Note> getNotes(String ownerId, Paging<Note> paging)
			throws LibException {
		if (StringUtil.isEmpty(ownerId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/notes";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since", paging.getSince().getUpdatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("until", paging.getMax().getUpdatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Note> noteList = FacebookNoteAdapter.createNoteList(response);
		for (Note note : noteList) {
			note.setUserId(ownerId);
		}
		updatePaging(noteList, paging);
		return noteList;
	}

	@Override
	public boolean uploadPhoto(File photo, String caption) throws LibException {
		if (photo == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + getUserId() + "/photos";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("source", photo);
		if (StringUtil.isNotEmpty(caption)) {
			httpRequestWrapper.addParameter("message", photo);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}


	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + albumId + "/photos";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("source", photo);
		if (StringUtil.isNotEmpty(caption)) {
			httpRequestWrapper.addParameter("message", photo);
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Photo showPhoto(String photoId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(photoId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + photoId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		Photo photo = FacebookPhotoAdapter.createPhoto(response);
		photo.setUserId(ownerId);
		return photo;
	}

	@Override
	public boolean destroyPhoto(String photoId) throws LibException {
		if (StringUtil.isEmpty(photoId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = GRAPH_BASE + photoId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public boolean createAlbum(String name, String description, String ownerId)
			throws LibException {
		if (StringUtil.isEmpty(name)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + ownerId + "/albums";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("name", name);
		if (StringUtil.isNotEmpty(description)) {
			httpRequestWrapper.addParameter("message", description);
		}
		String privacy = String.format("{'value':'%1$s'}", "ALL_FRIENDS");
		httpRequestWrapper.addParameter("privacy", privacy);

		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		if (StringUtil.isEmpty(albumId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = GRAPH_BASE + albumId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("method", "delete");
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		return "true".equalsIgnoreCase(response);
	}

	@Override
	public Album showAlbum(String albumId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(albumId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + albumId;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		Album album = FacebookAlbumAdapter.createAlbum(response);
		album.setUserId(ownerId);
		return album;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging)
			throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + ownerId + "/albums";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("limit", paging.getPageSize());
		if (paging.getSince() != null) {
			httpRequestWrapper.addParameter("since", paging.getSince().getCreatedTime().getTime());
		}
		if (paging.getMax() != null) {
			httpRequestWrapper.addParameter("until", paging.getMax().getCreatedTime().getTime());
		}
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Album> albumList = FacebookAlbumAdapter.createAlbumList(response);
		updatePaging(albumList, paging);
		
		for (Album album : albumList) {
			album.setUserId(ownerId);
		}
		return albumList;
	}

	@Override
	public List<Photo> getAlbumPhotos(String albumId, String ownerId,
			Paging<Photo> paging) throws LibException {
		if (StringUtil.isEmpty(albumId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = GRAPH_BASE + albumId + "/photos";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.GET, url, auth);
		httpRequestWrapper.addParameter("limit", paging.getPageSize());
		httpRequestWrapper.addParameter("offset", (paging.getPageIndex() - 1) * paging.getPageSize());
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		List<Photo> photos = FacebookPhotoAdapter.createPhotoList(response);
		updatePaging(photos, paging);
		
		for (Photo photo : photos) {
			photo.setUserId(ownerId);
		}
		return photos;
	}

}
