package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;

import com.cattong.commons.Paging;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.cache.ListCache;

public class InitAdapterTask extends AsyncTask<Void, Void, Void> {
	private ListCache<?, ?> cache;
	private CacheAdapter<?> adapter;
	private Object max;
	//private boolean isRefreshOnFirstEnter;
	public InitAdapterTask(ListCache<?, ?> cache, CacheAdapter<?> adapter) {
		this.cache = cache;
		this.adapter = adapter;
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
