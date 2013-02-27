package net.dev123.mblog.sina;

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
 * SinaResponseHandler 新浪Http响应处理类,包级别访问权限控制
 *
 * @version
 * @author 马庆升
 * @time 2010-8-30 下午02:32:40
 */
class SinaResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory.getLogger(SinaResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		if (Constants.DEBUG) {
			logger.debug("SinaResponseHandler : {}", responseString);
		}

		if (statusLine.getStatusCode() >= 300) {
			LibRuntimeException apiException =  SinaErrorAdaptor.parseError(responseString);
			throw apiException;
		}
		return responseString;
	}
}
