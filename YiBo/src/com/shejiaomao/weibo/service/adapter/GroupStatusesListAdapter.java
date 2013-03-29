package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.entity.Group;
import com.cattong.entity.Status;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.task.GroupStatusesPageDownTask;
import com.shejiaomao.weibo.service.task.GroupStatusesPageUpTask;

public class GroupStatusesListAdapter extends CacheAdapter<Status> {	
	private Group group;
    private List<Status> statusList;
    
	public GroupStatusesListAdapter(Context context, LocalAccount account, Group group) {
		super(context, account);
		this.group = group;
		statusList = new ArrayList<Status>();
		
		GroupStatusesPageUpTask task = new GroupStatusesPageUpTask(this);
        task.execute();
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
		Object obj = getItem(position);
		Status status = (Status) obj;
		if (status == null) {
			return convertView;
		}

		if (status instanceof LocalStatus
			&& ((LocalStatus)status).isDivider()) {
			LocalStatus localStatus = (LocalStatus)status;
			convertView = fillInDividerView(convertView, localStatus, position);
		} else {
		    convertView = StatusUtil.initConvertView(context, convertView, account.getServiceProvider());
		    StatusUtil.fillConvertView(convertView, status);
		}

		return convertView;
	}

	private View fillInDividerView(View convertView, final LocalStatus status, final int position) {
        if (status == null || !status.isDivider()) {
        	return null;
        }

        if (getItemViewType(position) == ITEM_VIEW_TYPE_REMOTE_DIVIDER) {
        	if (convertView == null) {
        	    convertView = inflater.inflate(R.layout.list_item_gap, null);
        	    ThemeUtil.setListViewGap(convertView);
        	}
        	View llLoadingState = convertView.findViewById(R.id.llLoadingState);
			if (status.isLoading()) {
				llLoadingState.setVisibility(View.VISIBLE);
			} else {
				llLoadingState.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					v.setClickable(false);
					v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);

					Status max = (Status)getItem(position - 1);
					Status since = (Status)getItem(position + 1);
					GroupStatusesPageDownTask task = new GroupStatusesPageDownTask(
						GroupStatusesListAdapter.this, status);
                    task.execute(max, since);
				}
			});
        } else {
        	if (convertView == null) {
        	    convertView = inflater.inflate(R.layout.list_item_more, null);
        	    ThemeUtil.setListViewMore(convertView);
        	}
        	if (status.isLoading()) {
        		convertView.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
        		convertView.findViewById(R.id.tvFooter).setVisibility(View.GONE);
        	} else {
        		convertView.findViewById(R.id.llLoadingState).setVisibility(View.GONE);
        		convertView.findViewById(R.id.tvFooter).setVisibility(View.VISIBLE);
        	}
			if (paging.hasNext()) {
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						v.setClickable(false);

	                    v.findViewById(R.id.llLoadingState).setVisibility(View.VISIBLE);
	                    v.findViewById(R.id.tvFooter).setVisibility(View.GONE);

	                    Status max = (Status)getItem(position - 1);
						Status since = (Status)getItem(position + 1);
						GroupStatusesPageDownTask task = new GroupStatusesPageDownTask(
							GroupStatusesListAdapter.this, status);
	                    task.execute(max, since);
					}
				});
			} else {
				((TextView)convertView.findViewById(R.id.tvFooter)).setText(R.string.label_no_more);
			}
        }

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
	public boolean addCacheToDivider(Status value, List<Status> list) {
		if (value == null || ListUtil.isEmpty(list)) {
			return false;
		}
		
		int pos = statusList.indexOf(value);
		if (pos == -1) {
			return false;
		}
		
		statusList.remove(pos);
		statusList.addAll(pos, list);
		this.notifyDataSetChanged();
		
		return false;
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
		if (statusList.size() == 0) {
			return null;
		}
		return statusList.get(0);
	}

	@Override
	public Status getMin() {
		if (statusList.size() == 0) {
			return null;
		}
		return statusList.get(getCount() - 1);
	}

	@Override
	public void clear() {
		statusList.clear();		
	}

	@Override
	public int getItemViewType(int position) {
		Status status = (Status)getItem(position);
		if (status == null) {
			return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
		}
		if (!(status instanceof LocalStatus)) {
			return ITEM_VIEW_TYPE_DATA;
		}
		
		LocalStatus localStatus = (LocalStatus)status;
		if (!localStatus.isDivider()) {
			return ITEM_VIEW_TYPE_DATA;
		}
		
		if (localStatus.isLocalDivider()) {
			return ITEM_VIEW_TYPE_LOCAL_DIVIDER;
		}
		return ITEM_VIEW_TYPE_REMOTE_DIVIDER;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		if (group == null || group.equals(this.group)) {
			return;
		}
		this.group = group;
		clear();
		
		this.notifyDataSetChanged();
	}
}
