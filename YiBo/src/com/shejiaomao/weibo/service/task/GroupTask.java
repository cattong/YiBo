package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.GroupActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalGroup;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;

public class GroupTask extends AsyncTask<Void, Void, List<Group>> {
	private static final String TAG = "GroupTask";
	private Weibo microBlog = null;

	private GroupActivity context;
	private GroupListAdapter adapter = null;
	private Paging<Group> paging;

	private LocalAccount account;

	private String resultMsg;
	public GroupTask(GroupListAdapter adapter) {
		this.adapter = adapter;
		this.context = (GroupActivity)adapter.getContext();
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	@Override
	protected List<Group> doInBackground(Void... params) {
		if (adapter == null || microBlog == null) {
			return null;
		}

	    List<Group> groupList = null;
	    GroupDao dao = new GroupDao(context);
	    paging = adapter.getPaging();
		try {
			if (paging.moveToNext()) {
				groupList = dao.getGroups(account, paging);
			}

			GroupCacheTask cacheTask = null;
			User user = (User) account.getUser();
			if (adapter.getCount() <= 1 && ListUtil.isEmpty(groupList)) {
				//paging.moveToPrevious();
				Paging<Group> remotePaging = new Paging<Group>();
				groupList = microBlog.getGroups(
					user.getUserId(), remotePaging);

				//缓冲远程数据;
				cacheTask = new GroupCacheTask(context, account);
			} else if (paging.getPageIndex() == 1) {
				//防止新建组，缓冲第一次。
				cacheTask = new GroupCacheTask(context, account);
				cacheTask.setCycleTime(2);
				cacheTask.setPageSize(20);
			}
			if (cacheTask != null) {
			    cacheTask.execute();
			}
		} catch (LibException e) {
			Logger.debug(TAG, e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			paging.moveToPrevious();
		}

		return groupList;
	}

	@Override
	protected void onPostExecute(List<Group> result) {
		if (ListUtil.isNotEmpty(result)) {
			adapter.addCacheToLast(result);
			Group group = result.get(0);
			if (!(group instanceof LocalGroup)) {
				GroupDao dao = new GroupDao(context);
				dao.save(account, result);
			}
		} else {
			adapter.notifyDataSetChanged();

			if (resultMsg != null) {
				Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		if (paging.hasNext()) {
			context.showMoreFooter();
		} else {
			context.showNoMoreFooter();
		}

	}

}
