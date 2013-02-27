package net.dev123.yibo.service.listener;

import net.dev123.yibo.YiBoApplication;
import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class AutoLoadMoreListener implements OnScrollListener {
    private static final String TAG = "AutoLoadMoreListener";

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
		    //Log.v(TAG, "已经停止：SCROLL_STATE_IDLE" + "-->" + view.getCount());
		    Context context = view.getContext();
		    YiBoApplication yibo = (YiBoApplication) context.getApplicationContext();
		    if (view.getLastVisiblePosition() == view.getCount() - 1
		    	&& yibo.isAutoLoadMore()) {
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
