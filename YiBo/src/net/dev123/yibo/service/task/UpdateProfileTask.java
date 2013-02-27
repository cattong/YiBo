package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileEditActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class UpdateProfileTask extends AsyncTask<Void, Void, User> {
	private static final String TAG = "UpdateProfilePhotoTask";
	private ProfileEditActivity context;
	private YiBoApplication yibo;

    private long accountId;

	private String screenName;
	private String description;

	private ProgressDialog dialog;
	private boolean isShowDialog;
	private String resultMsg;

	public UpdateProfileTask(ProfileEditActivity context, String screenName, String description) {
		this.context = context;
		this.yibo = (YiBoApplication)context.getApplicationContext();
		this.accountId = yibo.getCurrentAccount().getAccountId();
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

		MicroBlog microBlog = GlobalVars.getMicroBlog(accountId);
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
