package com.cattong.commons.util;

import java.util.List;

public class ObjectUtil {

	public static boolean isNull(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof List<?>) {
			return ((List<?>)object).size() == 0;
		}
		return false;
	}
	
	public static boolean isEquals(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null) {
			return false;
		}
		if (obj1 == obj2) {
			return true;
		}
		if (obj1.equals(obj2)) {
			return true;
		}
		return false;
	}
}
