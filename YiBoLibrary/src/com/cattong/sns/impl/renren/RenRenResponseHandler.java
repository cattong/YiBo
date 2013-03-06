package com.cattong.sns.impl.renren;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

class RenRenResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		Logger.debug("RenRenResponseHandler : {}", responseString);

		if (responseString != null
			&& responseString.contains("error_code")
			&& responseString.startsWith("{")) {
			try {
				JSONObject json = new JSONObject(responseString);
				if (json.has("error_code")) {
					// 明确是异常响应，而不是包含了error_code的文本
					int errorCode = json.getInt("error_code");
					String errorDesc = json.getString("error_msg");
					String requestPath = "";
					if (json.has("request_args")) {
						JSONArray jsonArray = json.getJSONArray("request_args");
						JSONObject jsonTmp = null;
						int size = jsonArray.length();
						for (int i = 0; i < size; i++) {
							jsonTmp = jsonArray.getJSONObject(i);
							if ("method".equals(jsonTmp.getString("key"))) {
								requestPath = jsonTmp.getString("value");
								break;
							}
						}
					}
					throw new LibRuntimeException(
						errorCode, requestPath,	errorDesc, ServiceProvider.RenRen);
				}
			} catch (JSONException e) {
				throw new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, ServiceProvider.RenRen);
			}
		}

		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}

		return responseString;
	}
}
