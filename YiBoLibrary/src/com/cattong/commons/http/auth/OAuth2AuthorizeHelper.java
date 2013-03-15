package com.cattong.commons.http.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.oauth.OAuth2;
import com.cattong.commons.oauth.OAuth2.DisplayType;
import com.cattong.commons.oauth.OAuth2.GrantType;
import com.cattong.commons.oauth.config.OAuthConfig;
import com.cattong.commons.util.ParseUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.UrlUtil;

public class OAuth2AuthorizeHelper {

	public OAuth2AuthorizeHelper() throws LibException {
	}
	
	public String getAuthorizeUrl(Authorization auth, GrantType grantType, DisplayType displayType) throws LibException {
		String authorizeUrl = null;
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
	    }
		
		OAuthConfig oAuthConfig = auth.getoAuthConfig();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(OAuth2.CLIENT_ID, oAuthConfig.getConsumerKey());
		
		if (grantType == GrantType.IMPLICIT) {
			paramMap.put(OAuth2.RESPONSE_TYPE, OAuth2.ResponseType.TOKEN.getTypeValue());
		} else if (grantType == GrantType.AUTHORIZATION_CODE) {
			paramMap.put(OAuth2.RESPONSE_TYPE, OAuth2.ResponseType.CODE.getTypeValue());
		}
		
		paramMap.put(OAuth2.DISPLAY_TYPE, displayType.getTypeValue());
		
		if (StringUtil.isNotEmpty(oAuthConfig.getCallbackUrl())) {
			paramMap.put(OAuth2.REDIRECT_URI, oAuthConfig.getCallbackUrl());
		}

		paramMap.put(OAuth2.SCOPE, oAuthConfig.getOAuthScope());
		
		try {
			authorizeUrl = UrlUtil.appendQueryParameters(
				oAuthConfig.getAuthorizeUrl(), paramMap, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.getMessage(), e);
		}
		
		return authorizeUrl;
	}

//	public Authorization getAccessTokenByRefreshToken(
//		Authorization auth, String refreshToken,
//		String state, String... scope) throws LibException {
//		if (auth == null || StringUtil.isEmpty(refreshToken)) {
//			throw new LibException(LibResultCode.E_PARAM_NULL);
//		}
//		
//		OAuthConfiguration oAuthConfig = auth.getoAuthConfig();
//		HttpRequestMessage httpRequestMessage =
//			new HttpRequestMessage(HttpMethod.POST, oAuthConfig.getAccessTokenUrl(), auth);
//		httpRequestMessage.addParameter(OAuth2.GRANT_TYPE, GrantType.REFRESH_TOKEN.getTypeValue());
//		httpRequestMessage.addParameter(OAuth2.REFRESH_TOKEN, refreshToken);
//		httpRequestMessage.addParameter(OAuth2.CLIENT_ID, oAuthConfig.getConsumerKey());
//		httpRequestMessage.addParameter(OAuth2.CLIENT_SECRET,oAuthConfig.getConsumerSecret());
//
//		httpRequestMessage.addParameter(OAuth2.SCOPE, oAuthConfig.getOAuthScope());
//	
//		if (StringUtil.isNotEmpty(state)) {
//			httpRequestMessage.addParameter(OAuth2.STATE, state);
//		}
//
//		
//		auth = HttpRequestHelper.execute(httpRequestMessage,
//				new OAuth2AccessTokenResponseHandler(auth));
//		
//		return auth;
//	}

	public Authorization retrieveAccessToken(Authorization auth, 
		String authorizationCode, String state) throws LibException {
		if (auth == null || StringUtil.isEmpty(authorizationCode)) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		OAuthConfig oAuthConfig = auth.getoAuthConfig();
		HttpRequestWrapper httpRequestMessage =	new HttpRequestWrapper(
			HttpMethod.POST, oAuthConfig.getAccessTokenUrl(), auth);
		httpRequestMessage.addParameter(OAuth2.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getTypeValue());
		httpRequestMessage.addParameter(OAuth2.CODE, authorizationCode);
		httpRequestMessage.addParameter(OAuth2.CLIENT_ID, oAuthConfig.getConsumerKey());
		httpRequestMessage.addParameter(OAuth2.CLIENT_SECRET, oAuthConfig.getConsumerSecret());

		httpRequestMessage.addParameter(OAuth2.SCOPE, oAuthConfig.getOAuthScope());
		if (StringUtil.isNotEmpty(state)) {
			httpRequestMessage.addParameter(OAuth2.STATE, state);
		}
		if (StringUtil.isNotEmpty(oAuthConfig.getCallbackUrl())) {
			httpRequestMessage.addParameter(OAuth2.REDIRECT_URI, oAuthConfig.getCallbackUrl());
		}

		OAuth2AccessTokenResponseHandler responseHandler = new OAuth2AccessTokenResponseHandler(auth);
		auth = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		return auth;
	}

	public Authorization retrieveAccessToken(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		String username = auth.getAccessToken();
		String password = auth.getAccessSecret();
		OAuthConfig oAuthConfig = auth.getoAuthConfig();		
		
		HttpRequestWrapper httpRequestMessage =	new HttpRequestWrapper(
			HttpMethod.POST, oAuthConfig.getAccessTokenUrl(), auth);
		httpRequestMessage.addParameter(OAuth2.CLIENT_ID, oAuthConfig.getConsumerKey());
		httpRequestMessage.addParameter(OAuth2.CLIENT_SECRET, oAuthConfig.getConsumerSecret());
		httpRequestMessage.addParameter(OAuth2.GRANT_TYPE, 
			GrantType.RESOURCE_OWNER_PASSWORD_CREDENTIALS.getTypeValue());
		httpRequestMessage.addParameter(OAuth2.USERNAME, username);
		httpRequestMessage.addParameter(OAuth2.PASSWORD, password);

		httpRequestMessage.addParameter(OAuth2.SCOPE, oAuthConfig.getOAuthScope());

		OAuth2AccessTokenResponseHandler responseHandler = new OAuth2AccessTokenResponseHandler(auth);
		auth = HttpRequestHelper.execute(httpRequestMessage, responseHandler);
		
		return auth;
	}
	
	public static String retrieveAuthorizationCodeFromQueryString(String url) {
		URI uri = URI.create(url);
		Map<String, String> parameters = UrlUtil.extractQueryStringParameters(uri);
		String code = null;
		if (parameters != null) {
			code = parameters.get(OAuth2.CODE);
		}
		return code;
	}

	public static Authorization retrieveAccessTokenFromFragment(String url) {
		URI uri = URI.create(url);
		Map<String, String> parameters = null;
		final String fragment = uri.getFragment();
	    if (StringUtil.isNotEmpty(fragment)) {
	    	parameters = new HashMap<String, String>();
	    	Scanner scanner = new Scanner(fragment);
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
	            final String[] nameValue = scanner.next().split("=");
	            if (nameValue.length == 0 || nameValue.length > 2){
	                throw new IllegalArgumentException("Bad parameter");
	            }
	            final String name = nameValue[0];
	            String value = null;
	            if (nameValue.length == 2){
	            	value = nameValue[1];
	            }
	            parameters.put(name, value);
	        }
	    }

	    Authorization auth = new Authorization(ServiceProvider.None);
		if (parameters != null) {
			if (parameters.containsKey(OAuth2.ACCESS_TOKEN)) {
				String accessToken = parameters.get(OAuth2.ACCESS_TOKEN);
				Long expiresIn = Long.valueOf(parameters.get(OAuth2.EXPIRES_IN));
				Date expiredAt = new Date(System.currentTimeMillis() + expiresIn * 1000);
				
				auth.setAccessToken(accessToken);
				auth.setExpiredAt(expiredAt);
				auth.setRefreshToken(parameters.get(OAuth2.REFRESH_TOKEN));
			}
		}
		
		return auth;
	}

	private class OAuth2AccessTokenResponseHandler implements ResponseHandler<Authorization> {
        
		private Authorization auth;
		private ServiceProvider sp;
		public OAuth2AccessTokenResponseHandler(Authorization auth) {
		    this.auth = auth;
		    this.sp = auth.getServiceProvider();
		}
		
		@Override
		public Authorization handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			try {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				final String responseString = EntityUtils.toString(entity);
				
				Logger.debug("OAuth2AccessTokenResponseHandler : {}", responseString);

				if (statusLine.getStatusCode() >= 300) {
					JSONObject exceptionJson = new JSONObject(responseString);
					String error = ParseUtil.getRawString(OAuth2.ERROR, exceptionJson);
					if (exceptionJson.has(OAuth2.ERROR_DESCRIPTION)) {
						error += (": " + ParseUtil.getRawString(OAuth2.ERROR_DESCRIPTION, exceptionJson));
					}
					String requestPath = ParseUtil.getRawString(OAuth2.ERROR_URI, exceptionJson);
					throw new LibRuntimeException(LibResultCode.OAUTH_EXCEPTION,
						requestPath, error, sp);
				}

				JSONObject json = null;
				try {
					json = new JSONObject(responseString);
				} catch (JSONException e) {
					// 响应格式非JSON时，分析字符串
					json = new JSONObject();
					Scanner scanner = new Scanner(responseString);
					scanner.useDelimiter("&");
					while (scanner.hasNext()) {
			            final String[] nameValue = scanner.next().split("=");
			            if (nameValue.length == 0 || nameValue.length > 2){
			                throw new IllegalArgumentException("Bad parameter.");
			            }
			            json.put(nameValue[0], nameValue.length == 2 ? nameValue[1] : null);
					}
					if (!json.has(OAuth2.ACCESS_TOKEN)) {
						Logger.debug(e.getMessage(), e);						
						throw e;
					}
				}

				String accessToken = ParseUtil.getRawString(OAuth2.ACCESS_TOKEN, json);
				Long expiresIn = ParseUtil.getLong(OAuth2.EXPIRES_IN, json);
				Date expiredAt = new Date(System.currentTimeMillis() + expiresIn * 1000);

				auth.setAccessToken(accessToken);
				auth.setExpiredAt(expiredAt);
				auth.setRefreshToken(ParseUtil.getRawString(OAuth2.REFRESH_TOKEN, json));
			
				return auth;
			} catch (JSONException e) {
				throw new LibRuntimeException(LibResultCode.JSON_PARSE_ERROR, e, sp);
			}
		}
	}

}
