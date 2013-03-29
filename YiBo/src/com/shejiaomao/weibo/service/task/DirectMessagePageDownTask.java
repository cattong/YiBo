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
import com.cattong.weibo.entity.DirectMessage;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessageUtil;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;

public class DirectMessagePageDownTask extends AsyncTask<DirectMessage, Void, Boolean> {
	private static final String TAG = "DirectMessageTask";
	private Weibo microBlog = null;
	private LocalAccount account;

	private CacheAdapter<DirectMessage> adapter = null;
	private LocalDirectMessage divider;
	private List<DirectMessage> messageList;
    private String resultMsg;

	public DirectMessagePageDownTask(DirectMessagesListAdapter adapter, LocalDirectMessage divider) {
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
	protected Boolean doInBackground(DirectMessage... params) {
		boolean isSuccess = false;
		if (microBlog == null ||
			params == null ||
			params.length != 2
		) {
			return isSuccess;
		}

		DirectMessage max = params[0];
		DirectMessage since = params[1];
		Paging<DirectMessage> paging = new Paging<DirectMessage>();
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);

		if (paging.moveToNext()) {
			try {
				//if (adapter instanceof ConversationListAdapter) {
				messageList = microBlog.getInboxDirectMessages(paging);
				//} else {
				messageList = microBlog.getOutboxDirectMessages(paging);
				//}
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), adapter.getContext());
				paging.moveToPrevious();
			}
		}
		//ListUtil.truncate(listMessage, paging.getMax(), paging.getSince());

		isSuccess = ListUtil.isNotEmpty(messageList);
		if (isSuccess && paging.hasNext()) {
			LocalDirectMessage localMessage = DirectMessageUtil.createDividerDirectMessage(
				messageList, account);
			messageList.add(localMessage);
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
        if (divider != null) {
        	divider.setLoading(false);
        }

		if (result) {
			adapter.addCacheToDivider(divider, messageList);
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(adapter.getContext(), R.string.msg_no_divider_data, Toast.LENGTH_LONG).show();
				adapter.remove(divider);
			}

			// 如果没有的话，界面加上提示!
			adapter.notifyDataSetChanged();
		}
	}
}
