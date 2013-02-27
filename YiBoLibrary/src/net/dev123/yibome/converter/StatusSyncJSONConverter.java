package net.dev123.yibome.converter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.Account;
import net.dev123.yibome.entity.StatusSyncResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-9-23 下午5:54:17
 **/
public class StatusSyncJSONConverter {

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
	
	public static JSONArray createAccountInfos(List<? extends Account> accountList) throws LibException {
		JSONArray jsonArray = new JSONArray();
		if (accountList == null || accountList.size() == 0) {
			return jsonArray;
		}

		for (Account account : accountList) {
			try {
				jsonArray.put(createAccountInfo(account));
			} catch (JSONException e) {
				if (Constants.DEBUG) e.printStackTrace();
				throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
			}
		}
		return jsonArray;
	}
	
	private static JSONObject createAccountInfo(Account account) throws JSONException {
		if (account == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("user_id", account.getUserId());
		json.put("service_provider", account.getServiceProviderNo());
		json.put("app_key", account.getAppKey());
		
		return json;
	}
}
