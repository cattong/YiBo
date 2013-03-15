package com.cattong.weibo;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;


public class PagingTest {

	/*测试上翻
	 * list:为已经预提取好的列表.
	 */
	public static <T> void pageUp(List<T> list, Weibo mBlog, Method method) {
		assertTrue(ListUtil.isNotEmpty(list));
		if (list.size() < 6) {
			assertTrue(false);
		}
		
		int size = list.size();
		int middle = size/2;
		T divide = list.get(middle);
		T target = list.get(middle - 1);
		
		Paging<T> pagingUp = new Paging<T>();
		pagingUp.setGlobalSince(divide);
		List<T> listTest = null;
		try {
			listTest = (List<T>)method.invoke(mBlog, pagingUp);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		assertTrue(ListUtil.isNotEmpty(listTest));
		assertTrue(listTest.get(listTest.size()-1).equals(target));
	}
	
	/*
	 * 测试下翻
	 */
	public static <T> void pageDown(List<T> list, Weibo mBlog, Method method) {
		assertTrue(ListUtil.isNotEmpty(list));
		if (list.size() < 6) {
			assertTrue(false);
		}
		
		int size = list.size();
		int middle = size/2;
		T divide = list.get(middle);
		T target = list.get(middle - 1);
		
		Paging<T> pagingDown = new Paging<T>();
		target = list.get(middle + 1);
		pagingDown.setGlobalMax(divide);
		
		List<T> listTest = null;
		try {
			listTest = (List<T>)method.invoke(mBlog, pagingDown);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		assertTrue(ListUtil.isNotEmpty(listTest));
		assertTrue(listTest.get(0).equals(target));
	}
	
	public static <T> void pageDown(List<T> list, Weibo mBlog, 
		Method method, Object args) {
		assertTrue(ListUtil.isNotEmpty(list));
		if (list.size() < 6) {
			assertTrue(false);
		}
		
		int size = list.size();
		int middle = size/2;
		T divide = list.get(middle);
		T target = list.get(middle - 1);
		
		Paging<T> pagingDown = new Paging<T>();
		target = list.get(middle + 1);
		pagingDown.setGlobalMax(divide);
		
		List<T> listTest = null;
		try {
			listTest = (List<T>)method.invoke(mBlog, args, pagingDown);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		assertTrue(ListUtil.isNotEmpty(listTest));
		assertTrue(listTest.get(0).equals(target));
	}
	
	/*
	 * 测试中间展开
	 */
	public static <T> void pageExpand(List<T> list, Weibo mBlog, Method method) {
		assertTrue(ListUtil.isNotEmpty(list));
		if (list.size() < 6) {
			assertTrue(false);
		}
		
		int size = list.size();
		
		Paging<T> pagingExpand = new Paging<T>();
		T max = list.get(0);
		T end = list.get(size - 1);
		T targetMax = list.get(1);
		T targetSince = list.get(size - 2);
		pagingExpand.setGlobalMax(max);
		pagingExpand.setGlobalSince(end);
		
		List<T> listTest = null;
		try {
			listTest = (List<T>)method.invoke(mBlog, pagingExpand);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		assertTrue(ListUtil.isNotEmpty(listTest));
		assertTrue(listTest.get(0).equals(targetMax));
		assertTrue(listTest.get(listTest.size()-1).equals(targetSince));
	}
}
