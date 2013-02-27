package net.dev123.mblog.fanfou;

import java.util.ArrayList;
import java.util.List;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-7-29 下午4:38:25
 **/
public class FanfouIDsAdaptor {
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}
}
