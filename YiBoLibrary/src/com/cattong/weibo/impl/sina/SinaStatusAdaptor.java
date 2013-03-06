package com.cattong.weibo.impl.sina;

import static com.cattong.commons.util.ParseUtil.escapeAngleBrackets;
import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.Location;
import com.cattong.entity.Status;

/**
 * SinaStatusAdaptor
 *
 * @version
 * @author 
 */
class SinaStatusAdaptor {

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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
	public static ArrayList<Status> createStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonList = null;
			if (jsonString.startsWith("[")) {
				jsonList = new JSONArray(jsonString);
			} else {
				JSONObject json = new JSONObject(jsonString);
				if (json.isNull("statuses")) {
					return new ArrayList<Status>(0);
				}
				jsonList = json.getJSONArray("statuses");
			}
						
			int size = jsonList.length();
			ArrayList<Status> statusList = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}
			
			return statusList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			String text = escapeAngleBrackets(getRawString("text", json));
			status.setText(SinaEmotions.normalizeEmotion(ServiceProvider.Sina, text));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("created_at", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			//status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			//status.setInReplyToScreenName(getRawString("in_reply_to_screen_name", json));
			if (!json.isNull("user")) {
				status.setUser(SinaUserAdaptor.createUser(json.getJSONObject("user")));
			}
			status.setRetweetCount(ParseUtil.getInt("reposts_count", json));
			status.setCommentCount(ParseUtil.getInt("comments_count", json));
			if (!json.isNull("retweeted_status")) {
				status.setRetweetedStatus(createStatus(json.getJSONObject("retweeted_status")));
			}
			if (!json.isNull("thumbnail_pic")) {
				status.setThumbnailPictureUrl(getRawString("thumbnail_pic", json));
			}
			if (!json.isNull("bmiddle_pic")) {
				status.setMiddlePictureUrl(getRawString("bmiddle_pic", json));
			}
			if (!json.isNull("original_pic")) {
				status.setOriginalPictureUrl(getRawString("original_pic", json));
			}
			if (!json.isNull("reposts_count")) {
				status.setRetweetCount(ParseUtil.getInt("reposts_count", json));
			}
			if (!json.isNull("comments_count")) {
				status.setCommentCount(ParseUtil.getInt("comments_count", json));
			}
			if (!json.isNull("geo")) {
				JSONObject geo = json.getJSONObject("geo");
				//String type = geo.getString("type"); //Point ...
				JSONArray coordinates = geo.getJSONArray("coordinates");
				double latitude = coordinates.getDouble(0);
				double longitude = coordinates.getDouble(1);
				Location location = new Location(latitude, longitude);
				status.setLocation(location);				
			}
			status.setServiceProvider(ServiceProvider.Sina);
			return status;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
}
