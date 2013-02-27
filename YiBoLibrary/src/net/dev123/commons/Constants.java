package net.dev123.commons;


public class Constants {
	public static final boolean DEBUG = true;

	//网络连接设置
	public static final int CONNECTION_POOL_SIZE = 128; // HTTP连接连接池大小
	public static final int CONNECTION_TIME_OUT = 30000; // 连接池连接超时时间，以毫秒为单位

	private static final String DALVIK = "yibo.dalvik";
	private static boolean IS_DALVIK;

	static {
		// detecting dalvik (Android platform)
		String dalvikDetected;
		try {
			// dalvik.system.VMRuntime class should be existing on Android platform.
			// @see http://developer.android.com/reference/dalvik/system/VMRuntime.html
			Class.forName("dalvik.system.VMRuntime");
			dalvikDetected = "true";
		} catch (ClassNotFoundException cnfe) {
			dalvikDetected = "false";
		}
		IS_DALVIK = Boolean.valueOf(System.getProperty(DALVIK, dalvikDetected));
	}

	public static boolean isDalvik() {
		return IS_DALVIK;
	}

	/** 饭否平台官方微博的ID **/
  	public static final String FANFOU_OFFICAL_USER_ID = "~0jFVfHMEtG4";

	/** 加密配置 */
	public static final byte[] KEY_BYTES  = { 0x6f, 0x68, 0x6d, 0x79, 0x67, 0x6f, 0x64, 0x21 };
	
	/** 用于探测url service是否可用 **/
	public static final String URL_SERVICE_DETECT = "urlServiceDetect";
	
	public static final byte[] PUBLIC_KEY = 
		("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQ580pDZVPZteKsMhzKO60hIKgN7" +
		"YoWWEENRBv9m0aWc18CwWZV72MeZdAlD7/DrUmIwzBFLRXT7GsdlTpJNbJCrgz4+U9" +
		"D7uE3la88IoCJpG1ZmrE4UqfdsDH0XcXnuwCO8lnV9rnelFuCXqK5TXieQcHA+gbLT" +
		"1UU9m3cXiERQIDAQAB").getBytes();
}
