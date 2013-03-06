package com.cattong.commons.oauth.signature;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestWrapper;
import com.cattong.commons.oauth.OAuth;
import com.cattong.commons.oauth.OAuthAccessor;
import com.cattong.commons.oauth.OAuthException;
import com.cattong.commons.oauth.OAuthProblemException;
import com.cattong.commons.oauth.OAuthUtil;
import com.cattong.commons.util.Base64;

public abstract class OAuthSignatureMethod {

	private String consumerSecret;

	private String tokenSecret;

	/** Compute the signature for the given base string. */
	protected abstract String getSignature(String baseString) throws OAuthException;

	/** Decide whether the signature is valid. */
	protected abstract boolean isValid(String signature, String baseString) throws OAuthException;

	/**
	 * Add a signature to the message.
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void sign(HttpRequestWrapper message) throws OAuthException, IOException, URISyntaxException {
		message.addParameter(OAuth.OAUTH_SIGNATURE, getSignature(message));
	}

	/**
	 * Check whether the message has a valid signature.
	 *
	 * @throws URISyntaxException
	 * @throws net.oauth.OAuthProblemException
	 *
	 * @throws OAuthProblemException
	 *             the signature is invalid
	 */
	public void validate(HttpRequestWrapper message) throws IOException, OAuthException, URISyntaxException,
			OAuthProblemException {
		OAuthUtil.requireParameters(message, OAuth.OAUTH_SIGNATURE);
		String signature = OAuthUtil.getSignature(message);
		String baseString = getBaseString(message);
		if (!isValid(signature, baseString)) {
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.SIGNATURE_INVALID);
			problem.setParameter(OAuth.OAUTH_SIGNATURE, signature);
			problem.setParameter("oauth_signature_base_string", baseString);
			problem.setParameter(OAuth.OAUTH_SIGNATURE_METHOD, OAuthUtil.getSignatureMethod(message));
			throw problem;
		}
	}

	protected String getSignature(HttpRequestWrapper message) throws OAuthException, IOException, URISyntaxException {
		String baseString = getBaseString(message);
		
		Logger.debug("OAuth BaseString : {}", baseString);
		
		String signature = getSignature(baseString);
		
		Logger.debug("OAuth Signature : {}", signature);
		
		return signature;
	}

	protected void initialize(OAuthAccessor accessor) throws OAuthException {
		String secret = accessor.consumer.consumerSecret;
		if (secret == null) {
			secret = "";
		}
		setConsumerSecret(secret);

		if (accessor.getAuthorization() != null) {
			setTokenSecret(accessor.getAuthorization().getAccessSecret());
		}
	}

	protected String getConsumerSecret() {
		return consumerSecret;
	}

	protected void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public static String getBaseString(HttpRequestWrapper message) throws IOException, URISyntaxException {
		Map<String, Object> parameters = null;
		String url = message.getUrl();

		int q = url.indexOf('?');
		if (q < 0) {
			parameters = message.getParameters();
		} else {
			// Combine the URL query string with the other parameters:
			parameters = new HashMap<String, Object>();
			parameters.putAll(OAuth.decodeForm(url.substring(q + 1)));
			parameters.putAll(message.getParameters());
			url = url.substring(0, q);
		}
		return OAuth.percentEncode(message.getMethod().toString().toUpperCase()) + '&'
				+ OAuth.percentEncode(normalizeUrl(url)) + '&' + OAuth.percentEncode(normalizeParameters(parameters));
	}

	protected static String normalizeUrl(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String scheme = uri.getScheme().toLowerCase();
		String authority = uri.getAuthority().toLowerCase();
		boolean dropPort = (scheme.equals("http") && uri.getPort() == 80)
				|| (scheme.equals("https") && uri.getPort() == 443);
		if (dropPort) {
			// find the last : in the authority
			int index = authority.lastIndexOf(":");
			if (index >= 0) {
				authority = authority.substring(0, index);
			}
		}
		String path = uri.getRawPath();
		if (path == null || path.length() <= 0) {
			path = "/"; // conforms to RFC 2616 section 3.2.2
		}
		// we know that there is no query and no fragment here.
		return scheme + "://" + authority + path;
	}

	protected static String normalizeParameters(Map<String, Object> parameters) throws IOException {
		if (parameters == null) {
			return "";
		}
		List<ComparableParameter> p = new ArrayList<ComparableParameter>(parameters.size());
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			if (!OAuth.OAUTH_SIGNATURE.equals(entry.getKey())) {
				p.add(new ComparableParameter(entry));
			}
		}
		Collections.sort(p);
		return OAuth.formEncode(getSortedParameters(p));
	}

	/**
	 * Determine whether the given strings contain the same sequence of
	 * characters. The implementation discourages a <a
	 * href="http://codahale.com/a-lesson-in-timing-attacks/">timing attack</a>.
	 */
	public static boolean equals(String x, String y) {
		if (x == null)
			return y == null;
		else if (y == null)
			return false;
		else if (y.length() <= 0)
			return x.length() <= 0;
		char[] a = x.toCharArray();
		char[] b = y.toCharArray();
		char diff = (char) ((a.length == b.length) ? 0 : 1);
		int j = 0;
		for (int i = 0; i < a.length; ++i) {
			diff |= a[i] ^ b[j];
			j = (j + 1) % b.length;
		}
		return diff == 0;
	}

	/**
	 * Determine whether the given arrays contain the same sequence of bytes.
	 * The implementation discourages a <a
	 * href="http://codahale.com/a-lesson-in-timing-attacks/">timing attack</a>.
	 */
	public static boolean equals(byte[] a, byte[] b) {
		if (a == null)
			return b == null;
		else if (b == null)
			return false;
		else if (b.length <= 0)
			return a.length <= 0;
		byte diff = (byte) ((a.length == b.length) ? 0 : 1);
		int j = 0;
		for (int i = 0; i < a.length; ++i) {
			diff |= a[i] ^ b[j];
			j = (j + 1) % b.length;
		}
		return diff == 0;
	}

	public static byte[] decodeBase64(String s) {
		byte[] b;
		try {
			b = s.getBytes(BASE64_ENCODING);
		} catch (UnsupportedEncodingException e) {
			System.err.println(e + "");
			b = s.getBytes();
		}
		return BASE64.decode(b);
	}

	public static String base64Encode(byte[] b) {
		byte[] b2 = BASE64.encode(b);
		try {
			return new String(b2, BASE64_ENCODING);
		} catch (UnsupportedEncodingException e) {
			System.err.println(e + "");
		}
		return new String(b2);
	}

	/**
	 * The character encoding used for base64. Arguably US-ASCII is more
	 * accurate, but this one decodes all byte values unambiguously.
	 */
	private static final String BASE64_ENCODING = "ISO-8859-1";
	private static final Base64 BASE64 = new Base64();

	/** The factory for signature methods. */
	public static OAuthSignatureMethod newMethod(OAuthAccessor accessor)
			throws OAuthException {
		try {
			String signatureMethod = accessor.consumer.getSignatureMethod();
			if (signatureMethod == null) {
				signatureMethod = OAuth.HMAC_SHA1;
			}
			Class<? extends OAuthSignatureMethod> methodClass = NAME_TO_CLASS.get(signatureMethod);
			if (methodClass != null) {
				OAuthSignatureMethod method = methodClass.newInstance();
				method.initialize(accessor);
				return method;
			}
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.SIGNATURE_METHOD_REJECTED);
			String acceptable = OAuth.percentEncode(NAME_TO_CLASS.keySet());
			if (acceptable.length() > 0) {
				problem.setParameter("oauth_acceptable_signature_methods", acceptable.toString());
			}
			throw problem;
		} catch (InstantiationException e) {
			throw new OAuthException(e);
		} catch (IllegalAccessException e) {
			throw new OAuthException(e);
		}
	}

	/**
	 * 注册签名类后, newMethod(name) 将尝试调用无参构造函数实例化类
	 */
	public static void registerMethodClass(String name, Class<? extends OAuthSignatureMethod> clazz) {
		if (clazz == null)
			unregisterMethod(name);
		else
			NAME_TO_CLASS.put(name, clazz);
	}

	/**
	 * 删除签名类后, newMethod(name) 将会失败。
	 */
	public static void unregisterMethod(String name) {
		NAME_TO_CLASS.remove(name);
	}

	private static final Map<String, Class<? extends OAuthSignatureMethod>> NAME_TO_CLASS = new ConcurrentHashMap<String, Class<? extends OAuthSignatureMethod>>();
	static {
		registerMethodClass("HMAC-SHA1", HMAC_SHA1.class);
		registerMethodClass("PLAINTEXT", PLAINTEXT.class);
	}

	private static class ComparableParameter implements Comparable<ComparableParameter> {

		ComparableParameter(Map.Entry<?, ?> value) {
			this.value = value;
			String n = toString(value.getKey());
			String v = toString(value.getValue());
			this.key = OAuth.percentEncode(n) + ' ' + OAuth.percentEncode(v);
			// ' ' is used because it comes before any character
			// that can appear in a percentEncoded string.
		}

		final Map.Entry<?, ?> value;

		private final String key;

		private static String toString(Object from) {
			return (from == null) ? null : from.toString();
		}

		public int compareTo(ComparableParameter that) {
			return this.key.compareTo(that.key);
		}

		@Override
		public String toString() {
			return key;
		}

	}

	/** Retrieve the original parameters from a sorted collection. */
	private static Map<String, Object> getSortedParameters(Collection<ComparableParameter> parameters) {
		if (parameters == null) {
			return null;
		}

		Map<String, Object> sorted = new LinkedHashMap<String, Object>();
		for (ComparableParameter parameter : parameters) {
			sorted.put(String.valueOf(parameter.value.getKey()), parameter.value.getValue());
		}
		return sorted;
	}

}
