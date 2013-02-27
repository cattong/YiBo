package net.dev123.mblog.sohu;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.IDs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SohuIDsAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:29:23
 */
public class SohuIDsAdaptor {
	/**
	 * 从JSON字符串中解析出IDs
	 *
	 * @param jsonString
	 * @return
	 * @throws LibException
	 */
	public static IDs createIDs(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			IDs ids = new IDs();
			JSONArray idsJsonArray = json.getJSONArray("ids");
			int size = idsJsonArray.length();
			int[] idsArray = new int[size];
			for (int i = 0; i < size; i++) {
				idsArray[i] = idsJsonArray.getInt(i);
			}
			ids.setIds(idsArray);
			ids.setNextCursor(json.getLong("next_cursor"));
			ids.setPreviousCursor(json.getLong("previous_cursor"));
			return ids;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

}
