package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.DirectMessage;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalDirectMessage;
import net.dev123.yibo.service.adapter.CacheAdapter;
import net.dev123.yibo.service.adapter.DirectMessageUtil;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DirectMessagePageDownTask extends AsyncTask<DirectMessage, Void, Boolean> {
	private static final String TAG = "DirectMessageTask";
	private MicroBlog microBlog = null;
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
        	resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, adapter.getContext());
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
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), adapter.getContext());
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
