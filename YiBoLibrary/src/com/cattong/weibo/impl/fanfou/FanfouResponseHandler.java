package com.cattong.weibo.impl.fanfou;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.ServiceProvider;

/**
 * @author cattong.com
 * @version Jul 19, 2011 10:39:17 PM
 *
 */
class FanfouResponseHandler implements ResponseHandler<String> {
	private static final Logger logger = LoggerFactory.getLogger(FanfouResponseHandler.class);

	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		
		logger.debug("FanfouResponseHandler : {}", responseString);

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
			throw new LibRuntimeException(LibResultCode.E_UNKNOWN_ERROR, ServiceProvider.Fanfou);
		}
		return responseString;
	}

}
