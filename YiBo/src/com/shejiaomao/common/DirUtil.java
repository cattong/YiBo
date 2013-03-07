package com.shejiaomao.common;

import java.io.File;

import com.cattong.commons.Logger;

import android.content.Context;
import android.os.Environment;

public class DirUtil {

	public static File getCacheDir(Context context) {
		File cacheFile = null;
		if (context == null) {
			return cacheFile;
		}
		
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			String packageName = context.getPackageName();
			int pos = packageName.lastIndexOf(".");
			String cachePath = Environment.getExternalStorageDirectory().getPath();
			cachePath += "/" + packageName.substring(pos + 1);
			cacheFile = new File(cachePath);
		} else {
			cacheFile = context.getCacheDir();
		}
		
		Logger.info("DownloadCacheDirectory:{}", Environment.getDownloadCacheDirectory().getPath());
		Logger.info("ExternalStorageDirectory:{}", Environment.getExternalStorageDirectory().getPath());
		Logger.info("RootDirectory:{}", Environment.getRootDirectory().getPath());
		
		return cacheFile;
	}
}
