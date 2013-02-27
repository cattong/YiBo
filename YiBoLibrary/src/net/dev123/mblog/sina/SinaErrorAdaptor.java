package net.dev123.mblog.sina;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.ExceptionCode.MicroBlog;
import net.dev123.exception.LibRuntimeException;

import org.json.JSONException;
import org.json.JSONObject;

public class SinaErrorAdaptor {
	private static final String API_USER_SCREEN_NAME_EXIST = "请换一个昵称";
	public static LibRuntimeException parseError(String errorString) {
		try {
			JSONObject json = new JSONObject(errorString);
			int errorCode = json.getInt("error_code");
			String errorDesc = json.getString("error");
			String[] errorDetails = errorDesc.split(":");
			if (errorDetails.length == 3) {
				//例如：{"request":"/statuses/update.json",
				//       "error_code":"400",
				//       "error":"40025:Error: repeated weibo text!"
				//      }

				errorCode = Integer.valueOf(errorDetails[0]);
				errorDesc = errorDetails[2].trim();
			} else if (errorDetails.length == 2) {
				errorCode = Integer.valueOf(errorDetails[0]);
				errorDesc = errorDetails[1].trim();
			}
			String requestPath = json.getString("request");
			LibRuntimeException apiException = new LibRuntimeException(
				errorCode, requestPath,	errorDesc, ServiceProvider.Sina);
			parseStatusCode(apiException);
			return apiException;
		} catch (JSONException e) {
			return new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.Sina);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null 
			|| apiException.getErrorCode() == ExceptionCode.UNKNOWN_EXCEPTION 
		    || StringUtil.isEmpty(apiException.getErrorDescription())) {
			return;
		}

		apiException.setStatusCode(apiException.getErrorCode());
		String errorDesc = apiException.getErrorDescription();
		switch (apiException.getErrorCode()) {
		case ExceptionCode.MicroBlog.API_INTERNAL_ERROR:
		    if (API_USER_SCREEN_NAME_EXIST.equals(errorDesc)) {
		    	apiException.setStatusCode(MicroBlog.API_USER_SCREEN_NAME_EXIST);
		    }
			break;
		default:
			
		}
		
		if (SinaRateLimitStatusAdaptor.ERROR.equals(errorDesc)) {
			apiException.setErrorCode(ExceptionCode.MicroBlog.API_RATE_LIMITED);
		}
	}
}
