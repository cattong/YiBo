package net.dev123.yibome;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.NullAuthorization;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.util.ParseUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibome.api.AccountService;
import net.dev123.yibome.api.ConfigAppService;
import net.dev123.yibome.api.EmotionService;
import net.dev123.yibome.api.GroupService;
import net.dev123.yibome.api.PointService;
import net.dev123.yibome.api.StatusService;
import net.dev123.yibome.api.UserService;
import net.dev123.yibome.conf.YiBoMeApiConfig;
import net.dev123.yibome.conf.YiBoMeApiConfigImpl;
import net.dev123.yibome.converter.PassportJSONConverter;
import net.dev123.yibome.entity.Passport;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class YiBoMe implements AccountService, GroupService, StatusService,
	UserService, EmotionService, PointService, ConfigAppService {
	protected final YiBoMeApiConfig conf;
	protected Authorization auth;
	protected ResponseHandler<String> responseHandler;

	public YiBoMe(Authorization auth) {
		if (auth == null) {
			throw new NullPointerException("Auth for YiBoMe is null");
		}
		this.auth = auth;
		this.conf = new YiBoMeApiConfigImpl();
		this.responseHandler = new YiBoMeResponseHandler();
	}

	public Authorization getAuth() {
		return auth;
	}

	public void setAuth(Authorization auth) {
		if (auth == null) {
			throw new NullPointerException("Auth for YiBoMe is null");
		}
		this.auth = auth;
	}

	public static Passport register(String username, String password,
			String confirmPassword, String email) throws LibException {
		Authorization nullAuth = new OAuthAuthorization(null, ServiceProvider.YiBoMe);
		YiBoMeApiConfig apiConf = new YiBoMeApiConfigImpl();
		ResponseHandler<String> responseHandler = new YiBoMeResponseHandler();

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, apiConf.getRegisterURL(), nullAuth);
		httpRequestMessage.addParameter("username", username);
		httpRequestMessage.addParameter("password", password);
		httpRequestMessage.addParameter("confirm_password", confirmPassword);
		httpRequestMessage.addParameter("email", email);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage,	responseHandler);
			JSONObject json = new JSONObject(response);
			return PassportJSONConverter.toPassport(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	public static Passport login(String username, String password) throws LibException {
		Authorization nullAuth = new OAuthAuthorization(null, ServiceProvider.YiBoMe);
		YiBoMeApiConfig apiConf = new YiBoMeApiConfigImpl();
		ResponseHandler<String> responseHandler = new YiBoMeResponseHandler();

		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, apiConf.getLoginURL(), nullAuth);
		httpRequestMessage.addParameter("username", username);
		httpRequestMessage.addParameter("password", password);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage,	responseHandler);
			JSONObject json = new JSONObject(response);
			return PassportJSONConverter.toPassport(json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		}
	}

	public static Date getTimeNow() throws LibException {
		Authorization nullAuth = new OAuthAuthorization(null, ServiceProvider.YiBoMe);
		YiBoMeApiConfig apiConf = new YiBoMeApiConfigImpl();
		ResponseHandler<String> responseHandler = new YiBoMeResponseHandler();
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.GET, apiConf.getTimeNowURL(), nullAuth);
		try {
			String response = HttpRequestHelper.execute(httpRequestMessage,	responseHandler);
			JSONObject json = new JSONObject(response);
			return ParseUtil.getDate("now", json);
		} catch (JSONException e) {
			throw new LibException(ExceptionCode.JSON_PARSE_ERROR);
		} catch (ParseException e) {
			throw new LibException(ExceptionCode.DATE_PARSE_ERROR);
		}
	}

	@Override
	public String getEmotionVersionInfo() throws LibException {
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.GET, conf.getEmotionVersionInfoURL(), auth);
		String response = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		return response;
	}
	
	public static boolean detectUrlServer() {
		boolean result = false;
		try {
			YiBoMeApiConfigImpl conf = new YiBoMeApiConfigImpl();
			HttpRequestMessage httpRequestMessage = new HttpRequestMessage(
				HttpMethod.POST, conf.getUrlServiceURL(), new NullAuthorization(ServiceProvider.None));
			httpRequestMessage.addParameter("target", net.dev123.commons.Constants.URL_SERVICE_DETECT);
			result = HttpRequestHelper.execute(httpRequestMessage, handler);
		} catch (LibException e) {}
		
		return result;
	}

	static ResponseHandler<Boolean> handler = new ResponseHandler<Boolean>() {
		@Override
		public Boolean handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			if (response.getStatusLine().getStatusCode() != 200) {
				return false;
			}
			return true;
		}
	};
}
