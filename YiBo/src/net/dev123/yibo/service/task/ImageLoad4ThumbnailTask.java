package net.dev123.yibo.service.task;

import java.net.URL;

import net.dev123.commons.ImageInfo;
import net.dev123.commons.ImageInfo.Format;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ImageUtil;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.StatusUtil;
import net.dev123.yibo.service.cache.ImageCache;
import net.dev123.yibo.service.cache.wrap.CachedImage;
import net.dev123.yibo.service.cache.wrap.CachedImageKey;
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

/*
 * 要确保进度条在imageView 后面
 */
public class ImageLoad4ThumbnailTask extends AsyncTask<net.dev123.mblog.entity.Status, Void, CachedImage> {
	private static final String TAG = "ImageLoad4ThumbnailTask";
	private static final String NET_EASE_TAG  = "_thumbnail";
	private ImageCache imageCache;

	private ImageView imageView;
	private TextView tvImageInfo;
	private CachedImageKey key;
	private String url;
	private boolean isHit = false;
	private Bitmap bitmap;
	private CachedImage wrap = null;

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
	    
		init();
	}
	
	private void init() {
	    imageCache = (ImageCache)CacheManager.getInstance().getCache(ImageCache.class.getName());
        //if (Constants.DEBUG) imageCache.stat();
	    key = new CachedImageKey(url, CachedImageKey.IMAGE_THUMBNAIL);
	    if (url != null
	    	&& (wrap = imageCache.get(key)) != null) {
		    bitmap = wrap.getWrap();
			isHit = true;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (tvImageInfo != null) {
        	tvImageInfo.setVisibility(View.GONE);
        	tvImageInfo.setText("");
        }
		if (isHit && imageView != null) {
			cancel(true);
			onPostExecute(wrap);
			return;
		}
		if (GlobalVars.NET_TYPE == NetType.NONE) {
			cancel(true);
			resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, imageView.getContext());
			onPostExecute(null);
		}
	}

	@Override
	protected CachedImage doInBackground(net.dev123.mblog.entity.Status... params) {
		if (imageView == null || url == null) {
			return wrap;
		}
		String bigImageUrl = null;
		if (params.length == 1) {
			net.dev123.mblog.entity.Status status = params[0];
			bigImageUrl = StatusUtil.getBigImageUrl(status);
		}

		if(Constants.DEBUG) Log.d(TAG, "Get thumbnail image from remote!");
		try {
			byte[] imageData = ImageUtil.getByteArrayByUrl(orignUrl);
			bitmap = ImageUtil.decodeByteArray(imageData);

			if (bitmap == null) {
				return null;
			}
			wrap = new CachedImage(bitmap);

			int maxWidth = 120 * YiBoApplication.getDensityDpi() / DisplayMetrics.DENSITY_DEFAULT;
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

				wrap = new CachedImage(bitmap);
			} else {
				//直接写入文件，不使用bitmap的压缩写入
				ImageCache.write(key, imageData);
				wrap.setLocalCached(true);
			}

			/**加入cache中**/
			imageCache.put(key, wrap);
			
			//获取ImageInfo;
			if (GlobalVars.IS_DETECT_IAMGE_INFO 
				&& StringUtil.isNotEmpty(bigImageUrl) 
				&& key != null) {
				try {
					ImageInfo imageInfo = null;
					URL imageUrl = new URL(bigImageUrl);
					imageInfo = ImageInfo.getImageInfo(imageUrl);
				    wrap.setImageInfo(imageInfo);
				} catch (Exception e) {
					if(Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
				}
			}
		} catch (LibException e) {
			if(Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), imageView.getContext());
		}

		return wrap;
	}

	@Override
	protected void onPostExecute(CachedImage result) {
		super.onPostExecute(result);

		if (result != null) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(bitmap);
			if (Constants.DEBUG) Log.v(TAG, "update imageview");
			ImageInfo imageInfo = wrap.getImageInfo();
			if (tvImageInfo != null
				&& imageInfo != null
				&& (imageInfo.getSize() > Constants.IMAGE_SIZE_THRESHOLD
					|| imageInfo.getFormat() == Format.GIF)) {
				tvImageInfo.setVisibility(View.VISIBLE);
				tvImageInfo.setText(imageInfo.getFormat()
					+ " " + imageInfo.getDisplaySize());
			}
		} else {
			imageView.setVisibility(View.GONE);
			if (Constants.DEBUG && resultMsg != null) {
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
