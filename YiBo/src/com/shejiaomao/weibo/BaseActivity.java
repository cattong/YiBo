package com.shejiaomao.weibo;

import java.util.List;

import com.cattong.commons.util.ListUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.shejiaomao.weibo.activity.ImageViewer4GifActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.SplashActivity;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.service.cache.ReclaimLevel;
import com.shejiaomao.weibo.service.listener.Back2TopDoubleClickListener;
import com.shejiaomao.weibo.service.listener.SlideFinishOnGestureListener;
import com.shejiaomao.weibo.service.listener.SlideFinishOnGestureListener.SlideDirection;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	protected GestureDetector detector; //触摸监听实例
	protected SlideFinishOnGestureListener gestureListener;
	protected SlideDirection slideDirection;
	protected Theme theme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (GlobalVars.IS_FULLSCREEN) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		theme = new Theme(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		NotificationManager notificationManager = (NotificationManager)
		    getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)this.getApplication();
		if (!sheJiaoMao.isShowStatusIcon()
			|| this.getClass().equals(ImageViewer4GifActivity.class)) {
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

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev == null) {
			return true;
		}
		boolean isGesture = false;
		if (GlobalVars.IS_ENABLE_GESTURE) {
			if (slideDirection == null) {
				slideDirection = SlideDirection.RIGHT;
			}
			if (detector == null) {
			    gestureListener = new SlideFinishOnGestureListener(this, slideDirection);
			    detector = new GestureDetector(this, gestureListener);
			}
			isGesture = detector.onTouchEvent(ev);
		}
		if (isGesture) {
			return isGesture;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
    	finish();
    }

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		CacheManager.getInstance().reclaim(ReclaimLevel.MODERATE);
		MobclickAgent.onEvent(this, "on_low_memory");
	}

	public Theme getSkinTheme() {
		return theme;
	}
	
	public SlideDirection getSlideDirection() {
		return slideDirection;
	}

	public void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
		if (gestureListener != null) {
		    gestureListener.setSlideDirection(slideDirection);
		}
	}
	
	public void setBack2Top(ListView listView) {
		if (listView == null) {
			return;
		}
		View llHeaderBase = this.findViewById(R.id.llHeaderBase);
		if (llHeaderBase == null) {
			return;
		}
		Back2TopDoubleClickListener back2TopListener = new Back2TopDoubleClickListener(listView);
	    llHeaderBase.setOnTouchListener(back2TopListener);	
	}
}
