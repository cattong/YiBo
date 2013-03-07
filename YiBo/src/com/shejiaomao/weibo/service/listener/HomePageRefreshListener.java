package com.shejiaomao.weibo.service.listener;

import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;

import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.adapter.GroupStatusesListAdapter;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;
import com.shejiaomao.weibo.service.task.CommentsPageUpTask;
import com.shejiaomao.weibo.service.task.DirectMessagePageUpTask;
import com.shejiaomao.weibo.service.task.GroupStatusesPageUpTask;
import com.shejiaomao.weibo.service.task.MetionsPageUpTask;
import com.shejiaomao.weibo.service.task.MyHomePageUpTask;
import com.shejiaomao.widget.PullToRefreshListView;
import com.shejiaomao.widget.PullToRefreshListView.OnRefreshListener;

public class HomePageRefreshListener implements OnRefreshListener {

	@Override
    public void onRefresh(PullToRefreshListView listView) {
		if (listView == null) {
			return;
		}
		Adapter adapter = listView.getAdapter();
		if (adapter == null) {
			return;
		}

        if (adapter instanceof HeaderViewListAdapter) {
        	adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        }
        CacheAdapter<?> cacheAdapter = (CacheAdapter<?>)adapter;
        if (cacheAdapter instanceof MyHomeListAdapter) {
        	MyHomePageUpTask task = new MyHomePageUpTask((MyHomeListAdapter)cacheAdapter);
        	task.setListView(listView);
        	task.execute();
        } else if (cacheAdapter instanceof GroupStatusesListAdapter) {
        	GroupStatusesPageUpTask task = 
        		new GroupStatusesPageUpTask((GroupStatusesListAdapter)cacheAdapter);
        	task.setListView(listView);
        	task.execute();
        } else if (cacheAdapter instanceof MentionsListAdapter) {
        	MetionsPageUpTask task = new MetionsPageUpTask((MentionsListAdapter)cacheAdapter);
        	task.setListView(listView);
        	task.execute();
        } else if (cacheAdapter instanceof CommentsListAdapter) {
        	CommentsPageUpTask task = new CommentsPageUpTask((CommentsListAdapter)cacheAdapter);
        	task.setListView(listView);
        	task.execute();
        } else if (cacheAdapter instanceof DirectMessagesListAdapter) {
        	DirectMessagePageUpTask task =
        	    new DirectMessagePageUpTask((DirectMessagesListAdapter)cacheAdapter);
        	task.setListView(listView);
            task.execute();
        }

	}

}
