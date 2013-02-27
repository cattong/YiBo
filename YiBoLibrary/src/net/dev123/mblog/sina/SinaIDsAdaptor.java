package net.dev123.mblog.sina;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.PagableList;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SinaIDsAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:29:23
 */
public class SinaIDsAdaptor {
	/**
	 * 从JSON字符串中解析出IDs
	 *
	 * @param jsonString
	 * @return
	 * @throws LibException
	 */
	public static PagableList<String> createPagableIdsList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<String>(0, 0, 0);
			}
			JSONObject json = new JSONObject(jsonString);
			JSONArray idsJsonArray = json.getJSONArray("ids");
			int size = idsJsonArray.length();
			long nextCursor = json.getLong("next_cursor");
			long previousCursor = json.getLong("previous_cursor");
			PagableList<String> ids = new PagableList<String>(size, previousCursor, nextCursor);
			for (int i = 0; i < size; i++) {
				ids.add(idsJsonArray.getString(i));
			}
			return ids;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}
}
