package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.CommentsListAdapter;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import net.dev123.yibo.service.adapter.MentionsListAdapter;
import net.dev123.yibo.service.cache.ListCache;
import android.os.AsyncTask;

public class InitAdapterTask extends AsyncTask<Void, Void, Void> {
	private ListCache<?, ?> cache;
	private CacheAdapter<?> adapter;
	private Object max;
	//private boolean isRefreshOnFirstEnter;
	public InitAdapterTask(ListCache<?, ?> cache, CacheAdapter<?> adapter) {
		this.cache = cache;
		this.adapter = adapter;
		//Context context = adapter.getContext();
		//YiBoApplication yibo = (YiBoApplication)context.getApplicationContext();
		//isRefreshOnFirstEnter = yibo.isRefreshOnFirstEnter();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		initAdapter();
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (max == null) {
			if (adapter instanceof MentionsListAdapter) {
			    MetionsPageUpTask task = new MetionsPageUpTask((MentionsListAdapter)adapter);
			    task.execute();	
			} else if (adapter instanceof CommentsListAdapter) {
				CommentsPageUpTask task = new CommentsPageUpTask((CommentsListAdapter)adapter);
				task.execute();
			} else if (adapter instanceof DirectMessagesListAdapter){
				DirectMessagePageUpTask task = new DirectMessagePageUpTask((DirectMessagesListAdapter)adapter);
				task.execute();
			}
		} else {
			if (adapter instanceof MentionsListAdapter) {
			    MetionsPageUpTask task = new MetionsPageUpTask((MentionsListAdapter)adapter);
			    task.setAutoUpdate(true);
			    task.execute();	
			} else if (adapter instanceof CommentsListAdapter) {
				CommentsPageUpTask task = new CommentsPageUpTask((CommentsListAdapter)adapter);
				task.setAutoUpdate(true);
				task.execute();
			} else if (adapter instanceof DirectMessagesListAdapter){
				DirectMessagePageUpTask task = new DirectMessagePageUpTask((DirectMessagesListAdapter)adapter);
				task.setAutoUpdate(true);
				task.execute();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void initAdapter() {
		Paging localPaging = adapter.getPaging();
		 
		localPaging.moveToNext();
		List list = cache.read(localPaging);
		cache.addAll(list);
		max = adapter.getMax();
		
		if (max != null) {
			String maxId = null;
			localPaging.setGlobalMax(max);
		} 
	}
}
