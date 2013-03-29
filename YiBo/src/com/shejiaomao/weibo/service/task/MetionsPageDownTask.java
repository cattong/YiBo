package com.shejiaomao.weibo.service.task;

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
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;

public class MetionsPageDownTask extends AsyncTask<com.cattong.entity.Status, Void, Boolean> {
	private static final String TAG = "MetionsTask";
	private Weibo microBlog;
	private LocalAccount account;

	private MentionsListAdapter adapter;
	private LocalStatus divider;
	private List<com.cattong.entity.Status> listStatus = null;
    private String resultMsg;
	public MetionsPageDownTask(MentionsListAdapter adapter, LocalStatus divider) {
		this.adapter = adapter;
		this.account = adapter.getAccount();
		this.divider = divider;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(true);
        }

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	cancel(true);
        	resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, adapter.getContext());
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

		Paging<com.cattong.entity.Status> paging = new Paging<com.cattong.entity.Status>();
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getMentionTimeline(paging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), adapter.getContext());
				paging.moveToPrevious();
			} finally {
		    	ListUtil.truncate(listStatus, paging.getMax(), paging.getSince());
		    }
		}
		ResponseCountUtil.getResponseCounts(listStatus, microBlog);

		isSuccess = listStatus != null && listStatus.size() > 0;
		if (isSuccess && paging.hasNext()) {
			LocalStatus localStatus = StatusUtil.createDividerStatus(listStatus, account);
			listStatus.add(localStatus);
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
        if (divider != null) {
        	divider.setLoading(false);
        }

		if (result) {
            adapter.addCacheToDivider(divider, listStatus);
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(adapter.getContext(), R.string.msg_no_divider_data, Toast.LENGTH_LONG).show();
				adapter.remove(divider);
			}

			// 如果没有的话,修改状态
			adapter.notifyDataSetChanged();
		}
	}
}
