package net.dev123.mblog.tencent;

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
 * TencentResponseHandler 腾讯Http响应处理类,包级别访问权限控制
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午02:32:40
 */
class TencentResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(TencentResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String jsonStirng = (entity == null ? null : EntityUtils.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("TencentResponseHandler : {}", jsonStirng);
		}

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
			throw new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.Tencent);
		}
		return dataString;
	}
}
