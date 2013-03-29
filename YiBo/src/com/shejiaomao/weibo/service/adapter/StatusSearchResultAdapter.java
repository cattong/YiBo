package com.shejiaomao.weibo.service.adapter;

import com.cattong.entity.Status;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.SearchActivity;

public class StatusSearchResultAdapter extends ArrayAdapter<Status> {
	private SearchActivity context;

	public StatusSearchResultAdapter(SearchActivity context) {
		super(context, R.layout.list_item_status);
		this.context = context;
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
}
