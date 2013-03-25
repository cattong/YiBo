package com.shejiaomao.weibo.service.task;

import java.io.File;

import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.Logger;
import com.cattong.commons.util.FileUtil;
import com.cattong.entity.StatusUpdate;
import com.shejiaomao.common.ImageQuality;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.cache.ImageCache;

public abstract class AbstractUpdateStatusTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	protected boolean rotateImage() {
		boolean isRotated = false;
		File image = statusUpdate.getImage();
		if (rotateDegrees != 0F) {
			String imagePath = ImageCache.getTempFolder()
				+ File.separator + System.currentTimeMillis() + "." 
				+ FileUtil.getFileExtensionFromName(image.getName());
			File rotatedImage = new File(imagePath);
			isRotated = ImageUtil.rotateImageFile(image, rotatedImage, rotateDegrees);
			if (isRotated) {
				image = rotatedImage;
		        statusUpdate.setImage(image);
			}
		}
		return isRotated;
	}

	protected boolean compressImage() {
		boolean isCompress = false;
		File image = statusUpdate.getImage();
		ImageQuality quality = sheJiaoMao.getImageUploadQuality();
		if (quality == ImageQuality.Un_Disposal 
			|| FileUtil.isGif(image.getAbsolutePath())) {
			return isCompress;
		}
		
		int imageSize = ImageQuality.Low.getSize();		
		if (quality == ImageQuality.Adaptive_Net) {
			switch (GlobalVars.NET_TYPE) {
			case WIFI:
				quality = ImageQuality.High;
				break;
			case NONE:
			case MOBILE_GPRS:
			case MOBILE_EDGE:
				quality = ImageQuality.Low;
				break;
			case UNKNOW:
			case MOBILE_3G:
				quality = ImageQuality.Middle;
				break;
			}
		}
		if (quality == ImageQuality.High) {
			imageSize = ImageQuality.High.getSize();
		} else if (quality == ImageQuality.Middle) {
			imageSize = ImageQuality.Middle.getSize();
		} else {
			imageSize = ImageQuality.Low.getSize();
		}
		if(Logger.isDebug()) {
			Log.d(TAG, "prefix size: " + imageSize);
		}

	    String destName = ImageCache.getTempFolder() + File.separator +
	        System.currentTimeMillis() + "." 
	    	+ FileUtil.getFileExtensionFromName(image.getName());
	    File dest = new File(destName);
	    isCompress = ImageUtil.scaleImageFile(image, dest, imageSize);
	    if (isCompress) {
	        image = dest;
	        statusUpdate.setImage(image);
	    }
	    
	    if (Logger.isDebug()) {
	    	Log.d(TAG, isCompress + " scale upload file, size: " + imageSize);
	    }
		

		if (Logger.isDebug()) {
			Log.d(TAG, "image file: " + image.getAbsolutePath());
		}
		return isCompress;
	}

	public AbstractUpdateStatusTask(EditMicroBlogActivity context, StatusUpdate statusUpdate) {
		this.context = context;
		this.statusUpdate = statusUpdate;
		sheJiaoMao = (SheJiaoMaoApplication) context.getApplicationContext();
	}

	private static final String TAG = AbstractUpdateStatusTask.class.getSimpleName();
	protected EditMicroBlogActivity context;
	protected SheJiaoMaoApplication sheJiaoMao;
	protected int rotateDegrees;
	protected StatusUpdate statusUpdate;

	public void setRotateDegrees(int rotateDegrees) {
		this.rotateDegrees = rotateDegrees;
	}

	public int getRotateDegrees() {
		return rotateDegrees;
	}
}
