package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.entity.Status;
import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.weibo.activity.MyFavoritesActivity;
import com.shejiaomao.weibo.db.LocalAccount;

public class MyFavoriteListAdapter extends CacheAdapter<Status> {
	private MyFavoritesActivity context;
	private List<Status> listStatus = new ArrayList<Status>();

	public MyFavoriteListAdapter(MyFavoritesActivity context, LocalAccount account) {
		super(context, account);
		this.context = context;
	}

	@Override
	public int getCount() {
		return listStatus.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position > listStatus.size() - 1) {
			return null;
		}

		return listStatus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Status status = (Status)getItem(position);
		if (status == null) {
			return null;
		}
		
		convertView = StatusUtil.initConvertView(context, convertView, status.getServiceProvider());
	    StatusUtil.fillConvertView(convertView, status);

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		listStatus.addAll(0, list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Status value, List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		listStatus.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<Status> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		listStatus.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}
	
	@Override
	public Status getMax() {
		Status max = null;
		if (listStatus != null && listStatus.size() > 0) {
			max = listStatus.get(0);
		}

		return max;
	}

	@Override
	public Status getMin() {
		Status min = null;
		if (listStatus != null && listStatus.size() > 0) {
			min = listStatus.get(listStatus.size() - 1);
		}

		return min;
	}

	@Override
	public void clear() {
		listStatus.clear();
	}

}
