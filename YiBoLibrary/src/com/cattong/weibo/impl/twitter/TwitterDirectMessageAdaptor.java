package com.cattong.weibo.impl.twitter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.weibo.entity.DirectMessage;

/**
 * TwitterDirectMessageAdaptor
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午03:56:24
 */
class TwitterDirectMessageAdaptor {

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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
	public static List<DirectMessage> createDirectMessageList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<DirectMessage>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			List<DirectMessage> msgs = new ArrayList<DirectMessage>(size);
			for (int i = 0; i < size; i++) {
				msgs.add(createDirectMessage(jsonList.getJSONObject(i)));
			}
			return msgs;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
			dmsg.setText(ParseUtil.getUnescapedString("text", json));
			if (!json.isNull("sender")) {
				dmsg.setSender(TwitterUserAdaptor.createUser(json.getJSONObject("sender")));
			}
			if (!json.isNull("recipient")) {
				dmsg.setRecipient(TwitterUserAdaptor.createUser(json.getJSONObject("recipient")));
			}
			dmsg.setServiceProvider(ServiceProvider.Twitter);
			return dmsg;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR, e);
		}
	}

}
