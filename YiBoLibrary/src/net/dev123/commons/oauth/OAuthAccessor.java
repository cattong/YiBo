package net.dev123.commons.oauth;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.dev123.commons.Constants;
import net.dev123.commons.http.HttpRequestMessage;
import net.dev123.commons.oauth.signature.OAuthSignatureMethod;
import net.dev123.commons.util.UrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAccessor implements Cloneable, Serializable {

	private static final long serialVersionUID = 5590788443138352999L;
	private static final Logger logger = LoggerFactory.getLogger(OAuthAccessor.class.getSimpleName());

	public final OAuthConsumer consumer;
	private OAuthToken oauthToken;

	public OAuthAccessor(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public OAuthToken getOAuthToken() {
		return oauthToken;
	}

	public void setOAuthToken(OAuthToken token) {
		this.oauthToken = token;
	}

	public void sign(HttpRequestMessage request) throws OAuthException, IOException,
			URISyntaxException {

		/******************************************/
		OAuthUtil.addRequiredParameters(this, request); // 检查并补全OAuth认证的必需参数
		OAuthSignatureMethod signatureMethod = OAuthSignatureMethod.newMethod(this);
		signatureMethod.sign(request);
		if (Constants.DEBUG){
			signatureMethod.validate(request);
		}
		/******************************************/

		Map<String, String> oauthParameters = new HashMap<String, String>();
		Iterator<Map.Entry<String, Object>> iterator = request.getParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			// 遍历参数，提取出OAuth参数
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getKey().startsWith("oauth_")) {
				oauthParameters.put(entry.getKey(), String.valueOf(entry.getValue()));
				iterator.remove();
			}
		}

		String url = request.getUrl();
		OAuthParameterStyle style = this.consumer.getParameterStyle();
		if (style == null){
			style = OAuthParameterStyle.AUTHORIZATION_HEADER;
		}
		switch (style) {
		case QUERY_STRING:
			url = UrlUtil.appendQueryParameters(url, oauthParameters);
			request.setUrl(url);
			break;
		case AUTHORIZATION_HEADER:
			//直落，与默认相同，即将签名信息添加至头部
		default:
			String oauthHeader = OAuthUtil.getAuthorizationHeader(null, oauthParameters);
			if (Constants.DEBUG) {
				logger.debug("OAuth Authorization Header : {}", oauthHeader);
			}
			request.addHeader("Authorization", oauthHeader);
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
