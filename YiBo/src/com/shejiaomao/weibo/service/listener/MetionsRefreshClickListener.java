package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.task.MetionsPageUpTask;

public class MetionsRefreshClickListener implements OnClickListener {

	private MentionsListAdapter adapter;
	public MetionsRefreshClickListener(MentionsListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		
		MetionsPageUpTask task = new MetionsPageUpTask(adapter);
        task.execute();
	}

	public MentionsListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(MentionsListAdapter adapter) {
		this.adapter = adapter;
	}

}
