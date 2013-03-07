package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Status;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.weibo.db.LocalAccount;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-24 下午12:19:27
 **/
public class UserTopicStatusListAdapter extends CacheAdapter<Status> {
	private Activity context;
    private List<Status> statusList = new ArrayList<Status>();
    private String keyword;

	public UserTopicStatusListAdapter(Activity context, LocalAccount account, String keyword) {
		super(context, account);
		this.context = context;
		this.account = account;
		this.keyword = keyword;
	}

	@Override
	public int getCount() {
		return statusList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= statusList.size()) {
			return null;
		}

		return statusList.get(position);
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
		
		convertView = StatusUtil.initConvertView(context, convertView, account.getServiceProvider());
	    StatusUtil.fillConvertView(convertView, status);
	    StatusHolder holder = (StatusHolder)convertView.getTag();
	    String text = holder.tvText.getText().toString();
	    holder.tvText.setText(Html.fromHtml(text.replaceAll(
	    	keyword, "<font color=\"#ff0000\">" + keyword + "</font>")));
		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		statusList.addAll(0, list);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Status status, List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		statusList.addAll(list);

		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean addCacheToLast(List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		statusList.addAll(list);

		this.notifyDataSetChanged();
		return true;
	}
	
	@Override
	public Status getMax() {
		Status max = null;
		if (ListUtil.isNotEmpty(statusList)) {
			max = statusList.get(0);
		}

		return max;
	}

	@Override
	public Status getMin() {
		Status min = null;
		if (statusList != null && statusList.size() > 0) {
			min = statusList.get(statusList.size() - 1);
		}
		return min;
	}

	@Override
	public boolean remove(int position) {
		if (position < 0 || position >= getCount()) {
			return false;
		}
		statusList.remove(position);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean remove(Status status) {
		if (status == null) {
			return false;
		}
		statusList.remove(status);
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public void clear() {
		statusList.clear();
	}
}
