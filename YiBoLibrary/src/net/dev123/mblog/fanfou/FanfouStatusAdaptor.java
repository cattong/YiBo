package net.dev123.mblog.fanfou;

import static net.dev123.commons.util.ParseUtil.escapeAngleBrackets;
import static net.dev123.commons.util.ParseUtil.getBoolean;
import static net.dev123.commons.util.ParseUtil.getDate;
import static net.dev123.commons.util.ParseUtil.getRawString;

import java.text.ParseException;
import java.util.ArrayList;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-7-28 上午10:30:04
 **/
public class FanfouStatusAdaptor {

	/**
	 * 从JSON字符串创建Status对象
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象
	 * @throws LibException
	 */
	public static Status createStatus(String jsonString) throws LibException {
		try {
			JSONObject json = new JSONObject(jsonString);
			return createStatus(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON字符串创建Status列表
	 *
	 * @param jsonString
	 *            JSON字符串
	 * @return Status对象列表
	 * @throws LibException
	 */
	public static ArrayList<Status> createStatusList(String jsonString) throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<Status>(0);
			}
			JSONArray jsonList = new JSONArray(jsonString);
			int size = jsonList.length();
			ArrayList<Status> statusList = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				statusList.add(createStatus(jsonList.getJSONObject(i)));
			}
			return statusList;

		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON对象创建Status对象，包级别访问控制
	 *
	 * @param json
	 *            JSON对象
	 * @return Status对象
	 * @throws LibException
	 */
	static Status createStatus(JSONObject json) throws LibException {
		try {
			Status status = new Status();
			status.setCreatedAt(getDate("created_at", json));
			status.setId(getRawString("id", json));
			String textWithHtmlTag = escapeAngleBrackets(getRawString("text", json));
			String textWithoutHtmlTag = textWithHtmlTag.replaceAll("&lt;b&gt;([^&lt;/b&gt;]*)&lt;/b&gt;", "$1");
			status.setText(textWithoutHtmlTag);
			status.setSource(getRawString("source", json));
			status.setTruncated(getBoolean("truncated", json));
			status.setInReplyToStatusId(getRawString("in_reply_to_status_id", json));
			status.setInReplyToUserId(getRawString("in_reply_to_user_id", json));
			status.setFavorited(getBoolean("favorited", json));
			status.setInReplyToScreenName(getRawString("in_reply_to_screen_name", json));
			if (!json.isNull("photo")) {
				JSONObject photoJson = new JSONObject(getRawString("photo", json));
				// 饭否图片大小顺序： imageurl<thumburl<largeurl
				status.setThumbnailPicture(getRawString("imageurl", photoJson));
				// 饭否的中图太小，这里直接设置为大图
				status.setMiddlePicture(getRawString("largeurl", photoJson));
				status.setOriginalPicture(getRawString("largeurl", photoJson));
				// 截掉微博内容尾巴的图片链接
//				String text = status.getText();
//				int index = text.lastIndexOf("http");
//				if (index != -1) {
//				    status.setText(text.substring(0, index));
//				}
			}
			if (!json.isNull("user")) {
				status.setUser(FanfouUserAdaptor.createUser(json.getJSONObject("user")));
			}
			if (!json.isNull("retweeted_status")) {
				status.setRetweetedStatus(createStatus(json.getJSONObject("retweeted_status")));
			}
			status.setServiceProvider(ServiceProvider.Fanfou);
			return status;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}
}
