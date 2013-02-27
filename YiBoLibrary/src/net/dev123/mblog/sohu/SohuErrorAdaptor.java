package net.dev123.mblog.sohu;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.ExceptionCode.MicroBlog;
import net.dev123.exception.LibRuntimeException;

import org.json.JSONException;
import org.json.JSONObject;

public class SohuErrorAdaptor {
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
			return new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.Sohu);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null
			|| apiException.getErrorCode() == ExceptionCode.UNKNOWN_EXCEPTION
			|| StringUtil.isEmpty(apiException.getErrorDescription())) {
			return;
		}

		String errorDesc = apiException.getErrorDescription();
		int errorCode = apiException.getErrorCode();
		apiException.setStatusCode(errorCode);
		switch(errorCode) {
		case 400:
			if (errorDesc.indexOf(API_USER_IS_FOLLOWING) != -1) {
				apiException.setStatusCode(MicroBlog.API_USER_IS_FOLLOWING);
			}
			break;
		case 401:
			if (errorDesc.indexOf(API_AUTH_AUTHENTICATION_FAIL) != -1) {
				apiException.setStatusCode(ExceptionCode.SC_UNAUTHORIZED);
			}
			break;
		case 501:
			if (errorDesc.indexOf(API_USER_SCREEN_NAME_EXIST) != -1) {
				apiException.setStatusCode(MicroBlog.API_USER_SCREEN_NAME_EXIST);
			}
			break;
		default:
			break;
		}

		if (API_REPEATED_UPDATE.equals(errorDesc)) {
			apiException.setStatusCode(MicroBlog.API_TWEET_REPEAT);
		} else if (SohuRateLimitStatusAdaptor.ERROR.equals(errorDesc)) {
			apiException.setErrorCode(MicroBlog.API_RATE_LIMITED);
		}
	}
}
