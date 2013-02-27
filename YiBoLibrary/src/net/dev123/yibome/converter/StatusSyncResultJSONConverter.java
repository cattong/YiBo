package net.dev123.yibome.converter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.StatusSyncResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-9-23 下午5:54:17
 **/
public class StatusSyncResultJSONConverter {

	public static List<StatusSyncResult> createStatusSyncResultList(String jsonString)
		throws LibException {
		try {
			if ("[]".equals(jsonString) || "{}".equals(jsonString)) {
				return new ArrayList<StatusSyncResult>(0);
			}
			JSONArray jsonArray = new JSONArray(jsonString);
			int size = jsonArray.length();
			ArrayList<StatusSyncResult> statusSyncResultList = 
				new ArrayList<StatusSyncResult>(size);
			for (int i = 0; i < size; i++) {
				statusSyncResultList.add(
					createStatusSyncResult(jsonArray.getJSONObject(i)));
			}
			return statusSyncResultList;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	public static StatusSyncResult createStatusSyncResult(JSONObject obj)
			throws LibException {
		try {
			StatusSyncResult result = new StatusSyncResult();
			if (!obj.isNull("user_id")) {
				result.setUserId(obj.getString("user_id"));
			}
			if (!obj.isNull("service_provider")) {
				result.setServiceProviderNo(obj.getInt("service_provider"));
			}
			if (!obj.isNull("is_success")) {
				result.setSuccess("true".equalsIgnoreCase(obj.getString("is_success")));
			}
			if (!obj.isNull("error_code")) {
				result.setErrorCode(obj.getString("error_code"));
			}
			if (!obj.isNull("error_desc")) {
				result.setErrorDesc(obj.getString("error_desc"));
			}
			return result;
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
}
