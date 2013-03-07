package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;

public class SocialGraphItemClickListener implements OnItemClickListener {
	private Activity context;
	public SocialGraphItemClickListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseAdapter adapter = AdapterUtil.getAdapter(parent.getAdapter());
		if (adapter == null || position >= adapter.getCount()) {
			return;
		}
		
		User user = (User)adapter.getItem(position);
		if (user == null) {
            return;
        }

		Intent intent = new Intent();
		intent.putExtra("USER", user);
		intent.setClass(parent.getContext(), ProfileActivity.class);
		context.startActivity(intent);
	}

}
