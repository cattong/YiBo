package net.dev123.mblog.netease;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibRuntimeException;

import org.json.JSONException;
import org.json.JSONObject;

public class NetEaseErrorAdaptor {

	public static LibRuntimeException parseError(String errorString) {
		try {
			JSONObject json = new JSONObject(errorString);
			int messageCode = json.getInt("message_code");
			// errorCode去messageCode后5位数
			int errorCode = ((messageCode % 1000000) % 100000);
			String errorDesc = json.getString("error");
			String requestPath = json.getString("request");
			LibRuntimeException apiException = new LibRuntimeException(errorCode, 
					requestPath, errorDesc, ServiceProvider.NetEase);
			parseStatusCode(apiException);
			return apiException;
		} catch (JSONException e) {
			return new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, 
					e, ServiceProvider.NetEase);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null ||
			apiException.getErrorCode() == ExceptionCode.UNKNOWN_EXCEPTION ||
			StringUtil.isEmpty(apiException.getErrorDescription())) {
			return;
		}

		String errorDesc = apiException.getErrorDescription().toLowerCase();
		int errorCode = apiException.getErrorCode();
		int errorCodePlaform = ExceptionCode.UNKNOWN_EXCEPTION;
		//取前三位
		int errorCodeHeaderThree = errorCode / 100;
		if (errorCodeHeaderThree == 400) {
			errorCodePlaform = ExceptionCode.MicroBlog.API_PARAMS_ERROR;
		}
		if (errorCodeHeaderThree == 401) {
			if (errorCode < 40104) {
				errorCodePlaform = ExceptionCode.MicroBlog.API_USER_NOT_EXIST;
			} else {
				switch (errorCode) {
				case 40104:
					errorCodePlaform = ExceptionCode.OAUTH_VERSION_REJECTED;
					break;
				case 40105:
					errorCodePlaform = ExceptionCode.OAUTH_PARAMETER_ABSENT;
					break;
				case 40106:
					errorCodePlaform = ExceptionCode.OAUTH_PARAMETER_REJECTED;
					break;
				case 40107:
					errorCodePlaform = ExceptionCode.OAUTH_TIMESTAMP_REFUSED;
					break;
				case 40108:
					errorCodePlaform = ExceptionCode.OAUTH_NONCE_USED;
					break;
				case 40109:
					errorCodePlaform = ExceptionCode.OAUTH_SIGNATURE_METHOD_REJECTED;
					break;
				case 40110:
					errorCodePlaform = ExceptionCode.OAUTH_SIGNATURE_INVALID;
					break;
				case 40111:
					errorCodePlaform = ExceptionCode.OAUTH_CONSUMER_KEY_UNKNOWN;
					break;
				case 40112:
					errorCodePlaform = ExceptionCode.OAUTH_CONSUMER_KEY_REJECTED;
					break;
				case 40113:
					errorCodePlaform = ExceptionCode.OAUTH_CONSUMER_KEY_REFUSED;
					break;
				case 40114:
					errorCodePlaform = ExceptionCode.OAUTH_TOKEN_USED;
					break;
				case 40115:
					errorCodePlaform = ExceptionCode.OAUTH_TOKEN_EXPIRED;
					break;
				case 40116:
					errorCodePlaform = ExceptionCode.OAUTH_TOKEN_REVOKED;
					break;
				case 40117:
					errorCodePlaform = ExceptionCode.OAUTH_TOKEN_REJECTED;
					break;
				case 40118:
					errorCodePlaform = ExceptionCode.OAUTH_ADDITIONAL_AUTHORIZATION_REQUIRED;
					break;
				case 40119:
					errorCodePlaform = ExceptionCode.OAUTH_PERMISSION_UNKNOWN;
					break;
				case 40120:
					errorCodePlaform = ExceptionCode.OAUTH_PERMISSION_DENIED;
					break;
				case 40121:
					errorCodePlaform = ExceptionCode.OAUTH_USER_REFUSED;
					break;
				default:
					errorCodePlaform = ExceptionCode.UNKNOWN_EXCEPTION;
				}
			}
		}
		if (errorCodeHeaderThree == 403) {
			switch (errorCode) {
			case 40303:
				errorCodePlaform = ExceptionCode.MicroBlog.API_IP_LIMITED;
				break;
			case 40306:
				errorCodePlaform = ExceptionCode.MicroBlog.API_CONTENT_ILLEGAL;
				break;
			case 40307:
				errorCodePlaform = ExceptionCode.MicroBlog.API_PERMISSION_ACCESS_LIMITED;
				break;
			case 40308:
			case 40312:
				errorCodePlaform = ExceptionCode.MicroBlog.API_INVOKE_RATE_TOO_QUICK;
				break;
			case 40314:
				errorCodePlaform = ExceptionCode.MicroBlog.API_MESSAGE_RECEIVER_NOT_FOLLOWER;
				break;
			case 40316:
				errorCodePlaform = ExceptionCode.MicroBlog.API_MESSAGE_NOT_OWNER;
				break;
			case 40319:
				errorCodePlaform = ExceptionCode.MicroBlog.API_MESSAGE_LIMITED;
				break;
			case 40320:
				errorCodePlaform = ExceptionCode.MicroBlog.API_CONTENT_OVER_LENGTH;
				break;
			default:
				errorCodePlaform = ExceptionCode.UNKNOWN_EXCEPTION;
			}
		}
		if (errorCodeHeaderThree == 404) {
			switch (errorCode) {
			case 40401:
				errorCodePlaform = ExceptionCode.MicroBlog.API_USER_NOT_EXIST;
				break;
			case 40402:
				errorCodePlaform = ExceptionCode.MicroBlog.API_TWEET_NOT_EXIST;
				break;
			case 40404:
				errorCodePlaform = ExceptionCode.MicroBlog.API_MESSAGE_NOT_EXIST;
				break;
			case 40407:
				errorCodePlaform = ExceptionCode.MicroBlog.API_USER_NOT_EXIST;
				break;
			}
		}
		if (errorCodeHeaderThree == 500) {
			errorCodePlaform = ExceptionCode.SC_INTERNAL_SERVER_ERROR;
		}
		
		apiException.setStatusCode(errorCodePlaform);
		apiException.setErrorCode(errorCode);
		apiException.setErrorDescription(errorDesc);
	}
}
