package com.cattong.weibo.impl.netease;

import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getDate;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

class NetEaseUserAdaptor {

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
			JSONArray jsonList = json.getJSONArray("users");
			long nextCursor = 0L;
			long previousCursor = 0L;
			if (!json.isNull("next_cursor")) {
				nextCursor = ParseUtil.getLong("next_cursor", json);
				previousCursor = ParseUtil.getLong("previous_cursor", json);
			}

			if (nextCursor == -1) {
				//网易的cursor分页，没有下一页时next_cursor为-1，这里做一下修改，统一为0
				nextCursor = 0;
			}

			int size = jsonList.length();
			PagableList<User> userList = new PagableList<User>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				userList.add(createUser(jsonList.getJSONObject(i)));
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
				return new PagableList<User>(0, 0, 0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<User> userList = new ArrayList<User>(size);
			for (int i = 0; i < size; i++) {
				userList.add(createUser(jsonList.getJSONObject(i)));
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
			user.setUserId(ParseUtil.getRawString("id", json));
			user.setName(getRawString("screen_name", json)); //网易的screen_name属性是个性网址
			user.setScreenName(getRawString("name", json)); //网易的name属性是用户昵称
			user.setLocation(getRawString("location", json));
			user.setDescription(getRawString("description", json));
			user.setProfileImageUrl(URLDecoder.decode(getRawString("profile_image_url", json), "UTF-8"));
			user.setVerified(getBoolean("verified", json));
			user.setFollowersCount(getInt("followers_count", json));
			user.setFriendsCount(getInt("friends_count", json));
			user.setCreatedAt(getDate(getRawString("created_at", json), "EEE MMM dd HH:mm:ss z yyyy"));
			user.setFavouritesCount(ParseUtil.getInt("favourites_count", json));
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
				int gender = getInt("gender", json);
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
			if (!json.isNull("status")) {
				JSONObject statusJSON = json.getJSONObject("status");
				user.setStatus(NetEaseStatusAdaptor.createStatus(statusJSON));
			}

			user.setServiceProvider(ServiceProvider.NetEase);
			return user;
		} catch (JSONException jsone) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		} catch (UnsupportedEncodingException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
}
