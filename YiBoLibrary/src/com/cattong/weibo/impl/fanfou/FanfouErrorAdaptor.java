package com.cattong.weibo.impl.fanfou;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cattong.com
 * @version Jul 19, 2011 10:29:05 PM
 * 
 */
class FanfouErrorAdaptor {
	private static final Map<Integer, String> ERROR_MAP = new HashMap<Integer, String>();
	
//	public static LibRuntimeException parseError(JSONObject json) {
//		try {
////			JSONObject json = new JSONObject(errorString);
//			int errorCode = json.getInt("error_code");
//			String errorDesc = json.getString("error");
//			String[] errorDetails = errorDesc.split(":");
//			if(errorDetails.length == 3){
//				//例如：{"request":"/statuses/update.json",
//				//       "error_code":"400",
//				//       "error":"40025:Error: repeated weibo text!"
//				//      }
//
//				errorCode = Integer.valueOf(errorDetails[0]);
//				errorDesc = ERROR_MAP.get(errorCode);
//				if (errorDesc == null) {
//					errorDesc = errorDetails[2].trim();
//				}
//			}
//			String requestPath = json.getString("request");
//			LibRuntimeException apiException = new LibRuntimeException(
//				errorCode, requestPath,
//				errorDesc, ServiceProvider.Sina
//			);
////			parseStatusCode(apiException);
//			return apiException;
//		} catch (JSONException e) {
//			return new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.Sina);
//		}
//	}
}
