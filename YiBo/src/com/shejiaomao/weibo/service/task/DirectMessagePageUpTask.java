package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.shejiaomao.maobo.R;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.DirectMessage;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;
import com.shejiaomao.weibo.service.adapter.DirectMessageUtil;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.widget.PullToRefreshListView;

public class DirectMessagePageUpTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "DirectMessagePageUpTask";
	public static ReentrantLock lock = new ReentrantLock();
    private Context context;
    private LocalAccount account;
	private Weibo microBlog = null;
	private UnreadCount unreadCount;
	private DirectMessagesListAdapter adapter;
	private PullToRefreshListView listView;

	private List<DirectMessage> inboxList = null;
	private List<DirectMessage> outboxList = null;
	private Paging<DirectMessage> inboxPaging;
	private Paging<DirectMessage> outboxPaging;
    private String resultMsg = null;
    private boolean isAutoUpdate = false;
    private boolean isEmptyAdapter = false;
    private boolean isUpdateConflict = false;

	public DirectMessagePageUpTask(DirectMessagesListAdapter adapter) {
		this.adapter = adapter;
		this.context = adapter.getContext();
		inboxList = new ArrayList<DirectMessage>();
        outboxList = new ArrayList<DirectMessage>();

		account = adapter.getAccount();
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isEmptyAdapter = (adapter.getMax() == null);
		boolean isCancel = false;

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	isCancel = true;
        	if (!isAutoUpdate) {
	    		resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, context);
	    		Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
        	}
        }
        if (unreadCount != null && unreadCount.getDireceMessageCount() <= 0) {
        	isCancel = true;
        }

        if (isCancel) {
        	cancel(true);
        	if (!isAutoUpdate) {
        		onPostExecute(false);
        	}
        }
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = false;
		if (microBlog == null) {
			return false;
		}

		if (isAutoUpdate) {
			lock.lock();
		} else {
			if (!lock.tryLock()) {
				isUpdateConflict = true;
				resultMsg = context.getString(R.string.msg_update_conflict);
				return false;
			}
		}

		inboxPaging = new Paging<DirectMessage>();
		DirectMessage inboxSince = adapter.getInboxMax();
		if (inboxSince instanceof LocalDirectMessage
			&& ((LocalDirectMessage)inboxSince).isDivider()) {
			inboxSince = null;
		}
		inboxPaging.setGlobalSince(inboxSince);
		inboxPaging.setPageSize(GlobalVars.UPDATE_COUNT);

		outboxPaging = new Paging<DirectMessage>();
		DirectMessage outboxSince = adapter.getOutboxMax();
		if (outboxSince instanceof LocalDirectMessage
			&& ((LocalDirectMessage)outboxSince).isDivider()) {
			outboxSince = null;
		}
		outboxPaging.setGlobalSince(outboxSince);
		outboxPaging.setPageSize(GlobalVars.UPDATE_COUNT);

		if (unreadCount == null) {
		    try {
		    	unreadCount = microBlog.getUnreadCount();
		    } catch(LibException e) {}
		}

		if (inboxPaging.hasNext()) {
			inboxPaging.moveToNext();
			try {
			    inboxList = microBlog.getInboxDirectMessages(inboxPaging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}

		if (outboxPaging.hasNext()) {
			outboxPaging.moveToNext();
			try {
				outboxList = microBlog.getOutboxDirectMessages(outboxPaging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}

		if (ListUtil.isNotEmpty(inboxList)
			&& (inboxPaging.hasNext())) {
			LocalDirectMessage localMessage = DirectMessageUtil.createDividerDirectMessage(
				inboxList, account);
			inboxList.add(localMessage);
		}
		if (ListUtil.isNotEmpty(inboxList)) {
			adapter.addNewInbox(inboxList);
		}

		if (ListUtil.isNotEmpty(outboxList)
			&& (outboxPaging.hasNext())) {
			LocalDirectMessage localMessage = DirectMessageUtil.createDividerDirectMessage(
				outboxList, account);
			outboxList.add(localMessage);
		}
		if (ListUtil.isNotEmpty(outboxList)) {
			adapter.addNewOutbox(outboxList);
		}

		isSuccess = ListUtil.isNotEmpty(inboxList) || ListUtil.isNotEmpty(outboxList);
		lock.unlock();
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		int cacheSize = adapter.getNewInboxSize() + adapter.getNewOutboxSize();
		if (result || cacheSize > 0) {
			if (isAutoUpdate) {
				if ((unreadCount == null
						&& adapter.getNewInboxList().size() > 0)
					|| (unreadCount != null
						&& unreadCount.getDireceMessageCount() > 0)) {
	                sendBroadcast();
				} else {
					addToAdapter();
				}
			} else {
				addToAdapter();
				if (unreadCount != null && unreadCount.getDireceMessageCount() > 0) {
					sendBroadcast();
				}
			}

			//清空提醒
			if (adapter instanceof DirectMessagesListAdapter) {
				ResetUnreadCountTask task = new ResetUnreadCountTask(
					context, account, UnreadType.DIRECT_MESSAGE);
			    task.execute();
			}
		} else {
			if (StringUtil.isNotEmpty(resultMsg) && !isAutoUpdate && !isEmptyAdapter) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else if (StringUtil.isNotEmpty(resultMsg) && !isAutoUpdate) {
				Toast.makeText(adapter.getContext(), R.string.msg_latest_data, Toast.LENGTH_LONG).show();
			}

			if (isEmptyAdapter) {
				setEmptyView();
			}
		}

		if (!isAutoUpdate && listView != null) {
			listView.onRefreshComplete();
		}
	}

	private void sendBroadcast() {
		if (!(adapter instanceof DirectMessagesListAdapter)) {
		    return;
		}
		DirectMessagesListAdapter inboxAdapter = (DirectMessagesListAdapter)adapter;
		//发送更新广播;
		Intent intent = new Intent(Constants.ACTION_RECEIVER_AUTO_UPDATE_NOTIFY);
		NotificationEntity entity = inboxAdapter.getNotificationEntity(unreadCount);
		intent.putExtra("NOTIFICATION_ENTITY", entity);
		intent.putExtra("ACCOUNT", inboxAdapter.getAccount());
		context.sendBroadcast(intent);
	}

	private void addToAdapter() {
		int inboxSize = adapter.getNewInboxSize();
		int outboxSize = adapter.getNewOutboxSize();
		//如果通知已经存在;
		if (inboxSize + outboxSize > 0) {
			NotificationManager notiManager = (NotificationManager)
		        context.getSystemService(Context.NOTIFICATION_SERVICE);
			int accountId = adapter.getAccount().getAccountId().intValue() * 100;
			notiManager.cancel(accountId + Skeleton.TYPE_DIRECT_MESSAGE);
		}

		adapter.refresh();

		if (!isAutoUpdate && !isEmptyAdapter) {
			Context context = adapter.getContext();
			String msg = context.getString(R.string.msg_refresh_direct_message,
				inboxSize, outboxSize);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

	private void setEmptyView() {
		LocalAccount account = adapter.getAccount();
		if (account == null) {
			return;
		}
		LocalDirectMessage divider = new LocalDirectMessage();
		divider.setDivider(true);
		divider.setLocalDivider(true);
		List<DirectMessage> messageList = new ArrayList<DirectMessage>();
		messageList.add(divider);
		adapter.addCacheToFirst(messageList);
	}

	public boolean isAutoUpdate() {
		return isAutoUpdate;
	}

	public void setAutoUpdate(boolean isAutoUpdate) {
		this.isAutoUpdate = isAutoUpdate;
	}

	public UnreadCount getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(UnreadCount unreadCount) {
		this.unreadCount = unreadCount;
	}

	public PullToRefreshListView getListView() {
		return listView;
	}

	public void setListView(PullToRefreshListView listView) {
		this.listView = listView;
	}
}
