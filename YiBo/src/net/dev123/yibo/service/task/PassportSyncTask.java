package net.dev123.yibo.service.task;

import java.util.Date;
import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.yibo.AccountsActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalAccountDao;
import net.dev123.yibo.service.adapter.AccountListAdapter;
import net.dev123.yibome.YiBoMe;
import net.dev123.yibome.entity.Account;
import net.dev123.yibome.entity.AccountSyncResult;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class PassportSyncTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = PassportSyncTask.class.getSimpleName();

	private AccountsActivity context;
	private AccountListAdapter alAdapter;
	private AccountSyncResult syncResult;
	private ProgressDialog progressDialog;
	private Button btnSync;

	private LocalAccountDao localAccountDao;
	private String message;

	public PassportSyncTask(AccountsActivity context) {
		this.context = context;
		this.alAdapter = context.getAccountListAdapter();
	}

	@Override
	protected void onPreExecute() {
    	btnSync = (Button) context.findViewById(R.id.btnAccountSync);
    	btnSync.setEnabled(false);

		progressDialog = ProgressDialog.show(context, "",
				context.getString(R.string.msg_passport_syncing), true, false);
	    progressDialog.setOwnerActivity(context);
	}

	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean isSynced = false;
		localAccountDao = new LocalAccountDao(context);
		List<? extends Account> accountList = localAccountDao.findAll();

		YiBoMe yiboMe = YiBoMeUtil.getYiBoMeOAuth(context);
		if (yiboMe == null) {
			return isSynced;
		}
		
		try {
			syncResult = yiboMe.syncAccounts(accountList);
			isSynced = localAccountDao.syncToDatabase(syncResult);
		} catch (LibException e) {
			if (Constants.DEBUG) {
				Log.d(TAG, e.getMessage());
			}
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}
		
		return isSynced;
	}

	protected void onPostExecute(Boolean isSynced) {
		if (isSynced) {
			ConfigSystemDao configDao = new ConfigSystemDao(context);
			configDao.put(Constants.LAST_SYNC_TIME, new Date(), "最后同步时间");

			List<LocalAccount> accountList = localAccountDao.findAllValid();

			//判断同步完成后YiBo的当前用户是否为空，首次进入即同步的时候，是没有当前用户的
			YiBoApplication yibo = (YiBoApplication) context.getApplication();
			LocalAccount current = yibo.getCurrentAccount();
			if (current != null) {
				// 重新从数据库取一次，因为当前帐号可能已被删除
				current = localAccountDao.findById(current.getAccountId());
			}
			if (current == null) {
				current = localAccountDao.getDefaultAccount();
				if (current == null) {
					if (ListUtil.isNotEmpty(accountList)) {
						current = accountList.get(0);
					}
				}
			}
			yibo.setCurrentAccount(current);
			
			alAdapter.clear();
			if (ListUtil.isNotEmpty(accountList)) {
				for (LocalAccount account : accountList) {
					alAdapter.add(account);
				}
			}
			alAdapter.notifyDataSetChanged();
			GlobalVars.reloadAccounts(context);
			
			context.updatePassportView();
			message = context.getString(R.string.msg_passport_sync_success);
		}
		
		try {
			if (progressDialog != null) {
		        progressDialog.dismiss();
			}
		} catch(Exception e) {}
		
		btnSync.setEnabled(true);
		if (StringUtil.isNotEmpty(message)) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

}
