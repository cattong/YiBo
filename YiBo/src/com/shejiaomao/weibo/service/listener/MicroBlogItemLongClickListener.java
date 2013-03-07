package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemLongClickListener;

import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;

public class MicroBlogItemLongClickListener implements OnItemLongClickListener {
    private Context context;
    
	public MicroBlogItemLongClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
        BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
        Status status = (Status)adapter.getItem(position);
		if (status == null 
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return false;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);

		intent.setClass(parent.getContext(), MicroBlogActivity.class);

		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_MY_HOME);
		
		return true;
	}

}
