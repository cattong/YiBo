package com.cattong.weibo.impl.sohu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.weibo.entity.ResponseCount;

/**
 * SohuCountAdaptor
 *
 * @version
 * @author cattong.com
 * @time 2010-8-31 上午01:43:59
 */
class SohuCountAdaptor {

	public static ResponseCount createCount(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createCount(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	public static List<ResponseCount> createCountList(String jsonString) throws LibException {
		try {
			ArrayList<ResponseCount> countList = null;
			JSONArray jsonArray = new JSONArray(jsonString);
			if (jsonArray.length() > 0) {
				int size = jsonArray.length();
				countList = new ArrayList<ResponseCount>(size);
				for (int i = 0; i < size; i++) {
					countList.add(createCount(jsonArray.getJSONObject(i)));
				}
			}
			return countList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	static ResponseCount createCount(JSONObject json) throws LibException {
		try {
			ResponseCount count = new ResponseCount();
			count.setCommentCount(json.getInt("comments_count"));
			count.setRetweetCount(json.getInt("transmit_count"));
			count.setStatusId(json.getString("id"));
			return count;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}

	}

}
