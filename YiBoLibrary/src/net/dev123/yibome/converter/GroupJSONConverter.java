package net.dev123.yibome.converter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.ParseUtil;
import net.dev123.yibome.entity.LocalGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupJSONConverter {

	public static JSONArray toJSONArray(List<LocalGroup> groups) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (LocalGroup group : groups) {
			jsonArray.put(toJSON(group));
		}
		return jsonArray;
	}

	public static List<LocalGroup> toGroupList(JSONArray jsonArray) throws JSONException {
		List<LocalGroup> groupList = new ArrayList<LocalGroup>();
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groupList.add(toGroup(jsonObject));
		}
		return groupList;
	}

	public static JSONObject toJSON(LocalGroup group) throws JSONException {
		if (group == null) {
			return null;
		}

		JSONObject json = new JSONObject();
		json.put("group_id", group.getRemoteGroupId());
		json.put("name", group.getName());
		json.put("state", group.getState());
		return json;
	}

	public static LocalGroup toGroup(JSONObject json) throws JSONException {
		if (json == null) {
			return null;
		}

		LocalGroup group = new LocalGroup();
		group.setGroupId(ParseUtil.getLong("group_id", json));
		group.setName(ParseUtil.getRawString("name", json));
		return group;
	}

}
