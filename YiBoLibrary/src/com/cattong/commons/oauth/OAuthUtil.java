package com.cattong.commons.oauth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.util.StringUtil;


public class OAuthUtil {

	public static final String AUTH_SCHEME = "OAuth";

	private static final Pattern AUTHORIZATION = Pattern.compile("\\s*(\\w*)\\s+(.*)");
	private static final Pattern NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");

	public static void requireParameters(HttpRequestWrapper requestMessage, String... names)
			throws OAuthProblemException {
		Set<String> present = requestMessage.getParameters().keySet();
		List<String> absent = new ArrayList<String>();
		for (String required : names) {
			if (!present.contains(required)) {
				absent.add(required);
			}
		}
		if (!absent.isEmpty()) {
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_ABSENT);
			problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_ABSENT, OAuth.percentEncode(absent));
			throw problem;
		}
	}

	public static String getSignature(HttpRequestWrapper requestMessage) throws OAuthProblemException {
		requireParameters(requestMessage, OAuth.OAUTH_SIGNATURE);
		return String.valueOf(requestMessage.getParameters().get(OAuth.OAUTH_SIGNATURE));
	}

	public static String getSignatureMethod(HttpRequestWrapper requestMessage) throws OAuthProblemException {
		requireParameters(requestMessage, OAuth.OAUTH_SIGNATURE_METHOD);
		return String.valueOf(requestMessage.getParameters().get(OAuth.OAUTH_SIGNATURE_METHOD));
	}

	public static void addRequiredParameters(OAuthAccessor accessor, HttpRequestWrapper requestMessage) {
		final Map<String, Object> pMap = requestMessage.getParameters();
		Authorization auth = accessor.getAuthorization();
		if (pMap.get(OAuth.OAUTH_TOKEN) == null
			&& auth != null
		    && StringUtil.isNotEmpty(auth.getAccessToken())) {
			requestMessage.addParameter(OAuth.OAUTH_TOKEN, auth.getAccessToken());
		}
		final OAuthConsumer consumer = accessor.consumer;
		if (pMap.get(OAuth.OAUTH_CONSUMER_KEY) == null) {
			requestMessage.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumer.consumerKey);
		}
		String signatureMethod = (String) pMap.get(OAuth.OAUTH_SIGNATURE_METHOD);
		if (signatureMethod == null) {
			signatureMethod = (String) consumer.getSignatureMethod();
			if (signatureMethod == null) {
				signatureMethod = OAuth.HMAC_SHA1;
			}
			requestMessage.addParameter(OAuth.OAUTH_SIGNATURE_METHOD, signatureMethod);
		}
		if (pMap.get(OAuth.OAUTH_TIMESTAMP) == null) {
			requestMessage.addParameter(OAuth.OAUTH_TIMESTAMP, (System.currentTimeMillis() / 1000) + "");
		}
		if (pMap.get(OAuth.OAUTH_NONCE) == null) {
			requestMessage.addParameter(OAuth.OAUTH_NONCE, StringUtil.getRandomString(32));
		}
		if (pMap.get(OAuth.OAUTH_VERSION) == null) {
			requestMessage.addParameter(OAuth.OAUTH_VERSION, OAuth.VERSION_1_0);
		}
	}

	/**
	 * Construct a WWW-Authenticate or Authentication header value, containing
	 * the given realm plus all the parameters whose names begin with "oauth_".
	 */
	public static String getAuthorizationHeader(String realm, Map<String, String> parameters) throws IOException {
		StringBuilder into = new StringBuilder();
		if (realm != null) {
			into.append(" realm=\"").append(OAuth.percentEncode(realm)).append('"');
		}
		if (parameters != null) {
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				if (parameter.getKey().startsWith("oauth_")) {
					if (into.length() > 0)
						into.append(",");
					into.append(" ");
					into.append(OAuth.percentEncode(parameter.getKey())).append("=\"");
					into.append(OAuth.percentEncode(parameter.getValue())).append('"');
				}
			}
		}
		return AUTH_SCHEME + into.toString();
	}

	/**
	 * Parse the parameters from an OAuth Authorization or WWW-Authenticate
	 * header. The realm is included as a parameter. If the given header doesn't
	 * start with "OAuth ", return an empty list.
	 */
	public static Map<String, String> decodeAuthorization(String authorization) {
		Map<String, String> into = new HashMap<String, String>();
		if (authorization != null) {
			Matcher m = AUTHORIZATION.matcher(authorization);
			if (m.matches()) {
				if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
					for (String nvp : m.group(2).split("\\s*,\\s*")) {
						m = NVP.matcher(nvp);
						if (m.matches()) {
							String name = OAuth.decodePercent(m.group(1));
							String value = OAuth.decodePercent(m.group(2));
							into.put(name, value);
						}
					}
				}
			}
		}
		return into;
	}

}
