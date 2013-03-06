package com.cattong.sns.impl.facebook;

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
import com.cattong.commons.util.ParseUtil;

class FacebookResponseHandler implements ResponseHandler<String> {

	public String handleResponse(final HttpResponse response)
			throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity, "UTF-8"));

		Logger.debug("FacebookResponseHandler : {}", responseString);

		if (responseString != null) {
			responseString = responseString.trim();
			try {
				JSONObject json = new JSONObject(responseString);
				if (json.has("data")) {
					responseString = ParseUtil.getRawString("data", json);
				} else if (json.has("error")) {
					json = json.getJSONObject("error");
					String errorDesc = json.getString("message");
					int errorCode = LibResultCode.E_UNKNOWN_ERROR;
					String errorType = ParseUtil.getRawString("type", json);
					if ("OAuthException".equals(errorType)) {
						errorCode = LibResultCode.OAUTH_EXCEPTION;
					}
					throw new LibRuntimeException(errorCode, "", errorDesc,
							ServiceProvider.Facebook);
				}

			} catch (JSONException e) {
				Logger.debug(e.getMessage(), e);
			}
		}

		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(),
					statusLine.getReasonPhrase());
		}

		return responseString;
	}
}
