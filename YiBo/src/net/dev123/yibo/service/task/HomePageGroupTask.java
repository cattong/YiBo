package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.BaseUser;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.GroupDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.HomePageGroupListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class HomePageGroupTask extends AsyncTask<Void, Void, List<Group>> {
	private static final String TAG = "GroupTask";
	private MicroBlog microBlog = null;

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
				user.getId(), paging);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
