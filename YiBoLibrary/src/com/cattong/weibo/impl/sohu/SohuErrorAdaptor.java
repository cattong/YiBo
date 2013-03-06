package com.cattong.weibo.impl.sohu;


import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;

class SohuErrorAdaptor {
	private static final String API_REPEATED_UPDATE = "Same status is not acceptable within 5 minutes.";
    private static final String API_USER_IS_FOLLOWING = "already on your list";
    private static final String API_USER_SCREEN_NAME_EXIST = "update failed";
    private static final String API_AUTH_AUTHENTICATION_FAIL = "This method requires authentication";

	public static LibRuntimeException parseError(String errorString) {
		try {
			JSONObject json = new JSONObject(errorString);
			//例如{"code":501,
			//     "error":"update failed.",
			//     "request":"/account/update_profile.json"}
			int errorCode = json.getInt("code");
			String errorDesc = json.getString("error");
			String requestPath = json.getString("request");
			LibRuntimeException apiException = new LibRuntimeException(
				errorCode, requestPath, errorDesc, ServiceProvider.Sohu);
			parseStatusCode(apiException);
			return apiException;
		} catch (JSONException e) {
			return new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Sohu);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null
			|| apiException.getErrorCode() == LibResultCode.E_UNKNOWN_ERROR
			|| StringUtil.isEmpty(apiException.getErrorDescr())) {
			return;
		}

		String errorDesc = apiException.getErrorDescr();
		int errorCode = apiException.getErrorCode();
		apiException.setStatusCode(errorCode);
		switch(errorCode) {
		case 400:
			if (errorDesc.indexOf(API_USER_IS_FOLLOWING) != -1) {
				apiException.setStatusCode(LibResultCode.API_MB_USER_IS_FOLLOWING);
			}
			break;
		case 401:
			if (errorDesc.indexOf(API_AUTH_AUTHENTICATION_FAIL) != -1) {
				apiException.setStatusCode(LibResultCode.SC_UNAUTHORIZED);
			}
			break;
		case 501:
			if (errorDesc.indexOf(API_USER_SCREEN_NAME_EXIST) != -1) {
				apiException.setStatusCode(LibResultCode.API_MB_USER_SCREEN_NAME_EXIST);
			}
			break;
		default:
			break;
		}

		if (API_REPEATED_UPDATE.equals(errorDesc)) {
			apiException.setStatusCode(LibResultCode.API_MB_TWEET_REPEAT);
		} else if (SohuRateLimitStatusAdaptor.ERROR.equals(errorDesc)) {
			apiException.setErrorCode(LibResultCode.API_MB_RATE_LIMITED);
		}
	}
}
