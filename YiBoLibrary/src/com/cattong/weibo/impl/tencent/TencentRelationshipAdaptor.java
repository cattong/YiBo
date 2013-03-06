package com.cattong.weibo.impl.tencent;

import static com.cattong.commons.util.ParseUtil.getBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.entity.Relationship;

/**
 * TencentRelationshipAdaptor
 *
 * @version
 * @author cattong.com
 */
class TencentRelationshipAdaptor {

	/**
	 * 从JSON字符串创建Relationship对象
	 *
	 * @param responseString
	 *            JSON字符串
	 * @return Relationship对象
	 * @throws LibException
	 */
	public static Relationship createRelationship(String responseString, String identyName) throws LibException {
		try {
			JSONObject json = new JSONObject(responseString);
			return createRelationship(json, identyName);
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
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
	static Relationship createRelationship(JSONObject json, String identyName) throws LibException {
		try {
			Relationship relationship = new Relationship();
			JSONObject sourceJson = json.getJSONObject(identyName);
			relationship.setSourceFollowingTarget(getBoolean("isfans", sourceJson));
			relationship.setSourceFollowedByTarget(getBoolean("isidol", sourceJson));
			return relationship;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR, e);
		}
	}
}
