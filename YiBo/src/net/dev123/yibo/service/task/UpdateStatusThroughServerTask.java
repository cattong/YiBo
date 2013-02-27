package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.LibException;
import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.listener.EditMicroBlogTweetCancelClickListener;
import net.dev123.yibo.service.listener.EditMicroBlogTweetRetryClickListener;
import net.dev123.yibo.widget.TweetProgressDialog;
import net.dev123.yibo.widget.TweetProgressDialog.State;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.StatusSyncResult;
import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-9 下午4:49:03
 **/
public class UpdateStatusThroughServerTask extends
	AbstractUpdateStatusTask<Void, ProgressHolder, Integer> {
	private static final String TAG = UpdateStatusThroughServerTask.class.getSimpleName();
	
	private List<LocalAccount> listAccount;
	private List<LocalAccount> listFailedAccount;
	
	private List<StatusSyncResult> syncResultList;

	private TweetProgressDialog dialog;
	
	private int successCount;
	private String resultMsg;

	public UpdateStatusThroughServerTask(EditMicroBlogActivity context,
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

		dialog.setDialogTitle(R.string.title_tweet_progress_server);
		dialog.setPositiveClickListener(null);
		EditMicroBlogTweetCancelClickListener canelClickLister =
			new EditMicroBlogTweetCancelClickListener(this, dialog);
		dialog.setNegativeClickListener(canelClickLister);
		
		initProgressHolder();
	}

	@Override
	protected Integer doInBackground(Void... params) {
		if (statusUpdate == null
			|| StringUtil.isBlank(statusUpdate.getStatus())
			|| ListUtil.isEmpty(listAccount)) {
			return successCount;
		}
		
		if (statusUpdate.getImage() != null) {
			rotateImage();
			compressImage();
		}
		
		YiBoMe yiboMe = YiBoMeUtil.getYiBoMeOAuth(context);
		if (yiboMe == null) {
			return successCount;
		}
		
		try {
			syncResultList = yiboMe.syncStatus(statusUpdate, listAccount);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.d(TAG, e.getMessage());
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}
		
		changeHolderStateAndSetSuccessCounter();
		if (successCount == listAccount.size()) {
			SystemClock.sleep(1000);
		}
		
		return successCount;
	}

	private Map<LocalAccount, ProgressHolder> accountHolderMap = new HashMap<LocalAccount, ProgressHolder>();
	private void initProgressHolder() {
		for (int i = 0; i < listAccount.size(); i++) {
			ProgressHolder holder = new ProgressHolder();
			holder.account = listAccount.get(i);
			holder.state = State.Loading;
			accountHolderMap.put(listAccount.get(i), holder);
			this.publishProgress(holder);
		}
	}
	
	private void changeHolderStateAndSetSuccessCounter() {
		if (ListUtil.isEmpty(syncResultList)) {
			listFailedAccount.addAll(listAccount);
			for (int i = 0, size = listAccount.size(); i < size; i++) {
				ProgressHolder holder = accountHolderMap.get(listAccount.get(i));
				holder.state = State.Failed;
				publishProgress(holder);
			}
			return;
		}
		
		StatusSyncResult syncResult = null;
		boolean failedFlag = false;
		LocalAccount account = null;
		for (int i = 0, size = listAccount.size(); i < size; i++) {
			account = listAccount.get(i);
			ProgressHolder holder = accountHolderMap.get(account);
			
			int resultListSize = syncResultList.size();
			for (int j = 0; j < resultListSize; j++) {
				syncResult = syncResultList.get(j);
				if (syncResult.getErrorCode() != null 
					&& account.getUserId().equals(syncResult.getUserId())
					&& account.getServiceProviderNo() == syncResult.getServiceProviderNo()) {
					failedFlag = true;
					break;
				}
			}
			
			if (failedFlag) {
				failedFlag = false;
				holder.state = State.Failed;
				listFailedAccount.add(account);
			} else {
				successCount++;
				holder.state = State.Success;
			}
			
			publishProgress(holder);
		}
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

		Button btnSend = (Button)((Activity)context).findViewById(R.id.btnOperate);
		EditText etText  = (EditText)((Activity)context).findViewById(R.id.etText);
		if (successCount == listAccount.size()) {
			//退出onPause清空临时保存数据
			if (etText != null) {
				etText.setText("");
			}
			String msg = context.getString(R.string.msg_status_success);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			Activity activity = (Activity) context;
			dialog.dismiss();
			activity.finish();
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

}
