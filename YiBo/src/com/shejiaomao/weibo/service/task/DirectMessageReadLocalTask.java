package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.adapter.DirectMessageUtil;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.cache.DirectMessageCache;
import com.shejiaomao.weibo.service.cache.wrap.DirectMessageWrap;

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
		Weibo microBlog = GlobalVars.getMicroBlog(account);
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
