package com.cattong.socialcat.impl.socialcat;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;

class SocialCatResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response)
			throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils
				.toString(entity));


	    Logger.debug("SocialCatResponseHandler : {}", responseString);

		if (statusLine.getStatusCode() >= 300) {
			throw new LibRuntimeException(statusLine.getStatusCode());
		}
		
		responseString = parseData(responseString);
		
		return responseString;
	}


	private String parseData(String responseString) throws LibRuntimeException{
		String data = null;
		try {
			JSONObject json = new JSONObject(responseString);
			int resultCode = json.getInt("resultCode");
			String msg = json.getString("msg");
			
			LibRuntimeException exception = null;
			if (resultCode != LibResultCode.ACTION_SUCCESS) {
				exception = new LibRuntimeException(resultCode, msg, msg, ServiceProvider.SocialCat);
			} else {
				data = json.getString("data");
			}
			 
			if (exception != null) {
				throw exception;
			}
		} catch (JSONException e) {
			throw new LibRuntimeException(
					LibResultCode.JSON_PARSE_ERROR, e,
					ServiceProvider.SocialCat);
		}
		
		return data;
	}
}
