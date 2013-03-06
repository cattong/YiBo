package com.cattong.weibo.impl.netease;

import static com.cattong.commons.util.ParseUtil.getBoolean;
import static com.cattong.commons.util.ParseUtil.getRawString;
import static com.cattong.commons.util.ParseUtil.getUnescapedString;

import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.entity.Relationship;

/**
 * NetEaseRelationshipAdaptor
 *
 * @version
 * @author cattong.com
 * @time 
 */
class NetEaseRelationshipAdaptor {

	/**
	 * 从JSON字符串创建Relationship对象
	 *
	 * @param responseString
	 *            JSON字符串
	 * @return Relationship对象
	 * @throws LibException
	 */
	public static Relationship createRelationship(String responseString) throws LibException {
		try {
			JSONObject json = new JSONObject(responseString);
			return createRelationship(json);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}

	/**
	 * 从JSON对象创建Relationship对象，包级别访问控制
	 *
	 * @param json
	 *            JSON对象
	 * @return Relationship对象
	 * @throws LibException
	 */
	static Relationship createRelationship(JSONObject json) throws LibException {
		try {
			Relationship relationship = new Relationship();
			JSONObject sourceJson = json.getJSONObject("source");
			JSONObject targetJson = json.getJSONObject("target");
			relationship.setSourceUserId(getRawString("id", sourceJson));
			relationship.setTargetUserId(getRawString("id", targetJson));
			relationship.setSourceScreenName(getUnescapedString("screen_name", sourceJson));
			relationship.setTargetScreenName(getUnescapedString("screen_name", targetJson));
			relationship.setSourceFollowingTarget(getBoolean("following", sourceJson));
			relationship.setSourceFollowedByTarget(getBoolean("followed_by", sourceJson));
			return relationship;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
}
