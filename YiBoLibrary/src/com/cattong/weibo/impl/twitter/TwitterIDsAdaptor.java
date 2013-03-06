package com.cattong.weibo.impl.twitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.PagableList;

/**
 * SinaIDsAdaptor
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午03:29:23
 */
class TwitterIDsAdaptor {
	/**
	 * 从JSON字符串中解析出IDs
	 *
	 * @param jsonString
	 * @return
	 * @throws LibException
	 */
	public static List<String> createIDs(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray idsJsonArray = json.getJSONArray("ids");
			int size = idsJsonArray.length();
			long nextCursor = json.getLong("next_cursor");
			long previousCursor = json.getLong("previous_cursor");
			PagableList<String> idsList = new PagableList<String>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				idsList.add(idsJsonArray.getString(i));
			}
			return idsList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<String> createIdsList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<String>(0);
			}

			JSONArray idsJsonArray = new JSONArray(jsonString);
			int size = idsJsonArray.length();
			List<String> ids = new ArrayList<String>(size);
			for (int i = 0; i < size; i++) {
				ids.add(idsJsonArray.getString(i));
			}
			return ids;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

}
