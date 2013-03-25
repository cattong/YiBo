package com.shejiaomao.weibo.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cattong.commons.Logger;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SheJiaoMaoApplication.changeLocale(SplashActivity.this);
				SheJiaoMaoApplication.initLocalization(SplashActivity.this);
			}			
		}, 
		2000);
		
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		MobclickAgent.updateOnlineConfig(this);

		if (Logger.isDebug()) {
			Log.v(TAG, "onCreate……");
		}
		//意外退出时，重启，清除通知;
		NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notiManager.cancelAll();

		Intent startIntent = new Intent(this, HomePageActivity.class);
		startIntent.putExtra("START", true);
		startActivityForResult(startIntent, Constants.REQUEST_CODE_SPLASH);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Logger.isDebug()) {
			Log.v(TAG, "onResume……");
		}
		MobclickAgent.onResume(this);
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Logger.isDebug()) {
			Log.v(TAG, "onPause……");
		}
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (Logger.isDebug()) {
			Log.v(TAG, "onNewIntent……");
		}
		Intent startIntent = new Intent(this, HomePageActivity.class);
		startActivityForResult(startIntent, Constants.REQUEST_CODE_SPLASH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Logger.isDebug()) {
			Log.v(TAG, "onActivityResult……");
		}

		if (resultCode == Constants.RESULT_CODE_SPLASH_EXIT) {
			moveTaskToBack(true);
			finish();
		    android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (Logger.isDebug()) {
			Log.v(TAG, "onStop……");
		}
	}

	@Override
	protected void onStart() {
		super.onStop();
		if (Logger.isDebug()) {
			Log.v(TAG, "onStart……");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Logger.isDebug()) {
			Log.v(TAG, "onDestroy……");
		}
	}

}
