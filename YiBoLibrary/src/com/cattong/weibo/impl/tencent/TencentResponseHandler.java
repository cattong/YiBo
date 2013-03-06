package com.cattong.weibo.impl.tencent;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

/**
 * TencentResponseHandler 腾讯Http响应处理类,包级别访问权限控制
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午02:32:40
 */
class TencentResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String jsonStirng = (entity == null ? null : EntityUtils.toString(entity));

		
		Logger.debug("TencentResponseHandler : {}", jsonStirng);

		String dataString = null;
		try {
			JSONObject json = new JSONObject(jsonStirng);
			int res = json.getInt("ret");
			int httpStatusCode = statusLine.getStatusCode();
			switch (res) {
			case 0: // 成功返回
				if (!json.isNull("data")) {
					dataString = json.getString("data");
				} else {
					dataString = "[]";
				}

				break;
			default:
				throw TencentErrorAdaptor.parseError(json);
			}

			if (httpStatusCode >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			}

		} catch (JSONException e) {
			throw new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.Tencent);
		}
		return dataString;
	}
}
