package com.shejiaomao.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.cattong.commons.Logger;

public class CompatibilityUtil {
    public static List<String> listSdk;                //sdk兼容列表;
	private static Method overridePenddingTransition;  //api level 5

    static {
    	listSdk = new ArrayList<String>(10);
    	listSdk.add("1.5");
    	listSdk.add("3");

		try {
			overridePenddingTransition = Activity.class.getMethod(
				"overridePendingTransition", new Class[] {Integer.TYPE, Integer.TYPE}
			);
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
    }
	/*
	 * 解决某些版本和设置，在传递参数EXTRA_OUTPUT，不起作用（1.5不起作用,只能getData())，
	 * 返回只有一半大小图片的bug;
	 */
	public static boolean hasImageCaptureBug() {
	    boolean hasBug = false;
	    String sdk = android.os.Build.VERSION.SDK;
	    for (String tempSdk : listSdk) {
	    	if (sdk.indexOf(tempSdk) != -1) {
	    		hasBug = true;
	    		break;
	    	}
	    }
        if ("me600".equals(getModel().toLowerCase())) {
        	hasBug = false;
        }
	    return hasBug;
	}

	/*
	 * 获得api level
	 */
	public static String getSdkVersion() {
		return android.os.Build.VERSION.SDK;
	}

	/*
	 * 获得固件版本
	 */
    public static String getRelease() {
    	return android.os.Build.VERSION.RELEASE;
    }

	/*
	 * 获得手机型号
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	public static boolean isSdk1_5() {
		String version = getSdkVersion();
		return "3".equals(version);
	}

	public static void overridePendingTransition(Activity activity,
		int enterAnim, int exitAnim) {
		try {
			if (overridePenddingTransition != null) {
			    overridePenddingTransition.invoke(activity, enterAnim, exitAnim);
			}
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
	}
}
