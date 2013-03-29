package com.shejiaomao.weibo.service.adapter;

import com.cattong.entity.Status;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.PublicTimelineActivity;
import com.shejiaomao.weibo.db.LocalAccount;

public class PublicTimelineListAdapter extends ArrayAdapter<Status> {
	private LocalAccount account = null;
	private PublicTimelineActivity context;

	public PublicTimelineListAdapter(PublicTimelineActivity context, LocalAccount account) {
		super(context, R.layout.list_item_status);
		this.account = account;
		this.context = context;
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

	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		this.account = account;
	}

}
