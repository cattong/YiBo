package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.entity.Group;

import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.GroupActivity;
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;

public class GroupListAdapter extends CacheAdapter<Group> {   
	private static final int ITEM_VIEW_TYPE_ADD = 0;
	private static final int ITEM_VIEW_TYPE_DATA = 1;
	
    private List<Group> groupList;
	public GroupListAdapter(GroupActivity context, LocalAccount account) {
		super(context, account);
		
		groupList = new ArrayList<Group>();
		groupList.add(null);//作为首行;
	}
	
	@Override
	public int getCount() {
		return groupList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= groupList.size()) {
			return null;
		}
		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (getItemViewType(position) == ITEM_VIEW_TYPE_ADD) {
			if (convertView == null) {
		        convertView = inflater.inflate(R.layout.list_item_group_add, null);
		        holder = new GroupHolder(convertView);
		        convertView.setTag(holder);
			}
		} else if (getItemViewType(position) == ITEM_VIEW_TYPE_DATA) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item_group, null);
				holder = new GroupHolder(convertView);
		        convertView.setTag(holder);
			} else {
				holder = (GroupHolder)convertView.getTag();
			}
			Group group = (Group)getItem(position);
			holder.tvGroupName.setText(group.getName());
		}
		
		return convertView;
	}
	
	@Override
	public boolean addCacheToFirst(List<Group> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(Group value, List<Group> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		groupList.addAll(list);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<Group> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		groupList.addAll(list);
		this.notifyDataSetChanged();

		return true;
	}
	
	@Override
	public boolean remove(Group t) {
		if (t == null) {
			return false;
		}
		boolean isSuccess = groupList.remove(t);
		if (isSuccess) {
			GroupDao dao = new GroupDao(context);
			dao.delete(account, t);
			this.notifyDataSetChanged();
		}
		return isSuccess;
	}
	
	@Override
	public Group getMax() {
		return null;
	}

	@Override
	public Group getMin() {
		return null;
	}

	@Override
	public void clear() {		
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return ITEM_VIEW_TYPE_ADD;
		} else {
			return ITEM_VIEW_TYPE_DATA;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}	
}
