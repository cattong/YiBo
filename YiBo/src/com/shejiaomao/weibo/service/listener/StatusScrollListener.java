package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.service.adapter.StatusHolder;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;
import com.shejiaomao.weibo.service.task.QueryResponseCountTask;

public class StatusScrollListener implements OnScrollListener {
    private static final String TAG = "StatusScrollListener";
    
    private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        	displayImage(view);
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		
		switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
		    //Log.v(TAG, "已经停止：SCROLL_STATE_IDLE" + "-->" + view.getCount()); 
		    Context context = view.getContext();
		    SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication) context.getApplicationContext();
		    if (view.getLastVisiblePosition() == view.getCount() - 1
		    	&& sheJiaoMao.isAutoLoadMore()) {
		    	view.getChildAt(view.getChildCount() - 1).performClick();
		    }
		    
		    displayImage(view);
		    break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
 		   //Log.v(TAG, "SCROLL_STATE_TOUCH_SCROLL:当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1");
 		   break;
        case OnScrollListener.SCROLL_STATE_FLING:
		    //Log.v(TAG, "SCROLL_STATE_FLING:由于用户的操作，屏幕产生惯性滑动时为2");
		    break;
	    }

	}

	private void displayImage(AbsListView listView) {
		int firstPos = listView.getFirstVisiblePosition();
		int lastPos = listView.getLastVisiblePosition();
		int totalCount = lastPos - firstPos + 1;
		
		Log.v(TAG, "滚动停止加载图片..");
		for (int i = 0; i < totalCount; i++) {
			View view = listView.getChildAt(i);
			Object tag = view.getTag();
		    if (!(tag instanceof StatusHolder)) {
		    	continue;
		    }
		    
		    StatusHolder holder = (StatusHolder)view.getTag();
		    ImageLoad4ThumbnailTask thumbnailTask = holder.thumbnailTask;
		    if (thumbnailTask != null 
		    	&& thumbnailTask.isCancelled() == false
		    	&& thumbnailTask.getStatus() == Status.PENDING) {
		    	thumbnailTask.execute();
		    }
		    
		    QueryResponseCountTask responseCountTask  = holder.responseCountTask;
		    if (responseCountTask != null
		    	&& responseCountTask.isCancelled() == false
		    	&& responseCountTask.getStatus() == Status.PENDING) {
		    	responseCountTask.execute();
		    }
		}
	}

}
