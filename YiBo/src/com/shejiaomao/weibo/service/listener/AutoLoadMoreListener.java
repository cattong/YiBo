package com.shejiaomao.weibo.service.listener;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;

public class AutoLoadMoreListener implements OnScrollListener {
    private static final String TAG = "AutoLoadMoreListener";

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
		    //Log.v(TAG, "已经停止：SCROLL_STATE_IDLE" + "-->" + view.getCount());
		    Context context = view.getContext();
		    SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication) context.getApplicationContext();
		    if (view.getLastVisiblePosition() == view.getCount() - 1
		    	&& sheJiaoMao.isAutoLoadMore()) {
		    	view.getChildAt(view.getChildCount() - 1).performClick();
		    }
		    break;
        case OnScrollListener.SCROLL_STATE_FLING:
		    //Log.v(TAG, "开始滚动：SCROLL_STATE_FLING");
		    break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
		   //Log.v(TAG, "正在滚动：SCROLL_STATE_TOUCH_SCROLL");
		   break;
	    }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

}
