package com.cattong.weibo.impl.tencent;

import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;
import com.cattong.entity.User;
import com.cattong.weibo.entity.Group;


class TencentGroupAdaptor {

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
	public static PagableList<Group> createPagableGroupList(
			String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<Group>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("info");
			long nextCursor = 0L;
			long previousCursor = 0L;
			
			int size = jsonList.length();
			PagableList<Group> groupList = new PagableList<Group>(size,
					previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				groupList.add(createGroup(jsonList.getJSONObject(i)));
			}
			return groupList;
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	public static ArrayList<User> createGroupMemberList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<User>(0);
			}

			JSONArray jsonList = new JSONArray(jsonString);
			
			int size = jsonList.length();
			ArrayList<User> userList = new ArrayList<User>(size);
			for (int i = 0; i < size; i++) {
				User user = TencentUserAdaptor.createUser(jsonList.getJSONObject(i));
				userList.add(user);
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
	static Group createGroup(JSONObject json) throws LibException {
		Group group = new Group();
		group.setId(getRawString("listid", json));
		group.setName(getRawString("name", json));
		group.setFullName(getRawString("name", json));
		group.setSlug(getRawString("slug", json));
		group.setDescription(getRawString("description", json));
		group.setSubscriberCount(getInt("fansnums", json));
		group.setMemberCount(getInt("membernums", json));
		group.setUri(getRawString("uri", json));
		group.setPublic("0".equals(getRawString("access", json)));
		//userList.setFollowing(getBoolean("following", json));

		return group;
	}
}
