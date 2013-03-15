package com.shejiaomao.common;

import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;

import com.shejiaomao.weibo.service.BaseListAdapter;

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
	
	public static BaseListAdapter<?> getBaseListAdapter(Adapter adapter) {
		if (adapter == null) {
			return null;
		}
		
		BaseListAdapter<?> baseListAdapter = null;
		if (adapter instanceof BaseListAdapter<?>) {
	        baseListAdapter = (BaseListAdapter<?>)adapter;
		} else if (adapter instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerViewAdapter = (HeaderViewListAdapter)adapter;
			if (headerViewAdapter.getWrappedAdapter() instanceof BaseListAdapter<?>) {
				baseListAdapter = (BaseListAdapter<?>)headerViewAdapter.getWrappedAdapter();
			}
		}
		
		return baseListAdapter;
	}
}
