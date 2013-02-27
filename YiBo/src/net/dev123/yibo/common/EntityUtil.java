package net.dev123.yibo.common;

import net.dev123.commons.util.StringUtil;
import net.dev123.mblog.entity.Status;
import net.dev123.yibo.service.cache.ImageCache;
import net.dev123.yibo.service.cache.wrap.CachedImageKey;

public class EntityUtil {

	public static String getThumbnailPicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getThumbnailPicture();
		} else {
			picture = status.getThumbnailPicture();
		}
		return picture;
	}
	
	public static String getMiddlePicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getMiddlePicture();
		} else {
			picture = status.getMiddlePicture();
		}
		return picture;
	}
	
	public static String getOriginalPicture(Status status) {
		String picture = null;
		if (status == null) {
			return null;
		}
		if (status.getRetweetedStatus() != null) {
			picture = status.getRetweetedStatus().getOriginalPicture();
		} else {
			picture = status.getOriginalPicture();
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
