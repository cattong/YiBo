package com.shejiaomao.weibo.service.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImage;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;

/*
 * 要确保进度条在imageView 后面
 */
public class ImageLoad4ThumbnailTask extends AsyncTask<com.cattong.entity.Status, Void, CachedImage> {
	private static final String TAG = "ImageLoad4ThumbnailTask";
	private static final String NET_EASE_TAG  = "_thumbnail";
	private ImageCache imageCache;

	private ImageView imageView;
	private TextView tvImageInfo;
	private CachedImageKey cachedImageKey;
	private String url;
	private boolean isMemoryHit = false;
	private boolean isHit = false;
	private Bitmap bitmap;
	private CachedImage cachedImage = null;

	private ProgressBar pBar;
	private String orignUrl;
	private String resultMsg;
	public ImageLoad4ThumbnailTask(ImageView imageView, String url) {
	    this.imageView = imageView;
	    this.orignUrl = url;
	    if (isNetEase(url)) {
	    	this.url = url + NET_EASE_TAG;
	    } else {
	    	this.url = url;
	    }
	    Context context = imageView.getContext();
	    ViewGroup viewGroup = (ViewGroup)imageView.getParent();
	    if (context instanceof MicroBlogActivity) {
		    this.pBar = (ProgressBar)viewGroup.getChildAt(1);
		    this.tvImageInfo = (TextView)((ViewGroup)viewGroup.getParent()).getChildAt(1);
	    } else {
	    	View view = viewGroup.getChildAt(1);
	    	if (view instanceof TextView) {
	    	    this.tvImageInfo = (TextView)viewGroup.getChildAt(1);
	    	}
	    }
	    
	    imageCache = (ImageCache)CacheManager.getInstance().getCache(ImageCache.class.getName());
	    cachedImageKey = new CachedImageKey(url, CachedImageKey.IMAGE_THUMBNAIL);
	    isHit = imageCache.containsKey(cachedImageKey);
	    if (isHit && (cachedImage = imageCache.getMemoryCached(cachedImageKey)) != null) {
	    	bitmap = cachedImage.getWrap();
	    	isMemoryHit = true;
    	}
	    
	    if (!isMemoryHit) {
	    	imageView.setImageDrawable(GlobalResource.getDefaultThumbnail(imageView.getContext()));
	    } else {
	    	imageView.setImageBitmap(bitmap);
	    }
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (tvImageInfo != null) {
        	tvImageInfo.setVisibility(View.GONE);
        	tvImageInfo.setText("");
        }
		if (isMemoryHit && imageView != null) {
			cancel(true);
			onPostExecute(cachedImage);
			return;
		}
		if (GlobalVars.NET_TYPE == NetType.NONE) {
			cancel(true);
			resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, imageView.getContext());
			onPostExecute(null);
		}
	}

	@Override
	protected CachedImage doInBackground(com.cattong.entity.Status... params) {
		if (imageView == null || url == null) {
			return cachedImage;
		}
		if (isHit) {
        	cachedImage = imageCache.get(cachedImageKey);
        	bitmap = cachedImage.getWrap();
        	return cachedImage;
        }
		
		String bigImageUrl = null;
		if (params.length == 1) {
			com.cattong.entity.Status status = params[0];
			bigImageUrl = StatusUtil.getBigImageUrl(status);
		}

		if(Logger.isDebug()) Log.d(TAG, "Get thumbnail image from remote!");
		try {
			byte[] imageData = ImageUtil.getByteArrayByUrl(orignUrl);
			bitmap = ImageUtil.decodeByteArray(imageData);

			if (bitmap == null) {
				return null;
			}
			cachedImage = new CachedImage(bitmap);

			int maxWidth = 120 * SheJiaoMaoApplication.getDensityDpi() / DisplayMetrics.DENSITY_DEFAULT;
			if (isNetEase(orignUrl)	
				&& (bitmap.getWidth() > maxWidth 
				|| bitmap.getHeight() > maxWidth)) {
				CachedImage midImgWrap = new CachedImage(bitmap);
				CachedImageKey midImgInfo = new CachedImageKey(orignUrl, CachedImageKey.IMAGE_MIDDLE);
				//直接写入文件，不使用bitmap的压缩写入
				ImageCache.write(midImgInfo, imageData);
				midImgWrap.setLocalCached(true);
				imageCache.put(midImgInfo, midImgWrap);

				Bitmap thumbnailMap = ImageUtil.scaleBitmapTo(bitmap, maxWidth);
				//销毁网易的中图
				if (!(thumbnailMap == bitmap || bitmap.isRecycled())) {
					bitmap.recycle();
				}
				bitmap = thumbnailMap;

				cachedImage = new CachedImage(bitmap);
			} else {
				//直接写入文件，不使用bitmap的压缩写入
				ImageCache.write(cachedImageKey, imageData);
				cachedImage.setLocalCached(true);
			}

			/**加入cache中**/
			imageCache.put(cachedImageKey, cachedImage);
			
		} catch (LibException e) {
			if(Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), imageView.getContext());
		}

		return cachedImage;
	}

	@Override
	protected void onPostExecute(CachedImage result) {
		super.onPostExecute(result);

		if (result != null) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(bitmap);			
			if (Logger.isDebug()) Log.v(TAG, "update imageview");
		} else {
			imageView.setVisibility(View.GONE);
			if (Logger.isDebug() && resultMsg != null) {
				Toast.makeText(imageView.getContext(),
					resultMsg, Toast.LENGTH_SHORT).show();
			}
		}

		if (pBar != null) {
			imageView.setVisibility(View.VISIBLE);
		    pBar.setVisibility(View.GONE);
		}
	}

	private boolean isNetEase(String url) {
		if (StringUtil.isEmpty(url)) {
			return false;
		}

		return url.indexOf(Constants.NET_EASE_IMAGE_URL_PREFIX) != -1;
	}
}
