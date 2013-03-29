package com.shejiaomao.weibo.activity;

import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.Logger;
import com.cattong.commons.http.HttpRequestHelper;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.GroupStatusesListAdapter;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.service.cache.Cache;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;
import com.shejiaomao.weibo.service.listener.HomePageOnGestureListener;
import com.shejiaomao.weibo.service.listener.HomePageScreenToggleClickListener;
import com.shejiaomao.weibo.service.listener.HomePageScreenToggleClickListener.ScreenToggle;
import com.shejiaomao.weibo.service.task.InitAppTask;
import com.shejiaomao.weibo.service.task.VerifyCredentialsTask;
import com.shejiaomao.weibo.widget.Skeleton;
import com.umeng.analytics.MobclickAgent;

public class HomePageActivity extends Activity {
	private static final String TAG = HomePageActivity.class.getSimpleName();
    private static final int DIALOG_EXIT = 1;

	private SheJiaoMaoApplication sheJiaoMao = null;
	private Skeleton skeleton = null;
	private ScreenToggle toggle = null;
	
	private GestureDetector detector;//触摸监听实例
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sheJiaoMao = (SheJiaoMaoApplication) getApplication();

		if (Logger.isDebug()) {
			Log.v(TAG, "onCreate……" + ", Intent : " + getIntent());
		}

		boolean isStartup = getIntent().getBooleanExtra("START", false);
		if (isStartup && savedInstanceState == null) {
			new InitAppTask(this).execute();
		} else {
			initComponents();
			updateContentView(savedInstanceState);
		}

		HomePageOnGestureListener gestureListener = new HomePageOnGestureListener(this);
		detector = new GestureDetector(this, gestureListener);
	}

	public void initComponents() {
		skeleton = new Skeleton(this);
        
		//启动service和注册接收器
		Intent serviceIntent = new Intent(this, AutoUpdateService.class);
		startService(serviceIntent);
	}

	public void updateContentView(Bundle savedInstanceState) {
		LocalAccount currentAccount = null;
		int contentType = Skeleton.TYPE_MY_HOME;

		if (savedInstanceState != null) {
	        if (savedInstanceState.containsKey(Constants.PREFS_KEY_CURRENT_ACCOUNT)) {
				long currentAccountId = savedInstanceState.getLong(Constants.PREFS_KEY_CURRENT_ACCOUNT);
				currentAccount = GlobalVars.getAccount(currentAccountId);
			}
	        if (savedInstanceState.containsKey("CONTENT_TYPE")) {
	        	contentType = savedInstanceState.getInt("CONTENT_TYPE");
	        }
		}

		if (currentAccount == null) {
			currentAccount = sheJiaoMao.getCurrentAccount();
		}

		if (currentAccount == null) {
			Intent accountsIntent = new Intent();
			accountsIntent.setClass(this, AccountsActivity.class);
			startActivityForResult(accountsIntent, Constants.REQUEST_CODE_ACCOUNTS);
		} else {
			setContentView(skeleton);
			sheJiaoMao.setCurrentAccount(currentAccount);
			skeleton.setCurrentAccount(currentAccount, true);
			skeleton.setContentType(contentType);

		    if (!currentAccount.isVerified()) {
				VerifyCredentialsTask task = new VerifyCredentialsTask(this, currentAccount);
				task.execute();
		    }
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (Logger.isDebug()) {
			Log.v(TAG, "onResume……" + ", Skeleton is " + skeleton);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Logger.isDebug()) {
			Log.v(TAG, "onActivityResult……" + ", Skeleton is " + skeleton);
		}

		Bundle bundle = null;
		if (data != null) {
			bundle = data.getExtras();
		}

		switch (requestCode) {
		case Constants.REQUEST_CODE_ACCOUNTS:
			switch (resultCode) {
			case Constants.RESULT_CODE_ACCOUNT_SWITCH:
				if (skeleton.getParent() == null) {
					setContentView(skeleton);
				}
				LocalAccount account = sheJiaoMao.getCurrentAccount();
				skeleton.setCurrentAccount(account, true);
				skeleton.setContentType(Skeleton.TYPE_MY_HOME);
				if (account != null && !account.isVerified()) {
					VerifyCredentialsTask task = new VerifyCredentialsTask(this, account);
					task.execute();
				}
				break;
			case Constants.RESULT_CODE_ACCOUNT_EXIT_APP:
				exitApp();
				break;
			default:
				break;
			}

			break;
		case Constants.REQUEST_CODE_MICRO_BLOG:
			if (resultCode == Constants.RESULT_CODE_MICRO_BLOG_DELETE) {
				Status status = (Status) bundle.getSerializable("STATUS");
				if (status != null) {
					ListView lvMicroBlog = (ListView) this.findViewById(R.id.lvMicroBlog);
					ListAdapter listAdapter = lvMicroBlog.getAdapter();
					CacheAdapter<?> adapter = AdapterUtil.getCacheAdapter(listAdapter);
					if (adapter != null) {
					    ((CacheAdapter<Status>)adapter).remove(status);
					}
				}
			}
			break;
		case Constants.REQUEST_CODE_SETTINGS:
			if (resultCode != Constants.RESULT_CODE_SUCCESS) {
                break;
			}
			if (sheJiaoMao.isAutoScreenOrientation()) {
	            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
			} else {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			//语言设置
			Configuration config = new Configuration();
		    config.locale = GlobalVars.LOCALE;
		    getResources().updateConfiguration(config, null);
		    
			ListView lvMicroBlog = (ListView)findViewById(R.id.lvMicroBlog);
			if (lvMicroBlog != null) {
				lvMicroBlog.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
				//字体改变时更新;
				ListAdapter adapter = lvMicroBlog.getAdapter();
				CacheAdapter<?> cacheAdapter = AdapterUtil.getCacheAdapter(adapter);
				if (cacheAdapter != null) {
                    cacheAdapter.notifyDataSetChanged();
		        }
			}
			break;
		case Constants.REQUEST_CODE_PROFILE_EDIT:
			if (resultCode == Constants.RESULT_CODE_SUCCESS) {
				skeleton.setContentType(Skeleton.TYPE_PROFILE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_options_home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		switch (item.getItemId()) {
		case R.id.menu_accounts:
			intent.putExtras(bundle);
			intent.setClass(HomePageActivity.this, AccountsActivity.class);
			this.startActivityForResult(intent,	Constants.REQUEST_CODE_ACCOUNTS);
			break;
		case R.id.menu_search:
			this.onSearchRequested();
			break;
		case R.id.menu_back2top:
			ListView lvMicroBlog = (ListView)this.findViewById(R.id.lvMicroBlog);
            if (lvMicroBlog != null) {
          	    lvMicroBlog.setSelection(1);
            }
			break;
		case R.id.menu_setting:
			intent.setClass(HomePageActivity.this, SettingActivity.class);
			this.startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
			break;
		case R.id.menu_fullscreen:
			GlobalVars.IS_FULLSCREEN = !GlobalVars.IS_FULLSCREEN;
			if (GlobalVars.IS_FULLSCREEN) {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				showScreenToggle();
				item.setTitle(R.string.menu_not_fullscreen);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				hideScreenToggle();
				item.setTitle(R.string.menu_fullscreen);
			}
			
//			String aboutMsg = getResources().getString(R.string.about);
//			aboutMsg = String.format(aboutMsg, GlobalResource.getVersionName(this));
//			new AlertDialog.Builder(this)
//			    .setTitle(this.getString(R.string.title_dialog_about))
//			    .setMessage(aboutMsg)
//			    .create()
//			    .show();
			break;
		case R.id.menu_quit:
			showDialog(DIALOG_EXIT);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SheJiaoMaoApplication.changeLocale(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (sheJiaoMao.isKeyBackExit()) {
			    showDialog(DIALOG_EXIT);
			} else {
			    this.moveTaskToBack(true);
			}
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean isGesture = false;
		if (GlobalVars.IS_ENABLE_GESTURE
				&& detector != null
				&& getSkeleton() != null ) {
			isGesture = detector.onTouchEvent(ev);
		}
		//miui rom list slide back support;
		isGesture = false;
		if (isGesture) {
			return isGesture;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		if (Logger.isDebug()) {
			Log.v(TAG, "onNewIntent……" + ", Intent : " + newIntent);
		}

		LocalAccount account = (LocalAccount)newIntent.getSerializableExtra("ACCOUNT");
		if (account == null) {
			updateContentView(null);
			return;
		}
		int contentType = newIntent.getIntExtra("CONTENT_TYPE", Skeleton.TYPE_MY_HOME);

		sheJiaoMao.setCurrentAccount(account); // 设置当前帐号
		skeleton.setCurrentAccount(account, true);
		skeleton.setContentType(contentType);

		// move to head
		ListView lvMicroBlog = (ListView) this.findViewById(R.id.lvMicroBlog);
		if (lvMicroBlog != null) {
			ListAdapter adapter = lvMicroBlog.getAdapter();
			CacheAdapter<?> cacheAdapter = AdapterUtil.getCacheAdapter(adapter);
			//有可能处于分组中
			if (contentType == Skeleton.TYPE_MY_HOME
				&& cacheAdapter instanceof GroupStatusesListAdapter) {
				Cache cache = CacheManager.getInstance().getCache(account);
				AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
				if (adapterCache != null) {
					cacheAdapter = adapterCache.getMyHomeListAdapter();
					lvMicroBlog.setAdapter(cacheAdapter);
					TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
					String title = "";
					if (account.getUser() != null) {
					    title += account.getUser().getScreenName() + "@";
					}
					title += account.getServiceProvider().getSpName();
					tvTitle.setText(title);
				}
			}
	        if (cacheAdapter != null && cacheAdapter.refresh()) {
	            cacheAdapter.reclaim(ReclaimLevel.MODERATE);
	        }
	        if (lvMicroBlog.getChildCount() > 1) {
	        	lvMicroBlog.setSelection(1);
	        }
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (Logger.isDebug()) {
			Log.v(TAG, "onPause……" + ", Skeleton is " + skeleton);
		}
	}

	protected void onStop() {
		super.onStop();
		if (Logger.isDebug()) {
			Log.v(TAG, "onStop……" + ", Skeleton is " + skeleton);
		}
		if (!sheJiaoMao.isShowStatusIcon()) {
			return;
		}

		int taskId = 0;
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	    List<RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
		if (ListUtil.isNotEmpty(taskInfoList)) {
			RunningTaskInfo taskInfo = taskInfoList.get(0);
			taskId = taskInfo.id;
		}
		if (this.getTaskId() != taskId) {
			NotificationManager notificationManager = (NotificationManager)
			    getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(this, SplashActivity.class);
			notificationIntent.setAction(Intent.ACTION_MAIN);
			notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			PendingIntent contentIntent = PendingIntent.getActivity(
				this, (int)System.currentTimeMillis(),
			    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = new Notification();
			notification.icon = R.drawable.icon_notification;
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_NO_CLEAR;

			String contentTitle = this.getString(R.string.app_name);
			String contentText = this.getString(R.string.label_ongoing);
			notification.contentIntent = contentIntent;
		    notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
			notificationManager.notify(R.string.app_name, notification);
		}
	}

	protected void onStart() {
		super.onStart();
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
		if (Logger.isDebug()) {
			Log.v(TAG, "onStart……" + ", Skeleton is " + skeleton);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
    	switch(id) {
    	case DIALOG_EXIT:
    		dialog =
    			new AlertDialog.Builder(this)
    			    .setTitle(R.string.title_dialog_alert)
    				.setMessage(R.string.msg_quit)
    				.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog,	int which) {
    						dialog.dismiss();
    						exitApp();
    					}
    				})
    				.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						dialog.dismiss();
    					}
    				})
    				.create();
    		break;
    	default:
    		break;
    	}
    	return dialog;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (skeleton == null) {
			return;
		}

		if (skeleton.getCurrentAccount() != null) {
			outState.putLong(Constants.PREFS_KEY_CURRENT_ACCOUNT, skeleton.getCurrentAccount().getAccountId());
		}
		if (skeleton.getContentType() > 0) {
			outState.putInt("CONTENT_TYPE", skeleton.getContentType());
		}

		if (Logger.isDebug()) {
			Log.v(TAG, "onSaveInstanceState……" + ", Skeleton is " + skeleton);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Logger.isDebug()) {
			Log.v(TAG, "onDestroy……" + ", Skeleton is " + skeleton);
		}
	}

	public void exitApp() {
		setResult(Constants.RESULT_CODE_SPLASH_EXIT);
		finish();

		Intent serviceIntent = new Intent(this, AutoUpdateService.class);
		stopService(serviceIntent);

		// 清除通知;
		NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancelAll();
		sheJiaoMao.setCurrentAccount(null);

		CacheManager.getInstance().clear();

		GlobalVars.clear();

		HttpRequestHelper.shutdown();

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
        if (skeleton != null) {
        	skeleton.reclaim();
        }
		CacheManager.getInstance().reclaim(ReclaimLevel.MODERATE);
		MobclickAgent.onEvent(this, "on_low_memory");
		if (Logger.isDebug()) {
		    Toast.makeText(this, "low memory!", Toast.LENGTH_SHORT).show();
		    Log.w(TAG, "low memory, will reclaim!");
		}
	}

	public Skeleton getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}
	
	
	private void showScreenToggle() {
		if (skeleton == null) {
			return;
		}
        
		final Activity context = this;
		if (toggle == null) {
			skeleton.postDelayed(new Runnable() {

				@Override
				public void run() {
					LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View tabToggle = inflater.inflate(R.layout.widget_tab_toggle, null);
					Theme theme = ThemeUtil.createTheme(context);
					Button btnTabToggle = (Button)tabToggle.findViewById(R.id.btnTabToggle);			
					btnTabToggle.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle"));
					
					PopupWindow popTabToggle = new PopupWindow(tabToggle, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					popTabToggle.setFocusable(false);
					popTabToggle.setOutsideTouchable(true);

					View llFooter = context.findViewById(R.id.llFooter);			
					toggle = new ScreenToggle(popTabToggle, context.getWindow().getDecorView(), null, llFooter);
					tabToggle.setOnClickListener(new HomePageScreenToggleClickListener(toggle));					
				} 

			},
			2000);
		}
	}
	
	private void hideScreenToggle() {
		if (skeleton == null || toggle == null) {
			return;
		}
		toggle.dismiss();
		toggle = null;
		
	}
}