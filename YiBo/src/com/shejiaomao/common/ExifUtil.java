package com.shejiaomao.common;

import com.cattong.commons.Logger;

import android.media.ExifInterface;
import android.text.TextUtils;

public class ExifUtil
{
    private ExifUtil() {
    }

    public static int getExifRotation(String imgPath) {
    	int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                case ExifInterface.ORIENTATION_NORMAL:
                	rotate = 0;
                	break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                	rotate = 90;
                	break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                	rotate = 180;
                	break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                	rotate = 270;
                	break;
                default:
                	rotate = 0;
                	break;
                }
            }
        } catch (Exception e) {
            Logger.debug("ExifUtil", e);
        }
        
        return rotate;
    }
}