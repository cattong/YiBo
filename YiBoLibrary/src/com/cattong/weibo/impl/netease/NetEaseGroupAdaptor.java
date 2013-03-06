package com.cattong.weibo.impl.netease;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.entity.Group;

/**
 * @author cattong.com
 * @version 创建时间：2011-12-13 下午3:16:36
 **/
class NetEaseGroupAdaptor {

	public static ArrayList<Group> createGroupList(String jsonString)
			throws LibException {
		if ("[]".equals(jsonString)
			|| "{}".equals(jsonString)
	        || StringUtil.isEmpty(jsonString)) {
			return new ArrayList<Group>(0);
		}
		
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			ArrayList<Group> groupList = new ArrayList<Group>(size);
			for(int i = 0; i < size; i++) {
				groupList.add(createGroup(jsonArray.getJSONObject(i)));
			}
			
			return groupList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
	
	static Group createGroup(JSONObject jsonObj) {
		Group group = new Group();
		group.setName(ParseUtil.getRawString("name", jsonObj));
		group.setId(ParseUtil.getRawString("id", jsonObj));
		
		return group;
	}
}
