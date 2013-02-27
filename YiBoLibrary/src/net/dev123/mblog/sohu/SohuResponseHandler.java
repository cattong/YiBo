package net.dev123.mblog.sohu;

import java.io.IOException;

import net.dev123.commons.Constants;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SohuResponseHandler 搜狐Http响应处理类
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午02:32:40
 */
class SohuResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(SohuResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("SohuResponseHandler : {}", responseString);
		}

		if (statusLine.getStatusCode() >= 300) {
			LibRuntimeException apiException =  SohuErrorAdaptor.parseError(responseString);
			throw apiException;
		}

		return responseString;
	}
}
