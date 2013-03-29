package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;

public class DestroyStatusTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG   = "DestroyStatusTask";
	private Context context;
	private CacheAdapter<com.cattong.entity.Status> adapter;
	private Weibo microBlog;
	private com.cattong.entity.Status status;
    private boolean isCloseContext = false;
	private ProgressDialog dialog = null;
	private String resultMsg = null;
	public DestroyStatusTask(CacheAdapter<com.cattong.entity.Status> adapter,
		com.cattong.entity.Status status
	) {
		this.adapter = adapter;
		this.context = adapter.getContext();
		this.status = status;
        this.isCloseContext = false;

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	public DestroyStatusTask(Context context, com.cattong.entity.Status status) {
		this.context = context;
		this.status = status;
        this.isCloseContext = true;

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_blog_delete_sending));
		dialog.setCancelable(true);
		dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
        if (microBlog == null || status == null) {
        	return false;
        }

		com.cattong.entity.Status newStatus = null;
		try {
			newStatus = microBlog.destroyStatus(status.getStatusId());
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return newStatus != null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (dialog != null &&
			dialog.getContext() != null
		) {
			try {
			    dialog.dismiss();
			} catch (Exception e) {}
		}

		if (result) {
			resultMsg = context.getString(R.string.msg_blog_delete_success);
		}
		if (result && isCloseContext) {
            Activity activity = (Activity)context;

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("STATUS", status);
            intent.putExtras(bundle);

            activity.setResult(Constants.RESULT_CODE_MICRO_BLOG_DELETE, intent);
            activity.finish();
		} else if (result) {
			adapter.remove(status);
		}

		Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			DestroyStatusTask.this.cancel(true);
		}
	};
}
