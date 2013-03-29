package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.cattong.commons.Logger;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.HomePageActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.umeng.update.UmengUpdateAgent;

public class InitAppTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = InitAppTask.class.getSimpleName();

	private HomePageActivity context;
    private SheJiaoMaoApplication shejiaomao;

	public InitAppTask(HomePageActivity context) {
		this.context = context;
		this.shejiaomao = (SheJiaoMaoApplication)context.getApplication();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context.setContentView(R.layout.splash);
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (Logger.isDebug()) {
			Log.v(TAG, "InitAppTask ... , Intent : " + context.getIntent());
		}

		//初始化
		context.initComponents();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (shejiaomao.isAutoScreenOrientation()) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} else {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		context.updateContentView(null);

		if (!NetUtil.isConnect(context)) {
			showNetSettingsDialog();
		}

		if (GlobalVars.IS_MOBILE_NET_UPDATE_VERSION) {
			UmengUpdateAgent.setUpdateOnlyWifi(false);
		}
		if (shejiaomao.isCheckNewVersionOnStartup()) {
			//检查更新
			UmengUpdateAgent.update(context);
		}

		//清除缓存
		StatusesCleanTask statusCleanTask = new StatusesCleanTask(context);
		statusCleanTask.execute();
		ImageCacheQuickCleanTask imageCacheTask = new ImageCacheQuickCleanTask(context);
		imageCacheTask.execute();
	}

	private void showNetSettingsDialog() {
		new AlertDialog.Builder(context)
		    .setTitle(context.getString(R.string.title_dialog_net_setting))
		    .setMessage(context.getString(R.string.msg_net_setting))
		    .setPositiveButton(context.getString(R.string.btn_net_setting),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.startActivityForResult(
							new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS),
							Constants.REQUEST_CODE_NET_SETTINGS
						);
					}
				})
			.setNegativeButton(context.getString(R.string.btn_net_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
			.show();
	}


}
