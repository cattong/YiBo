package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.Relation;
import net.dev123.yibo.db.SocialGraphDao;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SocialGraphCacheTask extends AsyncTask<Void, List<User>, Integer> {
	private static final String TAG = "SocialGraphCacheTask";

    private MicroBlog microBlog;
    private SocialGraphDao dao;
    private User user;
	private Relation relation;
	private int cacheCount;

	private int cycleTime = 20;
	private int pageSize = 50;
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

	public SocialGraphCacheTask(Context context, LocalAccount account, Relation relation) {
		this.relation = relation;
		this.user = (User) account.getUser();
		this.microBlog = GlobalVars.getMicroBlog(account);
		this.dao = new SocialGraphDao(context);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		cacheCount = 0;
		if (microBlog == null) {
			return cacheCount;
		}

		List<User> listUser = null;
		Paging<User> paging = new Paging<User>();
		paging.setPageSize(pageSize);

		while (paging.moveToNext() && cycleTime-- > 0) {
			try {
				if (relation == Relation.Followingship) {
					listUser = microBlog.getFriends(paging);
				} else {
				    listUser = microBlog.getFollowers(paging);
				}
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
			}
			if (listUser != null && listUser.size() > 0) {
				cacheCount += listUser.size();
				this.publishProgress(listUser);
			}
		}


		return cacheCount;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		int expectedCount = 0;
		if (relation == Relation.Followingship) {
			expectedCount = user.getFriendsCount();
		} else {
			expectedCount = user.getFollowersCount();
		}

		if (cacheCount == expectedCount) {
			System.out.println("SocialGraphCache is success!");
		}
	}

	@Override
	protected void onProgressUpdate(List<User>... values) {
		super.onProgressUpdate(values);

		if (values == null || values.length != 1) {
			return;
		}
		List<User> listSavedUser = values[0];
		if (relation == Relation.Followingship) {
			dao.saveFriends(user, listSavedUser);
		} else {
		    dao.saveFollowers(user, listSavedUser);
		}
	}


}
