package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TwitterRetweetTask extends AsyncTask<Void, Void, net.dev123.mblog.entity.Status> {
	private static final String TAG = TwitterRetweetTask.class.getSimpleName();

	private Context context;
	private long accountId;
	private net.dev123.mblog.entity.Status originalStatus;

	private ProgressDialog dialog;
	private String errorMsg;

	public TwitterRetweetTask(Context context, net.dev123.mblog.entity.Status status) {
		this.accountId = ((YiBoApplication)context.getApplicationContext()).getCurrentAccount().getAccountId();
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
	protected net.dev123.mblog.entity.Status doInBackground(Void... params) {
		if (originalStatus == null) {
			return null;
		}

		MicroBlog microBlog = GlobalVars.getMicroBlog(accountId);
		if (microBlog == null) {
			return null;
		}

		net.dev123.mblog.entity.Status newStatus = null;
		try {
			String originalStatusId = originalStatus.getId();
			if (originalStatus.getRetweetedStatus() != null) {
				originalStatusId = originalStatus.getRetweetedStatus().getId();
			}
			newStatus = microBlog.retweetStatus(originalStatusId, null, false);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
			errorMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return newStatus;
	}

	@Override
	protected void onPostExecute(net.dev123.mblog.entity.Status resultStatus) {
		if (dialog != null &&
			dialog.isShowing() &&
			dialog.getWindow() != null
		) {
			dialog.dismiss();
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
