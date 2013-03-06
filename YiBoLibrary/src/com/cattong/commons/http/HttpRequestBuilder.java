package com.cattong.commons.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.http.auth.OAuthAccessorFactory;
import com.cattong.commons.oauth.OAuth2;
import com.cattong.commons.oauth.OAuthAccessor;
import com.cattong.commons.oauth.OAuthException;
import com.cattong.commons.util.Base64;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.MimeTypeUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.UrlUtil;

public class HttpRequestBuilder {

	public static HttpRequest newHttpRequest(HttpRequestWrapper httpRequestWrapper)
		throws OAuthException, IOException, URISyntaxException, LibException {

		URI uri = URI.create(httpRequestWrapper.getUrl());
		boolean hasQuery = false;
		if (StringUtil.isNotEmpty(uri.getQuery())){
			hasQuery = true;
			//提取Query中的参数，添加到参数列表
			Map<String, String> queryParameters = UrlUtil.extractQueryStringParameters(uri);
			httpRequestWrapper.addParameters(queryParameters);
		}

		String requestUrl = uri.toASCIIString(); //此方法将所有URL中的非ASCII码字符使用UTF-8进行编码
		if (hasQuery){
			// Query中的参数已提取出来，去除url中的Query
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		httpRequestWrapper.setUrl(requestUrl);

		Map<String, File> fileParameters = new HashMap<String, File>();
		Iterator<Map.Entry<String, Object>> iterator = httpRequestWrapper.getParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			// 遍历参数，提取出文件参数，文件参数通常需要特殊处理，比如不参与OAuth签名
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() instanceof File) {
				final String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
				fileParameters.put(encodedName, (File) entry.getValue());
				iterator.remove();
			}
		}

		Authorization auth = httpRequestWrapper.getAuth();
		if (auth.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_1) {
			if (ServiceProvider.Fanfou == auth.getServiceProvider()
				|| ServiceProvider.Twitter == auth.getServiceProvider()) {
				boolean isMultipartRequest = fileParameters.size() > 0;
				Map<String, Object> stringParameters = null;
				if (isMultipartRequest) {
					stringParameters = new HashMap<String, Object>(httpRequestWrapper.getParameters());
					httpRequestWrapper.clearParameters(); // 饭否文件上传时所有参数都不参与签名
				}
				// 对OAuth请求进行签名，签名方法会根据OAuth参数风格进行请求URL的OAuth参数添加或者认证头部的添加
				OAuthAccessor accessor = OAuthAccessorFactory.getOAuthAccessorInstance(auth);
				accessor.sign(httpRequestWrapper);
				if (isMultipartRequest) {
					//把之前清除的参数再给重新置回去，以便进行下一步操作
					httpRequestWrapper.addParameters(stringParameters);
				}
			} else {
				// 对OAuth请求进行签名，签名方法会根据OAuth参数风格进行请求URL的OAuth参数添加或者认证头部的添加
				OAuthAccessor accessor = OAuthAccessorFactory.getOAuthAccessorInstance(auth);
				accessor.sign(httpRequestWrapper);
			}
		} else if (auth.getAuthVersion() == Authorization.AUTH_VERSION_OAUTH_2) {
			httpRequestWrapper.addParameter(OAuth2.ACCESS_TOKEN, auth.getAccessToken());
		} else if (auth.getAuthVersion() == Authorization.AUTH_VERSION_BASIC) {
			// Basic认证头部添加
			httpRequestWrapper.addHeader("Authorization", getBasicAuthorizationHeader(auth));
		}

		HttpRequest httpRequest = buildHttpRequest(httpRequestWrapper, fileParameters);

		return httpRequest;
	}

	private static HttpRequest buildHttpRequest(HttpRequestWrapper httpRequestWrapper,
			Map<String, File> fileParameters) throws IOException, UnsupportedEncodingException {

		HttpRequest httpRequest = null;
		String requestUrl = httpRequestWrapper.getUrl();
		HttpMethod method = httpRequestWrapper.getMethod();

        switch (method) {
        case GET:
        case DELETE:
        	if (httpRequestWrapper.getParameters().size() > 0) {
        		requestUrl = UrlUtil.appendQueryParameters(requestUrl, httpRequestWrapper.getParameters(), "UTF-8");
			}

        	if (method == HttpMethod.GET) {
        	    httpRequest = new HttpGet(requestUrl);
        	} else {
        	    httpRequest = new HttpDelete(requestUrl);
        	}
        	break;

        case POST:
        case PUT:
        	HttpEntityEnclosingRequest httpEntityEnclosingRequest = null;
        	if (method == HttpMethod.POST) {
        		httpEntityEnclosingRequest = new HttpPost(requestUrl);
        	} else {
        	    httpEntityEnclosingRequest = new HttpPut(requestUrl);
        	}

    		if (fileParameters.size() > 0) {
				MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);

				for (Map.Entry<String, Object> entry  : httpRequestWrapper.getParameters().entrySet()) {
					StringBody strBody = new StringBody(String.valueOf(entry.getValue()), Charset.forName("UTF-8"));
					multipartEntity.addPart(entry.getKey(), strBody);
				}

				File file = null;
				String fileExtension = null;
				String mineType = null;
				for (Map.Entry<String, File> entry : fileParameters.entrySet()) {
					file = entry.getValue();
					fileExtension = FileUtil.getFileExtensionFromName(file.getName());
					mineType = MimeTypeUtil.getSingleton().getMimeTypeFromExtension(fileExtension);
					if (StringUtil.isNotEmpty(mineType)) {
						// 添加文件参数,带上mimeType
						multipartEntity.addPart(entry.getKey(), new FileBody(file, mineType)); 
                	} else {
                		// 添加文件参数
                		multipartEntity.addPart(entry.getKey(), new FileBody(file)); 
                	}
				}

				httpEntityEnclosingRequest.setEntity(multipartEntity);
			} else {
				List<NameValuePair> stringParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, Object> entry : httpRequestWrapper.getParameters().entrySet()) {
					stringParams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(stringParams, "UTF-8");

				httpEntityEnclosingRequest.setEntity(entity);
			}

			httpRequest = httpEntityEnclosingRequest;
			break;
        }

        Map<String, String> headers = httpRequestWrapper.getHeaders();
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}

		return httpRequest;
	}

	/**
	 * 设置Basic认证的头部信息
	 *
	 * @param httpRequest
	 * @param auth
	 */
	private static String getBasicAuthorizationHeader(Authorization auth) {
		String authHeader = "";
		if (auth == null) {
			return authHeader;
		}

		String rawKeyPair = auth.getAccessToken() + ":" + auth.getAccessSecret();
		byte[] keyPairBytes = rawKeyPair.getBytes();
		String keyPair = new String(Base64.encodeBase64(keyPairBytes));
		authHeader = "Basic " + keyPair;
		return authHeader;
	}
}
