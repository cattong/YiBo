package net.dev123.commons.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
	          ex.printStackTrace();
	      }

	      return date;
	  }

	  public static Date parseNoHour(String dateString) {
		  return parse(dateString, LOCAL_SHORT_DATE_FORMAT);
	  }
	  
	  public static Calendar getCalendarByDate(Date date) {
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTime(date);

	      return calendar;
	  }

	  public static DateFormat getGMTDateFormat() {
			DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			return dateFormat;
	  }
	  
	    /**
	     * modify the <code>date</code> with <code>offset</code> days <p>
	     * 根据offset的值，修改date日期，当为正的时，获得offset天后的日期，当为负时，获得|offset|天前的日期;
	     * @param date
	     * @param offset negative value return abs(offset) days before date, 
	     *               positive value return offset days after date
	     * @return date after modified
	     */
		public static Date changeDay(Date date, int offset){
		    Calendar calendar = Calendar.getInstance();  
		    calendar.setTime(date);  
		    calendar.set(Calendar.DAY_OF_YEAR, (calendar.get(Calendar.DAY_OF_YEAR) + offset));  
		    return calendar.getTime();  
		}
}

