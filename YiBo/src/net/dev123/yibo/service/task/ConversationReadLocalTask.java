package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.yibo.db.LocalDirectMessage;
import net.dev123.yibo.service.adapter.ConversationListAdapter;
import net.dev123.yibo.service.cache.ConversationCache;
import net.dev123.yibo.service.cache.wrap.DirectMessageWrap;
import android.os.AsyncTask;

public class ConversationReadLocalTask extends AsyncTask<DirectMessage, Void, Void> {
	private ConversationListAdapter adapter;
	private ConversationCache cache;
	
	private Paging<DirectMessage> paging;
	private LocalDirectMessage divider;
	List<DirectMessageWrap> listWrap = null;
	public ConversationReadLocalTask(ConversationListAdapter adapter, 
		ConversationCache cache, LocalDirectMessage divider) {
	    this.cache = cache;
	    this.adapter = adapter;
	    this.divider = divider;
	    paging = adapter.getPaging();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		divider.setLoading(true);
	}

	@Override
	protected Void doInBackground(DirectMessage... params) {
		if (params == null 
			|| params.length != 2 
		    || !paging.hasNext()) {
			return null;
		}
		
		DirectMessage max = params[0];
		DirectMessage since = params[1];	
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);
		
		if (paging.moveToNext()) {
		    listWrap = cache.read(paging);
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		divider.setLoading(false);
        if (ListUtil.isNotEmpty(listWrap)) {
        	cache.remove(cache.size() - 1);
        	cache.addAll(cache.size(), listWrap);
        }
		if (ListUtil.isEmpty(listWrap) 
			|| listWrap.size() < paging.getPageSize() / 2) {
			paging.setLastPage(true);	
		}
        adapter.notifyDataSetChanged();
	}
}
