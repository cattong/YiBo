package com.cattong.weibo.impl.fanfou;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ParseUtil;
import com.cattong.weibo.entity.DirectMessage;

/**
 * @author cattong.com
 * @version 创建时间：2011-7-29 下午2:29:27
 **/
class FanfouDirectMessageAdaptor {

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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
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
			dmsg.setId(ParseUtil.getRawString("id", json));
			dmsg.setText(ParseUtil.getUnescapedString("text", json));
			dmsg.setSenderId(ParseUtil.getRawString("sender_id", json));
			dmsg.setRecipientId(ParseUtil.getRawString("recipient_id", json));
			dmsg.setCreatedAt(ParseUtil.getDate("created_at", json));
			dmsg.setSenderScreenName(ParseUtil.getRawString("sender_screen_name", json));
			dmsg.setRecipientScreenName(ParseUtil.getRawString("recipient_screen_name", json));
			if (!json.isNull("sender")) {
				dmsg.setSender(FanfouUserAdaptor.createUser(json.getJSONObject("sender")));
			}
			if (!json.isNull("recipient")) {
				dmsg.setRecipient(FanfouUserAdaptor.createUser(json.getJSONObject("recipient")));
			}
			dmsg.setServiceProvider(ServiceProvider.Fanfou);
			return dmsg;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(LibResultCode.DATE_PARSE_ERROR);
		}
	}

}
