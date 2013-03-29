package com.shejiaomao.weibo.service.adapter;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Account;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.AccountsActivity;
import com.shejiaomao.weibo.activity.AccountsActivity.Action;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class AccountListAdapter extends ArrayAdapter<LocalAccount> {

	private SheJiaoMaoApplication sheJiaoMao;
	private LayoutInflater inflater;
	private AccountsActivity context;
	private LocalAccountDao accountDao;
	private int defaultAccountPoistion = -1;

	public AccountListAdapter(AccountsActivity context) {
		super(context, R.layout.list_item_account);
		this.context = context;
		this.accountDao = new LocalAccountDao(context);
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
		this.inflater = context.getLayoutInflater();
		initList();
	}

	private void initList() {
		SharedPreferences prefs =
			context.getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, Context.MODE_PRIVATE);
		//获取新增加的帐号ID
		long newAccountId = prefs.getLong(Constants.PREFS_KEY_ACCOUNT_ADDED, 0);

		//清除SharedPreferences中临时保存的AccountId值
		prefs.edit().remove(Constants.PREFS_KEY_ACCOUNT_ADDED).commit();

		StringBuilder sql = new StringBuilder();
		sql.append("select * from Account where State = ").append(Account.STATE_ACTIVE);
		if (newAccountId > 0) {
			sql.append(" and Account_ID < " +  newAccountId );
		}
		sql.append(" order by Created_At asc");

		List<LocalAccount> accounts = accountDao.find(sql.toString());
		LocalAccount account = null;
		if (accounts != null && accounts.size() > 0) {
			for (int i=0; i<accounts.size(); i++) {
				account = accounts.get(i);
				if (account.isDefault()) {
					defaultAccountPoistion = i;
				}
				this.add(account);
			}
		}
		if (newAccountId > 0) {
			account = accountDao.findById(newAccountId);
			editAccount(Action.Add, account);
		}

		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LocalAccount account = this.getItem(position);
		AccountHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_account, null);
			holder = new AccountHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (AccountHolder)convertView.getTag();
		}
		holder.reset(account.getServiceProvider());
		
		String profileImageUrl = account.getUser().getProfileImageUrl();
		if (StringUtil.isNotEmpty(profileImageUrl)) {
			holder.headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileImageUrl, true);
			holder.headTask.execute();
		}
		
		holder.tvScreenName.setText(account.getUser().getScreenName());
		
		String spName = account.getServiceProvider().getSpName();
		if (account.isDefault()) {
			spName += getContext().getString(R.string.label_accounts_default);
		}
		holder.tvSPName.setText(spName);
		
		if (account.getTokenExpiredAt() != null 
			&& account.getTokenExpiredAt().getTime() < System.currentTimeMillis()) {
			holder.tvExpiredHint.setVisibility(View.VISIBLE);
		} else {
			holder.tvExpiredHint.setVisibility(View.GONE);
		}
		
		holder.ivDelAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
				.setTitle(R.string.title_dialog_alert)
				.setMessage(R.string.msg_accounts_delete_confirm)
				.setNegativeButton(R.string.btn_cancel,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
				.setPositiveButton(R.string.btn_confirm,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								account.setState(Account.STATE_INVALID);
								accountDao.update(account);
								editAccount(Action.Delete, account);
								Toast.makeText(context, R.string.msg_accounts_delete_successful, Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								Toast.makeText(context, R.string.msg_accounts_delete_failed, Toast.LENGTH_SHORT).show();
							}
						}
				})
				.show();
			}
		});

		
		return convertView;
	}

	public synchronized void editAccount(Action action, LocalAccount account) {
		if (account == null || action == null) {
			return;
		}
		int position = 0;
		switch (action) {
		case Add:
			position = getCount();
			insert(account, position); //列表中插入新的帐号
			if (account.isDefault()) {
				if (defaultAccountPoistion > 0 && position != defaultAccountPoistion) {
					getItem(defaultAccountPoistion).setDefault(false);
				}
				defaultAccountPoistion = getPosition(account);
			}
			GlobalVars.addAccount(account);
			break;
		case MakeDefault:
			if (accountDao.makeDefault(account)) {
				position = getPosition(account);
				if (defaultAccountPoistion > 0 && position != defaultAccountPoistion) {
					getItem(defaultAccountPoistion).setDefault(false);
				}
				defaultAccountPoistion = position;
			}
			break;
		case Delete:
			remove(account); // 列表中删除帐号
			GlobalVars.removeAccount(account);

			account.setState(Account.STATE_INVALID);
			account.setCreatedAt(new Date());
			accountDao.update(account);

			if (sheJiaoMao.getCurrentAccount()!= null
				&& sheJiaoMao.getCurrentAccount().getAccountId().equals(account.getAccountId())
				&& getCount() > 0 ) {
				//如果删除的帐号是当前帐号，且删除后帐号列表中帐户数多于一个
				sheJiaoMao.setCurrentAccount(getItem(0));
			}

			if (account.isDefault()) {
				//如果删除的帐号是默认帐号，且删除后帐号列表中帐户数多于一个
				if (getCount() > 0 && accountDao.makeDefault(getItem(0))) {
					defaultAccountPoistion = 0;
				} else {
					defaultAccountPoistion = -1;
				}
			}

			break;
		}

		this.notifyDataSetChanged(); //更新ListView
	}

	public LocalAccount getDefaultAccount() {
		if (defaultAccountPoistion > 0 && defaultAccountPoistion < getCount()) {
			return getItem(defaultAccountPoistion);
		}

		return null;
	}

}
