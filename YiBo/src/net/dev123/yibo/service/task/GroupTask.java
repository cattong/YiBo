package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.GroupActivity;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.GroupDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.GroupListAdapter;
import net.dev123.yibome.entity.LocalGroup;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GroupTask extends AsyncTask<Void, Void, List<Group>> {
	private static final String TAG = "GroupTask";
	private MicroBlog microBlog = null;

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
					user.getId(), remotePaging);

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
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
