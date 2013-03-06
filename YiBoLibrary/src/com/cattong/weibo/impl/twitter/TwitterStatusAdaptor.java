package com.cattong.weibo.impl.twitter;

import static com.cattong.commons.util.ParseUtil.escapeAngleBrackets;
import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.Status;
import com.cattong.entity.User;

class TwitterStatusAdaptor {

	/**
	 * 从JSON字符串创建Status对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象
	 * @throws LibException
	 */
	public static Status createStatus(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createStatus(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON字符串创建Status列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象列表
	 * @throws LibException
	 */
	public static List<Status> createStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			List<Status> statusList = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}

			return statusList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<Status> createSearchResultList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONObject resultJson = new JSONObject(jsonString);
			JSONArray jsonArray = resultJson.getJSONArray("results");
			int size = jsonArray.length();
			List<Status> statusList = new ArrayList<Status>(size);
			User user = null;
			Status status = null;
			JSONObject json = null;
			for (int i = 0; i < size; i++) {
				json = jsonArray.getJSONObject(i);
				user = new User();
				user.setUserId(getRawString("from_user_id_str", json));
				user.setScreenName(getRawString("from_user", json));
				user.setName(user.getScreenName());
				user.setProfileImageUrl(getRawString("profile_image_url", json));
				status = new Status();
				status.setStatusId(getRawString("id_str", json));
				status.setText(getRawString("text", json));
				status.setCreatedAt(getDate("created_at", json, "EEE, d MMM yyyy HH:mm:ss Z"));
				status.setSource(ParseUtil.getUnescapedString("source", json));
				//status.setInReplyToUserId(getRawString("to_user_id_str", json));
				status.setRetweetCount(0);
				status.setCommentCount(0);
				status.setUser(user);
				status.setServiceProvider(ServiceProvider.Twitter);
				if (json.has("entities")) {
					JSONObject entiiesJson = json.getJSONObject("entities");
					if (entiiesJson.has("media")) {
						JSONArray mediaArray = entiiesJson.getJSONArray("media");
						String imageUrl = getRawString("media_url_https", mediaArray.getJSONObject(0));
						status.setMiddlePictureUrl(imageUrl);
						status.setThumbnailPictureUrl(imageUrl + ":thumb");
						status.setOriginalPictureUrl(imageUrl + ":large");
					}
				}
				statusList.add(status);
			}

			return statusList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON对象创建Status对象，包级别访问控制
	 *
	 * @param json
	 *            JSON对象
	 * @return Status对象
	 * @throws LibException
	 */
	static Status createStatus(JSONObject json) throws LibException {
		try {
			Status status = new Status();
			status.setStatusId(getRawString("id", json));
			status.setText(escapeAngleBrackets(getRawString("text", json)));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("created_at", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			//status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			//status.setInReplyToScreenName(getRawString("in_reply_to_screen_name", json));
			status.setRetweetCount(getInt("retweet_count", json));
			status.setCommentCount(0);
			if (json.has("user")) {
				status.setUser(TwitterUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (json.has("retweeted_status")) {
				status.setRetweetedStatus(createStatus(json.getJSONObject("retweeted_status")));
				//官方RT形成的Status，转发后的微博文本变成  (RT 原作者ScreenName: 原微博内容)，
				//这里为了在列表显示时和国内几大微博统一处理，将转发后形成的新微博Text修改为RT
				status.setText("RT");
			}
			if (json.has("entities")) {
				JSONObject entiiesJson = json.getJSONObject("entities");
				if (entiiesJson.has("media")) {
					JSONArray mediaArray = entiiesJson.getJSONArray("media");
					String imageUrl = ParseUtil.getRawString("media_url_https", mediaArray.getJSONObject(0));
					/**
					 * We support different sizes: thumb, small, medium and large. 
					 * The media_url defaults to medium but you can retrieve the media in different sizes 
					 * by appending a colon + the size key 
					 * (for example: http://p.twimg.com/ARACoSZs_QA8BDB.jpg:thumb)
					 */
					status.setMiddlePictureUrl(imageUrl);
					status.setThumbnailPictureUrl(imageUrl + ":thumb");
					status.setOriginalPictureUrl(imageUrl + ":large");
				}
			}
			status.setServiceProvider(ServiceProvider.Twitter);
			return status;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}
}
