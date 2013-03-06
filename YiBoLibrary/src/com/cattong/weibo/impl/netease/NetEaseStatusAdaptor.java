package com.cattong.weibo.impl.netease;

import static com.cattong.commons.util.ParseUtil.escapeAngleBrackets;
import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Location;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.Emotions;

class NetEaseStatusAdaptor {

	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("http://126\\.fm/[a-zA-Z0-9]+");

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
	public static List<Status> createStatusList(String jsonString) throws LibException {
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
	public static List<Status> createStatusListFromTopRetweets(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<Status> statusList = new ArrayList<Status>(size);
			JSONObject json = null;
			for (int i = 0; i < size; i++) {
				json = jsonList.getJSONObject(i);
				statusList.add(createStatus(json.getJSONObject("status")));
			}
			return statusList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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

			String id = ParseUtil.getRawString("cursor_id", json);
			if (StringUtil.isEmpty(id)) {
				id = ParseUtil.getRawString("id", json);
			}
			status.setStatusId(id);

			status.setServiceProvider(ServiceProvider.NetEase);
			status.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
					escapeAngleBrackets(getRawString("text", json))));
			status.setSource(getRawString("source", json));
			status.setCreatedAt(getDate("created_at", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			//status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			//status.setInReplyToScreenName(getRawString("in_reply_to_user_name", json));
			status.setRetweetCount(getInt("retweet_count", json));
			status.setCommentCount(getInt("comments_count", json));

			if (!json.isNull("user")) {
				status.setUser(NetEaseUserAdaptor.createUser(json.getJSONObject("user")));
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

			if (StringUtil.isNotEmpty(status.getInReplyToStatusId())) {
				Status retweet = new Status();
				User user = new User();
				/**
				 * 获取Timeline接口返回的报文里，既有root开头的字段，又有in_reply开头的字段，
				 * 这里取原始微博（root开头的字段）。
				 */
				retweet.setStatusId(getRawString("root_in_reply_to_status_id", json));
				retweet.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase,
						getRawString("root_in_reply_to_status_text", json)));
				/**
				 * 单次转发时，root_in_reply_to_user_id和in_reply_to_user_id一样，都是原微博作者id;
				 * 多次转发时，root_in_reply_to_user_id是原微博作者，这里都设成原微博作者id
				 */
				user.setUserId(getRawString("root_in_reply_to_user_id", json));
				user.setScreenName(getRawString("root_in_reply_to_user_name", json));
				user.setName(getRawString("root_in_reply_to_screen_name", json));

				retweet.setServiceProvider(ServiceProvider.NetEase);
				user.setServiceProvider(ServiceProvider.NetEase);

				retweet.setUser(user);

				extractImageUrl(retweet);
				status.setRetweetedStatus(retweet);
			}

			extractImageUrl(status);

			return status;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}

	/**
	 * 解析出微博内容中的图片
	 *
	 * @param status
	 */
	public static void extractImageUrl(Status status) {
		if (status == null || StringUtil.isEmpty(status.getText())) {
			return;
		}

		Matcher matcher = IMAGE_URL_PATTERN.matcher(status.getText());
		String imageUrl = null;
		StringBuffer normalized = new StringBuffer();
		while (matcher.find()) {
			imageUrl = matcher.group();
			matcher.appendReplacement(normalized, "");
			status.setThumbnailPictureUrl(imageUrl);
			status.setMiddlePictureUrl(imageUrl);
			status.setOriginalPictureUrl(imageUrl);
		}
		matcher.appendTail(normalized);
		status.setText(normalized.length() > 0 ? normalized.toString() : "分享图片");
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
		List<Status> statuses = null;
		if (StringUtil.isEmpty(jsonString)) {
			return statuses;
		}
		if ("[]".equals(jsonString)||"{}".equals(jsonString)) {
			statuses = new ArrayList<Status>(0);
			return statuses;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			if (!jsonObject.isNull("results")) {
				JSONArray jsonList = jsonObject.getJSONArray("results");
				int size = jsonList.length();
				statuses = new ArrayList<Status>(size);
				for (int i = 0; i < size; i++) {
					JSONObject json = jsonList.getJSONObject(i);

					Status status = new Status();

					status.setServiceProvider(ServiceProvider.NetEase);
					status.setStatusId(getRawString("id", json));
					status.setText((Emotions.normalizeEmotion(
							ServiceProvider.NetEase, getRawString("text", json))));
					status.setSource(getRawString("source", json));
					status.setCreatedAt(getDate("created_at", json));

					User user = new User();
					user.setServiceProvider(ServiceProvider.NetEase);
					user.setUserId(getRawString("from_user_id",json));
					user.setScreenName(getRawString("from_user_name", json));
					user.setName(getRawString("from_user", json));
					user.setProfileImageUrl(getRawString("profile_image_url", json));
					status.setUser(user);
					NetEaseStatusAdaptor.extractImageUrl(status);

					if (!json.isNull("to_status_id")) {
						Status retweet = new Status();

						retweet.setServiceProvider(ServiceProvider.NetEase);
						retweet.setStatusId(getRawString("to_status_id", json));
						String text = getRawString("to_status_text", json);
						retweet.setText(Emotions.normalizeEmotion(ServiceProvider.NetEase, text));

						User rtUser = new User();
						rtUser.setServiceProvider(ServiceProvider.NetEase);
						rtUser.setUserId(getRawString("to_user_id", json));
						rtUser.setScreenName(getRawString("to_user_name", json));
						rtUser.setName(getRawString("to_user", json));

						retweet.setUser(rtUser);
						NetEaseStatusAdaptor.extractImageUrl(retweet);
						status.setRetweetedStatus(retweet);
					}

					statuses.add(status);
				}
			}

		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}

		return statuses;
	}
}
