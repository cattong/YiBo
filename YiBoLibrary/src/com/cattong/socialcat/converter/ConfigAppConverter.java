package com.cattong.socialcat.converter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.Constants;
import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.RsaUtil;
import com.cattong.entity.ConfigApp;

public class ConfigAppConverter {

	public static List<ConfigApp> createConfigAppList(String jsonString)
		throws LibException {
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			List<ConfigApp> appList = new ArrayList<ConfigApp>();
			ConfigApp app = null;
			for(int i = 0; i < jsonArray.length(); i++) {
				app = createConfigApp(jsonArray.getJSONObject(i));
				appList.add(app);
			}
			return appList;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
	
	public static ConfigApp createConfigApp(JSONObject jsonObj) throws LibException {
		try {
			ConfigApp app = new ConfigApp();
			app.setAppId(jsonObj.getLong("app_id"));
			app.setAppName(jsonObj.getString("app_name"));
			app.setAppKey(jsonObj.getString("app_key"));
			app.setAppSecret(RsaUtil.decryptWithPublicKey(
					jsonObj.getString("app_secret"), Constants.PUBLIC_KEY));
			app.setAuthFlow(jsonObj.getInt("auth_flow"));
			app.setAuthVersion(jsonObj.getInt("auth_version"));
			app.setPassportId(jsonObj.getLong("passport_id"));
			app.setServiceProviderNo(jsonObj.getInt("service_provider_no"));
			app.setShared(jsonObj.getBoolean("is_shared"));
			app.setState(jsonObj.getInt("state"));
			
			return app;
		} catch (JSONException e) {
			throw new LibException(LibResultCode.JSON_PARSE_ERROR);
		}
	}
}
