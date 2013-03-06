package com.cattong.commons.util;


public class ArrayUtil {

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	public static <T> boolean isNotEmpty(T[] array) {
		return array != null && array.length > 0;
	}
	
}
