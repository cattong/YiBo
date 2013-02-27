package net.dev123.commons.http.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.dev123.commons.Constants;
import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.HttpMethod;
import net.dev123.commons.http.HttpRequestHelper;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.oauth.OAuth;
import net.dev123.commons.oauth.OAuthAccessToken;
import net.dev123.commons.oauth.OAuthRequestToken;
import net.dev123.commons.oauth.config.OAuthConfiguration;
import net.dev123.commons.oauth.config.OAuthConfigurationFactory;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.exception.LibRuntimeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OAuthAuthorizeHelper {

	private ServiceProvider serviceProvider;
	private OAuthConfiguration oAuthConfig; 
	private String consumerKey;
	private String consumerSecret;
	private String callbackUrl;

	public OAuthAuthorizeHelper(ServiceProvider serviceProvider) {
		if (serviceProvider == null) {
			throw new NullPointerException("ServiceProvider must not be null!");
		}
		this.serviceProvider = serviceProvider;
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
	
	public void setCallbackUrl(String callbackUrl) throws LibException {
		if (StringUtil.isEmpty(callbackUrl)) {
			throw new LibException(ExceptionCode.PARAMETER_NULL);
		}
		this.callbackUrl = callbackUrl;
	}
	

	/**
	 * 获取RequestToken
	 *
	 * @return 获取RequestToken
	 * @throws LibException
	 * @see <a href="http://oauth.net/core/1.0a/#auth_step1">OAuth Core 1.0a - 6.1.
	 *    Obtaining an Unauthorized Request Token</a>
	 */
	public OAuthRequestToken retrieveOAuthRequestToken() throws LibException {
		OAuthAuthorization auth = new OAuthAuthorization(null, serviceProvider);
		auth.setConsumerKey(consumerKey);
		auth.setConsumerSecret(consumerSecret);
		HttpRequestMessage request = 
			new HttpRequestMessage(HttpMethod.GET, oAuthConfig.getOAuthRequestTokenURL(), auth);
		request.addParameter(OAuth.OAUTH_CALLBACK, callbackUrl);

		Map<String, String> responseMap = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler());
		String token = responseMap.get(OAuth.OAUTH_TOKEN);
		String tokenSecret = responseMap.get(OAuth.OAUTH_TOKEN_SECRET);
		boolean isCallbackConfirmed = Boolean.valueOf(responseMap.get(OAuth.OAUTH_CALLBACK_CONFIRMED));

		OAuthRequestToken requestToken = new OAuthRequestToken(token, tokenSecret);

		String userAuthorizationUrl = oAuthConfig.getOAuthAuthorizeURL();
		StringBuilder authorizationUrl = new StringBuilder(userAuthorizationUrl);
		if (authorizationUrl.indexOf("?") < 0) {
			authorizationUrl.append("?");
		} else {
			authorizationUrl.append("&");
		}
		authorizationUrl.append(OAuth.OAUTH_TOKEN).append("=").append(token);
		if (!isCallbackConfirmed) {
			// 使用的是 OAuth 1.0 规定的callback传递方式，需要通过授权url参数传递
			authorizationUrl.append("&");
			authorizationUrl.append(OAuth.OAUTH_CALLBACK).append("=").append(callbackUrl);
		}
		requestToken.setAuthorizationURL(authorizationUrl.toString());

		requestToken.setCallbackUrl(callbackUrl);

		return requestToken;
	}

	/**
	 * 通过OAuthRquestToken和Verifier获取OAuthAccessToken
	 *
	 * @param requestToken
	 *            OAuth rquest token
	 * @param oauthVerifier
	 *            OAuth verifier. AKA pin.
	 * @return 与提供的OAuthRquestToken相关的OAuthAccessToken
	 * @throws LibException
	 * @see <a href="http://oauth.net/core/1.0a/#auth_step2">OAuth Core 1.0a - 6.2.
	 *    Obtaining User Authorization</a>
	 */
	public OAuthAccessToken retrieveOAuthAccessToken(OAuthRequestToken requestToken,
		    String oauthVerifier) throws LibException {
		if (requestToken == null ) {//|| StringUtil.isEmpty(oauthVerifier)网易不需要
			throw new LibRuntimeException(ExceptionCode.PARAMETER_NULL);
		}

		OAuthAuthorization auth = new OAuthAuthorization(requestToken, serviceProvider);
		auth.setConsumerKey(consumerKey);
		auth.setConsumerSecret(consumerSecret);
		
		HttpRequestMessage request = 
			new HttpRequestMessage(HttpMethod.GET, oAuthConfig.getOAuthAccessTokenURL(), auth);
		request.addParameter(OAuth.OAUTH_VERIFIER, oauthVerifier);
		Map<String, String> responseMap = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler());

		String token = responseMap.get(OAuth.OAUTH_TOKEN);
		String tokenSecret = responseMap.get(OAuth.OAUTH_TOKEN_SECRET);
		String userId = responseMap.get("user_id");
		String username = responseMap.get("screen_name");

		OAuthAccessToken accessToken = new OAuthAccessToken(token, tokenSecret);
		accessToken.setUserId(userId);
		accessToken.setUsername(username);

		return accessToken;
	}

	/**
	 * 使用用户名和密码通过XAuth认证方式获取OAuthAccessToken.<br>
	 *
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 获取到的OAuthAccessToken
	 * @throws LibException
	 */
	public OAuthAccessToken retrieveOAuthAccessToken(String username,
			String password) throws LibException {
		OAuthAuthorization xAuth = new OAuthAuthorization(null, serviceProvider);
		xAuth.setConsumerKey(consumerKey);
		xAuth.setConsumerSecret(consumerSecret);
		
		HttpRequestMessage request = 
			new HttpRequestMessage(HttpMethod.POST, oAuthConfig.getOAuthAccessTokenURL(), xAuth);
		request.addParameter("x_auth_username", username);
		request.addParameter("x_auth_password", password);
		request.addParameter("x_auth_mode", "client_auth");
		Map<String, String> xAuthResponse = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler());

		String token = xAuthResponse.get(OAuth.OAUTH_TOKEN);
		String tokenSecret = xAuthResponse.get(OAuth.OAUTH_TOKEN_SECRET);
		String userId = xAuthResponse.get("user_id");

		OAuthAccessToken accessToken = new OAuthAccessToken(token, tokenSecret);
		accessToken.setUserId(userId);
		accessToken.setUsername(username);

		return accessToken;
	}

	private class FormEncodedResponseHandler implements ResponseHandler<Map<String, String>> {

		private final Logger logger = LoggerFactory.getLogger(FormEncodedResponseHandler.class.getSimpleName());

		@Override
		public Map<String, String> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() >= 300) {
				int statusCode = statusLine.getStatusCode();
				if (statusCode == HttpStatus.SC_FORBIDDEN
						&& (serviceProvider == ServiceProvider.Sina
								|| serviceProvider == ServiceProvider.NetEase)) {
					//在使用XAuth认证方式下，若用户名或密码错误，新浪、网易的响应是403，搜狐的是401，统一为401
					statusCode = HttpStatus.SC_UNAUTHORIZED;
				}
				if (serviceProvider == ServiceProvider.Tencent
						&& HttpStatus.SC_BAD_REQUEST == statusCode
						&& "Bad Request: Unsupported parameter".equals(statusLine.getReasonPhrase())){
					statusCode = ExceptionCode.OAUTH_TIMESTAMP_REFUSED;
				}
				throw new LibRuntimeException(statusCode);
			}

			Map<String, String> resultMap = new HashMap<String, String>();
			HttpEntity entity = response.getEntity();
			final String responseString = EntityUtils.toString(entity);

			if (Constants.DEBUG) {
				logger.debug("FormEncodedResponseHandler : {}", responseString);
			}

			Scanner scanner = new Scanner(responseString);
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
	            final String[] nameValue = scanner.next().split("=");
	            if (nameValue.length == 0 || nameValue.length > 2){
	            	 throw new LibRuntimeException(ExceptionCode.PARAMETER_ERROR, "", "Bad Parameter", ServiceProvider.None);
	            }

	            final String name = nameValue[0];
	            String value = null;
	            if (nameValue.length == 2){
	            	value = nameValue[1];
	            }
	            resultMap.put(name, value);
	        }
			return resultMap;
		}
		
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}
	
}
