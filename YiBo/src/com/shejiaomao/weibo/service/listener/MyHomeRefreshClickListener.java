package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;
import com.shejiaomao.weibo.service.task.MyHomePageUpTask;

public class MyHomeRefreshClickListener implements OnClickListener {

	private MyHomeListAdapter adapter;
	public MyHomeRefreshClickListener(MyHomeListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		
        MyHomePageUpTask task = new MyHomePageUpTask(adapter);
        task.execute();
	}

	public MyHomeListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(MyHomeListAdapter adapter) {
		this.adapter = adapter;
	}

}
