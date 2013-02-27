package net.dev123.commons.http.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.oauth2.OAuth2;
import net.dev123.commons.oauth2.OAuth2.GrantType;
import net.dev123.commons.oauth2.OAuth2AccessToken;
import net.dev123.commons.util.ParseUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.UrlUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2AuthorizeHelper {

	private ServiceProvider serviceProvider;
	private OAuthConfiguration oAuthConfig; 
	private String consumerKey;
	private String consumerSecret;
	private String callbackUrl;

	public OAuth2AuthorizeHelper(ServiceProvider serviceProvider) throws LibException {
		if (serviceProvider == null) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		this.serviceProvider = serviceProvider;
		this.oAuthConfig = 
			OAuthConfigurationFactory.getOAuthConfiguration(serviceProvider);
		this.consumerKey = oAuthConfig.getOAuthConsumerKey();
		this.consumerSecret = oAuthConfig.getOAuthConsumerSecret();
		this.callbackUrl = oAuthConfig.getOAuthCallbackURL();
	}
	
	public void setConsumer(String consumerKey, String consumerSecret) throws LibException {
		if (StringUtil.isEmpty(consumerKey) || StringUtil.isEmpty(consumerSecret)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}
	
	public void setCallbackUrl(String callbackUrl) throws LibException {
		if (StringUtil.isEmpty(callbackUrl)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		this.callbackUrl = callbackUrl;
	}
	
	public String getAuthrizationUrl(GrantType grantType, String state,
			String... scope) {

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(OAuth2.CLIENT_ID, oAuthConfig.getOAuthConsumerKey());
		if (grantType == GrantType.IMPLICIT){
			parameters.put(OAuth2.RESPONSE_TYPE, OAuth2.ResponseType.TOKEN.getTypeValue());
		} if (grantType == GrantType.AUTHORIZATION_CODE) {
			parameters.put(OAuth2.RESPONSE_TYPE, OAuth2.ResponseType.CODE.getTypeValue());
		}
		if (StringUtil.isNotEmpty(callbackUrl)) {
			parameters.put(OAuth2.REDIRECT_URI, callbackUrl);
		}
		if (scope == null || scope.length == 0) {
			scope = oAuthConfig.getOAuthScopes();
		}
		if (scope != null && scope.length > 0) {
			parameters.put(OAuth2.SCOPE, StringUtil.join(scope, oAuthConfig.getOAuthScopeSeparator()));
		}
		if (StringUtil.isNotEmpty(state)) {
			parameters.put(OAuth2.STATE, state);
		}
		try {
			return UrlUtil.appendQueryParameters(oAuthConfig.getOAuthAuthorizeURL(), parameters, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public OAuth2AccessToken getAccessTokenByRefreshToken(String refreshToken,
		String state, String... scope) throws LibException {
		if (StringUtil.isEmpty(refreshToken)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		Authorization auth = new NullAuthorization(serviceProvider);
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, oAuthConfig.getOAuthAccessTokenURL(), auth);
		httpRequestMessage.addParameter(OAuth2.GRANT_TYPE, GrantType.REFRESH_TOKEN.getTypeValue());
		httpRequestMessage.addParameter(OAuth2.REFRESH_TOKEN, refreshToken);
		httpRequestMessage.addParameter(OAuth2.CLIENT_ID, consumerKey);
		httpRequestMessage.addParameter(OAuth2.CLIENT_SECRET, consumerSecret);
		if (scope == null || scope.length == 0) {
			scope = oAuthConfig.getOAuthScopes();
		}
		if (scope != null && scope.length > 0) {
			httpRequestMessage.addParameter(OAuth2.SCOPE, StringUtil.join(scope, oAuthConfig.getOAuthScopeSeparator()));
		}
		if (StringUtil.isNotEmpty(state)) {
			httpRequestMessage.addParameter(OAuth2.STATE, state);
		}

		OAuth2AccessToken accessToken = HttpRequestHelper.execute(httpRequestMessage,
				new OAuth2AccessTokenResponseHandler());
		return accessToken;
	}

	public OAuth2AccessToken getAccessTokenByAuthorizationCode(String authorizationCode,
		String state, String... scope) throws LibException {
		if (StringUtil.isEmpty(authorizationCode)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		Authorization auth = new NullAuthorization(serviceProvider);
		HttpRequestMessage httpRequestMessage =
			new HttpRequestMessage(HttpMethod.POST, oAuthConfig.getOAuthAccessTokenURL(), auth);
		httpRequestMessage.addParameter(OAuth2.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getTypeValue());
		httpRequestMessage.addParameter(OAuth2.CODE, authorizationCode);
		httpRequestMessage.addParameter(OAuth2.CLIENT_ID, consumerKey);
		httpRequestMessage.addParameter(OAuth2.CLIENT_SECRET, consumerSecret);
		if (scope == null || scope.length == 0) {
			scope = oAuthConfig.getOAuthScopes();
		}
		if (scope != null && scope.length > 0) {
			httpRequestMessage.addParameter(OAuth2.SCOPE, StringUtil.join(scope, oAuthConfig.getOAuthScopeSeparator()));
		}
		if (StringUtil.isNotEmpty(state)) {
			httpRequestMessage.addParameter(OAuth2.STATE, state);
		}
		if (StringUtil.isNotEmpty(callbackUrl)) {
			httpRequestMessage.addParameter(OAuth2.REDIRECT_URI, callbackUrl);
		}

		OAuth2AccessToken accessToken = HttpRequestHelper.execute(httpRequestMessage,
				new OAuth2AccessTokenResponseHandler());
		return accessToken;
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

	public static OAuth2AccessToken retrieveAccessTokenFromFragment(String url) {
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

		OAuth2AccessToken token = null;
		if (parameters != null) {
			if (parameters.containsKey(OAuth2.ACCESS_TOKEN)) {
				String accessToken = parameters.get(OAuth2.ACCESS_TOKEN);
				Long expiresIn = Long.valueOf(parameters.get(OAuth2.EXPIRES_IN));
				Date expiresDate = new Date(System.currentTimeMillis() + expiresIn * 1000);
				token = new OAuth2AccessToken(accessToken, expiresDate);
				token.setRefreshToken(parameters.get(OAuth2.REFRESH_TOKEN));
				token.setScope(parameters.get(OAuth2.SCOPE));
				token.setTokenType(parameters.get(OAuth2.TOKEN_TYPE));
			}
		}
		return token;
	}

	private class OAuth2AccessTokenResponseHandler implements ResponseHandler<OAuth2AccessToken> {

		private final Logger logger = LoggerFactory.getLogger(OAuth2AccessTokenResponseHandler.class.getSimpleName());

		@Override
		public OAuth2AccessToken handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			try {
				StatusLine statusLine = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				final String responseString = EntityUtils.toString(entity);
				if (Constants.DEBUG) {
					logger.debug("OAuth2AccessTokenResponseHandler : {}", responseString);
				}

				if (statusLine.getStatusCode() >= 300) {
					JSONObject exceptionJson = new JSONObject(responseString);
					String error = ParseUtil.getRawString(OAuth2.ERROR, exceptionJson);
					if (exceptionJson.has(OAuth2.ERROR_DESCRIPTION)) {
						error += (": " + ParseUtil.getRawString(OAuth2.ERROR_DESCRIPTION, exceptionJson));
					}
					String requestPath = ParseUtil.getRawString(OAuth2.ERROR_URI, exceptionJson);
					throw new LibRuntimeException(ExceptionCode.OAUTH_EXCEPTION,
						requestPath, error, serviceProvider);
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
						if (Constants.DEBUG) {
							logger.debug(e.getMessage(), e);
						}
						throw e;
					}
				}

				String accessToken = ParseUtil.getRawString(OAuth2.ACCESS_TOKEN, json);
				Long expiresIn = ParseUtil.getLong(OAuth2.EXPIRES_IN, json);
				Date expiresDate = new Date(System.currentTimeMillis() + expiresIn * 1000);
				OAuth2AccessToken token = new OAuth2AccessToken(accessToken, expiresDate);
				token.setRefreshToken(ParseUtil.getRawString(OAuth2.REFRESH_TOKEN, json));
				token.setTokenType(ParseUtil.getRawString(OAuth2.TOKEN_TYPE, json));
				token.setScope(ParseUtil.getRawString(OAuth2.SCOPE, json));
				return token;
			} catch (JSONException e) {
				throw new LibRuntimeException(ExceptionCode.JSON_PARSE_ERROR, e, serviceProvider);
			}
		}
	}

}
