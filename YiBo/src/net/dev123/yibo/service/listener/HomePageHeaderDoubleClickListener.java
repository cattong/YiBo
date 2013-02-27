package net.dev123.yibo.service.listener;

import net.dev123.yibo.R;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.cache.ReclaimLevel;
import android.app.Activity;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HomePageHeaderDoubleClickListener extends OnDoubleClickListener {

	@Override
	public void onDoubleClick(View v) {
        //双击事件
	    Activity context = (Activity)v.getContext();
        ListView lvMicroBlog = (ListView)context.findViewById(R.id.lvMicroBlog);
        if (lvMicroBlog != null) {
      		ListAdapter adapter = lvMicroBlog.getAdapter();
			CacheAdapter<?> cacheAdapter = getCacheAdapter(adapter);
	        if (cacheAdapter != null) {
	            cacheAdapter.reclaim(ReclaimLevel.MODERATE);
	        }
	        if (lvMicroBlog.getChildCount() > 1) {
	        	lvMicroBlog.setSelection(1);
	        }
        }
	}
	
	private CacheAdapter<?> getCacheAdapter(ListAdapter adapter) {
		CacheAdapter<?> cacheAdapter = null;
		if (adapter instanceof CacheAdapter) {
	        cacheAdapter = (CacheAdapter<?>)adapter;
		} else if (adapter instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerViewAdapter = (HeaderViewListAdapter)adapter;
			if (headerViewAdapter.getWrappedAdapter() instanceof CacheAdapter<?>) {
				cacheAdapter = (CacheAdapter<?>)headerViewAdapter.getWrappedAdapter();
			}
		}
		return cacheAdapter;
	}
}
