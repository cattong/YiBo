package com.cattong.commons.oauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cattong.commons.util.StringUtil;


public class OAuth {

	public static final String VERSION_1_0 = "1.0";

	/** The encoding used to represent characters as bytes. */
	public static final String ENCODING = "UTF-8";

	/** The MIME type for a sequence of OAuth parameters. */
	public static final String FORM_ENCODED = "application/x-www-form-urlencoded";

	public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	public static final String OAUTH_SIGNATURE = "oauth_signature";
	public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	public static final String OAUTH_NONCE = "oauth_nonce";
	public static final String OAUTH_VERSION = "oauth_version";
	public static final String OAUTH_CALLBACK = "oauth_callback";
	public static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	public static final String HMAC_SHA1 = "HMAC-SHA1";
	public static final String RSA_SHA1 = "RSA-SHA1";

	/**
	 * Strings used for <a href="http://wiki.oauth.net/ProblemReporting">problem
	 * reporting</a>.
	 */
	public static class Problems {
		public static final String VERSION_REJECTED = "version_rejected";
		public static final String PARAMETER_ABSENT = "parameter_absent";
		public static final String PARAMETER_REJECTED = "parameter_rejected";
		public static final String TIMESTAMP_REFUSED = "timestamp_refused";
		public static final String NONCE_USED = "nonce_used";
		public static final String SIGNATURE_METHOD_REJECTED = "signature_method_rejected";
		public static final String SIGNATURE_INVALID = "signature_invalid";
		public static final String CONSUMER_KEY_UNKNOWN = "consumer_key_unknown";
		public static final String CONSUMER_KEY_REJECTED = "consumer_key_rejected";
		public static final String CONSUMER_KEY_REFUSED = "consumer_key_refused";
		public static final String TOKEN_USED = "token_used";
		public static final String TOKEN_NOT_AUTHORIZED = "token_not_authorized";
		public static final String TOKEN_EXPIRED = "token_expired";
		public static final String TOKEN_REVOKED = "token_revoked";
		public static final String TOKEN_REJECTED = "token_rejected";
		public static final String ADDITIONAL_AUTHORIZATION_REQUIRED = "additional_authorization_required";
		public static final String PERMISSION_UNKNOWN = "permission_unknown";
		public static final String PERMISSION_DENIED = "permission_denied";
		public static final String USER_REFUSED = "user_refused";

		public static final String OAUTH_ACCEPTABLE_VERSIONS = "oauth_acceptable_versions";
		public static final String OAUTH_ACCEPTABLE_TIMESTAMPS = "oauth_acceptable_timestamps";
		public static final String OAUTH_PARAMETERS_ABSENT = "oauth_parameters_absent";
		public static final String OAUTH_PARAMETERS_REJECTED = "oauth_parameters_rejected";
		public static final String OAUTH_PROBLEM_ADVICE = "oauth_problem_advice";

	    public static final Map<String, Integer> TO_HTTP_CODE = mapToHttpCode();

	    private static Map<String, Integer> mapToHttpCode() {
	        Integer badRequest = Integer.valueOf(400);
	        Integer unauthorized = Integer.valueOf(401);
	        Integer serviceUnavailable = Integer.valueOf(503);
	        Map<String, Integer> map = new HashMap<String, Integer>();

	        map.put(Problems.VERSION_REJECTED, badRequest);
	        map.put(Problems.PARAMETER_ABSENT, badRequest);
	        map.put(Problems.PARAMETER_REJECTED, badRequest);
	        map.put(Problems.TIMESTAMP_REFUSED, badRequest);
	        map.put(Problems.SIGNATURE_METHOD_REJECTED, badRequest);

	        map.put(Problems.NONCE_USED, unauthorized);
	        map.put(Problems.TOKEN_USED, unauthorized);
	        map.put(Problems.TOKEN_EXPIRED, unauthorized);
	        map.put(Problems.TOKEN_REVOKED, unauthorized);
	        map.put(Problems.TOKEN_REJECTED, unauthorized);
	        map.put(Problems.TOKEN_NOT_AUTHORIZED, unauthorized);
	        map.put(Problems.SIGNATURE_INVALID, unauthorized);
	        map.put(Problems.CONSUMER_KEY_UNKNOWN, unauthorized);
	        map.put(Problems.CONSUMER_KEY_REJECTED, unauthorized);
	        map.put(Problems.ADDITIONAL_AUTHORIZATION_REQUIRED, unauthorized);
	        map.put(Problems.PERMISSION_UNKNOWN, unauthorized);
	        map.put(Problems.PERMISSION_DENIED, unauthorized);

	        map.put(Problems.USER_REFUSED, serviceUnavailable);
	        map.put(Problems.CONSUMER_KEY_REFUSED, serviceUnavailable);
	        return Collections.unmodifiableMap(map);
	    }

	}

	private static String characterEncoding = ENCODING;

	public static void setCharacterEncoding(String encoding) {
		OAuth.characterEncoding = encoding;
	}

	public static String decodeCharacters(byte[] from) {
		if (characterEncoding != null) {
			try {
				return new String(from, characterEncoding);
			} catch (UnsupportedEncodingException e) {
				System.err.println(e + "");
			}
		}
		return new String(from);
	}

	public static byte[] encodeCharacters(String from) {
		if (characterEncoding != null) {
			try {
				return from.getBytes(characterEncoding);
			} catch (UnsupportedEncodingException e) {
				System.err.println(e + "");
			}
		}
		return from.getBytes();
	}

	/** Return true if the given Content-Type header means FORM_ENCODED. */
	public static boolean isFormEncoded(String contentType) {
		if (contentType == null) {
			return false;
		}
		int semi = contentType.indexOf(";");
		if (semi >= 0) {
			contentType = contentType.substring(0, semi);
		}
		return FORM_ENCODED.equalsIgnoreCase(contentType.trim());
	}

	 /**
     * Construct a form-urlencoded document containing the given sequence of
     * name/value pairs. Use OAuth percent encoding (not exactly the encoding
     * mandated by HTTP).
     */
    public static String formEncode(Map<String, Object> parameters)
            throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        formEncode(parameters, b);
        return decodeCharacters(b.toByteArray());
    }

    /**
     * Write a form-urlencoded document into the given stream, containing the
     * given sequence of name/value pairs.
     */
    public static void formEncode(Map<String, Object> parameters,
            OutputStream into) throws IOException {
        if (parameters != null) {
            boolean first = true;
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    into.write('&');
                }
                into.write(encodeCharacters(percentEncode(parameter.getKey())));
                into.write('=');
                into.write(encodeCharacters(percentEncode(String.valueOf(parameter.getValue()))));
            }
        }
    }

	/** Parse a form-urlencoded document. */
	public static Map<String, String> decodeForm(String form) {
		Map<String, String> parameters = new HashMap<String, String>();
		if (StringUtil.isNotEmpty(form)) {
			for (String nvp : form.split("\\&")) {
				int equals = nvp.indexOf('=');
				String name;
				String value;
				if (equals < 0) {
					name = decodePercent(nvp);
					value = null;
				} else {
					name = decodePercent(nvp.substring(0, equals));
					value = decodePercent(nvp.substring(equals + 1));
				}
				parameters.put(name, value);
			}
		}
		return parameters;
	}

	/** Construct a &-separated list of the given values, percentEncoded. */
	public static String percentEncode(Iterable<?> values) {
		StringBuilder p = new StringBuilder();
		for (Object v : values) {
			if (p.length() > 0) {
				p.append("&");
			}
			p.append(OAuth.percentEncode(String.valueOf(v)));
		}
		return p.toString();
	}

	public static String percentEncode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLEncoder.encode(s, ENCODING)
			// OAuth encodes some characters differently:
					.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
			// This could be done faster with more hand-crafted code.
		} catch (UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

	public static String decodePercent(String s) {
		try {
			return URLDecoder.decode(s, ENCODING);
			// This implements http://oauth.pbwiki.com/FlexibleDecoding
		} catch (java.io.UnsupportedEncodingException wow) {
			throw new RuntimeException(wow.getMessage(), wow);
		}
	}

}
