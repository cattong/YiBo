package com.cattong.commons.http.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.HttpMethod;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.oauth.OAuth;
import com.cattong.commons.oauth.config.OAuthConfig;


public class OAuthAuthorizeHelper {

	public OAuthAuthorizeHelper() throws LibException {
	}

	/**
	 * 获取RequestToken
	 *
	 * @return 获取RequestToken
	 * @throws LibException
	 * @see <a href="http://oauth.net/core/1.0a/#auth_step1">OAuth Core 1.0a - 6.1.
	 *    Obtaining an Unauthorized Request Token</a>
	 */
	public Authorization retrieveRequestToken(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		OAuthConfig oAuthConfig = auth.getoAuthConfig();
		HttpRequestWrapper request = new HttpRequestWrapper(
			HttpMethod.GET, oAuthConfig.getRequestTokenUrl(), auth);
		request.addParameter(OAuth.OAUTH_CALLBACK, oAuthConfig.getCallbackUrl());
        
		Map<String, String> responseMap = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler(auth));
		String token = responseMap.get(OAuth.OAUTH_TOKEN);
		String tokenSecret = responseMap.get(OAuth.OAUTH_TOKEN_SECRET);
		//boolean isCallbackConfirmed = Boolean.valueOf(responseMap.get(OAuth.OAUTH_CALLBACK_CONFIRMED));

		auth.setAccessToken(token);
		auth.setAccessSecret(tokenSecret);

		return auth;
	}

	public String getAuthorizeUrl(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibException(LibResultCode.E_PARAM_NULL);
		}
		
		OAuthConfig oauthConfig = auth.getoAuthConfig();
		StringBuilder authorizeUrl = new StringBuilder(oauthConfig.getAuthorizeUrl());
		if (authorizeUrl.indexOf("?") < 0) {
			authorizeUrl.append("?");
		} else {
			authorizeUrl.append("&");
		}
		authorizeUrl.append(OAuth.OAUTH_TOKEN)
		    .append("=")
		    .append(auth.getAccessToken());
		boolean isCallbackConfirmed = Boolean.valueOf("false");
		if (!isCallbackConfirmed) {
			// 使用的是 OAuth 1.0 规定的callback传递方式，需要通过授权url参数传递
			authorizeUrl.append("&");
			authorizeUrl.append(OAuth.OAUTH_CALLBACK)
			    .append("=")
			    .append(oauthConfig.getCallbackUrl());
		}
		
		return authorizeUrl.toString();
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
	public Authorization retrieveAccessToken(Authorization auth,
		    String oauthVerifier) throws LibException {
		if (auth == null ) {//|| StringUtil.isEmpty(oauthVerifier)网易不需要
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}

		OAuthConfig oAuthConfig = auth.getoAuthConfig();		
		String userAuthorizationUrl = oAuthConfig.getAuthorizeUrl();
		StringBuilder authorizationUrl = new StringBuilder(userAuthorizationUrl);
		if (authorizationUrl.indexOf("?") < 0) {
			authorizationUrl.append("?");
		} else {
			authorizationUrl.append("&");
		}
		authorizationUrl.append(OAuth.OAUTH_TOKEN).append("=").append(auth.getAccessToken());
		//if (!isCallbackConfirmed) {
			// 使用的是 OAuth 1.0 规定的callback传递方式，需要通过授权url参数传递
			authorizationUrl.append("&");
			authorizationUrl.append(OAuth.OAUTH_CALLBACK)
			    .append("=")
			    .append(oAuthConfig.getCallbackUrl());
		//}
		
		HttpRequestWrapper request = 
			new HttpRequestWrapper(HttpMethod.GET, oAuthConfig.getAccessTokenUrl(), auth);
		request.addParameter(OAuth.OAUTH_VERIFIER, oauthVerifier);
		Map<String, String> responseMap = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler(auth));

		String accessToken = responseMap.get(OAuth.OAUTH_TOKEN);
		String accessSecret = responseMap.get(OAuth.OAUTH_TOKEN_SECRET);
		String userId = responseMap.get("user_id");
		String username = responseMap.get("screen_name");

		auth.setAccessToken(accessToken);
		auth.setAccessSecret(accessSecret);
		//accessToken.setUserId(userId);
		//accessToken.setUsername(username);

		return auth;
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
	public Authorization retrieveAccessToken(Authorization auth) throws LibException {
		if (auth == null) {
			throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
		}
		
		String username = auth.getAccessToken();
		String password = auth.getAccessSecret();
		auth.setAccessToken(null);
		auth.setAccessSecret(null);
		OAuthConfig oAuthConfig = auth.getoAuthConfig();	
		HttpRequestWrapper request = new HttpRequestWrapper(HttpMethod.POST, 
			oAuthConfig.getAccessTokenUrl(), auth);
		request.addParameter("x_auth_username", username);
		request.addParameter("x_auth_password", password);
		request.addParameter("x_auth_mode", "client_auth");
		Map<String, String> xAuthResponse = HttpRequestHelper.execute(
			request, new FormEncodedResponseHandler(auth));

		String accessToken = xAuthResponse.get(OAuth.OAUTH_TOKEN);
		String accessSecret = xAuthResponse.get(OAuth.OAUTH_TOKEN_SECRET);
		String userId = xAuthResponse.get("user_id");

		auth.setAccessToken(accessToken);
		auth.setAccessSecret(accessSecret);
		//accessToken.setUserId(userId);
		//accessToken.setUsername(username);

		return auth;
	}

	private class FormEncodedResponseHandler implements ResponseHandler<Map<String, String>> {

		private Authorization auth;
		private ServiceProvider sp;
		public FormEncodedResponseHandler(Authorization auth) {
		    this.auth = auth;
		    this.sp = auth.getServiceProvider();
		}
		
		@Override
		public Map<String, String> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() >= 300) {
				int statusCode = statusLine.getStatusCode();
				if (statusCode == HttpStatus.SC_FORBIDDEN
					&& (sp == ServiceProvider.Sina
						|| sp == ServiceProvider.NetEase)) {
					//在使用XAuth认证方式下，若用户名或密码错误，新浪、网易的响应是403，搜狐的是401，统一为401
					statusCode = HttpStatus.SC_UNAUTHORIZED;
				}
				
				if (sp == ServiceProvider.Tencent
					&& HttpStatus.SC_BAD_REQUEST == statusCode
					&& "Bad Request: Unsupported parameter".equals(statusLine.getReasonPhrase())){
					statusCode = LibResultCode.OAUTH_TIMESTAMP_REFUSED;
				}
				
				throw new LibRuntimeException(statusCode);
			}

			Map<String, String> resultMap = new HashMap<String, String>();
			HttpEntity entity = response.getEntity();
			final String responseString = EntityUtils.toString(entity);

			Logger.debug("FormEncodedResponseHandler : {}", responseString);

			Scanner scanner = new Scanner(responseString);
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
	            final String[] nameValue = scanner.next().split("=");
	            if (nameValue.length == 0 || nameValue.length > 2){
	            	 throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR, "", "Bad Parameter", ServiceProvider.None);
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
	
}
