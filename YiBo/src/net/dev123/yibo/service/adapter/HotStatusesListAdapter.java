package net.dev123.yibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.mblog.entity.Status;
import net.dev123.yibo.db.LocalAccount;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class HotStatusesListAdapter extends CacheAdapter<Status> {
    private Activity context;
    private List<Status> listStatus = new ArrayList<Status>();

	public HotStatusesListAdapter(Activity context, LocalAccount account) {
		super(context, account);
		this.context = context;
		this.account = account;
	}

	@Override
	public int getCount() {
		return listStatus.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= listStatus.size()) {
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
		
		convertView = StatusUtil.initConvertView(context, convertView, account.getServiceProvider());
	    StatusUtil.fillConvertView(convertView, status);

		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		listStatus.addAll(0, list);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToDivider(Status status, List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		listStatus.addAll(list);

		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean addCacheToLast(List<Status> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		listStatus.addAll(list);

		this.notifyDataSetChanged();
		return true;
	}
	
	@Override
	public Status getMax() {
		Status max = null;
		if (ListUtil.isNotEmpty(listStatus)) {
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
	public boolean remove(int position) {
		if (position < 0 || position >= getCount()) {
			return false;
		}
		listStatus.remove(position);
		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean remove(Status status) {
		if (status == null) {
			return false;
		}
		listStatus.remove(status);
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public void clear() {
		listStatus.clear();
	}
}
