package com.shejiaomao.weibo.service.task;

import java.io.File;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.SettingActivity;

public class ImageCacheCleanTask extends AsyncTask<Void, Integer, Boolean> {
	private static final String TAG = ImageCacheCleanTask.class.getSimpleName();
	
	private static final int DELETING_TWEET_HISTORY = 1;
	private static final int DELETING_CACHED_IMAGES = 2;

	private SettingActivity context;
	private boolean isSilent; //是否静默删除模式，静默模式无任何提示
	private ProgressDialog progressDialog;

	private int totalImgCount = 0;
	private int successImgCount = 0;
	public ImageCacheCleanTask(SettingActivity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(context.getString(R.string.msg_setting_clearing));
		progressDialog.setOnCancelListener(onCancelListener);
		progressDialog.setOwnerActivity(context);
		progressDialog.setButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.cancel();
			}
		});
		progressDialog.setButton2(context.getString(R.string.btn_daemon), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.dismiss();
				isSilent = true;
			}
		});
		progressDialog.show();

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		clearCachedImages();
		return true;
	}

	private boolean clearCachedImages() {
		File cacheFolder = new File(SheJiaoMaoApplication.getSdcardCachePath());
		File innerCacheFolder = new File(SheJiaoMaoApplication.getInnerCachePath());
		if (cacheFolder.isDirectory()) {
			for (File folder : cacheFolder.listFiles()) {
				if (folder.isDirectory()) {
					totalImgCount += folder.list().length;
				} else {
					totalImgCount++;
				}
			}
		}
		if (innerCacheFolder.isDirectory()) {
			for (File folder : innerCacheFolder.listFiles()) {
				if (folder.isDirectory()) {
					totalImgCount += folder.list().length;
				} else {
					totalImgCount++;
				}
			}
		}

		if (cacheFolder.isDirectory()) {
			for (File folder : cacheFolder.listFiles()) {
				if (folder.isDirectory()) {
					for (String fileName : folder.list()) {
						File file = new File(folder.getPath() + File.separator + fileName);
						file.delete();
						successImgCount++;
						this.publishProgress(DELETING_CACHED_IMAGES);
					}
				} else {
					folder.delete();
					successImgCount++;
					this.publishProgress(DELETING_CACHED_IMAGES);
				}
			}
		}

		if (innerCacheFolder.isDirectory()) {
			for (File folder : innerCacheFolder.listFiles()) {
				if (folder.isDirectory()) {
					for (String fileName : folder.list()) {
						File file = new File(folder.getPath() + File.separator + fileName);
						file.delete();
						successImgCount++;
						this.publishProgress(DELETING_CACHED_IMAGES);
					}
				} else {
					folder.delete();
					successImgCount++;
					this.publishProgress(DELETING_CACHED_IMAGES);
				}
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		try {
			if (progressDialog != null) {
			    progressDialog.dismiss();
			}
		} catch(Exception e) {}

		if (result) {
		    Toast.makeText(context, R.string.msg_setting_clear_cache_success, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if(isSilent){
			return;
		}

		super.onProgressUpdate(values);

		int type = values[0];
		switch (type) {
		case DELETING_CACHED_IMAGES:
			progressDialog.setMessage(context.getString(
					R.string.msg_setting_clearing_image_cache, successImgCount, totalImgCount)
				);
			break;
		case DELETING_TWEET_HISTORY:
			progressDialog.setMessage(context.getString(
					R.string.msg_setting_clearing_tweet_history)
				);
			break;
		default:
			progressDialog.setMessage(context.getString(
					R.string.msg_setting_clearing)
				);
			break;
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			try {
			    dialog.dismiss();
			} catch(Exception e){}
			ImageCacheCleanTask.this.cancel(true);
		}
	};
}
