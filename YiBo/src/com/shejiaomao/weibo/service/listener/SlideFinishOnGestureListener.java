package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;

import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.ImageViewer4GifActivity;

public class SlideFinishOnGestureListener implements OnGestureListener {
	//mdpi下的比例计算;
	private static final float FACTOR_PORTRAIT;
	private static final float FACTOR_LANDSCAPE;
	//手指在屏幕上移动距离小于此值不会被认为是手势
	private static int SLIDE_MIN_DISTANCE_X;
	private static int SLIDE_MAX_DISTANCE_Y;

	private static int DISPLAY_WINDOW_WIDTH;
	private static int DISPLAY_WINDOW_HEIGHT;
	static {
		FACTOR_PORTRAIT = 100f / 320;
		FACTOR_LANDSCAPE = FACTOR_PORTRAIT;
	}

	private Context context;
	private SlideDirection slideDirection;

	//划动方向
	public enum SlideDirection {
		NONE,
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}

	public SlideFinishOnGestureListener(Context context, SlideDirection slideDirection) {
		this.context = context;
		this.slideDirection = slideDirection;
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

		if (distanceX > 0
			&& Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X
			&& Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y
			&& slideDirection == SlideDirection.LEFT) {
			//slide to left
			if (context instanceof ImageViewer4GifActivity) {
				((ImageViewer4GifActivity)context).onBackPressed();
			} else {
				((Activity)context).finish();
			}
			CompatibilityUtil.overridePendingTransition(
				((Activity)context), R.anim.slide_in_right, android.R.anim.fade_out
			);
			return true;
		} else if (
			distanceX < 0
			&& Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X
			&& Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y
			&& slideDirection == SlideDirection.RIGHT) {
			//slide to right
			if (context instanceof ImageViewer4GifActivity) {
				((ImageViewer4GifActivity)context).onBackPressed();
			} else {
				((Activity)context).finish();
			}
			CompatibilityUtil.overridePendingTransition(
				((Activity)context), R.anim.slide_in_left, android.R.anim.fade_out
			);
			return true;
		}

		if (Math.abs(distanceX) > SLIDE_MIN_DISTANCE_X
			&& Math.abs(distanceY) < SLIDE_MAX_DISTANCE_Y) {
			if (slideDirection == SlideDirection.LEFT) {
				Toast.makeText(context, context.getString(R.string.msg_gesture_left), Toast.LENGTH_SHORT).show();
			} else if (slideDirection == SlideDirection.RIGHT) {
				Toast.makeText(context, context.getString(R.string.msg_gesture_right), Toast.LENGTH_SHORT).show();
			}
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

	public SlideDirection getSlideDirection() {
		return slideDirection;
	}

	public void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
	}

}
