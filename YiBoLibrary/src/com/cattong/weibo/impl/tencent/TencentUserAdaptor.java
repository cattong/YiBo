package com.cattong.weibo.impl.tencent;

import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getLong;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Gender;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.User;


class TencentUserAdaptor {

	/**
	 * 从JSON字符串创建User对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return User对象
	 * @throws LibException
	 */
	public static User createUser(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createUser(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON字符串创建User对象列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return User对象列表
	 * @throws LibException
	 */
	public static PagableList<User> createPagableUserList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<User>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			int hasNext = ParseUtil.getInt("hasNext", json);
			long nextCursor = 1L; // 下一页
			long previousCursor = 2L; // 上一页
			if (hasNext == 1) {
				//数据已拉取完毕
				nextCursor = 0L;
			}

			JSONArray jsonList = null;
			if (json.has("info") && !"null".equals(json.getString("info"))) {
			    jsonList = json.getJSONArray("info");
			} else {
				jsonList = new JSONArray();
			}
			int size = jsonList.length();
			PagableList<User> userList = new PagableList<User>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				userList.add(createUser(jsonList.getJSONObject(i)));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON字符串创建User对象列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return User对象列表
	 * @throws LibException
	 */
	public static ArrayList<User> createUserList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<User>(0);
			}

			JSONObject json = new JSONObject(jsonString);
			if (!json.has("info")) {
				return new ArrayList<User>(0);
			}

			JSONArray jsonList = json.getJSONArray("info");
			int hasNext = ParseUtil.getInt("hasnext", json);
			long nextCursor = 1L; // 下一页
			long previousCursor = 2L; // 上一页
			if (hasNext == 1) {
				//数据已拉取完毕
				nextCursor = 0;
			}
			int size = jsonList.length();
			PagableList<User> userList = new PagableList<User>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				userList.add(createUser(jsonList.getJSONObject(i)));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	/**
	 * 从JSON对象创建User对象，包级别访问权限控制
	 *
	 * @param json
	 *            JSON对象
	 * @return User对象
	 * @throws LibException
	 */
	static User createUser(JSONObject json) throws LibException {
		try {
			User user = new User();
			user.setUserId(getRawString("name", json));
			user.setName(getRawString("name", json));
			user.setScreenName(getRawString("nick", json));
			user.setLocation(getRawString("location", json));
			user.setDescription(getRawString("introduction", json));
			
			String head = getRawString("head", json);
			if (StringUtil.isNotEmpty(head)) {
				user.setProfileImageUrl(head + "/50");
			}

			user.setVerified(getInt("isvip", json) == 1);
			user.setFollowersCount(getInt("fansnum", json));
			user.setFriendsCount(getInt("idolnum", json));
			user.setCreatedAt(null);
			user.setFavouritesCount(0);
			user.setStatusesCount(getInt("tweetnum", json));
			if (json.has("ismyfans")) {
				Relationship relationship = new Relationship();
				relationship.setTargetUserId(user.getUserId());
				relationship.setSourceFollowingTarget(getInt("ismyfans", json) == 1);
				relationship.setSourceFollowedByTarget(getInt("ismyidol", json) == 1);
				user.setRelationship(relationship);
			}
			user.setGender(Gender.Unkown);
			if (!json.isNull("sex")) {
				int gender = getInt("sex", json);
				switch (gender) {
				case 0:
					user.setGender(Gender.Unkown);
					break;
				case 1:
					user.setGender(Gender.Male);
					break;
				case 2:
					user.setGender(Gender.Female);
					break;
				default:
					break;
				}
			}

			if (!json.isNull("tweet")) {
				JSONArray tweets = json.getJSONArray("tweet");
				JSONObject tweet = tweets.getJSONObject(0);
				Status status = new Status();
				status.setText(getRawString("text", tweet));
				status.setSource(getRawString("from", tweet));
				status.setCreatedAt(new Date(getLong("timestamp", tweet) * 1000L));
				status.setStatusId(getRawString("id", tweet));
				status.setServiceProvider(ServiceProvider.Tencent);
				user.setStatus(status);
			}

			user.setServiceProvider(ServiceProvider.Tencent);
			return user;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}
}
