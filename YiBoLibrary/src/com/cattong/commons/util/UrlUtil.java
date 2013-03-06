package com.cattong.commons.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.protocol.HTTP;

public class UrlUtil {

	public static <T> String appendQueryParameters(String url, final Map<String, T> parameters)
		throws UnsupportedEncodingException {
	    return appendQueryParameters(url, parameters, null);
	}

	public static <T> String appendQueryParameters(String url, final Map<String, T> parameters,
		final String encoding) throws UnsupportedEncodingException {
		final StringBuilder result = new StringBuilder();
		if (url == null || parameters == null || parameters.size() == 0) {
			return result.toString();
		}

		String tempEncoding = (encoding != null ? encoding : HTTP.DEFAULT_CONTENT_CHARSET);
		for (Map.Entry<String, T> entry : parameters.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}

			result.append(URLEncoder.encode(entry.getKey(), tempEncoding));
			result.append("=");
			result.append(URLEncoder.encode(String.valueOf(entry.getValue()), tempEncoding));
		}

		if (url.indexOf("?") < 0) {
			result.insert(0, "?");
		} else {
			result.insert(0, "&");
		}

		result.insert(0, url);
		return result.toString();
	}

	public static Map<String, String> extractQueryStringParameters(URI uri){
		if (uri == null){
			return null;
		}

		Map<String, String> parameters = null;
		final String query = uri.getRawQuery();
	    if (StringUtil.isNotEmpty(query)) {
	    	parameters = new HashMap<String, String>();
	    	Scanner scanner = new Scanner(query);
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

		return parameters;
	}

	public static String encode(String str) {
		if (StringUtil.isEmpty(str)) {
			return str;
		}
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return str;
	}
	
    public static String getFileName(String url) {
    	if (StringUtil.isEmpty(url)) {
    		return null;
    	}
    	
    	int pos = url.lastIndexOf(File.separator);
    	if (pos == -1) {
    		return null;
    	}
    	return url.substring(pos + 1);
    }
}
