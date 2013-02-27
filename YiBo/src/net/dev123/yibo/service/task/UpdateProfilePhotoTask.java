package net.dev123.yibo.service.task;

import java.io.File;

import net.dev123.commons.util.FileUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileEditActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ImageQuality;
import net.dev123.yibo.common.ImageUtil;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.cache.ImageCache;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class UpdateProfilePhotoTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "UpdateProfilePhotoTask";
	private ProfileEditActivity context;
	private YiBoApplication yibo;
	private long accountId;
	private File image;

	private ProgressDialog dialog;
	private boolean isShowDialog;
	private String resultMsg;

	public UpdateProfilePhotoTask(ProfileEditActivity context, File image) {
		this.context = context;
		this.yibo = (YiBoApplication)context.getApplication();
		this.accountId = yibo.getCurrentAccount().getAccountId();
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

		MicroBlog microBlog = GlobalVars.getMicroBlog(accountId);
		if (microBlog == null) {
			return null;
		}

		User user = null;
		try {

			if (image != null) {
				String fileExtension = FileUtil.getFileExtensionFromName(image.getName());
				int size = ImageQuality.Low.getSize();
				ImageQuality quality = yibo.getImageUploadQuality();
				if (quality == ImageQuality.High
						|| GlobalVars.NET_TYPE == NetType.WIFI) {
                    size = ImageQuality.High.getSize();
				} else if (quality == ImageQuality.Middle
						||	quality == ImageQuality.Low	) {
					size = quality.getSize();
					if(Constants.DEBUG) Log.d(TAG, "prefix size: " + size);
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
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
