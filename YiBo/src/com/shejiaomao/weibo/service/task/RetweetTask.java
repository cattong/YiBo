package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class RetweetTask extends AsyncTask<Void, Void, com.cattong.entity.Status> {
	private static final String TAG = "DirectRetweetTask";

	private Weibo microBlog = null;
	private Context context = null;

	private String statusId = null;
	private String text = null;
	private boolean isComment = false;

	private com.cattong.entity.Status newStatus = null;
	private boolean isShowDialog = false;
	private ProgressDialog dialog;
	private String errorMsg = null;

	public RetweetTask(Context context, String statusId, String text, LocalAccount account) {
		this.context = context;
		this.statusId = statusId;
		this.text = text;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isShowDialog) {
		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_blog_sending));
		    dialog.setCancelable(true);
		    dialog.setOnCancelListener(onCancelListener);
		}
	}

	@Override
	protected com.cattong.entity.Status doInBackground(Void... params) {
		try {
			newStatus = microBlog.retweetStatus(statusId, text, isComment);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			errorMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return newStatus;
	}

	@Override
	protected void onPostExecute(com.cattong.entity.Status result) {
		if (dialog != null) {
			try {
			    dialog.dismiss();
			} catch (Exception e) {}
		}

		if (newStatus != null) {
			if (isShowDialog) {
				Toast.makeText(context, R.string.msg_status_success, Toast.LENGTH_LONG).show();
			    ((Activity)context).finish();
			}
		} else {
			Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);
			Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			RetweetTask.this.cancel(true);
			Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			if (btnSend != null) {
			    btnSend.setEnabled(true);
			}
		}
	};

	public boolean isComment() {
		return isComment;
	}

	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}

	public boolean isShowDialog() {
		return isShowDialog;
	}

	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

}
