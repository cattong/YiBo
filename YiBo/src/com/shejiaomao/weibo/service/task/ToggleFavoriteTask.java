package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalVars;

public class ToggleFavoriteTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG   = "ToggleFavoriteTask";

	private Weibo microBlog  = null;
	private Context context      = null;
    private View forUpdateView   = null;

	private com.cattong.entity.Status status = null;
	private ProgressDialog dialog = null;
	private String resultMsg = null;
	public ToggleFavoriteTask(Context context, com.cattong.entity.Status status) {
		this.context = context;
        this.status = status;

        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	public ToggleFavoriteTask(Context context, com.cattong.entity.Status status, View forUpdateView) {
		this.context = context;
        this.status = status;
        this.forUpdateView = forUpdateView;

        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
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

		com.cattong.entity.Status newStatus = null;
		try {
			if (status.isFavorited()) {
				newStatus = microBlog.destroyFavorite(status.getStatusId());
			} else {
			    newStatus = microBlog.createFavorite(status.getStatusId());
			}
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return newStatus != null ;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (dialog != null) {
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
