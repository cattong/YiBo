package com.shejiaomao.weibo.common.theme;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shejiaomao.maobo.R;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.common.Constants;

public class Theme extends AbsTheme {
	private static final String TAG = "Theme";
	
    public static final int RESOURCES_FROM_APK = 1;
    public static final int RESOURCES_FROM_ASSETS = 2;
    public static final int RESOURCES_FROM_SD = 3;
    public static final int RESOURCES_FROM_THIS = 4;
    
    public static String currentPackageName;
    private static String themeName;
    private static int themeType;
    private static final Object mStaticInit = new Object();
    private static boolean isInitialize;
    private static float density;
    
    private Context themeContext = null;
  
    static {
    	//currentPackageName = Constants.DEFAULT_PACKAGE_NAME;
    	isInitialize = false;
    }
    
    public Theme(Context context) {
        super(context);
        currentPackageName = context.getPackageName();
        getTheme();
    }
    
    @Override
	public Drawable getDrawable(String resName) {
		Drawable drawable = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "drawable", currentPackageName);
			drawable= res.getDrawable(resId);
		} catch (NotFoundException e) {
			if (Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
		}
		
		return drawable;
	}
	
	@Override
	public Drawable getDrawable(int resId) {
		return context.getResources().getDrawable(resId);
	}

	public Drawable getDrawableByColor(String resName) {
		Drawable drawable = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", currentPackageName);
			drawable= res.getDrawable(resId);
		} catch (NotFoundException e) {
			if (Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
		}
		
		return drawable;
	}
    
	@Override
	public int getColor(String resName) {
		int color = 0;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", currentPackageName);
			color = res.getColor(resId);
		} catch (NotFoundException e) {
			if (Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
		}
		
		return color;
	}

	@Override
	public int getColor(int resId) {
		return context.getResources().getColor(resId);
	}
	
    public Context getContext() {
    	return context;
    }
    
	@Override
	public ColorStateList getColorStateList(String resName) {
		ColorStateList colorStateList = null;
		
		try {
			Resources res = context.getResources();
			int resId = res.getIdentifier(resName, "color", currentPackageName);
			colorStateList = res.getColorStateList(resId);
		} catch (NotFoundException e) {
			if (Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
		}
		
		return colorStateList;
	}
	
	@Override
	public ColorStateList getColorStateList(int resId) {
		return context.getResources().getColorStateList(resId);
	}
	
    public String getCurrentSkinVersion(String paramString) {
    	String versionName = context.getResources().getString(R.string.defaultVersion);
		try {
			versionName = context.getPackageManager()
			    .getPackageInfo(currentPackageName, 0).versionName;
		} catch (NameNotFoundException e) {
			// 什么都不做
		}
		return versionName;
    }
    
    public String getResourcesPathInAssets(String resName) {
		DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        String drawableDpi;
        switch (metrics.densityDpi) {
        case DisplayMetrics.DENSITY_LOW:
        	drawableDpi = "drawable-ldpi";
        	break;
        case DisplayMetrics.DENSITY_MEDIUM:
        	drawableDpi = "drawable-mdpi";
        	break;
        case DisplayMetrics.DENSITY_HIGH:
        	drawableDpi = "drawable-hdpi";
        	break;
        case 320:
        	drawableDpi = "drawable-xhdpi";
        	break;
        default:
        	drawableDpi = "drawable-mdpi";
        }

        return currentPackageName + "/" + drawableDpi + "/" + resName + ".png";         
    }
    
    public Uri getResourcesUri(String resName) {
    	DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        String drawableDpi;
        switch (metrics.densityDpi) {
        case DisplayMetrics.DENSITY_LOW:
        	drawableDpi = "drawable-ldpi";
        	break;
        case DisplayMetrics.DENSITY_MEDIUM:
        	drawableDpi = "drawable-mdpi";
        	break;
        case DisplayMetrics.DENSITY_HIGH:
        	drawableDpi = "drawable-hdpi";
        	break;
        case 320:
        	drawableDpi = "drawable-xhdpi";
        	break;
        default:
        	drawableDpi = "drawable-mdpi";
        }

        File sdFolder = Environment.getExternalStorageDirectory();
        StringBuilder sb = new StringBuilder("/.sheJiaoMao/skins/");
        sb.append(drawableDpi);
        sb.append("/");
        sb.append(resName + ".png");
        File resFile = new File(sdFolder, sb.toString());
        Uri resUri = null;
        if (resFile.exists()) {
        	resUri = Uri.parse(resFile.getAbsolutePath());
        }
        
        return resUri;
    }
    
    @Override
    public void getTheme() {
        super.getTheme();
        if (!isInitialize) {
        	staticInit();
        }

        themeContext = getPackageContext(context, currentPackageName);
        if (themeContext != null) {
            context = themeContext;
        }
    }
    
	public int dip2px(int dipValue) {
		return (int) (dipValue * density + 0.5f);
	}

	public int px2dip(int pxValue) {
		return (int) (pxValue / density + 0.5f);
	}

	public int dip2px(float dipValue) {
		return (int) (dipValue * density + 0.5f);
	}

	public int px2dip(float pxValue) {
		return (int) (pxValue / density + 0.5f);
	}
	
    private void staticInit() {
        synchronized(mStaticInit) {
        	if (!isInitialize) {
                SharedPreferences preferences = context.getSharedPreferences(
                    	PREFS_NAME_THEME_SETTING, Context.MODE_PRIVATE);
                currentPackageName = preferences.getString(PREFS_KEY_PACKAGE_NAME, context.getPackageName());
                if (!isInstalled(currentPackageName)) {
                	currentPackageName = context.getPackageName();
                }
                    
                int type = preferences.getInt(PREFS_KEY_RESOURCE_TYPE, Context.MODE_WORLD_READABLE);
                this.themeType = type;
                    
                String name = preferences.getString(PREFS_KEY_THEME_NAME, "");
                this.themeName = name;
                
                density = context.getResources().getDisplayMetrics().density;
                
                isInitialize = true;
        	}
        }
    }
}