package net.dev123.yibo.common;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dev123.entity.Gender;
import net.dev123.exception.ExceptionCode;
import net.dev123.yibo.R;
import android.content.Context;
import android.util.Log;

public class ResourceBook extends Properties {
	/** serialVersionUID */
	private static final long serialVersionUID = -5182954528802329847L;
	private static final String TAG = ResourceBook.class.getSimpleName();
    private static final String STATUS_CODE = "STATUS_CODE_";
    private static final String GENDER = "GENDER_";

	private static ResourceBook codeBook;

	static {
		codeBook = new ResourceBook();
	}
	private ResourceBook() {
	}

	public static void loadResouceBook(Context context) {
		if (context == null) {
			return;
		}

		loadResouce(context, R.array.gender_book, GENDER);
		loadResouce(context, R.array.code_book, STATUS_CODE);
	}

	private static void loadResouce(Context context, int resouceId, String prefix) {
        String[] keyValueArray = context.getResources().getStringArray(resouceId);
        String keyValue = null;
        char split = '=';
        int splitPos = 0;
        Pattern p = Pattern.compile("\\w+\\[(-?\\d+)\\]");
        for (int i = 0; i < keyValueArray.length; i++) {
            keyValue = keyValueArray[i];
            splitPos = keyValue.indexOf(split);
            String key = keyValue.substring(0, splitPos);
            String value = keyValue.substring(splitPos + 1);
            int code = 0;
            Matcher m = p.matcher(key);
            if (m.find()) {
            	code = Integer.parseInt(m.group(1));
            } else {
            	throw new IllegalArgumentException("code book configure is error");
            }
            codeBook.put(prefix + code, value);

            if (Constants.DEBUG) {
            	Log.d(TAG, key + "|" + code + "|" + value);
            }
        }
	}

	public static String getValue(String key, Context context) {
		if (codeBook.size() == 0) {
			loadResouceBook(context);
		}
		return codeBook.containsKey(key) ? codeBook.get(key).toString() : null;
	}

	public static String getStatusCodeValue(int statusCode, Context context) {
		String key = STATUS_CODE + statusCode;
		String value = getValue(key, context);
		if (value == null) {
			key = STATUS_CODE + ExceptionCode.UNKNOWN_EXCEPTION;
			value = getValue(key, context) + ":" + statusCode;
		}
		return value;
	}

	public static String getGenderValue(Gender gender, Context context) {
		if (gender == null) {
			gender = Gender.UNKNOW;
		}
		String key = GENDER + gender.getGenderNo();
		return getValue(key, context);
	}
}
