package net.dev123.mblog.tencent;

import static net.dev123.commons.util.ParseUtil.getRawString;

import java.util.Date;

import net.dev123.commons.PagableList;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.Emotions;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.mblog.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TencentDirectMessageAdaptor
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午03:56:24
 */
public class TencentDirectMessageAdaptor {

	/**
	 * 从JSON字符串创建DirectMessage对象列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return JSON对象列表
	 * @throws LibException
	 */
	public static PagableList<DirectMessage> createPagableDirectMessageList(String jsonString, int type) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new PagableList<DirectMessage>(0, -1, 0);
			}

			JSONObject json = new JSONObject(jsonString);
			if (!json.has("info")) {
				return new PagableList<DirectMessage>(0, -1, 0);
			}

			JSONArray jsonList = json.getJSONArray("info");
			int hasNext = ParseUtil.getInt("hasnext", json);
			long nextCursor = 1L; // 下一页
			long previousCursor = 2L; // 上一页
			if (hasNext == 1) {
				//数据已拉取完毕
				nextCursor = 0;
			}
			int size = jsonList.length();
			PagableList<DirectMessage> msgs = new PagableList<DirectMessage>(size, previousCursor, nextCursor);
			if (type == 0) {
				for (int i = 0; i < size; i++) {
					msgs.add(createSentDirectMessage(jsonList.getJSONObject(i)));
				}
			} else if (type == 1) {
				for (int i = 0; i < size; i++) {
					msgs.add(createDirectMessage(jsonList.getJSONObject(i)));
				}
			}

			return msgs;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR, e);
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
	static DirectMessage createSentDirectMessage(JSONObject json) throws LibException {
		DirectMessage dmsg = new DirectMessage();

		dmsg.setCreatedAt(new Date(ParseUtil.getLong("timestamp", json) * 1000));
		dmsg.setId(ParseUtil.getRawString("id", json));
		dmsg.setText(Emotions.normalizeEmotion(
				ServiceProvider.Tencent, ParseUtil.getUnescapedString("text", json)));

		User sender = new User();
		sender.setServiceProvider(ServiceProvider.Tencent);
		sender.setName(getRawString("name", json));
		sender.setId(sender.getName());
		sender.setScreenName(getRawString("nick", json));
		String head = getRawString("head", json);
		if (StringUtil.isNotEmpty(head)) {
			sender.setProfileImageUrl(head + "/50");
		}
		dmsg.setSender(sender);
		dmsg.setSenderId(sender.getId());
		dmsg.setSenderScreenName(sender.getScreenName());

		User recipient = new User();
		recipient.setServiceProvider(ServiceProvider.Tencent);
		recipient.setName(getRawString("toname", json));
		recipient.setId(recipient.getName());
		recipient.setScreenName(getRawString("tonick", json));
		String recipientHead = getRawString("tohead", json);
		if (StringUtil.isNotEmpty(recipientHead)) {
			recipient.setProfileImageUrl(recipientHead + "/50");
		}
		dmsg.setRecipient(recipient);
		dmsg.setRecipientId(recipient.getId());
		dmsg.setRecipientScreenName(recipient.getScreenName());

		dmsg.setServiceProvider(ServiceProvider.Tencent);

		return dmsg;
	}

	static DirectMessage createDirectMessage(JSONObject json) throws LibException {
		DirectMessage dmsg = new DirectMessage();
		dmsg.setCreatedAt(new Date(ParseUtil.getLong("timestamp", json) * 1000));
		dmsg.setId(ParseUtil.getRawString("id", json));
		dmsg.setText(Emotions.normalizeEmotion(
				ServiceProvider.Tencent, ParseUtil.getUnescapedString("text", json)));

		User sender = new User();
		sender.setServiceProvider(ServiceProvider.Tencent);
		sender.setName(getRawString("name", json));
		sender.setId(sender.getName());
		sender.setScreenName(getRawString("nick", json));
		String head = getRawString("head", json);
		if (StringUtil.isNotEmpty(head)) {
			sender.setProfileImageUrl(head + "/50");
		}
		dmsg.setSender(sender);
		dmsg.setSenderId(sender.getId());
		dmsg.setSenderScreenName(sender.getScreenName());

		dmsg.setServiceProvider(ServiceProvider.Tencent);
		return dmsg;
	}
}
