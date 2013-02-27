package net.dev123.yibo.service.task;

import java.io.File;

import net.dev123.commons.util.FileUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ImageQuality;
import net.dev123.yibo.common.ImageUtil;
import net.dev123.yibo.service.cache.ImageCache;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-11 下午5:38:05
 **/
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
		ImageQuality quality = yibo.getImageUploadQuality();
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
		if(Constants.DEBUG) {
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
	    
	    if (Constants.DEBUG) {
	    	Log.d(TAG, isCompress + " scale upload file, size: " + imageSize);
	    }
		

		if (Constants.DEBUG) {
			Log.d(TAG, "image file: " + image.getAbsolutePath());
		}
		return isCompress;
	}

	public AbstractUpdateStatusTask(EditMicroBlogActivity context, StatusUpdate statusUpdate) {
		this.context = context;
		this.statusUpdate = statusUpdate;
		yibo = (YiBoApplication) context.getApplicationContext();
	}

	private static final String TAG = AbstractUpdateStatusTask.class.getSimpleName();
	protected EditMicroBlogActivity context;
	protected YiBoApplication yibo;
	protected int rotateDegrees;
	protected StatusUpdate statusUpdate;

	public void setRotateDegrees(int rotateDegrees) {
		this.rotateDegrees = rotateDegrees;
	}

	public int getRotateDegrees() {
		return rotateDegrees;
	}
}
