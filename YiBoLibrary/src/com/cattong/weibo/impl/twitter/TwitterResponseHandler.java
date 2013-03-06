package com.cattong.weibo.impl.twitter;

import java.io.IOException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import com.cattong.weibo.entity.RateLimitStatus;

/**
 * TwitterResponseHandler Twitter Http响应处理类,包级别访问权限控制
 *
 * @version
 * @author cattong.com
 * @time 2010-8-30 下午02:32:40
 */
class TwitterResponseHandler implements ResponseHandler<String> {
	private static final String STATUS_OVER_LENGTH = "Status is over 140 characters.";
	private static final String STATUS_DUPLICATE = "Status is a duplicate.";

	public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String responseString = (entity == null ? null : EntityUtils.toString(entity));

		Logger.debug("TwitterResponseHandler : {}", responseString);

		int statusCode = statusLine.getStatusCode();
		if (responseString!= null
			&& responseString.contains("error")
			&& responseString.startsWith("{")) {
			//因为代理在出现异常的情况下Status仍可能为200，所以根据内容判断异常而不是statusCode
			//确认响应是JSON对象字符串
			try {
				JSONObject json = new JSONObject(responseString);
				if (json.has("error")) {
					// 确定是异常JSON响应
					String error = json.getString("error");
					String request = json.getString("request");

					//如果是代理的情况，搜索接口响应420 Enhance Your Calm的时候，代理可能会返回的是200，这个时候做下判断
					if (statusCode == HttpStatus.SC_OK
							&& TwitterRateLimitStatusAdaptor.ENHANCE_YOUR_CALM_ERROR.equals(error)) {
						statusCode = TwitterRateLimitStatusAdaptor.ENHANCE_YOUR_CALM_CODE;
					}

					if (STATUS_OVER_LENGTH.equals(error)) {
						statusCode = LibResultCode.API_MB_CONTENT_OVER_LENGTH;
					} else if (STATUS_DUPLICATE.equals(error)) {
						statusCode = LibResultCode.API_MB_TWEET_REPEAT;
					}


					RateLimitStatus rateLimitStatus = null;
					if (statusCode == TwitterRateLimitStatusAdaptor.ENHANCE_YOUR_CALM_CODE) {
						throw new LibRuntimeException(LibResultCode.API_MB_SEARCH_RATE_LIMITED,
							request, error, ServiceProvider.Twitter);
					} else {
						rateLimitStatus = getRateLimitStatus(response);
					}

					if (rateLimitStatus != null && rateLimitStatus.getRemainingHits() == 0) {
						throw new LibRuntimeException(LibResultCode.API_MB_RATE_LIMITED,
							request, error, ServiceProvider.Twitter);
					}

					throw new LibRuntimeException(statusCode, request, error, ServiceProvider.Twitter);
				}
			} catch (JSONException e) {
				throw new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR,
					e, ServiceProvider.Twitter);
			}
		}

		if (statusCode >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}

		return responseString;
	}

	private RateLimitStatus getFeatureRateLimitStatus(final HttpResponse response) {
		int remainingHits; // "X-FeatureRateLimit-Remaining"
		int hourlyLimit; // "X-FeatureRateLimit-Limit"
		int resetTimeInSeconds; // Need to be calculated.
		Date resetTime; // new Date("X-FeatureRateLimit-Reset")

		Header limit = response.getFirstHeader("X-FeatureRateLimit-Limit");
		if (null != limit) {
			hourlyLimit = Integer.parseInt(limit.getValue());
		} else {
			return null;
		}
		Header remaining = response.getFirstHeader("X-FeatureRateLimit-Remaining");
		if (null != remaining) {
			remainingHits = Integer.parseInt(remaining.getValue());
		} else {
			return null;
		}
		Header reset = response.getFirstHeader("X-FeatureRateLimit-Reset");
		if (null != reset) {
			long longReset = Long.parseLong(reset.getValue());
			resetTimeInSeconds = (int) (longReset / 1000);
			resetTime = new Date(longReset * 1000);
		} else {
			return null;
		}

		RateLimitStatus rateLimit = new RateLimitStatus();
		rateLimit.setHourlyLimit(hourlyLimit);
		rateLimit.setRemainingHits(remainingHits);
		rateLimit.setResetedAt(resetTime);
		rateLimit.setResetTimeInSeconds(resetTimeInSeconds);

		return rateLimit;
	}

	private RateLimitStatus getRateLimitStatus(final HttpResponse response) {
		int remainingHits; // "X-RateLimit-Remaining"
		int hourlyLimit; // "X-RateLimit-Limit"
		int resetTimeInSeconds; // Need to be calculated.
		Date resetTime; // new Date("X-RateLimit-Reset")

		Header limit = response.getFirstHeader("X-RateLimit-Limit");
		if (null != limit) {
			hourlyLimit = Integer.parseInt(limit.getValue());
		} else {
			return null;
		}
		Header remaining = response.getFirstHeader("X-RateLimit-Remaining");
		if (null != remaining) {
			remainingHits = Integer.parseInt(remaining.getValue());
		} else {
			return null;
		}
		Header reset = response.getFirstHeader("X-RateLimit-Reset");
		if (null != reset) {
			long longReset = Long.parseLong(reset.getValue());
			resetTimeInSeconds = (int) (longReset / 1000);
			resetTime = new Date(longReset * 1000);
		} else {
			return null;
		}

		RateLimitStatus rateLimit = new RateLimitStatus();
		rateLimit.setHourlyLimit(hourlyLimit);
		rateLimit.setRemainingHits(remainingHits);
		rateLimit.setResetedAt(resetTime);
		rateLimit.setResetTimeInSeconds(resetTimeInSeconds);

		return rateLimit;
	}
}
