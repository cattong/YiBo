package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.CommentsListAdapter;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import net.dev123.yibo.service.adapter.GroupStatusesListAdapter;
import net.dev123.yibo.service.adapter.MentionsListAdapter;
import net.dev123.yibo.service.adapter.MyHomeListAdapter;
import net.dev123.yibo.service.task.CommentsPageUpTask;
import net.dev123.yibo.service.task.DirectMessagePageUpTask;
import net.dev123.yibo.service.task.GroupStatusesPageUpTask;
import net.dev123.yibo.service.task.MetionsPageUpTask;
import net.dev123.yibo.service.task.MyHomePageUpTask;
import net.dev123.yibo.widget.PullToRefreshListView;
import net.dev123.yibo.widget.PullToRefreshListView.OnRefreshListener;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;

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
