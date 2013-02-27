package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.BaseUser;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.UserGroupDao;
import net.dev123.yibome.entity.LocalGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GroupMemberCacheTask extends AsyncTask<Void, List<? extends BaseUser>, Integer> {
	private static final String TAG = "GroupMemberCacheTask";

    private MicroBlog microBlog;
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
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
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
