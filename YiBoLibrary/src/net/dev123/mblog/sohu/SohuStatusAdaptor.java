package net.dev123.mblog.sohu;

import static net.dev123.commons.util.ParseUtil.escapeAngleBrackets;
import static net.dev123.commons.util.ParseUtil.getBoolean;
import static net.dev123.commons.util.ParseUtil.getDate;
import static net.dev123.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.PagableList;
import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SohuStatusAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午01:39:16
 */
public class SohuStatusAdaptor {

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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
	public static List<Status> createStatusSearchResultList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			List<Status> statusList = null;
			JSONObject jsonObject = new JSONObject(jsonString);
			if (!jsonObject.isNull("statuses")) {
				statusList = SohuStatusAdaptor.createStatusList(jsonObject.getString("statuses"));
			}
			return statusList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static PagableList<Status> createPagableStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Status>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("statuses");
			int size = jsonList.length();
			long nextCursor = json.getLong("cursor_id");
			PagableList<Status> statusList = new PagableList<Status>(size, -1, nextCursor);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}
			return statusList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			status.setText(SohuEmotions.normalizeEmotion(ServiceProvider.Sohu, text));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("created_at", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			status.setInReplyToScreenName(getRawString("in_reply_to_screen_name", json));
			if (!json.isNull("user")) {
				status.setUser(SohuUserAdaptor.createUser(json.getJSONObject("user")));
			}

			if (status.getInReplyToStatusId() != null
					&& status.getInReplyToStatusId().length() > 0) {
				Status retweet = new Status();
				retweet.setId(status.getInReplyToStatusId());
				retweet.setText(escapeAngleBrackets(getRawString("in_reply_to_status_text", json)));
				User user = new User();
				user.setId(status.getInReplyToUserId());
				user.setName(status.getInReplyToScreenName());
				user.setScreenName(status.getInReplyToScreenName());
				user.setServiceProvider(ServiceProvider.Sohu);
				retweet.setUser(user);
				retweet.setServiceProvider(ServiceProvider.Sohu);
				status.setRetweetedStatus(retweet);
			}

			if (!json.isNull("small_pic")) {
				status.setThumbnailPicture(getRawString("small_pic", json));
			}
			if (!json.isNull("middle_pic")) {
				status.setMiddlePicture(getRawString("middle_pic", json));
			}
			if (!json.isNull("original_pic")) {
				status.setOriginalPicture(getRawString("original_pic", json));
			}
			status.setServiceProvider(ServiceProvider.Sohu);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR, e);
		}
	}
}
