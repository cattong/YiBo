package com.shejiaomao.weibo.service.adapter;

import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;

public class AdapterUtil {

	public static BaseAdapter getAdapter(Adapter adapter) {
		if (adapter == null) {
			return null;
		}
		BaseAdapter baseAdapter = null;
		if (adapter instanceof BaseAdapter) {
			baseAdapter = (BaseAdapter)adapter;
		} else if (adapter instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerViewAdapter = (HeaderViewListAdapter)adapter;
			if (headerViewAdapter.getWrappedAdapter() instanceof BaseAdapter) {
				baseAdapter = (BaseAdapter)headerViewAdapter.getWrappedAdapter();
			}
		}
		
		return baseAdapter;
	}
	
	public static CacheAdapter<?> getCacheAdapter(Adapter adapter) {
		if (adapter == null) {
			return null;
		}
		CacheAdapter<?> cacheAdapter = null;
		if (adapter instanceof CacheAdapter<?>) {
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
