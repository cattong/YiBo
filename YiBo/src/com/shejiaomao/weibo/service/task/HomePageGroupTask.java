package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.BaseUser;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.HomePageGroupListAdapter;

public class HomePageGroupTask extends AsyncTask<Void, Void, List<Group>> {
	private static final String TAG = "GroupTask";
	private Weibo microBlog = null;

	private Context context;
	private HomePageGroupListAdapter adapter;
	private LocalAccount account;

	private boolean hasChange;
	private String resultMsg;
	public HomePageGroupTask(Context context, HomePageGroupListAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
		this.hasChange = false;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected List<Group> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

	    List<Group> groupList = null;
	    Paging<Group> paging = new Paging<Group>();
	    //paging.setPageSize(Constants.)
		try {
			BaseUser user = account.getUser();
			groupList = microBlog.getGroups(
				user.getUserId(), paging);
		} catch (LibException e) {
			Logger.debug(TAG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			paging.moveToPrevious();
		}

		if (ListUtil.isNotEmpty(groupList)) {
			GroupDao dao = new GroupDao(context);
		    hasChange = dao.merge(account, groupList);
		}
		
		return groupList;
	}

	@Override
	protected void onPostExecute(List<Group> result) {
		if (adapter.getCount() <= 0 || hasChange) {
		    adapter.addGroupList(result);
		}
		
		if (resultMsg != null) {
			Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();
		}
	}

}
