package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.Group;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.GroupStatusesListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;

public class GroupStatusesPageDownTask extends AsyncTask<com.cattong.entity.Status, Void, Boolean> {
	private static final String TAG = "GroupStatusesTask";
	private LocalAccount account;
	private Weibo microBlog;
	
	private GroupStatusesListAdapter adapter;
	private Group group;
    private LocalStatus divider;
	private List<com.cattong.entity.Status> statusList;
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
        	resultMsg = ResourceBook.getResultCodeValue(
        		LibResultCode.NET_UNCONNECTED, adapter.getContext());
        	Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();

        	if (divider != null) {
        	    divider.setLoading(false);
        	}
        	adapter.notifyDataSetChanged();
        }
	}

	@Override
	protected Boolean doInBackground(com.cattong.entity.Status... params) {
		boolean isSuccess = false;
		if (microBlog == null 
			|| params == null 
			|| params.length != 2) {
			return isSuccess;
		}

		com.cattong.entity.Status max = params[0];
		com.cattong.entity.Status since = params[1];
		
		Paging<com.cattong.entity.Status> paging = new Paging<com.cattong.entity.Status>();;
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);
		if (paging.moveToNext()) {
		    try {
		    	statusList = microBlog.getGroupStatuses(group.getId(), paging);
		    } catch (LibException e) {
			    if (Logger.isDebug()) Log.e(TAG, "Task", e);
			    resultMsg = ResourceBook.getResultCodeValue(
			    	e.getErrorCode(), adapter.getContext());
			    paging.moveToPrevious();
		    }
		}
		ResponseCountUtil.getResponseCounts(statusList, microBlog);

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
		List<com.cattong.entity.Status> statusList = 
			new ArrayList<com.cattong.entity.Status>();
		statusList.add(divider);
		adapter.addCacheToLast(statusList);
	}
}
