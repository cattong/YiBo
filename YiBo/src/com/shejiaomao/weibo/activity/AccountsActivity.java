package com.shejiaomao.weibo.activity;

import com.shejiaomao.maobo.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Passport;
import com.cattong.entity.PointsLevel;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.ConfigSystemDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalAccountDao;
import com.shejiaomao.weibo.service.adapter.AccountListAdapter;
import com.shejiaomao.weibo.service.listener.AccountPassportClickListener;
import com.shejiaomao.weibo.service.listener.SlideFinishOnGestureListener.SlideDirection;
import com.shejiaomao.weibo.service.task.ConfigAppSyncTask;
import com.shejiaomao.weibo.service.task.QueryPointLevelTask;

public class AccountsActivity extends BaseActivity {
	private static final String TAG = AccountsActivity.class.getSimpleName();

	private static final int SWITCH_TO_MENU_ID = Menu.FIRST;
	private static final int SET_DEFAULT_MENU_ID = Menu.FIRST + 1;
	private static final int DELETE_MENU_ID = Menu.FIRST + 2;

	public enum Action {
		Add, //帐号添加动作
		Delete, //帐号删除动作
		MakeDefault //设置默认帐号
	}

	private AccountListAdapter alAdapter;
	private AccountsActivity context;
	private SheJiaoMaoApplication sheJiaoMao;
	private ConfigSystemDao configSystemDao;

	private Passport passport;
	private boolean isPassportLogin;
	private LocalAccount originCurrentAccount;
	private boolean isCustomKeyLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.accounts);
        this.setSlideDirection(SlideDirection.LEFT);

		this.context = AccountsActivity.this;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
	    this.originCurrentAccount = sheJiaoMao.getCurrentAccount();
        this.configSystemDao = new ConfigSystemDao(context);

        initComponents();

		initAccountsList();
		initPassport();
		bindEvent();

		updateBackButton();
	}
	
	private void initComponents() {
		LinearLayout llRoot = (LinearLayout)findViewById(R.id.llRoot);
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ThemeUtil.setRootBackground(llRoot);
    	ThemeUtil.setSecondaryHeader(llHeaderBase);

    	//通行证头部
    	LinearLayout llPassport = (LinearLayout)findViewById(R.id.llPassport);
    	ImageView ivPassportPicture = (ImageView)findViewById(R.id.ivPassportPicture);
    	TextView tvPassportEmail = (TextView)findViewById(R.id.tvPassportEmail);
    	TextView tvUsageDescription = (TextView)findViewById(R.id.tvUsageDescription);
    	Button btnPassport = (Button)findViewById(R.id.btnPassport);
    	ThemeUtil.setHeaderProfile(llPassport);
    	ivPassportPicture.setImageDrawable(theme.getDrawable("icon_header_default"));
    	int content = theme.getColor("content");
    	tvPassportEmail.setTextColor(content);
    	tvUsageDescription.setTextColor(theme.getColor("quote"));
    	ThemeUtil.setBtnActionPositive(btnPassport);

    	//帐号列表
    	ListView lvAccounts = (ListView)findViewById(R.id.lvAccounts);
    	LinearLayout llAddAccountFrame = (LinearLayout)findViewById(R.id.llAddAccountFrame);
    	LinearLayout llAddAccount = (LinearLayout)findViewById(R.id.llAddAccount);
    	ImageView ivAddAccount = (ImageView)findViewById(R.id.ivAddAccount);
    	TextView tvAddAccount = (TextView)findViewById(R.id.tvAddAccount);
    	ImageView ivAddAccountMore = (ImageView)findViewById(R.id.ivAddAccountMore);
    	lvAccounts.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
    	lvAccounts.setPadding(0, 0, 0, 0);
    	ThemeUtil.setListViewStyle(lvAccounts);
    	llAddAccountFrame.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
    	int padding1 = theme.dip2px(1);
    	llAddAccountFrame.setPadding(padding1, padding1, padding1, padding1);
    	llAddAccount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_all_corner"));
    	int padding8 = theme.dip2px(8);
    	llAddAccount.setPadding(padding8, padding8, padding8, padding8);
    	ivAddAccount.setImageDrawable(theme.getDrawable("icon_account_add_normal"));
    	tvAddAccount.setTextColor(content);
    	ivAddAccountMore.setImageDrawable(theme.getDrawable("icon_more_detail"));

    	//工具栏
    	LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);
    	ImageView ivAccountSync = (ImageView)findViewById(R.id.ivAccountSync);
    	TextView tvAccountSync = (TextView)findViewById(R.id.tvAccountSync);
    	TextView tvLastSyncTime = (TextView)findViewById(R.id.tvLastSyncTime);
    	Button btnAccountSync = (Button)findViewById(R.id.btnAccountSync);
    	llFooterAction.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
    	int padding12 = theme.dip2px(12);
    	llFooterAction.setPadding(padding12, padding8, padding12, padding8);
    	ivAccountSync.setImageDrawable(theme.getDrawable("icon_account_sync_normal"));
    	tvAccountSync.setTextColor(content);
    	tvLastSyncTime.setTextColor(content);
    	ThemeUtil.setBtnActionNegative(btnAccountSync);

    	TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.title_accounts));
	}
	
	private void initAccountsList() {
		LinearLayout llAddAccount = (LinearLayout) this.findViewById(R.id.llAddAccount);
		llAddAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AccountsActivity.this, AddAccountActivity.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_ACCOUNT_ADD);
				CompatibilityUtil.overridePendingTransition(context,
					R.anim.slide_in_left, android.R.anim.fade_out);
			}
		});

		final ListView lvAccounts = (ListView) this.findViewById(R.id.lvAccounts);
		registerForContextMenu(lvAccounts);

		alAdapter = new AccountListAdapter(this);
		lvAccounts.setAdapter(alAdapter);
		setListViewHeightBasedOnChildren(lvAccounts);
		alAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				setListViewHeightBasedOnChildren(lvAccounts);
				updateBackButton();
				super.onChanged();
			}
		});

		lvAccounts.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LocalAccount account = (LocalAccount)parent.getItemAtPosition(position);
				switchAccount(account);
			}
		});
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});

		Button btnPassport = (Button) findViewById(R.id.btnPassport);
		btnPassport.setOnClickListener(new AccountPassportClickListener(this));

		Button btnSync = (Button) findViewById(R.id.btnAccountSync);
		if (isPassportLogin) {
			btnSync.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//new PassportSyncTask(context).execute();
				}
			});
		}
	}
	
	public void showPassportPoints(PointsLevel pl) {
		if (pl == null) {
			return;
		}
		TextView tvUsageDescription = (TextView)findViewById(R.id.tvUsageDescription);
		tvUsageDescription.setText(context.getString(
			R.string.label_passport_points, pl.getPoints(), pl.getTitle()));
	}

	public void showUsageDescription() {
		TextView tvUsageDescription = (TextView) findViewById(R.id.tvUsageDescription);
		tvUsageDescription.setText(R.string.hint_passport_usage);
	}

	public void initPassport() {
		Button btnPassport = (Button) findViewById(R.id.btnPassport);
		TextView tvPassportEmail = (TextView) findViewById(R.id.tvPassportEmail);
		Button btnAccountSync = (Button) findViewById(R.id.btnAccountSync);
		TextView tvLastSyncTime = (TextView) findViewById(R.id.tvLastSyncTime);
		
		passport = configSystemDao.getPassport();		
		if (passport != null && StringUtil.isNotEmpty(passport.getUsername())) {
			showPassportPoints(passport.getPointsLevel());
			tvPassportEmail.setText(passport.getEmail());
			btnPassport.setText(R.string.btn_passport_logout);
			ThemeUtil.setBtnActionPositive(btnAccountSync);
			btnAccountSync.setEnabled(true);
			
			String lastSyncTime = configSystemDao.getString(Constants.LAST_SYNC_TIME);
			if (StringUtil.isNotEmpty(lastSyncTime)) {
				lastSyncTime = getString(R.string.label_passport_last_synced_time, lastSyncTime);
				tvLastSyncTime.setText(lastSyncTime);
				tvLastSyncTime.setVisibility(View.VISIBLE);
			}
			
			if (passport.getPointsLevel() == null 
				|| StringUtil.isEmpty(passport.getPointsLevel().getMilitaryRank())
			    || isNeedQueryPointLevel) {
				new QueryPointLevelTask(this).execute();
			}
			isPassportLogin = true;
			isCustomKeyLevel = passport.getPointsLevel().getPoints() >= Constants.POINTS_CUSTOM_SOURCE_LEVEL ;
		} else {
			showUsageDescription();
			tvPassportEmail.setText(R.string.label_passport_username);
			btnPassport.setText(R.string.btn_passport_login);
			ThemeUtil.setBtnActionNegative(btnAccountSync);
			btnAccountSync.setEnabled(false);			
			isPassportLogin = false;
			isCustomKeyLevel = false;
		}
		// isCustomKeyLevel = true; // 调试用
		if (isPassportLogin && isCustomKeyLevel) {
			ConfigAppSyncTask appSyncTask = new ConfigAppSyncTask(this);
			appSyncTask.execute();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int position = adapterMenuInfo.position;
		LocalAccount account = alAdapter.getItem(position);
		
		menu.setHeaderTitle(account.getUser().getScreenName() + "/" + account.getServiceProvider().getSpName());
		menu.add(Menu.NONE, SWITCH_TO_MENU_ID, Menu.NONE, R.string.menu_accounts_switch_to);
		menu.add(Menu.NONE, SET_DEFAULT_MENU_ID, Menu.NONE, R.string.menu_accounts_set_default);
	    menu.add(Menu.NONE, DELETE_MENU_ID, Menu.NONE, R.string.menu_accounts_delete);
		
	    super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int position = menuInfo.position;
		final LocalAccount account = alAdapter.getItem(position);
		final AccountListAdapter listAdapter = alAdapter;

		switch (item.getItemId()) {
		case SWITCH_TO_MENU_ID:
			switchAccount(account);
			break;
		case SET_DEFAULT_MENU_ID:
			//设为默认被选中
			if (account.isSnsAccount()) {
				Toast.makeText(context,
					R.string.msg_accounts_operation_unsupported,
					Toast.LENGTH_SHORT).show();
				break;
			}
			if (Logger.isDebug()) {
				Log.d(TAG, "Set Account " + account + " as default");
			}
			try {
				listAdapter.editAccount(Action.MakeDefault, account);
				Toast.makeText(context, R.string.msg_accounts_set_successful,
					Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				if (Logger.isDebug()) {
					Log.e(TAG, e.getMessage(), e);
				}
				Toast.makeText(context, R.string.msg_accounts_set_failed,
					Toast.LENGTH_LONG).show();
			}

			break;
		case DELETE_MENU_ID:
			//删除帐号被选中
			if (Logger.isDebug()) Log.d(TAG, "Delete Account " + account);
			new AlertDialog.Builder(context)
			.setTitle(R.string.title_dialog_alert)
			.setMessage(R.string.msg_accounts_delete_confirm)
			.setNegativeButton(R.string.btn_cancel,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
			.setPositiveButton(R.string.btn_confirm,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							alAdapter.editAccount(Action.Delete, account);
							Toast.makeText(context, R.string.msg_accounts_delete_successful,
								Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage(), e);
							Toast.makeText(context, R.string.msg_accounts_delete_failed,
								Toast.LENGTH_SHORT).show();
						}
					}
			})
			.show();
			break;
		default:
			break;
		}

		return true;
	}

	private void switchAccount(LocalAccount account) {
		if (account == null || account.isSnsAccount()) {
			Toast.makeText(context, R.string.msg_accounts_operation_unsupported,
				Toast.LENGTH_SHORT).show();
			return;
		}
		if (Logger.isDebug()) {
			Log.d(TAG, "Switch To Account " + account);
		}
		if (originCurrentAccount == null
			|| !originCurrentAccount.equals(account)) {
			sheJiaoMao.setCurrentAccount(account);
			this.setResult(Constants.RESULT_CODE_ACCOUNT_SWITCH);
		}
		this.finish();
		CompatibilityUtil.overridePendingTransition(context,
			R.anim.slide_in_right, android.R.anim.fade_out);
	}
	
	private void switchAccount() {
		if (alAdapter.getCount() < 1) {
			this.setResult(Constants.RESULT_CODE_ACCOUNT_EXIT_APP);
			this.finish();
			CompatibilityUtil.overridePendingTransition(
					context, R.anim.slide_in_right, android.R.anim.fade_out);
		} else {
			switchAccount(sheJiaoMao.getCurrentAccount());
		}
	}

	@Override
	public void onBackPressed() {
		goBack();
	}

	private void goBack() {
		LocalAccountDao accountDao = new LocalAccountDao(this);
		passport = configSystemDao.getPassport();
		if (passport != null 
			&& StringUtil.isNotEmpty(passport.getUsername())
			&& accountDao.hasUnsyncedAccounts()) {
			new AlertDialog.Builder(context)
			.setTitle(R.string.title_dialog_alert)
			.setMessage(R.string.msg_accounts_unsynced_detected)
			.setNegativeButton(R.string.btn_accounts_sync_later,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						 switchAccount();
					}
				})
			.setPositiveButton(R.string.btn_accounts_sync_now,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//new PassportSyncTask(context).execute();
					}
			})
			.show();
		} else {
			 switchAccount();
		}
	}

	private void updateBackButton() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		if (alAdapter.getCount() > 0) {
			btnBack.setVisibility(View.VISIBLE);
		} else {
			btnBack.setVisibility(View.INVISIBLE);
		}
	}

	public void updatePassportView() {
		initPassport();
	}

	public AccountListAdapter getAccountListAdapter() {
		return alAdapter;
	}

	private boolean isNeedQueryPointLevel = true;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.RESULT_CODE_SUCCESS) {
			switch (requestCode) {
			case Constants.REQUEST_CODE_PASSPORT_LOGIN:
				isNeedQueryPointLevel = false;
				initPassport();
				bindEvent();
				break;
			case Constants.REQUEST_CODE_ACCOUNT_ADD:
				SharedPreferences prefs =
					context.getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, Context.MODE_PRIVATE);
				//获取新增加的帐号ID
				long newAccountId = prefs.getLong(Constants.PREFS_KEY_ACCOUNT_ADDED, 0);
				//清除SharedPreferences中临时保存的AccountId值
				prefs.edit().remove(Constants.PREFS_KEY_ACCOUNT_ADDED).commit();
				if (newAccountId > 0) {
					LocalAccountDao accountDao = new LocalAccountDao(context);
					LocalAccount account = accountDao.findById(newAccountId);
					alAdapter.editAccount(Action.Add, account);
				}
				alAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	/**
	 * 设置ListView的高度，只有高度固定，才不会和外层的ScrollView产生冲突。
	 * ScrollView内部嵌入ListView会引起ListView显示不全（只显示一行半）或无法滚动ListView。
	 *
	 * @param listView
	 */
	private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        if (listAdapter.getCount() == 0) {
        	listView.setVisibility(View.GONE);
        	return;
        } else {
        	listView.setVisibility(View.VISIBLE);
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

	public boolean isPassportLogin() {
		return isPassportLogin;
	}

	public ConfigSystemDao getConfigSystemDao() {
		return configSystemDao;
	}

	public void setPassportLogin(boolean isPassportLogin) {
		this.isPassportLogin = isPassportLogin;
		passport = null;
	}

	public Passport getPassport() {
		return passport;
	}
}
