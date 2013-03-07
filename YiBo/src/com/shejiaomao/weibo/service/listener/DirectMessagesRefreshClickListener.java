package com.shejiaomao.weibo.service.listener;

import com.cattong.weibo.entity.DirectMessage;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.task.DirectMessagePageUpTask;

public class DirectMessagesRefreshClickListener implements OnClickListener {
	private DirectMessagesListAdapter adapter;
	public DirectMessagesRefreshClickListener(DirectMessagesListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		
		DirectMessagePageUpTask task = new DirectMessagePageUpTask(adapter);
        task.execute();
	}

	public CacheAdapter<DirectMessage> getAdapter() {
		return adapter;
	}

	public void setAdapter(DirectMessagesListAdapter adapter) {
		this.adapter = adapter;
	}


}
