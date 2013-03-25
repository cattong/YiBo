package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;

public class GroupCacheTask extends AsyncTask<Void, List<Group>, Integer> {
	private static final String TAG = "GroupCacheTask";
	
    private Weibo microBlog;
    private LocalAccount account;
    private GroupDao dao;

	private int cacheCount;
	
	private int cycleTime = 2;
	private int pageSize = 100;

	public GroupCacheTask(Context context, LocalAccount account) {
		this.account = account;
		this.microBlog = GlobalVars.getMicroBlog(account);
		this.dao = new GroupDao(context);
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		cacheCount = 0;
		if (microBlog == null) {
			return cacheCount;
		}
		
		List<Group> groupList = new ArrayList<Group>();
		Paging<Group> paging = new Paging<Group>();
		paging.setPageSize(pageSize);
		
		String userId = account.getUser().getUserId();
		List<Group> tempList = null;
		while (paging.moveToNext() && cycleTime-- > 0) {
			try {
				tempList = microBlog.getGroups(userId, paging);
				if (ListUtil.isNotEmpty(tempList)) {
					groupList.addAll(tempList);
				}
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
			}
		}

		if (ListUtil.isNotEmpty(groupList)) {
		    dao.merge(account, groupList);
		}
		
		return cacheCount;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);		
		
		if (Logger.isDebug()) {
			Log.d(TAG, "cache group count: " + cacheCount);
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(int cycleTime) {
		this.cycleTime = cycleTime;
	}
	
}
