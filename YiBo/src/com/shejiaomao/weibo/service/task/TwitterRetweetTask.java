package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.GlobalVars;

public class TwitterRetweetTask extends AsyncTask<Void, Void, com.cattong.entity.Status> {
	private static final String TAG = TwitterRetweetTask.class.getSimpleName();

	private Context context;
	private long accountId;
	private com.cattong.entity.Status originalStatus;

	private ProgressDialog dialog;
	private String errorMsg;

	public TwitterRetweetTask(Context context, com.cattong.entity.Status status) {
		this.accountId = ((SheJiaoMaoApplication)context.getApplicationContext()).getCurrentAccount().getAccountId();
		this.context = context;
		this.originalStatus = status;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_blog_sending));
		dialog.setCancelable(true);
		dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected com.cattong.entity.Status doInBackground(Void... params) {
		if (originalStatus == null) {
			return null;
		}

		Weibo microBlog = GlobalVars.getMicroBlog(accountId);
		if (microBlog == null) {
			return null;
		}

		com.cattong.entity.Status newStatus = null;
		try {
			String originalStatusId = originalStatus.getStatusId();
			if (originalStatus.getRetweetedStatus() != null) {
				originalStatusId = originalStatus.getRetweetedStatus().getStatusId();
			}
			newStatus = microBlog.retweetStatus(originalStatusId, null, false);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, e.getMessage(), e);
			errorMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return newStatus;
	}

	@Override
	protected void onPostExecute(com.cattong.entity.Status resultStatus) {
		if (dialog != null) {
			try {
			dialog.dismiss();
			} catch(Exception e) {}
		}

		if (resultStatus != null && errorMsg == null) {
			Toast.makeText(context, context.getString(R.string.msg_status_success), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			TwitterRetweetTask.this.cancel(true);
		}
	};

}
