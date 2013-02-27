package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.CommentsListAdapter;
import net.dev123.yibo.service.task.CommentsPageUpTask;
import android.view.View;
import android.view.View.OnClickListener;

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
