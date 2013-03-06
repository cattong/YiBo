package com.cattong.sns.impl.qqzone;

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

public class QQZone extends Sns {

	private static final String REST_BASE = "https://graph.qq.com/";
	private static final String RESPONSE_FORMAT = "json";

	private String openId; // 腾讯针对应用生成的用户在应用中的唯一标识
	private String screenName;
	private ResponseHandler<String> responseHandler;

	public QQZone(Authorization auth) {
		super(auth);
		this.responseHandler = new QQZoneResponseHandler();
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
			HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
					HttpMethod.GET, url, auth);
			String response = HttpRequestHelper.execute(httpRequestWrapper,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				if (json.has("openid")) {
					openId = ParseUtil.getRawString("openid", json);
				}
			} catch (JSONException e) {
				throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
			}

		}
		return openId;
	}

	@Override
	public List<User> getFriends(Paging<User> paging) throws LibException {
		return null;
	}

	@Override
	public User showUser(String userId) throws LibException {
		if (StringUtil.isEmpty(userId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		User user = null;
		if (StringUtil.isEquals(openId, userId)) {
			String url = REST_BASE + "user/get_user_info";
			HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
					HttpMethod.POST, url, auth);
			appendAddtionalParameters(httpRequestWrapper);
			String response = HttpRequestHelper.execute(httpRequestWrapper,
					responseHandler);
			try {
				JSONObject json = new JSONObject(response);
				user = new User();
				user.setServiceProvider(ServiceProvider.QQZone);
				user.setUserId(openId);
				user.setScreenName(ParseUtil.getRawString("nickname", json));
				user.setName(user.getScreenName());
				user.setHeadUrl(ParseUtil.getRawString("figureurl_2", json));
			} catch (Exception e) {
				throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(LibResultCode.E_PARAM_ERROR);
		}

		String url = REST_BASE + "shuoshuo/add_topic";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, url, auth);
		String status = QQZoneEmotions.specializeEmotion(ServiceProvider.QQZone, statusMessage);
		httpRequestWrapper.addParameter("con", status);
		appendAddtionalParameters(httpRequestWrapper);
		String response = HttpRequestHelper.execute(httpRequestWrapper,
				responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			// {"data":{"msg":"","ret":0},"err":{"code":0},"richinfo":null}
			if (json.has("data")) {
				json = json.getJSONObject("data");
			}
			return ParseUtil.getInt("ret", json) == 0;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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

	private void appendAddtionalParameters(HttpRequestWrapper httpRequestWrapper)
			throws LibException {
		httpRequestWrapper.addParameter("oauth_consumer_key",
				oauthConfig.getConsumerKey());
		httpRequestWrapper.addParameter("openid", getUserId());
		httpRequestWrapper.addParameter("format", RESPONSE_FORMAT);
	}

	@Override
	public boolean areFriends(String listSourceUserId, String listTargetUserId)
			throws LibException {

		return false;
	}

	@Override
	public boolean createNote(String subject, String content,
			String... tags) throws LibException {
		if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(content)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		String url = REST_BASE + "blog/add_one_blog";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("title", subject);
		httpRequestWrapper.addParameter("content", content);
		appendAddtionalParameters(httpRequestWrapper);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getInt("ret", json) == 0;
		} catch (Exception e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String topicUrl = REST_BASE + "shuoshuo/add_topic";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(
				HttpMethod.POST, topicUrl, auth);
		// 先向默认相册传照片
		String uploadUrl = REST_BASE + "photo/upload_pic";
		HttpRequestWrapper uploadRequestMessage = new HttpRequestWrapper(
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

			httpRequestWrapper.addParameter("richtype", 1); // 1表示图片
			httpRequestWrapper.addParameter("richval", richval);

			httpRequestWrapper.addParameter("con", caption);
			appendAddtionalParameters(httpRequestWrapper);
			String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
			JSONObject json = new JSONObject(response);
			// {"data":{"msg":"","ret":0},"err":{"code":0},"richinfo":null}
			if (json.has("data")) {
				json = json.getJSONObject("data");
			}
			int ret = ParseUtil.getInt("ret", json);
			return ret == 0;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	@Override
	public boolean uploadPhoto(File photo, String albumId, String caption)
			throws LibException {
		if (photo == null || StringUtil.isEmpty(albumId)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}

		String uploadUrl = REST_BASE + "photo/upload_pic";
		HttpRequestWrapper uploadRequestMessage = new HttpRequestWrapper(
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
		if (StringUtil.isEmpty(name)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = REST_BASE + "photo/add_album";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		httpRequestWrapper.addParameter("albumname", name);
		if (StringUtil.isNotEmpty(description)) {
			httpRequestWrapper.addParameter("albumdesc", description);
		}
		int privacyInt = 4; // 1=公开；3=只主人可见； 4=QQ好友可见； 5=问答加密
		httpRequestWrapper.addParameter("priv", privacyInt);

		appendAddtionalParameters(httpRequestWrapper);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
		try {
			JSONObject json = new JSONObject(response);
			return ParseUtil.getInt("ret", json) == 0;
		} catch (Exception e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		if (StringUtil.isEmpty(ownerId)) {
			ownerId = getUserId();
		}
		String url = REST_BASE + "photo/list_album";
		HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(HttpMethod.POST, url, auth);
		appendAddtionalParameters(httpRequestWrapper);
		String response = HttpRequestHelper.execute(httpRequestWrapper, responseHandler);
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
