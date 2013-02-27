package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.adapter.MentionsListAdapter;
import net.dev123.yibo.service.task.MetionsPageUpTask;
import android.view.View;
import android.view.View.OnClickListener;

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
