package com.cattong.sns.impl.kaixin;

import java.io.File;
import java.util.List;

import org.apache.http.client.ResponseHandler;
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
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.sns.Sns;
import com.cattong.sns.entity.Album;
import com.cattong.sns.entity.Note;
import com.cattong.sns.entity.Photo;
import com.cattong.sns.entity.Status;
import com.cattong.sns.entity.User;

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
			HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
					HttpMethod.GET, url, auth);
			httpRequestWrapper.addParameter("fields", "uid");
			String response = HttpRequestHelper.execute(httpRequestWrapper,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				this.userId = ParseUtil.getRawString("uid", json);
			} catch (JSONException e) {
				throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
			}
		}
		return this.userId;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return null;
	}

	@Override
	public User showUser(String userId) throws LibException {
		String url = REST_BASE + "users/me." + RESPONSE_FORMAT;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.GET, url, auth);
		String response = HttpRequestHelper.execute(httpRequestWrapper,
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = REST_BASE + "records/add." + RESPONSE_FORMAT;
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, url, auth);
		String status = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, statusMessage);
		httpRequestWrapper.addParameter("content", status);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("rid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public boolean areFriends(String listSourceUserId, String listTargetUserId)
			throws LibException {
		return false;
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
	public boolean createNote(String subject, String content,
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}
		String url = REST_BASE + "records/add." + RESPONSE_FORMAT;
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("pic", photo);
		String specCaption = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, caption);
		httpRequestWrapper.addParameter("content", specCaption);
		httpRequestWrapper.addParameter("save_to_album", true);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("rid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = REST_BASE + "photo/upload." + RESPONSE_FORMAT;
		HttpRequestWrapper httpRequestWrapper =
			new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("albumId", albumId);
		String specCaption = KaiXinEmotions.specializeEmotion(ServiceProvider.KaiXin, caption);
		httpRequestWrapper.addParameter("title", specCaption);
		httpRequestWrapper.addParameter("pic", photo);
		httpRequestWrapper.addParameter("send_news", true);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return json.has("pid");
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
	public boolean createAlbum(String name, String description, String ownerId)
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
