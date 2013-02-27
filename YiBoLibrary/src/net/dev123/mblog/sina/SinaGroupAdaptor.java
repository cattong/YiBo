package net.dev123.mblog.sina;

import static net.dev123.commons.util.ParseUtil.getBoolean;
import static net.dev123.commons.util.ParseUtil.getInt;
import static net.dev123.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;

import net.dev123.commons.PagableList;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.mblog.entity.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SinaUserAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:58:58
 */
public class SinaGroupAdaptor {

	/**
	 * 从JSON字符串创建User对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return User对象
	 * @throws LibException
	 */
	public static Group createGroup(String jsonString)
			throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createGroup(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
	public static PagableList<Group> createPagableGroupList(
			String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Group>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("lists");
			long nextCursor = 0L;
			long previousCursor = 0L;
			if (json.has("next_cursor")) {
				nextCursor = ParseUtil.getLong("next_cursor", json);
				previousCursor = ParseUtil.getLong("previous_cursor", json);
			}
			int size = jsonList.length();
			PagableList<Group> userList = new PagableList<Group>(size,
					previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				userList.add(createGroup(jsonList.getJSONObject(i)));
			}
			return userList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
	public static ArrayList<Group> createGroupList(String jsonString)
			throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Group>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			ArrayList<Group> userListList = new ArrayList<Group>(size);
			for (int i = 0; i < size; i++) {
				userListList.add(createGroup(jsonArray.getJSONObject(i)));
			}
			return userListList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
	static Group createGroup(JSONObject json) throws LibException {
		Group group = new Group();
		group.setId(getRawString("id", json));
		group.setName(getRawString("name", json));
		group.setFullName(getRawString("full_name", json));
		group.setSlug(getRawString("slug", json));
		group.setDescription(getRawString("description", json));
		group.setSubscriberCount(getInt("subscriber_count", json));
		group.setMemberCount(getInt("member_count", json));
		group.setUri(getRawString("uri", json));
		group.setPublic("public".equals(getRawString("mode", json)));
		group.setFollowing(getBoolean("following", json));

		try {
			if (!json.isNull("user")) {
				User user = SinaUserAdaptor.createUser(json.getJSONObject("user"));
				group.setUser(user);
			}
		} catch (JSONException jsone) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
		return group;
	}
}
