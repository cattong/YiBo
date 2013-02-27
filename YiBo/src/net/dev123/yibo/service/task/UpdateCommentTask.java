package net.dev123.yibo.service.task;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Comment;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class UpdateCommentTask extends AsyncTask<Void, Void, Comment> {
	private static final String TAG  = "UpdateCommentTask";

	private MicroBlog microBlog;
	private Context context;

	private String text;
	private String commentId;
	private String statusId;
	private Comment newComment;

	private ProgressDialog dialog;
	private boolean isShowDialog = false;
	private String resultMsg = null;
	public UpdateCommentTask(Context context, String text, String statusId, LocalAccount account) {
		this.context  = context;
		this.text     = text;
		this.statusId = statusId;

		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	public UpdateCommentTask(Context context, String text, String statusId, String commentId, LocalAccount account) {
		this.context   = context;
		this.text      = text;
		this.commentId = commentId;
		this.statusId  = statusId;

		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isShowDialog) {
		    dialog = ProgressDialog.show(context, null,
		    	context.getString(R.string.msg_comment_sending));
		    dialog.setCancelable(true);
		    dialog.setOnCancelListener(onCancelListener);
		}
	}

	@Override
	protected Comment doInBackground(Void... params) {
		if (StringUtil.isEmpty(text)) {
			resultMsg = context.getString(R.string.msg_comment_empty);
			return null;
		}
        if (StringUtil.isEmpty(statusId)) {
			return null;
        }

		try {
			if (commentId == null) {
				newComment = microBlog.createComment(text, statusId);
			} else {
				newComment = microBlog.createComment(text, statusId, commentId);
			}
		} catch(LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return newComment;
	}

	@Override
	protected void onPostExecute(Comment result) {
		super.onPostExecute(result);

		if (isShowDialog && dialog != null) {
			try {
			    dialog.dismiss();
			} catch(Exception e){}
		}

        if (newComment != null) {
        	if (isShowDialog) {
        	    Toast.makeText(context, R.string.msg_comment_success, Toast.LENGTH_LONG).show();
        	    Intent intent = new Intent();
        	    intent.putExtra("RESULT_COMMENT", newComment);
        	    ((Activity)context).setResult(Constants.RESULT_CODE_SUCCESS, intent);
			    ((Activity)context).finish();
        	}
        } else {
        	if (isShowDialog) {
			    Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			    btnSend.setEnabled(true);
        	}
        	Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
        }
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			UpdateCommentTask.this.cancel(true);
			Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);
		}
	};

	public boolean isShowDialog() {
		return isShowDialog;
	}

	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

}
