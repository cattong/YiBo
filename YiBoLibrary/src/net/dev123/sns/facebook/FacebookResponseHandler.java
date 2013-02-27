package net.dev123.sns.facebook;

import java.io.IOException;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ParseUtil;
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

class FacebookResponseHandler implements ResponseHandler<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(FacebookResponseHandler.class.getSimpleName());

	public String handleResponse(final HttpResponse response)
			throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity, "UTF-8"));

		if (Constants.DEBUG) {
			logger.debug("FacebookResponseHandler : {}", responseString);
		}

		if (responseString != null) {
			responseString = responseString.trim();
			try {
				JSONObject json = new JSONObject(responseString);
				if (json.has("data")) {
					responseString = ParseUtil.getRawString("data", json);
				} else if (json.has("error")) {
					json = json.getJSONObject("error");
					String errorDesc = json.getString("message");
					int errorCode = ExceptionCode.UNKNOWN_EXCEPTION;
					String errorType = ParseUtil.getRawString("type", json);
					if ("OAuthException".equals(errorType)) {
						errorCode = ExceptionCode.OAUTH_EXCEPTION;
					}
					throw new LibRuntimeException(errorCode, "", errorDesc,
							ServiceProvider.Facebook);
				}

			} catch (JSONException e) {
				if (Constants.DEBUG) {
					logger.debug(e.getMessage(), e);
				}
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
