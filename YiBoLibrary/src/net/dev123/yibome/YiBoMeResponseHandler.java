package net.dev123.yibome;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * YiBoMeResponseHandler YiBoMe Http响应处理类,包级别访问权限控制
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午02:32:40
 */
class YiBoMeResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(YiBoMeResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response)
			throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils
				.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("YiBoMeResponseHandler : {}", responseString);
		}

		if (statusLine.getStatusCode() >= 300) {
			throw YiBoMeErrorAdapter.parseError(responseString);
		}
		return responseString;
	}

	static class YiBoMeErrorAdapter {
		static LibRuntimeException parseError(String responseString) {
			try {
				JSONObject json = new JSONObject(responseString);
				int code = json.getInt("error_code");
				String desc = json.getString("error_desc");
				String requestPath = json.getString("request_path");
				LibRuntimeException exception = new LibRuntimeException(code, desc, requestPath, ServiceProvider.YiBoMe);
				return exception;
			} catch (JSONException e) {
				throw new LibRuntimeException(
						ExceptionCode.JSON_PARSE_ERROR, e,
						ServiceProvider.YiBoMe);
			}
		}
	}
}
