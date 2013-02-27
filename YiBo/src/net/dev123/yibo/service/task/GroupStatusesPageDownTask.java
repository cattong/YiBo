package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.adapter.GroupStatusesListAdapter;
import net.dev123.yibo.service.adapter.StatusUtil;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GroupStatusesPageDownTask extends AsyncTask<net.dev123.mblog.entity.Status, Void, Boolean> {
	private static final String TAG = "GroupStatusesTask";
	private LocalAccount account;
	private MicroBlog microBlog;
	
	private GroupStatusesListAdapter adapter;
	private Group group;
    private LocalStatus divider;
	private List<net.dev123.mblog.entity.Status> statusList;
	private boolean isEmptyAdapter = false;
    private String resultMsg = null;
	
	public GroupStatusesPageDownTask(GroupStatusesListAdapter adapter, LocalStatus divider) {
		this.adapter = adapter;
		this.group = adapter.getGroup();
		this.divider = divider;
		this.account = adapter.getAccount();
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(true);
        }
        isEmptyAdapter = (adapter.getCount() == 0);
        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	cancel(true);
        	resultMsg = ResourceBook.getStatusCodeValue(
        		ExceptionCode.NET_UNCONNECTED, adapter.getContext());
        	Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();

        	if (divider != null) {
        	    divider.setLoading(false);
        	}
        	adapter.notifyDataSetChanged();
        }
	}

	@Override
	protected Boolean doInBackground(net.dev123.mblog.entity.Status... params) {
		boolean isSuccess = false;
		if (microBlog == null 
			|| params == null 
			|| params.length != 2) {
			return isSuccess;
		}

		net.dev123.mblog.entity.Status max = params[0];
		net.dev123.mblog.entity.Status since = params[1];
		
		Paging<net.dev123.mblog.entity.Status> paging = new Paging<net.dev123.mblog.entity.Status>();;
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);
		if (paging.moveToNext()) {
		    try {
		    	statusList = microBlog.getGroupStatuses(group.getId(), paging);
		    } catch (LibException e) {
			    if (Constants.DEBUG) Log.e(TAG, "Task", e);
			    resultMsg = ResourceBook.getStatusCodeValue(
			    	e.getExceptionCode(), adapter.getContext());
			    paging.moveToPrevious();
		    }
		}
		Util.getResponseCounts(statusList, microBlog);

		isSuccess = ListUtil.isNotEmpty(statusList);
		if (isSuccess 
			&& (paging.hasNext()
				|| (paging.isLastPage() 
					&& since == null))) {
			LocalStatus localStatus = StatusUtil.createDividerStatus(statusList, account);
			if (paging.isLastPage() && since == null) {
				localStatus.setLocalDivider(true);
				adapter.getPaging().setLastPage(true);
			}
			statusList.add(localStatus);
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
        if (divider != null) {
        	divider.setLoading(false);
        }

		if (result) {
			if (divider == null) {
				adapter.addCacheToLast(statusList);
			} else {
				adapter.addCacheToDivider(divider, statusList);
			}
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(adapter.getContext(), R.string.msg_no_divider_data, Toast.LENGTH_LONG).show();
				adapter.remove(divider);
			}
			
			if (isEmptyAdapter) {
				setEmptyView();
			}
			// 如果没有的话,修改状态
			adapter.notifyDataSetChanged();
		}
	}
	
	private void setEmptyView() {
		LocalAccount account = adapter.getAccount();
		if (account == null) {
			return;
		}
		LocalStatus divider = new LocalStatus();
		divider.setDivider(true);
		divider.setLocalDivider(true);	
		List<net.dev123.mblog.entity.Status> statusList = 
			new ArrayList<net.dev123.mblog.entity.Status>();
		statusList.add(divider);
		adapter.addCacheToLast(statusList);
	}
}
