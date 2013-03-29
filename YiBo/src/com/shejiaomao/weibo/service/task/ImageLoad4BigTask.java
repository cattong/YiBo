package com.shejiaomao.weibo.service.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.shejiaomao.maobo.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

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

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.LibRuntimeException;
import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.util.ArrayUtil;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.ImageViewer4GifActivity;
import com.shejiaomao.weibo.activity.ImageViewerActivity;
import com.shejiaomao.weibo.activity.ImageWebViewerActivity;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImage;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;

public class ImageLoad4BigTask extends AsyncTask<Void, Integer, CachedImage> {
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
			progressDialog = new ProgressDialog(context); 
			progressDialog.setMessage(context.getString(R.string.msg_big_image_loading));
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(onCancelListener);
			progressDialog.setOwnerActivity(context);
			progressDialog.setMax(100);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.show();
			if (isHit) {
		        cancel(true);
		        onPostExecute(null);
			}
		} catch (BadTokenException e) {
			if (Logger.isDebug()) {
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

		if(Logger.isDebug()) Log.i(LOG_TAG, "Get middle image from remote!");
		String filePath = ImageCache.getImageSavePath(imageInfo);
		File destFile = new File(filePath);
		try {
			//destFile = ImageUtil.getFileByUrl(url, destFile);
			ProgressResponseHandler progressResponseHandler = new ProgressResponseHandler(destFile);
			destFile = HttpRequestHelper.getContent(url, progressResponseHandler);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(LOG_TAG, e.getMessage(), e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			destFile.delete();
		}

		return wrap;

	}

	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (ArrayUtil.isEmpty(values)) {
			return;
		}
		int value = values[0];
		progressDialog.setProgress(value);
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
	
	class ProgressResponseHandler implements ResponseHandler<File> {
		private File file;
		
		public ProgressResponseHandler(File file) {
			this.file = file;
		}

		@Override
		public File handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
			if (file == null) {
				throw new LibRuntimeException(LibResultCode.E_PARAM_NULL);
			}
	        return writeToFile(response.getEntity());
		}
		
		public File writeToFile(final HttpEntity entity) throws ClientProtocolException, IOException {
	        if (entity == null) {
	        	throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR);
	        }
	        InputStream instream = entity.getContent();
	        if (instream == null) {
	            return null;
	        }
	        long contentLength = entity.getContentLength();
	        if (contentLength > Integer.MAX_VALUE) {
	        	throw new LibRuntimeException(LibResultCode.E_PARAM_ERROR);
	        }
	        
			Logger.verbose("FileResponseHandler Content Type : {}", entity.getContentType());

	        if (!file.exists()) {
	        	file.createNewFile();
	        }

	        FileOutputStream fos = null;
	        int readedLength = 0;
	        try {
	        	fos = new FileOutputStream(file);
	        	byte[] tmp = new byte[4096];
	            int l;
	            while ((l = instream.read(tmp)) != -1) {
	                fos.write(tmp, 0, l);
	                readedLength += l;
	                //显示进度;
	                publishProgress((int)(readedLength * 100/contentLength));
	            }
	            fos.flush();
	        } catch (FileNotFoundException e) {
	        	Logger.debug(e.getMessage(), e);
	        } finally {
	            instream.close();
	            fos.close();
	        }

	        return file;
		}
	}
}
