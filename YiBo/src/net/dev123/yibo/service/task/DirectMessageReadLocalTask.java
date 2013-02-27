package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalDirectMessage;
import net.dev123.yibo.service.adapter.DirectMessageUtil;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import net.dev123.yibo.service.cache.DirectMessageCache;
import net.dev123.yibo.service.cache.wrap.DirectMessageWrap;
import android.os.AsyncTask;

public class DirectMessageReadLocalTask extends AsyncTask<DirectMessage, Void, Void> {
	private DirectMessagesListAdapter adapter;
	private DirectMessageCache cache;
	
	private Paging<DirectMessage> paging;
	private LocalDirectMessage divider;
	List<DirectMessageWrap> listWrap = null;
	List<DirectMessage> listMessage = null;
	public DirectMessageReadLocalTask(DirectMessagesListAdapter adapter, 
			DirectMessageCache cache, LocalDirectMessage divider) {
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
		    || !paging.hasNext()
		) {
			return null;
		}
		
		DirectMessage inboxMax = params[0];
		DirectMessage outboxMax = params[1];	
		
		if (paging.moveToNext()) {
		    listWrap = cache.read(paging);
		}
        
		if (ListUtil.isEmpty(listWrap)) {
			listMessage = getDataFromRemote(inboxMax, outboxMax);
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		divider.setLoading(false);
		if (ListUtil.isEmpty(listWrap) && ListUtil.isEmpty(listMessage)) {
			paging.setLastPage(true);
			adapter.notifyDataSetChanged();
			return;
		}
		
        if (ListUtil.isNotEmpty(listWrap)) {
        	cache.remove(cache.size() - 1);
        	cache.addAll(cache.size(), listWrap);
        	adapter.notifyDataSetChanged();
        } else if (ListUtil.isNotEmpty(listMessage)) {
        	adapter.addCacheToDivider(divider, listMessage);
        }
		
	}

	private List<DirectMessage> getDataFromRemote(DirectMessage inboxMax, DirectMessage outboxMax) {
		LocalAccount account = adapter.getAccount();
		MicroBlog microBlog = GlobalVars.getMicroBlog(account);
		List<DirectMessage> messageList = new ArrayList<DirectMessage>();
		if (microBlog == null) {
			return messageList;
		}
		
	    try {
	    	Paging<DirectMessage> paging = new Paging<DirectMessage>();
	    	paging.setGlobalMax(inboxMax);
	    	paging.moveToNext();
			List<DirectMessage> tempList = microBlog.getInboxDirectMessages(paging);
            if (ListUtil.isNotEmpty(tempList)) {
            	messageList.addAll(tempList);
            }
            
            paging = new Paging<DirectMessage>();
            paging.moveToNext();
            paging.setGlobalMax(outboxMax);
            tempList = microBlog.getOutboxDirectMessages(paging);
            if (ListUtil.isNotEmpty(tempList)) {
            	messageList.addAll(tempList);
            }
	    } catch (LibException e) {
		    //resultMsg = e.getDescription();
		    paging.moveToPrevious();
	    }

		if (ListUtil.isNotEmpty(messageList)) {
			LocalDirectMessage localMessage = 
				DirectMessageUtil.createDividerDirectMessage(messageList, account);
			messageList.add(localMessage);
		}
		
		return messageList;
	}
}
