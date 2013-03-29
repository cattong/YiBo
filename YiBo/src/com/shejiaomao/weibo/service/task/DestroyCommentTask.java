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
import com.cattong.entity.Comment;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;

public class DestroyCommentTask extends AsyncTask<Void, Void, Comment> {
	private static final String LOG = "DestroyCommentTask";

	private Context context = null;
	private CacheAdapter<Comment> adapter;
	private Weibo microBlog = null;
	private Comment comment = null;

	private ProgressDialog dialog;
	private String resultMsg;
	public DestroyCommentTask(CacheAdapter<Comment> adapter, Comment comment) {
		this.context = adapter.getContext();
		this.adapter = adapter;
		this.comment = comment;
        this.microBlog  = GlobalVars.getMicroBlog(adapter.getAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_comment_destorying));
	    dialog.setCancelable(true);
	    dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Comment doInBackground(Void... params) {
		Comment result = null;
		if (microBlog == null || comment == null) {
			return result;
		}

        try {
			result = microBlog.destroyComment(comment.getCommentId());
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(LOG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}

		return result;
	}

	@Override
	protected void onPostExecute(Comment result) {
	    super.onPreExecute();
	    if (dialog != null &&
	        dialog.getContext() != null
	    ) {
	    	try {
	    	    dialog.dismiss();
	    	} catch (Exception e) {}
	    }

	    if (result != null) {
	    	adapter.remove(comment);
        	Toast.makeText(context, R.string.msg_comment_destroy_success, Toast.LENGTH_LONG).show();
	    } else {
	    	Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
	    }

	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			DestroyCommentTask.this.cancel(true);
		}
	};
}
