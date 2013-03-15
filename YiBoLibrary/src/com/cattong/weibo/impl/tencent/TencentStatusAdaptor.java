package com.cattong.weibo.impl.tencent;

import static com.cattong.commons.util.ParseUtil.escapeAngleBrackets;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getLong;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.Emotions;


class TencentStatusAdaptor {

	private static final Pattern HREF_PATTERN = Pattern.compile("<a href=\"http://url\\.cn/[a-zA-Z0-9]+\" target=\"_blank\">(http://url\\.cn/[a-zA-Z0-9]+)</a>");

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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static PagableList<Status> createPagableStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Status>(0, -1, 0L);
			}

			JSONObject json = new JSONObject(jsonString);
			if (!json.has("info")) {
				return new PagableList<Status>(0, -1, 0L);
			}

			JSONArray jsonList = json.getJSONArray("info");
			int hasNext = ParseUtil.getInt("hasnext", json);
			long nextCursor = 1L; // 下一页
			long previousCursor = 2L; // 上一页
			if (hasNext == 1) {
				//timeline接口，1表示数据已拉取完毕
				nextCursor = 0L;
			}

			Map<String, String> nickToNameMap = new HashMap<String, String>();
			JSONObject usernames = json.getJSONObject("user");
			Iterator<?> iterator = usernames.keys();
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				nickToNameMap.put(key, getRawString(key, usernames));
			}

			int size = jsonList.length();
			PagableList<Status> statusList = new PagableList<Status>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				Status status = createStatus(jsonList.getJSONObject(i));
				//attachScreenName(status, nickToNameMap);
				statusList.add(status);
			}
			return statusList;

		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static PagableList<Status> createStatusSearchResult(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Status>(0, -1, 0L);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("info");
			int hasNext = ParseUtil.getInt("hasnext", json);
			long nextCursor = 1L; // 下一页
			long previousCursor = 2L; // 上一页
			if (hasNext == 2) {
				//1表示数据还有下一页，可以下翻，
				//2表示数据已经到最后一页，可以上翻
				//3表示上下都可以翻
				//数据已拉取完毕
				nextCursor = 0L;
			}

			Map<String, String> nickToNameMap = new HashMap<String, String>();
			JSONObject usernames = json.getJSONObject("user");
			Iterator<?> iterator = usernames.keys();
			while (iterator.hasNext()){
				String key = iterator.next().toString();
				nickToNameMap.put(key, getRawString(key, usernames));
			}

			int size = jsonList.length();
			PagableList<Status> statusList = new PagableList<Status>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				Status status = createStatus(jsonList.getJSONObject(i));
				//attachScreenName(status, nickToNameMap);
				statusList.add(status);
			}

			return statusList;

		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			String text = getRawString("text", json);
			//因腾讯的微博内容中的链接地址是HTML代码，此处去掉HTML代码
			text = text.replaceAll(HREF_PATTERN.pattern(), "$1");
			text = Emotions.normalizeEmotion(ServiceProvider.Tencent, text);
			status.setText(escapeAngleBrackets(text));
			status.setSource(getRawString("from", json));
			status.setCreatedAt(new Date(getLong("timestamp", json) * 1000L));
			status.setTruncated(false);
			status.setInReplyToStatusId(null);
			status.setFavorited(false);
			status.setRetweetCount(getInt("count", json));
			status.setCommentCount(getInt("mcount", json));
			if (!json.isNull("source")) { //源微博
				status.setRetweetedStatus(createStatus(json.getJSONObject("source")));
			}

			User user = new User();
			user.setServiceProvider(ServiceProvider.Tencent);
			user.setName(getRawString("name", json));
			user.setUserId(user.getName());
			user.setScreenName(getRawString("nick", json));
			user.setLocation(getRawString("location", json));

			user.setVerified(getInt("isvip", json) == 1);
			String head = getRawString("head", json);
			if (StringUtil.isNotEmpty(head)) {
				user.setProfileImageUrl(head + "/50");
			}
			status.setUser(user);

			if (!json.isNull("image")) {
				String imageString = getRawString("image", json);
				String[] images = imageString.replace("[\"", "").replace("\"]", "").replace("\\/", "/").replace("\"", "").split(",");
				String image = images[0];
				if (StringUtil.isNotEmpty(image)) {
					status.setThumbnailPictureUrl(image + "/160");
					status.setMiddlePictureUrl(image + "/460");
					status.setOriginalPictureUrl(image + "/2000");
				}
			}

			if (StringUtil.isEmpty(status.getText())) {
				//若微博内容为空
				if (status.getRetweetedStatus() != null) {
					//转发微博的时候，则自动补上“转发微博”
					status.setText("转发微博");
				} else if (status.getThumbnailPictureUrl() != null) {
					status.setText("分享图片");
				}
			}

			status.setServiceProvider(ServiceProvider.Tencent);
			return status;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	private static void attachScreenName(Status status, Map<String, String> nameToNickMap){
		if (status == null || nameToNickMap == null || nameToNickMap.size() == 0) {
			return;
		}
		String text = status.getText();
		Matcher matcher = NAME_PATTERN.matcher(text);
		StringBuffer attached = new StringBuffer();
		while (matcher.find()) {
			String nick = nameToNickMap.get(matcher.group(1));
			if (nick != null) {
				matcher.appendReplacement(attached, String.format(NAME_LINK_FORMAT, matcher.group(), nick));
			}
		}
		matcher.appendTail(attached);
		status.setText(attached.toString());

		attachScreenName(status.getRetweetedStatus(), nameToNickMap);
	}

	private static final Pattern NAME_PATTERN = Pattern.compile("@([a-zA-Z][a-zA-Z0-9_-]{1,19})");
	private static final String NAME_LINK_FORMAT = "<a href=\"%1$s\">%2$s</a>";
}
