package com.shejiaomao.weibo.service.cache.wrap;

import com.cattong.commons.util.EncryptUtil;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.StringUtil;
import android.net.Uri;

public class CachedImageKey {

	public static final int IMAGE_HEAD_MINI   = 0;
	public static final int IMAGE_HEAD_NORMAL = 1;
	public static final int IMAGE_THUMBNAIL   = 2;
	public static final int IMAGE_MIDDLE      = 3;
	public static final int IMAGE_ORIGIN      = 4;
	public static final int IMAGE_TEMP        = 5;

	private String imageUrl;
	private String cachedName; //md5码
	private int cacheType;

	public CachedImageKey(String imageUrl, int cacheType) {
		if (StringUtil.isEmpty(imageUrl)) {
			throw new IllegalArgumentException("url is null");
		}
		this.imageUrl = imageUrl;
		this.cacheType = cacheType;
		switch(cacheType) {
		case IMAGE_HEAD_MINI: break;
		case IMAGE_HEAD_NORMAL: break;
		case IMAGE_THUMBNAIL: break;
		case IMAGE_MIDDLE: break;
		case IMAGE_ORIGIN: break;
		case IMAGE_TEMP: break;
		default: this.cacheType = IMAGE_TEMP; break;
		}

		String fileExtension = FileUtil.getFileExtensionFromUrl(imageUrl);
		boolean hasExtension = StringUtil.isNotBlank(fileExtension);
		if (!hasExtension) {
			fileExtension = "jpg";
		}
		if (cacheType == IMAGE_MIDDLE || cacheType == IMAGE_ORIGIN) {
			this.cachedName = EncryptUtil.getMD5(imageUrl) + "." + fileExtension;
		} else {
			this.cachedName = Uri.encode(imageUrl);
			if (!hasExtension) {
		       this.cachedName += "." + fileExtension;
			}
		}
	}

	@Override
	public int hashCode() {
		return imageUrl.hashCode() * 10 + cacheType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CachedImageKey other = (CachedImageKey) obj;
		if (cachedName == null) {
			if (other.cachedName != null)
				return false;
		} else if (!cachedName.equals(other.cachedName))
			return false;
		if (cacheType != other.cacheType)
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		return true;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getCachedName() {
		return cachedName;
	}

	public void setCachedName(String cachedName) {
		this.cachedName = cachedName;
	}

	public int getCacheType() {
		return cacheType;
	}

	public void setCacheType(int cacheType) {
		this.cacheType = cacheType;
	}

}
