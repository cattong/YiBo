package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.MyHomeListAdapter;
import net.dev123.yibo.service.task.MyHomePageUpTask;
import android.view.View;
import android.view.View.OnClickListener;

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
