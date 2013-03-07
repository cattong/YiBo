package com.shejiaomao.weibo.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsLevel;

public class Util {

	public static String getVersionName(Context context) {
		String versionName = null;
		if (context == null) {
			return versionName;
		}

		try {			
			String packageName = context.getPackageName();
			PackageManager packageManager = context.getPackageManager();
			versionName = packageManager.getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) { 
			Logger.debug(e.getMessage(), e);
		}
		
		return versionName;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		if (context == null) {
			return versionCode;
		}

		try {			
			String packageName = context.getPackageName();
			PackageManager packageManager = context.getPackageManager();
			versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) { 
			Logger.debug(e.getMessage(), e);
		}
		
		return versionCode;
	}
	
	public static String getApplicationMetaData(Context context, String metaDataName) {
		String metaDataValue = null;
		if (context == null || StringUtil.isEmpty(metaDataName)) {
			return metaDataValue;
		}
		
		String packageName = context.getPackageName();
		ApplicationInfo appInfo;
		try {
			PackageManager packageManager = context.getPackageManager();
			appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			Object obj = appInfo.metaData.get(metaDataName);
			if (obj != null) {
				metaDataValue = obj.toString();
			}
		} catch (NameNotFoundException e) {
			Logger.debug(e.getMessage(), e);
		}
		
        return metaDataValue;
	}
	
	public static String getPassportUsername(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		String username = prefs.getString(Constants.PREFS_NAME_USERNAME, null);
		
		return username;
	}
	
	public static String getPassportAccessToken(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		String accessToken = prefs.getString(Constants.PREFS_NAME_ACCESS_TOKEN, null);
		
		return accessToken;
	}
	
	public static String getAccessToken(Context context) {
		String username = Util.getPassportUsername(context);
		String accessToken = Util.getPassportAccessToken(context);
		
		Logger.debug("username:{}, accessToken:{}", username, accessToken);
		
		//return "863020017969605_M2AtaAmCwYwIyXFImz53h7DHwO3yTSu0";
		return username + "_" + accessToken;
	}
	
	public static int getPoints(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		int points = prefs.getInt(Constants.PREFS_NAME_POINTS, 0);
		
		return points;
	}
	
	public static int getChannelPoints(Context context, String channel) {
		SharedPreferences prefs = context.getSharedPreferences(
			Constants.PREFS_NAME_APP_SETTING, 0);
		int channelPoints = prefs.getInt(channel, 0);
		
		return channelPoints;
	}
	
	public static void setPrefs(Context context, Passport passport) {
		if (context == null || passport == null) {
			return;
		}
		
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_NAME_EMAIL, passport.getEmail());
		//editor.putString(Constants.PREFS_NAME_USERNAME, passport.getUsername());
		editor.putInt(Constants.PREFS_NAME_POINTS, passport.getPoints());
		editor.putInt(Constants.PREFS_NAME_TOTAL_POINTS, passport.getTotalPoints());
		editor.putString(Constants.PREFS_NAME_ACCESS_TOKEN, passport.getAccessToken());
		
		PointsLevel pointsLevel = passport.getPointsLevel();
		if (pointsLevel != null) {
			editor.putString(Constants.PREFS_NAME_POINTS_TITLE, pointsLevel.getTitle());
			editor.putString(Constants.PREFS_NAME_MILITARY_RANK, pointsLevel.getMilitaryRank());
		}
		
		editor.commit();
	}
	
	public static String getPromoterId(Context context) {
		return getApplicationMetaData(context, "PROMOTER_ID");
	}
}
