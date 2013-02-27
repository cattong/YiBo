package net.dev123.mblog.sina;

import static net.dev123.commons.util.ParseUtil.escapeAngleBrackets;
import static net.dev123.commons.util.ParseUtil.getBoolean;
import static net.dev123.commons.util.ParseUtil.getDate;
import static net.dev123.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;

import net.dev123.commons.ServiceProvider;
import net.dev123.entity.GeoLocation;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SinaStatusAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午01:06:38
 */
public class SinaStatusAdaptor {

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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<Status> statusList = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}
			return statusList;

		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			status.setId(getRawString("id", json));
			String text = escapeAngleBrackets(getRawString("text", json));
			status.setText(SinaEmotions.normalizeEmotion(ServiceProvider.Sina, text));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("created_at", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			status.setInReplyToScreenName(getRawString("in_reply_to_screen_name", json));
			if (!json.isNull("user")) {
				status.setUser(SinaUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("retweeted_status")) {
				status.setRetweetedStatus(createStatus(json.getJSONObject("retweeted_status")));
			}
			if (!json.isNull("thumbnail_pic")) {
				status.setThumbnailPicture(getRawString("thumbnail_pic", json));
			}
			if (!json.isNull("bmiddle_pic")) {
				status.setMiddlePicture(getRawString("bmiddle_pic", json));
			}
			if (!json.isNull("original_pic")) {
				status.setOriginalPicture(getRawString("original_pic", json));
			}
			if (!json.isNull("geo")) {
				JSONObject geo = json.getJSONObject("geo");
				//String type = geo.getString("type"); //Point ...
				JSONArray coordinates = geo.getJSONArray("coordinates");
				double latitude = coordinates.getDouble(0);
				double longitude = coordinates.getDouble(1);
				GeoLocation location = new GeoLocation(latitude, longitude);
				status.setGeoLocation(location);				
			}
			status.setServiceProvider(ServiceProvider.Sina);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}
}
