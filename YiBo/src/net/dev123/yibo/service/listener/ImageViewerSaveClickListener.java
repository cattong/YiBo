package net.dev123.yibo.service.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.dev123.commons.util.FileUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class ImageViewerSaveClickListener implements OnClickListener {
	private static final String TAG = ImageViewerSaveClickListener.class.getSimpleName();

    private String imagePath;
	public ImageViewerSaveClickListener(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public void onClick(View v) {
		if (StringUtil.isEmpty(imagePath)) {
			return;
		}

		final Context context = v.getContext();
		YiBoApplication yibo = (YiBoApplication)context.getApplicationContext();

		String imageFolder = yibo.getImageFolder();
		if (imageFolder.toLowerCase().startsWith("/sdcard")) {
			String state = Environment.getExternalStorageState();
			if (!state.equals(Environment.MEDIA_MOUNTED)) {
				String msg = null;
			    if (state.equals(Environment.MEDIA_REMOVED)) {
			    	msg = context.getString(R.string.msg_image_save_fail_sdcard_removed);
			    } else if (state.equals(Environment.MEDIA_UNMOUNTED)) {
			    	msg = context.getString(R.string.msg_image_save_fail_sdcard_unmounted);
			    } else if (state.equals(Environment.MEDIA_SHARED)) {
			    	msg = context.getString(R.string.msg_image_save_fail_sdcard_shared);
			    }

			    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			    return;
			}
		}

		// 如果SD卡已挂载，则保存图片
		File image = new File(imagePath);

		String fileExtension = MimeTypeMap.getFileExtensionFromUrl(imagePath);
		if (StringUtil.isEmpty(fileExtension)) {
			if (FileUtil.isGif(image.getAbsolutePath())){
				fileExtension = "gif";
			} else {
				fileExtension = "png";
			}
		}
		final String extension = fileExtension;

		String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String destName = Constants.PICTURE_NAME_PREFIX + fileName + "." + extension;

		final File dest = new File(imageFolder, destName);

		boolean isSuccess = false;
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(image).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			isSuccess = true;
		} catch (IOException e) {
			if (Constants.DEBUG) {
				Log.e(TAG, e.getMessage(), e);
			}

			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			try {
				if (inputChannel != null) {
					inputChannel.close();
				}
				if (outputChannel != null) {
					outputChannel.close();
				}
			} catch (IOException e) {
				if (Constants.DEBUG) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}

		if (isSuccess) {
			final String absolutePath = dest.getAbsolutePath();
			new MediaScannerConnectionClient() {
				MediaScannerConnection mConnection = new MediaScannerConnection(context, this);
				{
					mConnection.connect();
				}

				@Override
				public void onScanCompleted(String path, Uri uri) {
					mConnection.disconnect();
					mConnection = null;
				}

				@Override
				public void onMediaScannerConnected() {
					String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
					mConnection.scanFile(absolutePath, mime);
				}
			};
			String msg = context.getString(R.string.msg_image_save_success, imageFolder + File.separator + destName);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

}
