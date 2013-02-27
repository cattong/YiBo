package net.dev123.mblog.netease;

import java.text.ParseException;
import java.util.ArrayList;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.Emotions;
import net.dev123.mblog.entity.DirectMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * NetEaseDirectMessageAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:56:24
 */
public class NetEaseDirectMessageAdaptor {

	/**
	 * 从JSON字符串创建DirectMessage对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return DirectMessage对象
	 * @throws LibException
	 */
	public static DirectMessage createDirectMessage(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createDirectMessage(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON字符串创建DirectMessage对象列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return JSON对象列表
	 * @throws LibException
	 */
	public static ArrayList<DirectMessage> createDirectMessageList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<DirectMessage>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<DirectMessage> msgs = new ArrayList<DirectMessage>(size);
			for (int i = 0; i < size; i++) {
				msgs.add(createDirectMessage(jsonList.getJSONObject(i)));
			}
			return msgs;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON对象创建DirectMessage对象，包级别访问控制
	 *
	 * @param json
	 *            JSON对象
	 * @return DirectMessage对象
	 * @throws LibException
	 */
	static DirectMessage createDirectMessage(JSONObject json) throws LibException {
		try {
			DirectMessage dmsg = new DirectMessage();
			dmsg.setCreatedAt(ParseUtil.getDate("created_at", json));
			dmsg.setId(ParseUtil.getRawString("id", json));
			dmsg.setRecipientId(ParseUtil.getRawString("recipient_id", json));
			dmsg.setRecipientScreenName(ParseUtil.getRawString("recipient_screen_name", json));
			dmsg.setSenderId(ParseUtil.getRawString("sender_id", json));
			dmsg.setSenderScreenName(ParseUtil.getRawString("sender_screen_name", json));
			dmsg.setText(Emotions.normalizeEmotion(
					ServiceProvider.NetEase, ParseUtil.getUnescapedString("text", json)));
			if (!json.isNull("sender")) {
				dmsg.setSender(NetEaseUserAdaptor.createUser(json.getJSONObject("sender")));
				dmsg.setSenderScreenName(dmsg.getSender().getScreenName());
			}
			if (!json.isNull("recipient")) {
				dmsg.setRecipient(NetEaseUserAdaptor.createUser(json.getJSONObject("recipient")));
				dmsg.setRecipientScreenName(dmsg.getRecipient().getScreenName());
			}
			dmsg.setServiceProvider(ServiceProvider.NetEase);
			return dmsg;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}

}
