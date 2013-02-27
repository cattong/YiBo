package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.BaseUser;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.UserQuickSelectorActivity;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.TaskDao;
import net.dev123.yibo.service.adapter.UserQuickSelectorListAdapter;
import android.os.AsyncTask;

public class UserQuickSelectorRecentTask extends AsyncTask<Void, Void, List<? extends BaseUser>> {
	private static final String TAG = "UserQuickSelectorTask";
	private MicroBlog microBlog = null;
	
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
