package com.shejiaomao.weibo.service.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OnDoubleClickListener implements OnTouchListener {
	private static final long DOUBLE_CLICK_INTERVAL_TIME = 1500;

	private int count = 0;
	private long firstClick = 0L;
	private long secondClick = 0L;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}

		count++;
		if (count == 1) {
		    firstClick = System.currentTimeMillis();
		    return false;
		}

	    secondClick = System.currentTimeMillis();
	    if (secondClick - firstClick > DOUBLE_CLICK_INTERVAL_TIME) {
	    	count = 1;
	    	firstClick = secondClick;
//	    	count = 0;
//		    firstClick = 0L;
//		    secondClick = 0L;
	    	return false;
	    }

        //双击事件
	    onDoubleClick(v);

	    count = 0;
	    firstClick = 0L;
	    secondClick = 0L;

	    return true;
	}

  
  public abstract void onDoubleClick(View v);
  	
}
