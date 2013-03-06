package com.cattong.weibo.impl.sina;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;

/**
 * SinaResponseHandler 新浪Http响应处理类,包级别访问权限控制
 */
class SinaResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		Logger.debug("SinaResponseHandler : {}", responseString);

		if (statusLine.getStatusCode() >= 300) {
			LibRuntimeException apiException =  SinaErrorAdaptor.parseError(responseString);
			throw apiException;
		}
		return responseString;
	}
}
