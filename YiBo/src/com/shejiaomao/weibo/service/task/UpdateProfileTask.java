package com.shejiaomao.weibo.service.task;

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
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileEditActivity;
import com.shejiaomao.weibo.common.GlobalVars;

public class UpdateProfileTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "UpdateProfilePhotoTask";
	private ProfileEditActivity context;
	private SheJiaoMaoApplication sheJiaoMao;

    private long accountId;

	private String screenName;
	private String description;

	private ProgressDialog dialog;
	private boolean isShowDialog;
	private String resultMsg;

	public UpdateProfileTask(ProfileEditActivity context, String screenName, String description) {
		this.context = context;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		this.accountId = sheJiaoMao.getCurrentAccount().getAccountId();
		this.screenName = screenName;
		this.description = description;
		this.isShowDialog = true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isShowDialog) {
		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_profile_updating));
		    dialog.setCancelable(true);
		    dialog.setOnCancelListener(onCancelListener);
		    dialog.setOwnerActivity((Activity)context);
		}
	}

	@Override
	protected User doInBackground(Void... params) {
		if (screenName == null) {
			return null;
		}

		Weibo microBlog = GlobalVars.getMicroBlog(accountId);
		if (microBlog == null) {
			return null;
		}

		User user = null;
		try {
			 if (description == null) {
				 description = "";
			 }
	         user = microBlog.updateProfile(screenName, null, null, null, description);
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
			if (isShowDialog) {
			    Toast.makeText(context, R.string.msg_profile_updated, Toast.LENGTH_LONG).show();
				context.updateUser(resultUser);
			}
		} else {
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);
			UpdateProfileTask.this.cancel(true);
		}
	};
}
