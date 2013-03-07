package com.shejiaomao.weibo.service.task;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.os.AsyncTask;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.service.cache.ImageCache;

public class ImageCacheQuickCleanTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "ImageCacheQuickCleanTask";	
	private static final int NORMAL_EXPIRED_DAYS = 10;
	private static final int HEAD_EXPIRED_DAYS = 20;
	
	private Activity context;
	public ImageCacheQuickCleanTask(Activity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		clearCachedImages();
		return true;
	}

	private boolean clearCachedImages() {		
		int cleanCount = cleanExpiredImage();
		
		Logger.debug(TAG, "clean image count: " + cleanCount);

		return true;
	}
	
	private int cleanExpiredImage() {
		int cleanCount = 0;

		File cacheFolder = new File(SheJiaoMaoApplication.getSdcardCachePath());
		File innerCacheFolder = new File(SheJiaoMaoApplication.getInnerCachePath());
		FileFilter filter = null;
		if (innerCacheFolder.isDirectory()) {
			filter = null;
			for (File folder : innerCacheFolder.listFiles()) {
				String path = folder.getAbsolutePath();
				if (!folder.isDirectory()
					|| path.endsWith(ImageCache.IMAGE_EMOTIONS)) {
					continue;
				}

				filter = normalFileFilter;
				if (folder.getAbsolutePath().endsWith(ImageCache.IMAGE_HEAD_MINI)
					|| folder.getAbsolutePath().endsWith(ImageCache.IMAGE_HEAD_NORMAL)) {
					filter = headFileFilter;
				}
				
				for (File file : folder.listFiles(filter)) {
					file.delete();
					//Log.d(TAG, "deleting: " + file.getAbsolutePath());
					cleanCount++;
				}				
			}
		}
		
		if (cacheFolder.isDirectory()) {
			filter = null;
			for (File folder : cacheFolder.listFiles()) {
				String path = folder.getAbsolutePath();
				if (!folder.isDirectory() 
					|| path.endsWith(ImageCache.IMAGE_EMOTIONS)) {
					continue;
				}
				
				filter = normalFileFilter;
				if (path.endsWith(ImageCache.IMAGE_HEAD_MINI)
					|| path.endsWith(ImageCache.IMAGE_HEAD_NORMAL)) {
					filter = headFileFilter;
				}
				
				for (File file : folder.listFiles(filter)) {
					file.delete();
					//Log.d(TAG, "deleting: " + file.getAbsolutePath());
					cleanCount++;
				}
			}
		}
		
		return cleanCount;
	}
	
	ExpiredTimeFilter normalFileFilter = new ExpiredTimeFilter(NORMAL_EXPIRED_DAYS);
	ExpiredTimeFilter headFileFilter = new ExpiredTimeFilter(HEAD_EXPIRED_DAYS);
	private class ExpiredTimeFilter implements FileFilter {
        private long expiredTime;
        
        public ExpiredTimeFilter(int remainDays) {
        	long intervalTime = 1000 * 60 * 60 * 24; //1天的毫秒值
    		expiredTime = System.currentTimeMillis() - remainDays * intervalTime;
        }
        
		@Override
		public boolean accept(File file) {
			if (file.lastModified() > expiredTime) {
				return false;
			}
			return true;
		}
	};
}
