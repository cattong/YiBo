package net.dev123.yibome.converter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.commons.util.RsaUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.entity.ConfigApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-31 下午3:01:33
 **/
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
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
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}
}
