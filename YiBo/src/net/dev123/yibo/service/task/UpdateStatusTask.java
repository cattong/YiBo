package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.entity.StatusUpdate;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.sns.Sns;
import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateStatusTask extends AbstractUpdateStatusTask<Void, Void, net.dev123.mblog.entity.Status> {
	private static final String TAG = UpdateStatusTask.class.getSimpleName();

    private LocalAccount account;

	private ProgressDialog dialog;
	private boolean isShowDialog = false;
	private String resultMsg = null;

	public UpdateStatusTask(EditMicroBlogActivity context, StatusUpdate statusUpdate, LocalAccount account) {
		super(context, statusUpdate);
		this.account = account;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (isShowDialog) {
		    dialog = ProgressDialog.show(context, null,
		    		context.getString(R.string.msg_blog_sending));
		    dialog.setCancelable(true);
		    dialog.setOnCancelListener(onCancelListener);
		    dialog.setOwnerActivity((Activity)context);
		}
	}

	@Override
	protected net.dev123.mblog.entity.Status doInBackground(Void... params) {
		net.dev123.mblog.entity.Status newStatus = null;
		
		if (statusUpdate.getImage() != null) {
			rotateImage();
			compressImage();
		}
		
		try {
			if (account.isSnsAccount()) {
				Sns sns = GlobalVars.getSns(account);
				if (sns != null) {
					boolean result = false;
					if (statusUpdate.getImage() != null) {
						result = sns.uploadPhoto(statusUpdate.getImage(), statusUpdate.getStatus());
					} else {
						result = sns.createStatus(statusUpdate.getStatus());
					}
					if (result) {
						newStatus = new net.dev123.mblog.entity.Status();
					}
				}
			} else {
				MicroBlog microBlog = GlobalVars.getMicroBlog(account);
				if (microBlog != null) {
					newStatus = microBlog.updateStatus(statusUpdate);
				}
			}

		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return newStatus;
	}

	@Override
	protected void onPostExecute(net.dev123.mblog.entity.Status result) {
		super.onPostExecute(result);

		if (isShowDialog && dialog != null && dialog.getContext() != null) {
			try {
				dialog.dismiss();
			} catch(Exception e){}
		}

		if (result != null) {
			if (!context.isUpdateSinaAndPauseOthers()) {
				//退出onPause清空临时保存数据
				EditText etText  = (EditText)((Activity)context).findViewById(R.id.etText);
				if (etText != null) {
					etText.setText("");
				}
			}
			if (isShowDialog) {
			    Toast.makeText(context, R.string.msg_status_success, Toast.LENGTH_LONG).show();

			    if (context.isUpdateSinaAndPauseOthers()) {
			    	List<LocalAccount> sinaList = new ArrayList<LocalAccount>();
			    	sinaList.add(account);
			    	context.removeAllSinaAccount(sinaList);
			    	context.updateSelectorText();
			    	Button btnSend = (Button) ((Activity)context).findViewById(R.id.btnOperate);
					btnSend.setEnabled(true);
			    } else {
			    	((Activity)context).finish();
			    }
			}
		} else {
			Button btnSend = (Button) ((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);

			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			Button btnSend = (Button) ((Activity)context).findViewById(R.id.btnOperate);
			btnSend.setEnabled(true);
			UpdateStatusTask.this.cancel(true);
		}
	};

	public boolean isShowDialog() {
		return isShowDialog;
	}

	public void setShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

}
