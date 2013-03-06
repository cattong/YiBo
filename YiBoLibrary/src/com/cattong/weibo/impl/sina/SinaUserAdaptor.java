package com.cattong.weibo.impl.sina;

import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.entity.Gender;
import com.cattong.entity.Relationship;
import com.cattong.entity.User;


class SinaUserAdaptor {

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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			JSONArray jsonArray = json.getJSONArray("users");
			long nextCursor = 0L;
			long previousCursor = 0L;
			if (json.has("next_cursor")) {
				nextCursor = ParseUtil.getLong("next_cursor", json);
				previousCursor = ParseUtil.getLong("previous_cursor", json);
			}
			int size = jsonArray.length();
			PagableList<User> userList = new PagableList<User>(size, previousCursor, nextCursor);
			JSONObject jsonObject = null;
			for (int i = 0; i < size; i++) {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject == null || jsonObject.toString().equals("{}")) {
					continue;
				}
				userList.add(createUser(jsonObject));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			
			JSONArray jsonArray = null;
			if (jsonString.startsWith("[")) {
				jsonArray = new JSONArray(jsonString);
			} else {
				JSONObject json = new JSONObject(jsonString);
				jsonArray = json.getJSONArray("users");
			}
			
			int size = jsonArray.length();
			ArrayList<User> userList = new ArrayList<User>(size);
			JSONObject jsonObject = null;
			for (int i = 0; i < size; i++) {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject == null || jsonObject.toString().equals("{}")) {
					continue;
				}
				userList.add(createUser(jsonObject));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			user.setUserId(getRawString("id", json));
			user.setName(getRawString("domain", json)); //新浪不支持name属性，用个性网址填充
			user.setScreenName(getRawString("screen_name", json));
			user.setLocation(getRawString("location", json));
			user.setDescription(getRawString("description", json));
			user.setProfileImageUrl(getRawString("profile_image_url", json));
			user.setVerified(getBoolean("verified", json));
			user.setVerifyInfo(getRawString("verified_reason", json));
			user.setFollowersCount(getInt("followers_count", json));
			user.setFriendsCount(getInt("friends_count", json));
			user.setCreatedAt(getDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy"));
			user.setFavouritesCount(json.getInt("favourites_count"));
			user.setStatusesCount(getInt("statuses_count", json));
			if (json.has("following")) {
				Relationship relationship = new Relationship();
				relationship.setTargetUserId(user.getUserId());
				relationship.setSourceFollowingTarget(getBoolean("following", json));
				relationship.setSourceFollowedByTarget(getBoolean("follow_me", json));
				user.setRelationship(relationship);
			}
			user.setGender(Gender.Unkown);
			if (!json.isNull("gender")) {
				String gender = getRawString("gender", json);
				if ("m".equals(gender)) {
					user.setGender(Gender.Male);
				} else if ("f".equals(gender)) {
					user.setGender(Gender.Female);
				} else if ("n".equals(gender)) {
					user.setGender(Gender.Unkown);
				}
			}
			if (!json.isNull("status")) {
				JSONObject statusJSON = json.getJSONObject("status");
				user.setStatus(SinaStatusAdaptor.createStatus(statusJSON));
			}
			user.setServiceProvider(ServiceProvider.Sina);
			return user;
		} catch (JSONException jsone) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}
}
