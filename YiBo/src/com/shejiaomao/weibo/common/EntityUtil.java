package com.shejiaomao.weibo.common;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Status;

import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;

public class EntityUtil {

	public static String getThumbnailPicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getThumbnailPictureUrl();
		} else {
			picture = status.getThumbnailPictureUrl();
		}
		return picture;
	}
	
	public static String getMiddlePicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getMiddlePictureUrl();
		} else {
			picture = status.getMiddlePictureUrl();
		}
		return picture;
	}
	
	public static String getOriginalPicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getOriginalPictureUrl();
		} else {
			picture = status.getOriginalPictureUrl();
		}
		return picture;
	}
	
	//获得本地缓冲的最大图片信息
	public static CachedImageKey getMaxLocalCachedImageInfo(Status status) {
    	CachedImageKey info = null;
    	String imagePath = null;
        if (status == null || !hasPicture(status)) {
        	return info;
        }
        
		String imageUrl = getOriginalPicture(status);
		info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_MIDDLE);
		imagePath = ImageCache.getRealPath(info);
		if (StringUtil.isNotEmpty(imagePath)) {
			return info;
		}
		
		imageUrl = getMiddlePicture(status);
		info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_MIDDLE);
		imagePath = ImageCache.getRealPath(info);
		if (StringUtil.isNotEmpty(imagePath)) {
			return info;
		}
		
		imageUrl = getThumbnailPicture(status);
		info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_THUMBNAIL);
		imagePath = ImageCache.getRealPath(info);
		return info;		
	}
	
    //获得本地缓冲的最大图片路径
    public static String getMaxLocalCachedPicture(Status status) {
    	String imagePath = null;
        if (status == null || !hasPicture(status)) {
        	return imagePath;
        }
        
		String imageUrl = getOriginalPicture(status);
		CachedImageKey info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_MIDDLE);
		imagePath = ImageCache.getRealPath(info);
		if (StringUtil.isNotEmpty(imagePath)) {
			return imagePath;
		}
		
		imageUrl = getMiddlePicture(status);
		info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_MIDDLE);
		imagePath = ImageCache.getRealPath(info);
		if (StringUtil.isNotEmpty(imagePath)) {
			return imagePath;
		}
		
		imageUrl = getThumbnailPicture(status);
		info = new CachedImageKey(imageUrl, CachedImageKey.IMAGE_THUMBNAIL);
		imagePath = ImageCache.getRealPath(info);
		return imagePath;		
    }
    
    public static boolean hasPicture(Status status) {
    	String imageUrl = getThumbnailPicture(status);
    	return StringUtil.isNotEmpty(imageUrl);
    }
}
