package com.cattong.weibo.impl.fanfou;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;

/**
 * @author cattong.com
 * @version 创建时间：2011-7-29 下午4:38:25
 **/
class FanfouIDsAdaptor {
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}
}
