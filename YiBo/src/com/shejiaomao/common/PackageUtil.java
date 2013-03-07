package com.shejiaomao.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.cattong.commons.Logger;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.AppInfo;
import com.cattong.entity.Os;

public class PackageUtil {

	/** 
	 * 查询手机内所有应用 
	 * @param context 
	 * @return 
	 */  
	public static List<PackageInfo> getAllPackageInfos(Context context) {	    
	    PackageManager pManager = context.getPackageManager();  
	    //获取手机内所有应用  
	    List<PackageInfo> appList = pManager.getInstalledPackages(0);
	    
	    return appList;
	}
	
	public static List<AppInfo> getAllAppInfos(Context context) {
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		
	    List<PackageInfo> packageInfoList = getAllPackageInfos(context);	    
	    if (ListUtil.isEmpty(packageInfoList)) {
	    	return appInfoList;
	    }
	    
	    PackageManager pManager = context.getPackageManager();
	    for (PackageInfo packageInfo : packageInfoList) {
	    	AppInfo appInfo = new AppInfo();
	    	appInfo.setAppId(packageInfo.applicationInfo.packageName);
	    	String appName = pManager.getApplicationLabel(packageInfo.applicationInfo).toString();
	    	appInfo.setAppName(appName);
	    	appInfo.setPackageName(packageInfo.applicationInfo.packageName);
	    	appInfo.setOs(Os.Android);
	    	appInfo.setVersionName(packageInfo.versionName == null ? "unknow" : packageInfo.versionName);
	    	appInfo.setVersionCode("" + packageInfo.versionCode);
	    	
	    	appInfoList.add(appInfo);
	    }
	    
	    return appInfoList;
	}
	
	/** 
	 * 查询手机内用户安装的应用 
	 * @param context 
	 * @return 
	 */ 
	public static List<PackageInfo> getUserPackageInfos(Context context) {
	    List<PackageInfo> appList = new ArrayList<PackageInfo>();
	    
	    PackageManager pManager = context.getPackageManager();
	    //获取手机内所有应用  
	    List<PackageInfo> packageInfoList = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < packageInfoList.size(); i++) {  
	        PackageInfo packageInfo = (PackageInfo) packageInfoList.get(i);
	        Logger.info("name:{};package name:{}", 
	        	pManager.getApplicationLabel(packageInfo.applicationInfo),
	        	packageInfo.applicationInfo.packageName);
	        //判断是否为非系统预装的应用程序  
	        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
	            // customs applications  
	        	appList.add(packageInfo);  
	        }
	    } 
	    
	    return appList;  
	}
	
	/** 
	 * 查询手机内的系统应用 
	 * @param context 
	 * @return 
	 */ 
	public static List<PackageInfo> getSystemPackageInfos(Context context) {
	    List<PackageInfo> appList = new ArrayList<PackageInfo>(); 
	    
	    PackageManager pManager = context.getPackageManager();
	    //获取手机内所有应用  
	    List<PackageInfo> packageInfoList = pManager.getInstalledPackages(0);
	    for (int i = 0; i < packageInfoList.size(); i++) {
	        PackageInfo packageInfo = (PackageInfo) packageInfoList.get(i);
	        //判断是否为系统预装的应用程序  
	        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
	        	appList.add(packageInfo);
	        }
	    }  
	    
	    return appList;  
	}
	
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageInfo packageInfo = null;
		if (context == null || StringUtil.isEmpty(packageName)) {
			return packageInfo;
		}
		
		PackageManager packageManager = context.getPackageManager();		
		try {
			packageInfo = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			Logger.debug(e.getMessage(), e);
		}
		
		return packageInfo;
	}
	
	public static void startApp(Context context, String packageName) {
		if (context == null || StringUtil.isEmpty(packageName)) {
			return;
		}
		
		PackageManager packageManager = context.getPackageManager();		
		
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		context.startActivity(intent);
	}
}
