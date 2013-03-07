package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.SocialGraphTask;

public class SocialGraphListAdapter extends CacheAdapter<User> {
	private int socialGraphType = SocialGraphTask.TYPE_FOLLOWERS;

    private List<User> listUserInfo = null;
    public SocialGraphListAdapter(Activity context, LocalAccount account, int socialGraphType) {
    	super(context, account);
    	this.context = context;
    	this.socialGraphType = socialGraphType;
    	listUserInfo = new ArrayList<User>();
    }

	@Override
	public int getCount() {
		return listUserInfo.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= listUserInfo.size()) {
			return null;
		}

		return listUserInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
        convertView = UserUtil.initConvertView(context, convertView);

		User user = (User) obj;
		UserUtil.fillConvertView(convertView, user);

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<User> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(User value, List<User> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		listUserInfo.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<User> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		listUserInfo.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}
	@Override
	public User getMax() {
		User max = null;
		if (listUserInfo != null && listUserInfo.size() > 0) {
			max = listUserInfo.get(0);
		}
		return max;
	}

	@Override
	public User getMin() {
		User min = null;
		if (listUserInfo != null && listUserInfo.size() > 0) {
			min = listUserInfo.get(0);
		}
		return min;
	}

	@Override
	public void clear() {
		listUserInfo.clear();
	}

	public int getSocialGraphType() {
		return socialGraphType;
	}

	public void setSocialGraphType(int socialGraphType) {
		this.socialGraphType = socialGraphType;
	}
}
