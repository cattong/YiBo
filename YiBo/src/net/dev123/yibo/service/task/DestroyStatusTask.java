package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.CacheAdapter;
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

public class DestroyStatusTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG   = "DestroyStatusTask";
	private Context context;
	private CacheAdapter<net.dev123.mblog.entity.Status> adapter;
	private MicroBlog microBlog;
	private net.dev123.mblog.entity.Status status;
    private boolean isCloseContext = false;
	private ProgressDialog dialog = null;
	private String resultMsg = null;
	public DestroyStatusTask(CacheAdapter<net.dev123.mblog.entity.Status> adapter,
		net.dev123.mblog.entity.Status status
	) {
		this.adapter = adapter;
		this.context = adapter.getContext();
		this.status = status;
        this.isCloseContext = false;

		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	public DestroyStatusTask(Context context, net.dev123.mblog.entity.Status status) {
		this.context = context;
		this.status = status;
        this.isCloseContext = true;

		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
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

		net.dev123.mblog.entity.Status newStatus = null;
		try {
			newStatus = microBlog.destroyStatus(status.getId());
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
