package net.dev123.sns.renren;

import java.io.IOException;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RenRenResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(RenRenResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("RenRenResponseHandler : {}", responseString);
		}

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
				throw new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.RenRen);
			}
		}

		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}

		return responseString;
	}
}
