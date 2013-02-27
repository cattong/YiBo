package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ToggleFavoriteTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG   = "ToggleFavoriteTask";

	private MicroBlog microBlog  = null;
	private Context context      = null;
    private View forUpdateView   = null;

	private net.dev123.mblog.entity.Status status = null;
	private ProgressDialog dialog = null;
	private String resultMsg = null;;
	public ToggleFavoriteTask(Context context, net.dev123.mblog.entity.Status status) {
		this.context = context;
        this.status = status;

        YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	public ToggleFavoriteTask(Context context, net.dev123.mblog.entity.Status status, View forUpdateView) {
		this.context = context;
        this.status = status;
        this.forUpdateView = forUpdateView;

        YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (status.isFavorited()) {
			dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_favorite_destroy_sending));
		} else {
		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_favorite_add_sending));
		}
		dialog.setCancelable(true);
		dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = true;
		if (microBlog == null) {
			return isSuccess;
		}

		net.dev123.mblog.entity.Status newStatus = null;
		try {
			if (status.isFavorited()) {
				newStatus = microBlog.destroyFavorite(status.getId());
			} else {
			    newStatus = microBlog.createFavorite(status.getId());
			}
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return newStatus != null ;
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
			status.setFavorited(!status.isFavorited());
			if (status.isFavorited()) {
				resultMsg = context.getString(R.string.msg_favorite_add_success);
			} else {
				resultMsg = context.getString(R.string.msg_favorite_destroy_success);
			}

			if (context instanceof MicroBlogActivity) {
				((MicroBlogActivity)context).initFavButton(status);
			} else if (forUpdateView != null) {
				View ivFavorite = forUpdateView.findViewById(R.id.ivFavorite);
				if (ivFavorite != null && status.isFavorited()) {
				    ivFavorite.setVisibility(View.VISIBLE);
				} else if (ivFavorite != null) {
					ivFavorite.setVisibility(View.GONE);
				}
			}
		}

		Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			ToggleFavoriteTask.this.cancel(true);
		}
	};
}
