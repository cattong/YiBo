package net.dev123.mblog;

import net.dev123.commons.http.HttpRequestHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestHelper.class.getSimpleName());
	private static final long SLEEP_TIME = 5000;

	public static void sleep() {
		sleep(SLEEP_TIME);
	}

	public static void sleep(long millis) {
		try {
			logger.debug("Thread sleep {} millis", millis);
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
