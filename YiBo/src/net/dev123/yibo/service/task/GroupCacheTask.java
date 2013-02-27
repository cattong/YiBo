package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.GroupDao;
import net.dev123.yibo.db.LocalAccount;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GroupCacheTask extends AsyncTask<Void, List<Group>, Integer> {
	private static final String TAG = "GroupCacheTask";
	
    private MicroBlog microBlog;
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
		
		String userId = account.getUser().getId();
		List<Group> tempList = null;
		while (paging.moveToNext() && cycleTime-- > 0) {
			try {
				tempList = microBlog.getGroups(userId, paging);
				if (ListUtil.isNotEmpty(tempList)) {
					groupList.addAll(tempList);
				}
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
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
		
		if (Constants.DEBUG) {
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
