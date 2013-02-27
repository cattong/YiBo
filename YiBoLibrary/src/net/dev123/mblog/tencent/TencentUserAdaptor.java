package net.dev123.mblog.tencent;

import static net.dev123.commons.util.ParseUtil.getInt;
import static net.dev123.commons.util.ParseUtil.getLong;
import static net.dev123.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;
import java.util.Date;

import net.dev123.commons.PagableList;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.Gender;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.mblog.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TencentUserAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:58:58
 */
public class TencentUserAdaptor {

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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			user.setId(getRawString("name", json));
			user.setName(getRawString("name", json));
			user.setScreenName(getRawString("nick", json));
			user.setLocation(getRawString("location", json));
			user.setDescription(getRawString("introduction", json));
			user.setContributorsEnabled(false);

			String head = getRawString("head", json);
			if (StringUtil.isNotEmpty(head)) {
				user.setProfileImageUrl(head + "/50");
			}

			user.setUrl(null);
			user.setProtected(false);
			user.setGeoEnabled(false);
			user.setVerified(getInt("isvip", json) == 1);
			user.setFollowersCount(getInt("fansnum", json));
			user.setFriendsCount(getInt("idolnum", json));
			user.setCreatedAt(null);
			user.setFavouritesCount(0);
			user.setStatusesCount(getInt("tweetnum", json));
			user.setFollowing(getInt("ismyidol", json) == 1);
			user.setFollowedBy(getInt("ismyfans", json) == 1);
			user.setBlocking(getInt("ismyblack", json) == 1);
			user.setGender(Gender.UNKNOW);
			if (!json.isNull("sex")) {
				int gender = getInt("sex", json);
				switch (gender) {
				case 0:
					user.setGender(Gender.UNKNOW);
					break;
				case 1:
					user.setGender(Gender.MALE);
					break;
				case 2:
					user.setGender(Gender.FEMALE);
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
				status.setId(getRawString("id", tweet));
				status.setServiceProvider(ServiceProvider.Tencent);
				user.setStatus(status);
			}

			user.setServiceProvider(ServiceProvider.Tencent);
			return user;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}
}
