package net.dev123.yibo;

import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mobclick.android.MobclickAgent;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				YiBoApplication.changeLocale(SplashActivity.this);
				YiBoApplication.initLocalization(SplashActivity.this);
			}			
		}, 
		2000);
		
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		MobclickAgent.updateOnlineConfig(this);

		if (Constants.DEBUG) {
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
		if (Constants.DEBUG) {
			Log.v(TAG, "onResume……");
		}
		MobclickAgent.onResume(this);
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Constants.DEBUG) {
			Log.v(TAG, "onPause……");
		}
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (Constants.DEBUG) {
			Log.v(TAG, "onNewIntent……");
		}
		Intent startIntent = new Intent(this, HomePageActivity.class);
		startActivityForResult(startIntent, Constants.REQUEST_CODE_SPLASH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Constants.DEBUG) {
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
		if (Constants.DEBUG) {
			Log.v(TAG, "onStop……");
		}
	}

	@Override
	protected void onStart() {
		super.onStop();
		if (Constants.DEBUG) {
			Log.v(TAG, "onStart……");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Constants.DEBUG) {
			Log.v(TAG, "onDestroy……");
		}
	}

}
