package com.cattong.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeSpanUtil {
	private static final int ONE_HOUR_IN_SECONDS = 60 * 60;
	private static final int TWO_HOUR_IN_SECONDS = ONE_HOUR_IN_SECONDS * 2;
	private static final int ONE_DAY_IN_SECONDS  = 24 * ONE_HOUR_IN_SECONDS;
	private static long TODAY_IN_SECONDS;
    private static long THIS_YEAR_IN_SECONDS;
    
	private static SimpleDateFormat timeDateFormat  = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private static SimpleDateFormat yearDateFormat  = new SimpleDateFormat("yy-MM-dd HH:mm");

	public static String timeFormatWithinSeconds   = " within %1$d seconds";
	public static String timeFormatHalfMinuteAgo   = " half minute ago";
	public static String timeFormatWithinOneMinute = " within 1 minute";
	public static String timeFormatOneMinuteAgo    = " 1 minute ago";
	public static String timeFormatMinutesAgo      = " %1$d minutes ago";
	public static String timeFormatToday           = " Today %1$s";
	public static String timeFormatOneHourAgo      = " 1 hour ago";
	public static String timeFormatHoursAgo        = " %1$d hours ago";
	public static String timeFormatOneDayAgo       = " 1 day ago";
	public static String timeFormatDaysAgo         = " %1$d days ago";
	public static String timeFormatWeeksAgo        = " %1$d weeks ago";

	static {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		TODAY_IN_SECONDS = (System.currentTimeMillis() - calendar.getTimeInMillis()) / 1000;
		
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		THIS_YEAR_IN_SECONDS = (System.currentTimeMillis() - calendar.getTimeInMillis()) / 1000;
	}
	
	private TimeSpanUtil() {
		throw new AssertionError("not intended to be instantiated.");
	}

	public static String toTimeSpanString(Date date) {
		if (date == null) {
			return "";
		}

		long milliseconds = date.getTime();
		int deltaInSeconds = (int) ((System.currentTimeMillis() - milliseconds) / 1000);
        if (deltaInSeconds <= ONE_HOUR_IN_SECONDS) {
        	return toTimeSpanString(deltaInSeconds);
        } else if (deltaInSeconds <= TODAY_IN_SECONDS) {
		    return String.format(timeFormatToday, timeDateFormat.format(date));
		} else if (deltaInSeconds <= THIS_YEAR_IN_SECONDS) {
			return monthDateFormat.format(date);
		}

		return yearDateFormat.format(date);
	}

	public static String toTimeSpanString(int deltaInSeconds) {
		if (deltaInSeconds < 5) {
			return String.format(timeFormatWithinSeconds, 5);
		} else if (deltaInSeconds < 10) {
			return String.format(timeFormatWithinSeconds, 10);
		} else if (deltaInSeconds < 20) {
			return String.format(timeFormatWithinSeconds, 20);
		} else if (deltaInSeconds < 40) {
			return timeFormatHalfMinuteAgo;
		} else if (deltaInSeconds < 60) {
			return timeFormatWithinOneMinute;
		}

		if (deltaInSeconds < ONE_HOUR_IN_SECONDS) {
			int minutes = deltaInSeconds / 60;
			if (minutes == 1) {
				return timeFormatOneMinuteAgo;
			}
			return String.format(timeFormatMinutesAgo, minutes);
		}

		if (deltaInSeconds < TWO_HOUR_IN_SECONDS) {
			return timeFormatOneHourAgo;
		}
		if (deltaInSeconds < ONE_DAY_IN_SECONDS) {
			int hours = ((deltaInSeconds + 15 * 60) / ONE_HOUR_IN_SECONDS);
			if (hours < 24) {
				return String.format(timeFormatHoursAgo, hours);
			}
		}
		if (deltaInSeconds < 2 * ONE_DAY_IN_SECONDS) {
			return timeFormatOneDayAgo;
		}

		if (deltaInSeconds < 14 * ONE_DAY_IN_SECONDS) {
		    int days = deltaInSeconds / ONE_DAY_IN_SECONDS;
		    return String.format(timeFormatDaysAgo, days);
		}

		int weeks = deltaInSeconds / (ONE_DAY_IN_SECONDS * 7);
		return String.format(timeFormatWeeksAgo, weeks);

	}
}
