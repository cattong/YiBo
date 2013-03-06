package com.cattong.commons.util;

import java.util.Iterator;
import java.util.List;

public class ListUtil {

	public static <T> boolean isEmpty(List<T> list) {
		return list == null || list.size() == 0;
	}

	public static <T> boolean isNotEmpty(List<T> list) {
		return list != null && list.size() > 0;
	}
	/*
	 * 默认list的排序为倒序, T支持equal方法
	 */
	public static <T> List<T> truncate(List<T> list, T max, T since) {
		if (isEmpty(list)) {
			return list;
		}

		if (max != null) {
			list = truncateFromHead(list, max);
		}

		if (since != null) {
			list = truncateFromTail(list, since);
		}

		return list;
	}

	public static <T> List<T> truncateFromHead(List<T> list, T max) {
		if (isEmpty(list) || max == null) {
			return list;
		}

		T temp = null;
		int hitPos = -1;
		int length = list.size();
		for (int i = 0; i < length; i++) {
			temp = list.get(i);
			if (max.equals(temp)) {
				hitPos = i;
				break;
			}
		}

		while (hitPos > -1) {
			list.remove(0);
			hitPos--;
		}

		return list;
	}

	public static <T> List<T> truncateFromTail(List<T> list, T since) {
		if (isEmpty(list) || since == null) {
			return list;
		}

		Iterator<T> iterator = list.iterator();
		T temp = null;
		boolean isExist = false;
		while (iterator.hasNext()) {
			temp = iterator.next();
			isExist = (isExist || temp.equals(since));
			if (isExist) {
				iterator.remove();
			}
		}

		return list;
	}
}
