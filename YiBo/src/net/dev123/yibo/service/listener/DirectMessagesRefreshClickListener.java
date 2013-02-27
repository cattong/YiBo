package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.DirectMessage;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import net.dev123.yibo.service.task.DirectMessagePageUpTask;
import android.view.View;
import android.view.View.OnClickListener;

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
