package com.shejiaomao.weibo.service.task;

import java.io.File;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.FileUtil;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ImageQuality;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileEditActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.cache.ImageCache;

public class UpdateProfilePhotoTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "UpdateProfilePhotoTask";
	private ProfileEditActivity context;
	private SheJiaoMaoApplication sheJiaoMao;
	private long accountId;
	private File image;

	private ProgressDialog dialog;
	private boolean isShowDialog;
	private String resultMsg;

	public UpdateProfilePhotoTask(ProfileEditActivity context, File image) {
		this.context = context;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
		this.accountId = sheJiaoMao.getCurrentAccount().getAccountId();
		this.image = image;
		this.isShowDialog = true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isShowDialog) {
		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_profile_photo_uploading));
		    dialog.setCancelable(true);
		    dialog.setOnCancelListener(onCancelListener);
		    dialog.setOwnerActivity((Activity)context);
		}
	}

	@Override
	protected User doInBackground(Void... params) {
		if (image == null) {
			return null;
		}

		Weibo microBlog = GlobalVars.getMicroBlog(accountId);
		if (microBlog == null) {
			return null;
		}

		User user = null;
		try {

			if (image != null) {
				String fileExtension = FileUtil.getFileExtensionFromName(image.getName());
				int size = ImageQuality.Low.getSize();
				ImageQuality quality = sheJiaoMao.getImageUploadQuality();
				if (quality == ImageQuality.High
						|| GlobalVars.NET_TYPE == NetType.WIFI) {
                    size = ImageQuality.High.getSize();
				} else if (quality == ImageQuality.Middle
						||	quality == ImageQuality.Low	) {
					size = quality.getSize();
					if(Logger.isDebug()) Log.d(TAG, "prefix size: " + size);
					//对低速网络进行压缩
					if (GlobalVars.NET_TYPE == NetType.MOBILE_GPRS ||
						GlobalVars.NET_TYPE == NetType.MOBILE_EDGE
					) {
                        size = ImageQuality.Low.getSize();
					}
				}
			    String destName = ImageCache.getTempFolder() + File.separator +
		            System.currentTimeMillis() + "." + fileExtension;
	            File dest = new File(destName);
	            boolean isSuccess = ImageUtil.scaleImageFile(image, dest, size);
	            if (isSuccess) {
	    	        image = dest;
	            }
	            user = microBlog.updateProfileImage(image);
			}
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return user;
	}

	@Override
	protected void onPostExecute(User resultUser) {
		super.onPostExecute(resultUser);

		if (isShowDialog
		    && dialog != null
			&& dialog.getContext() != null	) {
			try {
				dialog.dismiss();
			} catch(Exception e){}
		}

		if (resultUser != null) {
			Toast.makeText(context, R.string.msg_profile_photo_uploaded, Toast.LENGTH_LONG).show();
			context.updateProfileImage(resultUser.getProfileImageUrl());
			context.updateUser(resultUser);
		} else {
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);
			UpdateProfilePhotoTask.this.cancel(true);
		}
	};
}
