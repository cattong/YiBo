package com.cattong.weibo.impl.sina;

import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getInt;
import static com.cattong.commons.util.ParseUtil.getRawString;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.entity.User;
import com.cattong.weibo.entity.Group;

/**
 * SinaUserAdaptor
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午03:58:58
 */
class SinaGroupAdaptor {

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


	public static List<Group> createGroupList(String jsonString)
			throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Group>(0);
			}
			
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonList = json.getJSONArray("lists");
			int size = jsonList.length();
			List<Group> groupList = new ArrayList<Group>(size);
			for (int i = 0; i < size; i++) {
				groupList.add(createGroup(jsonList.getJSONObject(i)));
			}
			return groupList;
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
	static Group createGroup(JSONObject json) throws LibException {
		Group group = new Group();
		group.setId(getRawString("idstr", json));
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
		return group;
	}
}
