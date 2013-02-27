package net.dev123.mblog.sina;

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
 * SinaCountAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午01:43:59
 */
public class SinaCountAdaptor {

	public static ResponseCount createCount(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createCount(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}

	}

	public static UnreadCount createRemindCount(String jsonString) throws LibException {
		UnreadCount count = null;
		try {
			JSONObject json = new JSONObject(jsonString);
			count = new UnreadCount();
			if (!json.isNull("new_status")) {
				count.setStatusCount(json.getInt("new_status"));
			}
			count.setMetionCount(json.getInt("mentions"));
			count.setCommentCount(json.getInt("comments"));
			count.setDireceMessageCount(json.getInt("dm"));
			count.setFollowerCount(json.getInt("followers"));
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
		return count;
	}

	public static boolean createResetRemindCount(String jsonString) throws LibException {
		boolean isSuccess = false;

		try {
			JSONObject json = new JSONObject(jsonString);
			isSuccess = json.getBoolean("result");
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}

		return isSuccess;
	}
}
