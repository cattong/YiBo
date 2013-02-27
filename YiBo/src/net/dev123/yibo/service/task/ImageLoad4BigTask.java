package net.dev123.yibo.service.task;

import java.io.File;

import net.dev123.commons.util.FileUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.yibo.ImageViewer4GifActivity;
import net.dev123.yibo.ImageViewerActivity;
import net.dev123.yibo.ImageWebViewerActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CacheManager;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.ImageUtil;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.cache.ImageCache;
import net.dev123.yibo.service.cache.wrap.CachedImage;
import net.dev123.yibo.service.cache.wrap.CachedImageKey;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

public class ImageLoad4BigTask extends AsyncTask<Void, Void, CachedImage> {
    private static final String LOG_TAG = ImageLoad4BigTask.class.getSimpleName();
    private static final int WEB_VIEW_MIN_SIZE = 800 * 800;

	private ImageCache imageCache;
	private CachedImageKey imageInfo;
	private boolean isHit = false;

	private Activity context;
	private ProgressDialog progressDialog;
	private String url;
	private String resultMsg;

	public ImageLoad4BigTask(Activity context, String url) {
		this.context = context;
		this.url = url;
		this.imageCache = (ImageCache) CacheManager.getInstance().getCache(ImageCache.class.getName());
		imageInfo = new CachedImageKey(url, CachedImageKey.IMAGE_MIDDLE);
	    if (url != null
	    	&& imageCache.containsKey(imageInfo)) {
			isHit = true;
	    }
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			progressDialog = ProgressDialog.show(context, null, 
				context.getString(R.string.msg_big_image_loading));
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(onCancelListener);
			progressDialog.setOwnerActivity(context);
			if (isHit) {
		        cancel(true);
		        onPostExecute(null);
			}
		} catch (BadTokenException e) {
			if (Constants.DEBUG) {
				Log.d(LOG_TAG, e.getMessage(), e);
			}
			cancel(true);
		}

	}

	@Override
	protected CachedImage doInBackground(Void... params) {
		CachedImage wrap = null;
		if (url == null) {
			return wrap;
		}

		if(Constants.DEBUG) Log.i(LOG_TAG, "Get middle image from remote!");
		String filePath = ImageCache.getImageSavePath(imageInfo);
		File destFile = new File(filePath);
		try {
			destFile = ImageUtil.getFileByUrl(url, destFile);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(LOG_TAG, e.getMessage(), e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			destFile.delete();
		}

		return wrap;

	}

	@Override
	protected void onPostExecute(CachedImage result) {
		super.onPostExecute(result);
		if (progressDialog == null) {
			return;
		}

		if (progressDialog.isShowing()) {
			try {
			    progressDialog.dismiss();
			} catch(Exception e) {}
		}

		if (StringUtil.isNotEmpty(resultMsg)) {
			Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
			return;
		}

		String realPath = ImageCache.getRealPath(imageInfo);
		BitmapFactory.Options options = ImageUtil.getBitmapOptions(realPath);

        double width = options.outWidth;
        double height = options.outHeight;

        Intent intent = new Intent();
        if ((width > 0 && (height / width > 3))
        	|| (width * height > WEB_VIEW_MIN_SIZE)) {
        	//较长的图片，使用WebView来显示，同时传递图片的长宽数值
        	intent = new Intent(context, ImageWebViewerActivity.class);
        	intent.putExtra("image-width", width);
        	intent.putExtra("image-hight", height);
        } else {
        	if (FileUtil.isGif(realPath)) {
				intent = new Intent(context, ImageViewer4GifActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			} else {
				intent = new Intent(context, ImageViewerActivity.class);
			}
        }
		intent.putExtra("image-path", realPath);
		intent.putExtra("mode", ImageViewerActivity.Mode.View.toString());
		context.startActivity(intent);
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			ImageLoad4BigTask.this.cancel(true);
			String filePath = ImageCache.getImageSavePath(imageInfo);
			File imageFile = new File(filePath);
			imageFile.delete();
		}
	};
}
