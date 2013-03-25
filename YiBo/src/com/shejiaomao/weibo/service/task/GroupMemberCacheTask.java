package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalGroup;
import com.shejiaomao.weibo.db.UserGroupDao;

public class GroupMemberCacheTask extends AsyncTask<Void, List<? extends BaseUser>, Integer> {
	private static final String TAG = "GroupMemberCacheTask";

    private Weibo microBlog;
    private LocalAccount account;
    private LocalGroup group;
    private UserGroupDao dao;

	private int cacheCount;

	private int cycleTime = 20;
	private int pageSize = 100;
	public GroupMemberCacheTask(Context context, LocalAccount account, LocalGroup group) {
		this.account = account;
		this.group = group;
		this.microBlog = GlobalVars.getMicroBlog(account);
		this.dao = new UserGroupDao(context);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		cacheCount = 0;
		if (microBlog == null) {
			return cacheCount;
		}

		List<? extends BaseUser> userList = null;
		Paging<User> paging = new Paging<User>();
		paging.setPageSize(pageSize);

		while (paging.moveToNext() && cycleTime-- > 0) {
			try {
				userList = microBlog.getGroupMembers(group.getSpGroupId(), paging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
			}
			if (ListUtil.isNotEmpty(userList)) {
				cacheCount += userList.size();
				this.publishProgress(userList);
			}
		}

		return cacheCount;
	}

	@Override
	protected void onProgressUpdate(List<? extends BaseUser>... values) {
		super.onProgressUpdate(values);
		if (values == null || values.length != 1) {
			return;
		}

		List<? extends BaseUser> userList = values[0];
		if (ListUtil.isNotEmpty(userList)) {
			dao.batchSave(account, group, userList);
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (cacheCount == group.getMemberCount()) {
			System.out.println("GroupMemberCache is success!");
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
