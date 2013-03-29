package com.shejiaomao.common;

import java.util.Properties;

import com.shejiaomao.maobo.R;
import android.content.Context;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Gender;

public class ResourceBook extends Properties {
	private static final long serialVersionUID = -5182954528802329847L;
	
    private static final String RESULT_CODE = "RESULT_CODE_";
    private static final String GENDER = "GENDER_";

	private static ResourceBook resourceBook;

	static {
		resourceBook = new ResourceBook();
	}
	
	private ResourceBook() {
	}

	public static void loadResourceBook(Context context) {
		if (context == null) {
			return;
		}

		loadResource(context, R.array.gender_book, GENDER);
		loadResource(context, R.array.result_code_book, RESULT_CODE);
	}

	public static void loadResultCodeBook(Context context, int resourceId) {
		loadResource(context, resourceId, RESULT_CODE);
	}
	
	public static void loadGenderBook(Context context, int resourceId) {
		loadResource(context, resourceId, GENDER);
	}
	
	private static void loadResource(Context context, int resouceId, String prefix) {
        String[] keyValueArray = context.getResources().getStringArray(resouceId);

        String key = null;
        String value = null;
        for (int i = 0; i < keyValueArray.length; i++) {
            String[] keyValue = keyValueArray[i].split("=");
            if (keyValue == null || keyValue.length != 2) {
            	throw new IllegalArgumentException("code book configure is error");
            }
            key = keyValue[0];            
            value = keyValue[1];
            
            resourceBook.put(prefix + key, value);

            Logger.verbose("loadResource:{}|{}", key, value);
        }
	}

	public static String getValue(String key, Context context) {
		if (resourceBook.size() == 0) {
			loadResourceBook(context);
		}
		return resourceBook.containsKey(key) ? resourceBook.get(key).toString() : null;
	}

	public static String getResultCodeValue(int resultCode, Context context) {
		if (resourceBook.size() == 0) {
			loadResourceBook(context);
		}
		
		String key = RESULT_CODE + resultCode;
		String value = getValue(key, context);
		if (value == null) {
			key = RESULT_CODE + LibResultCode.E_UNKNOWN_ERROR;
			value = getValue(key, context) + ":" + resultCode;
		}
		return value;
	}

	public static String getResultCodeValue(LibException e, Context context) {
		int resultCode = e.getErrorCode();
		if (StringUtil.isNotEmpty(e.getErrorDescr()) 
			&& !StringUtil.isNumeric(e.getErrorDescr())) {
			return e.getErrorDescr();
		}
		
		String value = getResultCodeValue(resultCode, context);		
		return value;
	}
	
	public static String getGenderValue(Gender gender, Context context) {
		if (gender == null) {
			gender = Gender.Unkown;
		}
		String key = GENDER + gender.getGenderNo();
		return getValue(key, context);
	}
}
