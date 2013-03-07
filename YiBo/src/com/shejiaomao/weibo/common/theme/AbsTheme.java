package com.shejiaomao.weibo.common.theme;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;

public abstract class AbsTheme {
	private static final String TAG = "AbsTheme";
    
	public static final String PREFS_NAME_THEME_SETTING = "SHEJIAOMAO_THEME_SETTING";
    public static final String PREFS_KEY_PACKAGE_NAME   = "THEME_PACKAGE_NAME";
    public static final String PREFS_KEY_RESOURCE_TYPE  = "THEME_RESOURCE_TYPE";
    public static final String PREFS_KEY_THEME_NAME     = "THEME_NAME";
    
    protected Context context;
    
    public AbsTheme(Context context) {
        this.context = context;
    }
    
	public abstract Drawable getDrawable(String resName);
	
	public abstract Drawable getDrawable(int resId);
	
	public abstract int getColor(String resName);
	
	public abstract int getColor(int resId);
    
	public abstract ColorStateList getColorStateList(String resName);
	
	public abstract ColorStateList getColorStateList(int resId);
	
    public void getTheme() {
    }
    
    public boolean isInstalled(String packageName) {
		boolean isInstalled = false;         
		try {                  
			context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
			isInstalled = true;                
		} catch (NameNotFoundException e) {  
		    Logger.error(e.getMessage(), e);
		}
   
		return isInstalled;  
    }
    
//    public boolean isInstalled(String packageName) {
//    	boolean isInstalled = false; 
//
//        List<PackageInfo> infoList = context.getPackageManager().getInstalledPackages(0);
//        for (PackageInfo info : infoList) {
//            if (info.packageName.equals(packageName)) {
//            	isInstalled = true;
//                break;
//            }
//        }
//      
//        return isInstalled;
//    }
    
    protected Context getPackageContext(Context context, String packageName) {
    	Context packageContext = null;
    	if (StringUtil.isEmpty(packageName)) {
    		return packageContext;
    	}
		try {
			packageContext = context.createPackageContext(
				packageName, Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			Logger.debug(e.getMessage(), e);
		}
		
		return packageContext;
	}
}