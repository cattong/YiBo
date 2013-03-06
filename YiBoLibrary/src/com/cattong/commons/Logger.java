package com.cattong.commons;

import org.slf4j.LoggerFactory;

public class Logger {

	public final static int VERBOSE  = 0;
	public final static int DEBUG    = 1;
	public final static int INFO     = 2;
	public final static int WARN     = 3;
	public final static int ERROR    = 4;
	
	public static int level = DEBUG;
	
	private static org.slf4j.Logger logger;
	static {
		logger = LoggerFactory.getLogger("SheJiaoMao");
	}
	
	private Logger() {
		
	}
	
	public static boolean isDebug() {
		return Logger.level <= Logger.DEBUG;
	}
	
	public static void verbose(String logStr) {
		if (level > VERBOSE) {
			return;
		}
		
		logger.trace(logStr);
	}
	
	public static void verbose(String logStr, Object obj) {
		if (level > VERBOSE) {
			return;
		}
		
		logger.trace(logStr, obj);
	}

	public static void verbose(String logStr, Object... objs) {
		if (level > VERBOSE) {
			return;
		}
		
		logger.trace(logStr, objs);
	}

	public static void verbose(String logStr, Throwable e) {
		if (level >= VERBOSE) {
			return;
		}
		
		logger.trace(logStr, e);
	}
	
	public static void debug(String logStr) {
		if (level > DEBUG) {
			return;
		}
		
		logger.debug(logStr);
	}
	
	public static void debug(String logStr, Object obj) {
		if (level > DEBUG) {
			return;
		}
		
		logger.debug(logStr, obj);
	}

	public static void debug(String logStr, Object... objs) {
		if (level > DEBUG) {
			return;
		}
		
		logger.debug(logStr, objs);
	}

	public static void debug(String logStr, Throwable e) {
		if (level > DEBUG) {
			return;
		}
		
		logger.debug(logStr, e);
	}
	
	public static void info(String logStr) {
		if (level > INFO) {
			return;
		}
		
		logger.info(logStr);
	}
	
	public static void info(String logStr, Object obj) {
		if (level > INFO) {
			return;
		}
		
		logger.info(logStr, obj);
	}
	
	public static void info(String logStr, Object... objs) {
		if (level > INFO) {
			return;
		}
		
		logger.info(logStr, objs);
	}

	public static void info(String logStr, Throwable e) {
		if (level > INFO) {
			return;
		}
		
		logger.debug(logStr, e);
	}
	
	public static void warn(String logStr) {
		if (level > WARN) {
			return;
		}
		
		logger.warn(logStr);
	}
	
	public static void warn(String logStr, Object obj) {
		if (level > WARN) {
			return;
		}
		
		logger.warn(logStr, obj);
	}
	
	public static void warn(String logStr, Object... objs) {
		if (level > WARN) {
			return;
		}
		
		logger.warn(logStr, objs);
	}

	public static void warn(String logStr, Throwable e) {
		if (level > WARN) {
			return;
		}
		
		logger.debug(logStr, e);
	}
	
	public static void error(String logStr) {
		if (level > ERROR) {
			return;
		}
		
		logger.error(logStr);
	}
	
	public static void error(String logStr, Object obj) {
		if (level > ERROR) {
			return;
		}
		
		logger.error(logStr, obj);
	}
	
	public static void error(String logStr, Object... objs) {
		if (level > ERROR) {
			return;
		}
		
		logger.error(logStr, objs);
	}
	
	public static void error(String logStr, Throwable e) {
		if (level > ERROR) {
			return;
		}
		
		logger.error(logStr, e);
	}
}
