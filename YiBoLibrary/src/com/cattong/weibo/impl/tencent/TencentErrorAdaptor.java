package com.cattong.weibo.impl.tencent;


import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.ServiceProvider;

class TencentErrorAdaptor {
	public static LibRuntimeException parseError(JSONObject response) {
		try {
			int retCode = response.getInt("ret");
			int errorCode = 0;
			String errorDesc = response.getString("msg");
			if (!response.isNull("errcode")) {
				errorCode = response.getInt("errcode");
				errorCode = translateErrorCode(retCode, errorCode);
			}
			String requestPath = ""; // 腾讯接口异常未返回请求路径
			LibRuntimeException apiException = new LibRuntimeException(
				errorCode, requestPath, errorDesc, ServiceProvider.Tencent);
			return apiException;
		} catch (JSONException e) {
			return new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Tencent);
		}
	}

	private static int translateErrorCode(int retCode, int errorCode) {
		int libErrorCode = retCode;
		if (errorCode > 0) {
			if (retCode == 3) {
				// 二级错误字段【验签失败】
				switch (errorCode) {
				case 1: // 无效TOKEN,被吊销
					libErrorCode = LibResultCode.OAUTH_TOKEN_REVOKED;
					break;
				case 2: // 请求重放
					libErrorCode = LibResultCode.OAUTH_NONCE_USED;
					break;
				case 3: // access_token不存在
					libErrorCode = LibResultCode.OAUTH_TOKEN_REJECTED;
					break;
				case 4: // access_token超时
					libErrorCode = LibResultCode.OAUTH_TOKEN_EXPIRED;
					break;
				case 5: // oauth 版本不对
					libErrorCode = LibResultCode.OAUTH_VERSION_REJECTED;
					break;
				case 6: // 签名方法不对
					libErrorCode = LibResultCode.OAUTH_SIGNATURE_METHOD_REJECTED;
					break;
				case 7: // 参数错
					libErrorCode = LibResultCode.OAUTH_PARAMETER_REJECTED;
					break;
				case 9: // 验证签名失败
					libErrorCode = LibResultCode.OAUTH_SIGNATURE_INVALID;
					break;
				case 10: // 网络错误
					libErrorCode = LibResultCode.NET_ISSUE;
					break;
				case 11: // 参数长度不对
					libErrorCode = LibResultCode.OAUTH_PARAMETER_REJECTED;
					break;
				case 8:
				case 12:
				case 13:
				case 14:
				case 15: // 处理失败
					libErrorCode = LibResultCode.SC_INTERNAL_SERVER_ERROR;
					break;
				}
			} else if (retCode == 4) {
				// 二级错误字段【发表接口】
				switch (errorCode) {
				case 4: // 表示有过多脏话
					libErrorCode = LibResultCode.API_MB_CONTENT_ILLEGAL;
					break;
				case 5: // 禁止访问，如城市，uin黑名单限制等
					libErrorCode = LibResultCode.API_MB_PERMISSION_ACCESS_DENIED;
					break;
				case 6: // 删除时：该记录不存在。发表时：父节点已不存在
					libErrorCode = LibResultCode.API_MB_ITEM_NOT_EXIST;
					break;
				case 8: // 内容超过最大长度：420字节 （以进行短url处理后的长度计）
					libErrorCode = LibResultCode.API_MB_CONTENT_OVER_LENGTH;
					break;
				case 9: // 包含垃圾信息：广告，恶意链接、黑名单号码等
					libErrorCode = LibResultCode.API_MB_CONTENT_ILLEGAL;
					break;
				case 10: // 发表太快，被频率限制
					libErrorCode = LibResultCode.API_MB_INVOKE_RATE_TOO_QUICK;
					break;
				case 11: // 源消息已删除，如转播或回复时
					libErrorCode = LibResultCode.API_MB_TWEET_NOT_EXIST;
					break;
				case 12: // 源消息审核中
					libErrorCode = LibResultCode.API_MB_ITEM_REVIEWING;
					break;
				case 13: // 重复发表
					libErrorCode = LibResultCode.API_MB_TWEET_REPEAT;
					break;
				}
			}
		} else {
			switch (retCode) {
			case 1: // 参数错误
				libErrorCode = HttpStatus.SC_BAD_REQUEST;
				break;
			case 2: // 频率受限
				libErrorCode = LibResultCode.API_MB_RATE_LIMITED;
				break;
			case 3: // 鉴权失败
				libErrorCode = HttpStatus.SC_UNAUTHORIZED;
				break;
			case 4: // 服务器内部错误
				libErrorCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
				break;
			}
		}
		return libErrorCode;
	}
}
