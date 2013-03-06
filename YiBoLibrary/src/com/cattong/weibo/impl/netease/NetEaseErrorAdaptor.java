package com.cattong.weibo.impl.netease;


import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;

class NetEaseErrorAdaptor {

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
			return new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, 
					e, ServiceProvider.NetEase);
		}
	}

	private static void parseStatusCode(LibRuntimeException apiException) {
		if (apiException == null ||
			apiException.getErrorCode() == LibResultCode.E_UNKNOWN_ERROR ||
			StringUtil.isEmpty(apiException.getErrorDescr())) {
			return;
		}

		String errorDesc = apiException.getErrorDescr().toLowerCase();
		int errorCode = apiException.getErrorCode();
		int errorCodePlaform = LibResultCode.E_UNKNOWN_ERROR;
		//取前三位
		int errorCodeHeaderThree = errorCode / 100;
		if (errorCodeHeaderThree == 400) {
			errorCodePlaform = LibResultCode.API_MB_PARAMS_ERROR;
		}
		if (errorCodeHeaderThree == 401) {
			if (errorCode < 40104) {
				errorCodePlaform = LibResultCode.API_MB_USER_NOT_EXIST;
			} else {
				switch (errorCode) {
				case 40104:
					errorCodePlaform = LibResultCode.OAUTH_VERSION_REJECTED;
					break;
				case 40105:
					errorCodePlaform = LibResultCode.OAUTH_PARAMETER_ABSENT;
					break;
				case 40106:
					errorCodePlaform = LibResultCode.OAUTH_PARAMETER_REJECTED;
					break;
				case 40107:
					errorCodePlaform = LibResultCode.OAUTH_TIMESTAMP_REFUSED;
					break;
				case 40108:
					errorCodePlaform = LibResultCode.OAUTH_NONCE_USED;
					break;
				case 40109:
					errorCodePlaform = LibResultCode.OAUTH_SIGNATURE_METHOD_REJECTED;
					break;
				case 40110:
					errorCodePlaform = LibResultCode.OAUTH_SIGNATURE_INVALID;
					break;
				case 40111:
					errorCodePlaform = LibResultCode.OAUTH_CONSUMER_KEY_UNKNOWN;
					break;
				case 40112:
					errorCodePlaform = LibResultCode.OAUTH_CONSUMER_KEY_REJECTED;
					break;
				case 40113:
					errorCodePlaform = LibResultCode.OAUTH_CONSUMER_KEY_REFUSED;
					break;
				case 40114:
					errorCodePlaform = LibResultCode.OAUTH_TOKEN_USED;
					break;
				case 40115:
					errorCodePlaform = LibResultCode.OAUTH_TOKEN_EXPIRED;
					break;
				case 40116:
					errorCodePlaform = LibResultCode.OAUTH_TOKEN_REVOKED;
					break;
				case 40117:
					errorCodePlaform = LibResultCode.OAUTH_TOKEN_REJECTED;
					break;
				case 40118:
					errorCodePlaform = LibResultCode.OAUTH_ADDITIONAL_AUTHORIZATION_REQUIRED;
					break;
				case 40119:
					errorCodePlaform = LibResultCode.OAUTH_PERMISSION_UNKNOWN;
					break;
				case 40120:
					errorCodePlaform = LibResultCode.OAUTH_PERMISSION_DENIED;
					break;
				case 40121:
					errorCodePlaform = LibResultCode.OAUTH_USER_REFUSED;
					break;
				default:
					errorCodePlaform = LibResultCode.E_UNKNOWN_ERROR;
				}
			}
		}
		if (errorCodeHeaderThree == 403) {
			switch (errorCode) {
			case 40303:
				errorCodePlaform = LibResultCode.API_MB_IP_LIMITED;
				break;
			case 40306:
				errorCodePlaform = LibResultCode.API_MB_CONTENT_ILLEGAL;
				break;
			case 40307:
				errorCodePlaform = LibResultCode.API_MB_PERMISSION_ACCESS_LIMITED;
				break;
			case 40308:
			case 40312:
				errorCodePlaform = LibResultCode.API_MB_INVOKE_RATE_TOO_QUICK;
				break;
			case 40314:
				errorCodePlaform = LibResultCode.API_MB_MESSAGE_RECEIVER_NOT_FOLLOWER;
				break;
			case 40316:
				errorCodePlaform = LibResultCode.API_MB_MESSAGE_NOT_OWNER;
				break;
			case 40319:
				errorCodePlaform = LibResultCode.API_MB_MESSAGE_LIMITED;
				break;
			case 40320:
				errorCodePlaform = LibResultCode.API_MB_CONTENT_OVER_LENGTH;
				break;
			default:
				errorCodePlaform = LibResultCode.E_UNKNOWN_ERROR;
			}
		}
		if (errorCodeHeaderThree == 404) {
			switch (errorCode) {
			case 40401:
				errorCodePlaform = LibResultCode.API_MB_USER_NOT_EXIST;
				break;
			case 40402:
				errorCodePlaform = LibResultCode.API_MB_TWEET_NOT_EXIST;
				break;
			case 40404:
				errorCodePlaform = LibResultCode.API_MB_MESSAGE_NOT_EXIST;
				break;
			case 40407:
				errorCodePlaform = LibResultCode.API_MB_USER_NOT_EXIST;
				break;
			}
		}
		if (errorCodeHeaderThree == 500) {
			errorCodePlaform = LibResultCode.SC_INTERNAL_SERVER_ERROR;
		}
		
		apiException.setStatusCode(errorCodePlaform);
		apiException.setErrorCode(errorCode);
		apiException.setErrorDescr(errorDesc);
	}
}
