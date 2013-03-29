package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.db.LocalAccount;

public class TopicListAdapter extends CacheAdapter<String> {   
	
    private List<String> topicList;
	public TopicListAdapter(Activity context, LocalAccount account) {
		super(context, account);
		
		topicList = new ArrayList<String>();
	}
	
	@Override
	public int getCount() {
		return topicList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= topicList.size()) {
			return null;
		}
		return topicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TopicHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_topic, null);
			holder = new TopicHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (TopicHolder)convertView.getTag();
		}
		
		String topic = (String)getItem(position);
		holder.tvTopic.setText(topic);
		
		return convertView;
	}
	
	@Override
	public boolean addCacheToFirst(List<String> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(String value, List<String> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		topicList.addAll(list);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<String> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		topicList.addAll(list);
		this.notifyDataSetChanged();

		return true;
	}
	
	@Override
	public boolean remove(String t) {
		if (t == null) {
			return false;
		}
		boolean isSuccess = topicList.remove(t);
		if (isSuccess) {
			this.notifyDataSetChanged();
		}
		return isSuccess;
	}
	
	@Override
	public String getMax() {
		return null;
	}

	@Override
	public String getMin() {
		return null;
	}

	@Override
	public void clear() {		
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}	
}
