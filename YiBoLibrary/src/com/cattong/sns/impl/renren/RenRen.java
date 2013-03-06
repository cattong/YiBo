package com.cattong.sns.impl.renren;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.OAuth2;
import com.cattong.commons.util.EncryptUtil;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.sns.Sns;
import com.cattong.sns.entity.Album;
import com.cattong.sns.entity.Note;
import com.cattong.sns.entity.Photo;
import com.cattong.sns.entity.Status;
import com.cattong.sns.entity.User;

public class RenRen extends Sns {

	private static final String REST_BASE = "http://api.renren.com/restserver.do";
	private static final String API_VERSION = "1.0";
	private static final String RESPONSE_FORMAT = "json";

	public static final String PROPERTY_FEED_TYPE = "feed_type";

	private ResponseHandler<String> responseHandler;
	private String userId;
	private String screenName;

	public RenRen(Authorization auth) {
		super(auth);
		this.responseHandler = new RenRenResponseHandler();
	}

	@Override
	public String getScreenName() throws LibException {
		if (StringUtil.isEmpty(screenName)) {
			this.userId = getUserId();
			User user = showUser(userId);
			if (user != null) {
				this.screenName = user.getScreenName();
			}
		}
		return this.screenName;
	}

	@Override
	public String getUserId() throws LibException {
		if (StringUtil.isEmpty(userId)) {
			TreeMap<String, String> params = new TreeMap<String, String>();
			params.put("method", "users.getLoggedInUser");
			String json = sendPostRestRequest(params, null);
			this.userId = RenRenUserAdapter.createUserId(json);
		}
		return this.userId;
	}



	private static String[] feedType = {
		"10", // 更新状态
		"11", // Page更新状态
		"20", // 发表日志
		"21", // 分享日志
		"22", // Page发表日志
		"23", // Page分享日志
		"30", // 上传照片
		"31", // Page上传照片
		"32", // 分享照片
		"33", // 分享相册
		"34", // 修改头像的新鲜事
		"35", // page修改头像的新鲜事
		"36", // Page分享照片
		"40", // 成为好友的新鲜事
		"41", // 成为page粉丝的新鲜事
		"50", // 分享视频
		"51", // 分享链接
		"52", // 分享音乐
		"53", // Page分享视频
		"54", // Page分享链接
		"55"  // Page分享音乐
	};



	/******* FriendshipMethods ********/

	@Override
	public boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException {
		if (StringUtil.isEmpty(sourceUserId)
			|| StringUtil.isEmpty(targetUserId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.areFriends");
		params.put("uids1", sourceUserId);
		params.put("uids2", targetUserId);

		String jsonString = sendPostRestRequest(params);
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject json = jsonArray.getJSONObject(0);
			return ParseUtil.getInt("are_friends", json) == 1;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}




	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "friends.getFriends");
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String json = sendPostRestRequest(params);
		List<User> listUser = RenRenUserAdapter.createSimpleUserList(json);
		updatePaging(listUser, paging);
		return listUser;
	}

	/******* NoteMethods ********/

	@Override
	public boolean createNote(String subject, String content, String... tags)
			throws LibException {
		if (StringUtil.isEmpty(subject)
			|| StringUtil.isEmpty(content)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.addBlog");
		params.put("title", subject);
		params.put("content", content);
		int visible = 1; // 好友可见
		params.put("visable", String.valueOf(visible));

		String response = sendPostRestRequest(params);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("id");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public Note showNote(String noteId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(noteId) || StringUtil.isEmpty(ownerId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.get");
		params.put("id", noteId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		Note note = RenRenNoteAdapter.createNote(response);
		note.setUserId(ownerId);
		return note;
	}

	@Override
	public List<Note> getNotes(String ownerId, Paging<Note> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "blog.gets");
		params.put("uid", ownerId);
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String resonse = sendPostRestRequest(params);
		List<Note> notes = RenRenNoteAdapter.createNoteList(resonse);
		if (notes != null && notes.size() > 0) {
			for (Note note : notes) {
				note.setUserId(ownerId);
			}
		}
		updatePaging(notes, paging);
		return notes;
	}


	private boolean isPage(String profileId) throws LibException {
		if (StringUtil.isEmpty(profileId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "pages.isPage");
		params.put("page_id", profileId);
		try {
			String json = sendPostRestRequest(params);
			return RenRenBaseAdapter.createIntegerResult(json) == 1;
		} catch (LibException e) {
			if (e.getErrorCode() == 20302) {
				return false;
			} else {
				throw e;
			}
		}

	}

	/******* UserMethods ********/

	private static final String USER_FULL_FIELD = "uid,name,sex,star,zidou,"
			+ "vip,birthday,email_hash,tinyurl,headurl,mainurl,"
			+ "hometown_location,work_info,university_info,hs_info";

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "users.getInfo");
		params.put("uids", userId);
		params.put("fields", USER_FULL_FIELD);

		String json = sendPostRestRequest(params, null);
		List<User> listUser = RenRenUserAdapter.createUserList(json);
		User user = null;
		if (ListUtil.isNotEmpty(listUser) && listUser.size() == 1) {
			user = listUser.get(0);
		}
		return user;
	}

	@Override
	public List<User> showUsers(List<String> listUserId) throws LibException {
		if (ListUtil.isEmpty(listUserId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		StringBuffer toIds = new StringBuffer();
		for (String userId : listUserId) {
			toIds.append(userId + ",");
		}
		toIds.deleteCharAt(toIds.length() - 1);

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "users.getInfo");
		params.put("uids", toIds.toString());
		params.put("fields", USER_FULL_FIELD);

		String json = sendPostRestRequest(params, null);
		List<User> listUser = RenRenUserAdapter.createUserList(json);
		return listUser;
	}

	@Override
	public boolean createStatus(String statusMessage)
			throws LibException {
		if (StringUtil.isEmpty(statusMessage)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.set");
		String status = RenRenEmotions.specializeEmotion(ServiceProvider.RenRen, statusMessage);
		params.put("status", status);
		String jsonString = sendPostRestRequest(params);

		return RenRenBaseAdapter.createIntegerResult(jsonString) == 1;
	}

	@Override
	public List<Status> getStatuses(String profileId,
			Paging<Status> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.gets");
		if (StringUtil.isNotEmpty(profileId)) {
			params.put("uid", profileId);
		}
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));

		String jsonString = sendPostRestRequest(params);
		List<Status> statusList = RenRenStatusAdapter.createStatusList(jsonString);
		if (statusList.size() > 0) {
			for (Status status : statusList) {
				status.setUserId(profileId);
			}
		}
		updatePaging(statusList, paging);
		return statusList;
	}

	@Override
	public Status showStatus(String statusId, String ownerId) throws LibException {
		if (StringUtil.isEmpty(statusId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "status.get");
		params.put("owner_id", ownerId);

		String jsonString = sendPostRestRequest(params);
		Status status = RenRenStatusAdapter.createStatus(jsonString);
		status.setUserId(ownerId);
		return status;
	}


	private String sendPostRestRequest(TreeMap<String, String> parameters) throws LibException {
		if (parameters == null) {
			return null;
		}
		return sendPostRestRequest(parameters, null);
	}

	private String sendPostRestRequest(TreeMap<String, String> parameters,
			Map<String, File> fileParameters) throws LibException {
		if (parameters == null) {
			return null;
		}

		parameters.put(OAuth2.ACCESS_TOKEN, auth.getAccessToken());
		parameters.put("v", API_VERSION);
		parameters.put("format", RESPONSE_FORMAT);
		parameters.put("call_id", String.valueOf(System.currentTimeMillis()));

		StringBuffer sb = new StringBuffer();
		Iterator<Map.Entry<String, String>> iterator = parameters.entrySet()
				.iterator();
		Map.Entry<String, String> entry = null;
		while (iterator.hasNext()) {
			entry = iterator.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		sb.append(oauthConfig.getConsumerSecret());
		String sig = "";
		try {
			sig = EncryptUtil.getMD5(new String(sb.toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
		}
		parameters.put("sig", sig);

		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, REST_BASE, auth);
		httpRequestWrapper.addParameters(parameters);
		httpRequestWrapper.addParameters(fileParameters);

		return HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
	}

	/** PhotoMethods */

	@Override
	public boolean uploadPhoto(File photo, String caption) throws LibException {
		return uploadPhoto(photo, null, caption);
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption) throws LibException {
		if (photo == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.upload");
		if (StringUtil.isNotEmpty(caption)) {
			String specCaption = RenRenEmotions.specializeEmotion(ServiceProvider.RenRen, caption);
			params.put("caption", specCaption);
		}
		if (StringUtil.isNotEmpty(albumId)) {
			params.put("aid", albumId);
		}
		Map<String, File> fileParameters = new TreeMap<String, File>();
		fileParameters.put("upload", photo);
		String response = sendPostRestRequest(params, fileParameters);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("pid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.get");
		params.put("pids", photoId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		List<Photo> photos = RenRenPhotoAdapter.createPhotoList(response);
		Photo photo = null;
		if (photos.size() > 0) {
			photo = photos.get(0);
			photo.setUserId(ownerId);
		}
		return photo;
	}

	@Override
	public boolean destroyPhoto(String photoId) throws LibException {
		return false;
	}

	@Override
	public boolean createAlbum(String name, String description, String ownerId)
			throws LibException {
		if (StringUtil.isEmpty(name)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.createAlbum");
		params.put("name", name);
		if (StringUtil.isNotEmpty(description)) {
			params.put("description", description);
		}
		int visible = 1; // 好友可见
		params.put("visable", String.valueOf(visible));
		String response = sendPostRestRequest(params);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("aid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean destroyAlbum(String albumId) throws LibException {
		return false;
	}

	@Override
	public List<Album> getAlbums(String ownerId, Paging<Album> paging) throws LibException {
		if (paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.getAlbums");
		params.put("uid", ownerId);
		params.put("page", String.valueOf(paging.getPageIndex()));
		params.put("count", String.valueOf(paging.getPageSize()));
		String response = sendPostRestRequest(params);
		List<Album> albumList = RenRenAlbumAdapter.createAlbumList(response);
		updatePaging(albumList, paging);
		return albumList;
	}

	@Override
	public Album showAlbum(String albumId, String ownerId) throws LibException {
		return null;
	}

	@Override
	public List<Photo> getAlbumPhotos(String albumId, String ownerId, Paging<Photo> paging)
			throws LibException {
		if (StringUtil.isEmpty(albumId) || paging == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		if (!paging.isPagePaging()) {
			initPagePaging(paging);
		}
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", "photos.get");
		params.put("aid", albumId);
		params.put("uid", ownerId);
		String response = sendPostRestRequest(params);
		List<Photo> photos = RenRenPhotoAdapter.createPhotoList(response);
		if (photos.size() > 0) {
			for (Photo photo : photos) {
				photo.setUserId(ownerId);
			}
		}
		updatePaging(photos, paging);
		return photos;
	}

	@Override
	public boolean destroyStatus(String statusId) throws LibException {
		return false;
	}

	@Override
	public boolean destroyNote(String noteId) throws LibException {
		return false;
	}

}
