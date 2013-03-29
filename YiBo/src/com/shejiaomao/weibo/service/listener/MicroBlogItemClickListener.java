package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cattong.entity.Status;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;

public class MicroBlogItemClickListener implements OnItemClickListener {
	private Context context;
	
	public MicroBlogItemClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
        Status status = (Status)adapter.getItem(position);
		if (status == null 
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putSerializable("STATUS", status);
		CacheAdapter<?> cacheAdapter = AdapterUtil.getCacheAdapter(adapter);
		if (cacheAdapter instanceof MyHomeListAdapter) {
			bundle.putInt("SOURCE", Constants.REQUEST_CODE_MY_HOME);
			bundle.putInt("POSITION", position - 1);
		}
		intent.putExtras(bundle);

		intent.setClass(parent.getContext(), MicroBlogActivity.class);
		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_MICRO_BLOG);
		CompatibilityUtil.overridePendingTransition(
			(Activity)context, R.anim.slide_in_right, android.R.anim.fade_out
		);
	}


}
