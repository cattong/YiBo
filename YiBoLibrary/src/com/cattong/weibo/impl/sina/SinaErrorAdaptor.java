package com.cattong.weibo.impl.sina;


import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;

class SinaErrorAdaptor {
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
			return new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Sina);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null 
			|| apiException.getErrorCode() == LibResultCode.E_UNKNOWN_ERROR 
		    || StringUtil.isEmpty(apiException.getErrorDescr())) {
			return;
		}

		apiException.setStatusCode(apiException.getErrorCode());
		String errorDesc = apiException.getErrorDescr();
		switch (apiException.getErrorCode()) {
		case LibResultCode.API_MB_INTERNAL_ERROR:
		    if (API_USER_SCREEN_NAME_EXIST.equals(errorDesc)) {
		    	apiException.setStatusCode(LibResultCode.API_MB_USER_SCREEN_NAME_EXIST);
		    }
			break;
		default:
			
		}
		
		if (SinaRateLimitStatusAdaptor.ERROR.equals(errorDesc)) {
			apiException.setErrorCode(LibResultCode.API_MB_RATE_LIMITED);
		}
	}
}
