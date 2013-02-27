package net.dev123.commons.http;

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

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.http.auth.Authorization;
import net.dev123.commons.http.auth.BasicAuthorization;
import net.dev123.commons.http.auth.OAuth2Authorization;
import net.dev123.commons.http.auth.OAuthAccessorFactory;
import net.dev123.commons.http.auth.OAuthAuthorization;
import net.dev123.commons.oauth.OAuthAccessor;
import net.dev123.commons.oauth.OAuthException;
import net.dev123.commons.oauth2.OAuth2;
import net.dev123.commons.util.Base64;
import net.dev123.commons.util.FileUtil;
import net.dev123.commons.util.MimeTypeUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.commons.util.UrlUtil;
import net.dev123.exception.LibException;

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

public class HttpRequestBuilder {

	public static HttpRequest newHttpRequest(HttpRequestMessage httpRequestMessage)
		throws OAuthException, IOException, URISyntaxException, LibException {

		URI uri = URI.create(httpRequestMessage.getUrl());
		boolean hasQuery = false;
		if (StringUtil.isNotEmpty(uri.getQuery())){
			hasQuery = true;
			//提取Query中的参数，添加到参数列表
			Map<String, String> queryParameters = UrlUtil.extractQueryStringParameters(uri);
			httpRequestMessage.addParameters(queryParameters);
		}

		String requestUrl = uri.toASCIIString(); //此方法将所有URL中的非ASCII码字符使用UTF-8进行编码
		if (hasQuery){
			// Query中的参数已提取出来，去除url中的Query
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		httpRequestMessage.setUrl(requestUrl);

		Map<String, File> fileParameters = new HashMap<String, File>();
		Iterator<Map.Entry<String, Object>> iterator = httpRequestMessage.getParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			// 遍历参数，提取出文件参数，文件参数通常需要特殊处理，比如不参与OAuth签名
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() instanceof File) {
				final String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
				fileParameters.put(encodedName, (File) entry.getValue());
				iterator.remove();
			}
		}

		Authorization auth = httpRequestMessage.getAuth();
		if (auth instanceof OAuthAuthorization) {
			if (ServiceProvider.Fanfou == auth.getServiceProvider()
				|| ServiceProvider.Twitter == auth.getServiceProvider()) {
				boolean isMultipartRequest = fileParameters.size() > 0;
				Map<String, Object> stringParameters = null;
				if (isMultipartRequest) {
					stringParameters = new HashMap<String, Object>(httpRequestMessage.getParameters());
					httpRequestMessage.clearParameters(); // 饭否文件上传时所有参数都不参与签名
				}
				// 对OAuth请求进行签名，签名方法会根据OAuth参数风格进行请求URL的OAuth参数添加或者认证头部的添加
				OAuthAccessor accessor = OAuthAccessorFactory.getOAuthAccessorInstance((OAuthAuthorization) auth);
				accessor.sign(httpRequestMessage);
				if (isMultipartRequest) {
					//把之前清除的参数再给重新置回去，以便进行下一步操作
					httpRequestMessage.addParameters(stringParameters);
				}
			} else {
				// 对OAuth请求进行签名，签名方法会根据OAuth参数风格进行请求URL的OAuth参数添加或者认证头部的添加
				OAuthAccessor accessor = OAuthAccessorFactory.getOAuthAccessorInstance((OAuthAuthorization) auth);
				accessor.sign(httpRequestMessage);
			}
		} else if (auth instanceof OAuth2Authorization) {
			httpRequestMessage.addParameter(OAuth2.ACCESS_TOKEN, auth.getAuthToken());
		} else if (auth instanceof BasicAuthorization) {
			// Basic认证头部添加
			httpRequestMessage.addHeader("Authorization", getBasicAuthorizationHeader((BasicAuthorization) auth));
		}

		HttpRequest httpRequest = buildHttpRequest(httpRequestMessage, fileParameters);

		return httpRequest;
	}

	private static HttpRequest buildHttpRequest(HttpRequestMessage httpRequestMessage,
			Map<String, File> fileParameters) throws IOException, UnsupportedEncodingException {

		HttpRequest httpRequest = null;
		String requestUrl = httpRequestMessage.getUrl();
		HttpMethod method = httpRequestMessage.getMethod();

        switch (method) {
        case GET:
        case DELETE:
        	if (httpRequestMessage.getParameters().size() > 0) {
        		requestUrl = UrlUtil.appendQueryParameters(requestUrl, httpRequestMessage.getParameters(), "UTF-8");
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

				for (Map.Entry<String, Object> entry  : httpRequestMessage.getParameters().entrySet()) {
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
						multipartEntity.addPart(entry.getKey(), new FileBody(file, mineType)); // 添加文件参数,带上mimeType
                	} else {
                		multipartEntity.addPart(entry.getKey(), new FileBody(file)); // 添加文件参数
                	}
				}

				httpEntityEnclosingRequest.setEntity(multipartEntity);
			} else {
				List<NameValuePair> stringParams = new ArrayList<NameValuePair>();
				for (Map.Entry<String, Object> entry : httpRequestMessage.getParameters().entrySet()) {
					stringParams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(stringParams, "UTF-8");

				httpEntityEnclosingRequest.setEntity(entity);
			}

			httpRequest = httpEntityEnclosingRequest;
			break;
        }

        Map<String, String> headers = httpRequestMessage.getHeaders();
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
	private static String getBasicAuthorizationHeader(BasicAuthorization auth) {
		String authHeader = "";
		if (auth == null) {
			return authHeader;
		}

		String rawKeyPair = auth.getUserName() + ":" + auth.getPassword();
		byte[] keyPairBytes = rawKeyPair.getBytes();
		String keyPair = new String(Base64.encodeBase64(keyPairBytes));
		authHeader = "Basic " + keyPair;
		return authHeader;
	}
}
