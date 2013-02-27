package net.dev123.mblog.fanfou;

import java.io.IOException;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Weiping Ye
 * @version Jul 19, 2011 10:39:17 PM
 *
 */
class FanfouResponseHandler implements ResponseHandler<String> {
	private static final Logger logger = LoggerFactory.getLogger(FanfouResponseHandler.class.getSimpleName());

	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("FanfouResponseHandler : {}", responseString);
		}

		if (statusLine.getStatusCode() != 200) {
			//TODO: 貌似饭否没有错误信息说明，如果有的话，这里要补上
//			JSONObject json = null;
//			try {
//				json = new JSONObject(responseString);
//				LibRuntimeException apiException =  FanfouErrorAdaptor.parseError(json);
//				throw apiException;
//			} catch (JSONException e) {
//				throw new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, ServiceProvider.Fanfou);
//			}
			throw new LibRuntimeException(ExceptionCode.UNKNOWN_EXCEPTION, ServiceProvider.Fanfou);
		}
		return responseString;
	}

}
