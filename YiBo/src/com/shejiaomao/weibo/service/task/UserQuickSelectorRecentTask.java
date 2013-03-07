package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;

import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.BaseUser;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.activity.UserQuickSelectorActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.TaskDao;
import com.shejiaomao.weibo.service.adapter.UserQuickSelectorListAdapter;

public class UserQuickSelectorRecentTask extends AsyncTask<Void, Void, List<? extends BaseUser>> {
	private static final String TAG = "UserQuickSelectorTask";
	private Weibo microBlog = null;
	
	private UserQuickSelectorActivity context;
	private UserQuickSelectorListAdapter adapter = null;
	private Paging<User> paging;
	
	private LocalAccount account;
	public UserQuickSelectorRecentTask(UserQuickSelectorListAdapter adapter) {
		this.adapter = adapter;
		this.context = (UserQuickSelectorActivity)adapter.getContext();
		this.account = adapter.getAccount();
		this.microBlog = GlobalVars.getMicroBlog(account);
	}
	
	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}
	
	@Override
	protected List<? extends BaseUser> doInBackground(Void... params) {
		if (adapter == null || microBlog == null) {
			return null;
		}

	    List<? extends BaseUser> userList = null;
	    TaskDao dao = new TaskDao(context);
	    
	    paging = adapter.getPaging();
	    if (paging.hasNext()) {
	    	paging.moveToNext();
	    	userList = dao.findRecentContact(account, paging);
	    }
	    
	    return userList;
	}

	@Override
	protected void onPostExecute(List<? extends BaseUser> result) {
		if (ListUtil.isNotEmpty(result)) {
			adapter.addCacheToDivider(null, (List<User>) result);
		} else {
			adapter.notifyDataSetChanged();
		}

		if (paging.hasNext()) {
			((UserQuickSelectorActivity)context).showMoreFooter();
		} else {
			((UserQuickSelectorActivity)context).showNoMoreFooter();
		}

	}
}
