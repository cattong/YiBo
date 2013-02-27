package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Constants;
import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.entity.BaseUser;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.GroupMemberActivity;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.UserGroupDao;
import net.dev123.yibo.service.adapter.GroupMemberListAdapter;
import net.dev123.yibome.entity.LocalGroup;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GroupMemberTask extends AsyncTask<Void, Void, List<? extends BaseUser>> {
	private static final String TAG = "GroupMemeberTask";
	private MicroBlog microBlog = null;

	private GroupMemberActivity context;
	private GroupMemberListAdapter adapter = null;
	private Paging<? extends BaseUser> paging;

	private LocalGroup group;
	private LocalAccount account;

	private String resultMsg;
	public GroupMemberTask(GroupMemberListAdapter adapter, LocalGroup group) {
		this.adapter = adapter;
		this.context = (GroupMemberActivity)adapter.getContext();
		this.group = group;
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
	    boolean isFirstLoad = context.isFirstLoad();
	    UserGroupDao dao = new UserGroupDao(context);
		try {
			paging = adapter.getPaging();
			if (!isFirstLoad && paging.moveToNext()) {
				userList = dao.getMembers(group, account.getServiceProvider(), paging);
				if (paging.getPageIndex() == 1 && ListUtil.isEmpty(userList)) {
					isFirstLoad = true;
					context.setFirstLoad(isFirstLoad);
				}
			}

			GroupMemberCacheTask cacheTask = null;
			if (isFirstLoad) {
				//第一次加载时，做一个缓冲task
				if (paging.getPageIndex() == 1 && adapter.getCount() == 0) {
					Paging<User> remotePaging = new Paging<User>();
					adapter.setPaging(remotePaging);
					//缓冲远程数据;
					cacheTask = new GroupMemberCacheTask(context, account, group);
				}

				paging = adapter.getPaging();
				if (paging.moveToNext()) {
				    userList = microBlog.getGroupMembers(group.getSpGroupId(), (Paging<User>) paging);
				}
			} else if (paging.getPageIndex() == 1) {
				//防止新关注，缓冲第一次。
				cacheTask = new GroupMemberCacheTask(context, account, group);
				cacheTask.setCycleTime(1);
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

		return userList;
	}

	@Override
	protected void onPostExecute(List<? extends BaseUser> result) {
		if (ListUtil.isNotEmpty(result)) {
			adapter.addCacheToLast((List<BaseUser>) result);
		} else {
			adapter.notifyDataSetChanged();

			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		if (paging.hasNext()) {
			context.showMoreFooter();
		} else {
			context.showNoMoreFooter();
		}

	}

}
