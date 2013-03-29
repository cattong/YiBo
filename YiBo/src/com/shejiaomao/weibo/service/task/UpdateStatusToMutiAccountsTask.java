package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.StatusUpdate;
import com.cattong.sns.Sns;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTweetCancelClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTweetRetryClickListener;
import com.shejiaomao.weibo.widget.TweetProgressDialog;
import com.shejiaomao.weibo.widget.TweetProgressDialog.State;

public class UpdateStatusToMutiAccountsTask extends AbstractUpdateStatusTask<Void, ProgressHolder, Integer> {
	private static final String TAG = "UpdateStatusToMutiAccountsTask";

	private List<LocalAccount> listAccount;
    private List<LocalAccount> listFailedAccount;

    private boolean isRetry = true;

	private TweetProgressDialog dialog;
	private String resultMsg = null;

	public UpdateStatusToMutiAccountsTask(EditMicroBlogActivity context,
		StatusUpdate statusUpdate, List<LocalAccount> listAccount) {
		super(context, statusUpdate);
		this.listAccount = listAccount;
		this.listFailedAccount = new ArrayList<LocalAccount>();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (dialog == null) {
			View parent = ((Activity) context).findViewById(R.id.btnOperate);
			dialog = new TweetProgressDialog(context, parent);
		    dialog.setListUpdateAccount(listAccount);
			dialog.show();
		}

		dialog.setDialogTitle(R.string.title_tweet_progress);
		dialog.setPositiveClickListener(null);
		EditMicroBlogTweetCancelClickListener canelClickLister =
			new EditMicroBlogTweetCancelClickListener(this, dialog);
		dialog.setNegativeClickListener(canelClickLister);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		if (statusUpdate == null
			|| StringUtil.isBlank(statusUpdate.getStatus())
			|| ListUtil.isEmpty(listAccount)) {
			return 0;
		}

		if (statusUpdate.getImage() != null && isRetry) {
			rotateImage();
			compressImage();
		}

		String text = statusUpdate.getStatus();
		com.cattong.entity.Status newStatus = null;
		for (int i = 0; i < listAccount.size(); i++) {
			LocalAccount account = listAccount.get(i);

			if (account == null) {
				continue;
			}

			ProgressHolder holder = new ProgressHolder();
			holder.account = account;
			holder.state = State.Loading;
			newStatus = null;
			this.publishProgress(holder);

			try {
				StatusUpdate toUpdate = new StatusUpdate(text);
				toUpdate.setImage(statusUpdate.getImage());
				toUpdate.setLocation(statusUpdate.getLocation());
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
							newStatus = new com.cattong.entity.Status();
						}
					}
				} else {
					Weibo microBlog = GlobalVars.getMicroBlog(account);
					if (microBlog != null) {
						newStatus = microBlog.updateStatus(toUpdate);
					}
				}
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}

			if (newStatus != null) {
				holder.state = State.Success;
			} else {
				holder.state = State.Failed;
				listFailedAccount.add(account);
			}
			this.publishProgress(holder);
		}
		if (listFailedAccount.size() < 1) {
			SystemClock.sleep(1000);
		}

		return listAccount.size() - listFailedAccount.size();
	}

	@Override
	protected void onProgressUpdate(ProgressHolder... values) {
		super.onProgressUpdate(values);
		if (values == null
			|| values.length == 0
			|| values[0] == null) {
			return;
		}

		ProgressHolder holder = values[0];
		if (dialog != null) {
			dialog.updateState(holder.account, holder.state);
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		int successCount = result;
		Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
		EditText etText  = (EditText)((Activity)context).findViewById(R.id.etText);
		if (successCount == listAccount.size()) {
			String msg = context.getString(R.string.msg_status_success);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			if (context.isUpdateSinaAndPauseOthers()) {
		    	context.removeAllSinaAccount(listAccount);
		    	context.updateSelectorText();
		    	dialog.dismiss();
				btnSend.setEnabled(true);
		    } else {
				//退出onPause清空临时保存数据
				if (etText != null) {
					etText.setText("");
				}
				dialog.dismiss();
		    	((Activity)context).finish();
		    }
//			Activity activity = (Activity) context;
//			dialog.dismiss();
//			activity.finish();
		} else if (successCount < listAccount.size() && successCount >= 0) {
			btnSend.setEnabled(true);

			UpdateStatusToMutiAccountsTask task = new UpdateStatusToMutiAccountsTask(
				context, statusUpdate, listFailedAccount);
			task.setDialog(dialog);
			task.setRetry(false);

			EditMicroBlogTweetRetryClickListener retryClickListener =
				new EditMicroBlogTweetRetryClickListener(task);
			dialog.setPositiveClickListener(retryClickListener);
			dialog.setPositiveBtnText(R.string.btn_retry);
		} else {
			btnSend.setEnabled(true);
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}

	}

	public List<LocalAccount> getListAccount() {
		return listAccount;
	}

	public void setListAccount(List<LocalAccount> listAccount) {
		this.listAccount = listAccount;
	}

	public void setDialog(TweetProgressDialog dialog) {
		this.dialog = dialog;
	}

	public boolean isRetry() {
		return isRetry;
	}

	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}

	public TweetProgressDialog getDialog() {
		return dialog;
	}
}

class ProgressHolder {
	LocalAccount account;
	State state;
}