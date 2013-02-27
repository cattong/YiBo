package net.dev123.mblog.netease;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.UnreadCount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SinaCountAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-31 上午01:43:59
 */
public class NetEaseCountAdaptor {

	public static UnreadCount createRemindCount(String jsonString) throws LibException {
		UnreadCount count = null;
		try {
			JSONObject json = new JSONObject(jsonString);
			count = new UnreadCount();
			count.setStatusCount(json.getInt("timelineCount"));
			count.setMetionCount(json.getInt("replyCount"));
			count.setCommentCount(json.getInt("commentOfMeCount"));
			count.setDireceMessageCount(json.getInt("directMessageCount"));
			count.setFollowerCount(json.getInt("followedCount"));
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
