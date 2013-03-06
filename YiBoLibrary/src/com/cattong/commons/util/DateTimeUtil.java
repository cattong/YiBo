package com.cattong.commons.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.cattong.commons.Logger;

public class DateTimeUtil {
    public static final String LOCAL_SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_LONG_DATE_FORMAT  = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_LONG_DATE_FORMAT  = "yyyy/MM/dd HH:mm:ss";
    public static final String STANDARD_SHORT_DATE_FORMAT = "yyyy/MM/dd";
    
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
    
    public static String getShortFormat(Date date) {
        if (date == null) {
    	    return null;
        }
        return getFormatString(date, LOCAL_SHORT_DATE_FORMAT);
    }
    
    public static String getLongFormat(Date date) {
        if (date == null) {
    	    return null;
        }
        return getFormatString(date, LOCAL_LONG_DATE_FORMAT);
    }
    
    public static String getFormatString(Date date, String dateFormat) {
        if (date == null 
    	    || StringUtil.isEmpty(dateFormat)) {
    	    return null;
        }
    
        return new SimpleDateFormat(dateFormat).format(date);
    }
    
    public static DateFormat getGMTDateFormat() {
    	DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	return dateFormat;
    }
    
    public static Date parse(String dateString, String dateFormat) {
        if (StringUtil.isEmpty(dateString) 
      	    || StringUtil.isEmpty(dateFormat)) {
            return null;
        }
    
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (Exception ex) {
            Logger.debug("debug", ex);
        }
    
        return date;
    }
    
    public static Date parseShortFormat(String dateString) {
        return parse(dateString, LOCAL_SHORT_DATE_FORMAT);
    }
    
    public static Date parseLongFormat(String dateString) {
        return parse(dateString, LOCAL_LONG_DATE_FORMAT);
    }
    
    /**
     * modify the <code>date</code> with <code>offset</code> days <p>
     * 根据offset的值，修改date日期，当为正的时，获得offset天后的日期，当为负时，获得|offset|天前的日期;
     * @param date
     * @param offset negative value return abs(offset) days before date, 
     *               positive value return offset days after date
     * @return date after modified
     */
    public static Date offsetDay(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.DAY_OF_YEAR, (calendar.get(Calendar.DAY_OF_YEAR) + offset));  
        return calendar.getTime();
    }
    
    public static Date offsetHour(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.HOUR_OF_DAY, (calendar.get(Calendar.HOUR_OF_DAY) + offset));  
        return calendar.getTime();
    }
 
    public static Date offsetMinute(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) + offset));  
        return calendar.getTime();
    }

    public static Date offsetSecond(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) + offset));  
        return calendar.getTime();
    }
    
    public static Date convertToShortHour(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	return calendar.getTime();
    }
    
    public static Date convertToShortDate(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	return calendar.getTime();
    }
    
    public static Date convertToShortWeek(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	return calendar.getTime();
    }
    
    public static Date convertToShortMonth(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	return calendar.getTime();
    }
    
    public static Date convertToShortYear(Date date) {
    	if (date == null) {
    		return null;
    	}
    	
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, Calendar.JANUARY);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	return calendar.getTime();
    }
}

