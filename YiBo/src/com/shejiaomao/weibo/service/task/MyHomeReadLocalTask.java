package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.cache.MyHomeCache;
import com.shejiaomao.weibo.service.cache.wrap.StatusWrap;

public class MyHomeReadLocalTask extends AsyncTask<com.cattong.entity.Status, Void, Void> {
	private MyHomeListAdapter adapter;
	private MyHomeCache cache;
	
	private Paging<com.cattong.entity.Status> paging;
	private LocalStatus divider;
	List<StatusWrap> listWrap = null;
	List<com.cattong.entity.Status> listStatus = null;
	public MyHomeReadLocalTask(MyHomeListAdapter adapter, MyHomeCache cache, LocalStatus divider) {
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
	protected Void doInBackground(com.cattong.entity.Status... params) {
		if (params == null 
			|| params.length != 2 
			|| !paging.hasNext()) {
			return null;
		}
		
		com.cattong.entity.Status max = params[0];
		com.cattong.entity.Status since = params[1];
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);
		
		if (paging.moveToNext()) {
		    listWrap = cache.read(paging);
		}
        
		if (ListUtil.isEmpty(listWrap)) {
			listStatus = getDataFromRemote(max, since);
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		divider.setLoading(false);
		if (ListUtil.isEmpty(listWrap) && ListUtil.isEmpty(listStatus)) {
			paging.setLastPage(true);
			adapter.notifyDataSetChanged();
			return;
		}
		
        if (ListUtil.isNotEmpty(listWrap)) {
        	cache.remove(cache.size() - 1);
        	cache.addAll(cache.size(), listWrap);
        	adapter.notifyDataSetChanged();
        } else if (ListUtil.isNotEmpty(listStatus)) {
        	adapter.addCacheToDivider(divider, listStatus);
        }
		
	}

	private List<com.cattong.entity.Status> getDataFromRemote(com.cattong.entity.Status max, com.cattong.entity.Status since) {
		LocalAccount account = adapter.getAccount();
		Weibo microBlog = GlobalVars.getMicroBlog(account);
		List<com.cattong.entity.Status> listStatus = null;
		if (microBlog == null) {
			return listStatus;
		}
		
		Paging<com.cattong.entity.Status> paging = new Paging<com.cattong.entity.Status>();
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);
		
		if (paging.moveToNext()) {
		    try {
			    listStatus = microBlog.getHomeTimeline(paging);
		    } catch (LibException e) {
			    //resultMsg = e.getDescription();
			    paging.moveToPrevious();
		    }
		}
		ResponseCountUtil.getResponseCounts(listStatus, microBlog);

		boolean isSuccess = ListUtil.isNotEmpty(listStatus);
		if (isSuccess && paging.hasNext()) {
			LocalStatus localStatus = StatusUtil.createDividerStatus(listStatus, account);
			listStatus.add(localStatus);
		}
		
		return listStatus;
	}
}
