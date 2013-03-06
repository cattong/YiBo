package com.cattong.commons;

import java.util.Date;

import org.junit.Test;

import com.cattong.commons.util.DateTimeUtil;

public class Util {

	@Test
	public void convertToShortHour() {
		Date currentDate = new Date();
		
		Date result = DateTimeUtil.convertToShortHour(currentDate);
		System.out.println(DateTimeUtil.getLongFormat(result));
		
		result = DateTimeUtil.convertToShortDate(currentDate);
		System.out.println(DateTimeUtil.getLongFormat(result));
		
		result = DateTimeUtil.convertToShortWeek(currentDate);
		System.out.println(DateTimeUtil.getLongFormat(result));
		
		result = DateTimeUtil.convertToShortMonth(currentDate);
		System.out.println(DateTimeUtil.getLongFormat(result));
		
		result = DateTimeUtil.convertToShortYear(currentDate);
		System.out.println(DateTimeUtil.getLongFormat(result));
	}
	
	@Test
	public void parseDate() {
		Date result = DateTimeUtil.parseLongFormat("2012-11-16");
		System.out.println(DateTimeUtil.getLongFormat(result));
	}
}
