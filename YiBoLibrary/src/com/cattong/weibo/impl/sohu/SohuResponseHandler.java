package com.cattong.weibo.impl.sohu;

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
 * SohuResponseHandler 搜狐Http响应处理类
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午02:32:40
 */
class SohuResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

	
	    Logger.debug("SohuResponseHandler : {}", responseString);
		if (statusLine.getStatusCode() >= 300) {
			LibRuntimeException apiException =  SohuErrorAdaptor.parseError(responseString);
			throw apiException;
		}

		return responseString;
	}
}
