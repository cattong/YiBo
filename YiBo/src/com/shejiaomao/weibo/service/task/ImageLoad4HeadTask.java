package com.shejiaomao.weibo.service.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.common.NetType;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImage;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;

public class ImageLoad4HeadTask extends AsyncTask<Void, Void, Bitmap> {
	private static final String LOG_TAG = "ImageLoad4HeadTask";
	private ImageCache imageCache;

	private ImageView imageView;
	private CachedImageKey imageInfo;
	private boolean isHit = false;
	private Bitmap bitmap;
	private CachedImage wrap = null;
	private String url = null;
    private boolean isMini;

	public ImageLoad4HeadTask(ImageView imageView, String url, boolean isMini) {
	    this.imageView = imageView;
	    this.url = url;
	    this.isMini = isMini;

	    imageCache = (ImageCache)CacheManager.getInstance().getCache(ImageCache.class.getName());
	    if (StringUtil.isNotEmpty(url)) {
	    	if (isMini) {
	    		imageInfo = new CachedImageKey(url, CachedImageKey.IMAGE_HEAD_MINI);
	    	} else {
	    		imageInfo = new CachedImageKey(url, CachedImageKey.IMAGE_HEAD_NORMAL);
	    	}
	    	if ((wrap = imageCache.get(imageInfo)) != null) {
		    	bitmap = wrap.getWrap();
				isHit = true;
	    	}
	    }
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (StringUtil.isEmpty(url)) {
			imageView.setImageDrawable(
				GlobalResource.getDefaultMinHeader(imageView.getContext()));
			cancel(true);
		}
		if (isHit && imageView != null) {
			wrap.hit();
			onPostExecute(bitmap);

	        cancel(true);
	        return;
		}
		if (GlobalVars.NET_TYPE == NetType.NONE) {
			cancel(true);
		}
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		if (imageView == null || url == null) {
			return bitmap;
		}

		if(Logger.isDebug()) Log.i(LOG_TAG, "Get Header image from remote!");
		try {
			Bitmap newBitmap = ImageUtil.getBitmapByUrl(url);

			/**加入cache中**/
			if (newBitmap != null) {
				//生成mini图
				Bitmap scaleBitmap = ImageUtil.scaleBitmapTo(newBitmap, SheJiaoMaoApplication.getSmallAvatarSize());
				Bitmap roundBitmap = ImageUtil.getRoundedCornerBitmap(scaleBitmap);
				if (scaleBitmap != newBitmap) {
					scaleBitmap.recycle();
				}

				CachedImage resultWrap = new CachedImage(roundBitmap);
				if (isMini) {
					bitmap = roundBitmap;
				}
				imageInfo.setCacheType(CachedImageKey.IMAGE_HEAD_MINI);
			    imageCache.put(imageInfo, resultWrap);

			    //生成normal图
			    scaleBitmap = ImageUtil.scaleBitmapTo(newBitmap, SheJiaoMaoApplication.getNormalAvatarSize());
			    roundBitmap = ImageUtil.getRoundedCornerBitmap(scaleBitmap);
				scaleBitmap.recycle();

				resultWrap = new CachedImage(roundBitmap);
				imageInfo.setCacheType(CachedImageKey.IMAGE_HEAD_NORMAL);
			    imageCache.put(imageInfo, resultWrap);

			    newBitmap.recycle();
				if (!isMini) {
					bitmap = roundBitmap;
				}
			}
		} catch (LibException e) {
			if(Logger.isDebug()) Log.e(LOG_TAG, e.getMessage(), e);
		}

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		if (result != null) {
		    imageView.setImageBitmap(result);
			if(Logger.isDebug()) Log.v(LOG_TAG, "update imageview");
		}
	}


}
