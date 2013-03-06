package com.cattong.commons.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


import org.json.JSONException;
import org.json.JSONObject;

public final class ParseUtil {
	private ParseUtil() {
		// should never be instantiated
		throw new AssertionError();
	}

	private static ThreadLocal<Map<String, SimpleDateFormat>> formatMap = new ThreadLocal<Map<String, SimpleDateFormat>>() {
		@Override
		protected Map<String, SimpleDateFormat> initialValue() {
			return new HashMap<String, SimpleDateFormat>();
		}
	};

	public static String getUnescapedString(String str, JSONObject json) {
		return HTMLUtil.unescape(getRawString(str, json));
	}

	public static String getRawString(String name, JSONObject json) {
		try {
			if (json.isNull(name)) {
				return null;
			} else {
				return json.getString(name);
			}
		} catch (JSONException jsone) {
			return null;
		}
	}

	public static String getURLDecodedString(String name, JSONObject json) {
		String returnValue = getRawString(name, json);
		if (null != returnValue) {
			try {
				returnValue = URLDecoder.decode(returnValue, "UTF-8");
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		return returnValue;
	}

	public static Date getDate(String name, JSONObject json) throws ParseException {
		return getDate(name, json, "EEE MMM d HH:mm:ss z yyyy");
	}

	public static Date getDate(String name, JSONObject json, String format) throws ParseException {
		String dateStr = getUnescapedString(name, json);
		if (null == dateStr || dateStr.trim().length() == 0 || "null".equals(dateStr)) {
			return null;
		} else {
			return getDate(dateStr, format);
		}
	}

	public static Date getDate(String dateString, String format) throws ParseException {
		SimpleDateFormat sdf = formatMap.get().get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.get().put(format, sdf);
		}
		return sdf.parse(dateString);
	}

	public static int getInt(String name, JSONObject elem) {
		String str = getRawString(name, elem);
		if (null == str || "".equals(str) || "null".equals(str)) {
			return -1;
		} else {
			if (str.endsWith("+")) {
				//some count over 100 will be expressed as "100+" in twitter
	            str = str.substring(0, str.length() - 1);
	            return Integer.valueOf(str) + 1;
	        }
			return Integer.valueOf(str);
		}
	}

	public static long getLong(String name, JSONObject json) {
		String str = getRawString(name, json);
		if (null == str || "".equals(str) || "null".equals(str)) {
			return -1;
		} else {
			if (str.endsWith("+")) {
				//some count over 100 will be expressed as "100+" in twitter
	            str = str.substring(0, str.length() - 1);
	            return Long.valueOf(str) + 1;
	        }
			return Long.valueOf(str);
		}
	}

	public static double getDouble(String name, JSONObject json) {
		String str2 = getRawString(name, json);
		if (null == str2 || "".equals(str2) || "null".equals(str2)) {
			return -1;
		} else {
			return Double.valueOf(str2);
		}
	}

	public static boolean getBoolean(String name, JSONObject json) {
		String str = getRawString(name, json);
		if (null == str || "null".equals(str)) {
			return false;
		}
		return Boolean.valueOf(str);
	}

	public static String escapeAngleBrackets(String text){
    	if (text == null) {
    		return text;
    	}

    	return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}