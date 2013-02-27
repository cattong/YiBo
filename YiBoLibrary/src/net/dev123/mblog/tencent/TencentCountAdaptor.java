package net.dev123.mblog.tencent;

import java.util.ArrayList;
import java.util.List;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.ResponseCount;
import net.dev123.mblog.entity.UnreadCount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TencentCountAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午01:43:59
 */
public class TencentCountAdaptor {

	public static ResponseCount createCount(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createCount(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	public static List<ResponseCount> createCountList(String jsonString) throws LibException {
		try {
			List<ResponseCount> countList = null;
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
	}

	static ResponseCount createCount(JSONObject json) throws LibException {
		try {
			ResponseCount count = new ResponseCount();
			count.setCommentsCount(json.getInt("comments"));
			count.setRetweetCount(json.getInt("rt"));
			count.setStatusId(json.getString("id"));
			return count;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}

	}

	public static UnreadCount createRemindCount(String jsonString) throws LibException {
		UnreadCount count = null;
		try {
			JSONObject json = new JSONObject(jsonString);
			count = new UnreadCount();
			count.setStatusCount(json.getInt("home"));
			count.setMetionCount(json.getInt("mentions"));
			count.setCommentCount(0);
			count.setDireceMessageCount(json.getInt("private"));
			count.setFollowerCount(json.getInt("fans"));
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}
		return count;
	}

	public static boolean createResetRemindCount(String jsonString) throws LibException {
		boolean isSuccess = false;

		try {
			JSONObject json = new JSONObject(jsonString);
			isSuccess = json.getBoolean("result");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
		}

		return isSuccess;
	}
}
