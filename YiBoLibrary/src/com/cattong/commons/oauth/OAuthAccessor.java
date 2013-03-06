package com.cattong.commons.oauth;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.signature.OAuthSignatureMethod;
import com.cattong.commons.util.UrlUtil;

public class OAuthAccessor implements Cloneable, Serializable {
	private static final long serialVersionUID = 5590788443138352999L;

	public final OAuthConsumer consumer;

	private Authorization auth;
	
	public OAuthAccessor(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public Authorization getAuthorization() {
		return auth;
	}

	public void setAuthorization(Authorization auth) {
		this.auth = auth;
	}

	public void sign(HttpRequestWrapper requestMessage) throws OAuthException, IOException,
			URISyntaxException {

		/******************************************/
		OAuthUtil.addRequiredParameters(this, requestMessage); // 检查并补全OAuth认证的必需参数
		OAuthSignatureMethod signatureMethod = OAuthSignatureMethod.newMethod(this);
		signatureMethod.sign(requestMessage);
		if (Logger.level <= Logger.DEBUG) {
			signatureMethod.validate(requestMessage);
		}
		/******************************************/

		Map<String, String> oauthParameters = new HashMap<String, String>();
		Iterator<Map.Entry<String, Object>> iterator = requestMessage.getParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			// 遍历参数，提取出OAuth参数
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getKey().startsWith("oauth_")) {
				oauthParameters.put(entry.getKey(), String.valueOf(entry.getValue()));
				iterator.remove();
			}
		}

		String url = requestMessage.getUrl();
		OAuthParameterStyle style = this.consumer.getParameterStyle();
		if (style == null){
			style = OAuthParameterStyle.AUTHORIZATION_HEADER;
		}
		switch (style) {
		case QUERY_STRING:
			url = UrlUtil.appendQueryParameters(url, oauthParameters);
			requestMessage.setUrl(url);
			break;
		case AUTHORIZATION_HEADER:
			//直落，与默认相同，即将签名信息添加至头部
		default:
			String oauthHeader = OAuthUtil.getAuthorizationHeader(null, oauthParameters);
		
			Logger.debug("OAuth Authorization Header : {}", oauthHeader);
			
			requestMessage.addHeader("Authorization", oauthHeader);
			break;
		}
	}

	@Override
	public OAuthAccessor clone() {
		try {
			return (OAuthAccessor) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
