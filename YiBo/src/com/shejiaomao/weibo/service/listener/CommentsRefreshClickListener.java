package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.service.task.CommentsPageUpTask;

public class CommentsRefreshClickListener implements OnClickListener {

	private CommentsListAdapter adapter;
	public CommentsRefreshClickListener(CommentsListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		
		CommentsPageUpTask task = new CommentsPageUpTask(adapter);
        task.execute();
	}

	public CommentsListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(CommentsListAdapter adapter) {
		this.adapter = adapter;
	}
}
