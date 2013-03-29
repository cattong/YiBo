package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;

import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AccountsActivity;
import com.shejiaomao.weibo.common.Constants;

public class HomePageOnGestureListener implements OnGestureListener {
	//mdpi下的比例计算;
	private static final float FACTOR_PORTRAIT; 
	private static final float FACTOR_LANDSCAPE;
	//水平滑动的参数
	private static int SLIDE_MIN_DISTANCE_X;
	private static int SLIDE_MAX_DISTANCE_Y;
	
	private static int DISPLAY_WINDOW_WIDTH;
	private static int DISPLAY_WINDOW_HEIGHT;
	static {
		FACTOR_PORTRAIT = 120f / 320;
		FACTOR_LANDSCAPE = FACTOR_PORTRAIT;
	}
	
    private Context context;
    //private int orientation;
	public HomePageOnGestureListener(Context context) {
		this.context = context;
		initEnv(context);
	}
	
	private void initEnv(Context context) {
		// 获得屏幕大小
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DISPLAY_WINDOW_WIDTH = display.getWidth();
		DISPLAY_WINDOW_HEIGHT = display.getHeight();
		
		SLIDE_MIN_DISTANCE_X = (int)(DISPLAY_WINDOW_WIDTH * FACTOR_PORTRAIT);
		SLIDE_MAX_DISTANCE_Y = (int)(DISPLAY_WINDOW_HEIGHT * FACTOR_LANDSCAPE);
		SLIDE_MAX_DISTANCE_Y = 120;
		//orientation = context.getResources().getConfiguration().orientation;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float x1 = (e1 != null ? e1.getX() : 0);
		float x2 = (e2 != null ? e2.getX() : 0);
		float y1 = (e1 != null ? e1.getY() : 0);
		float y2 = (e2 != null ? e2.getY() : 0);
		float distanceX = x1 - x2;
		float distanceY = y1 - y2;
		
		//切换帐号，符合条件
		if (
			distanceX < 0 && //slide to right
			Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X &&
			Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y
		) {
			Intent intent = new Intent();
			intent.setClass(context, AccountsActivity.class);
			((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_ACCOUNTS);
			CompatibilityUtil.overridePendingTransition(
				(Activity)context, R.anim.slide_in_left, android.R.anim.fade_out
			);
			return true;
		}
			
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
}
