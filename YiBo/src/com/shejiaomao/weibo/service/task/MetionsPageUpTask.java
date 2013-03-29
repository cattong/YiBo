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
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.widget.PullToRefreshListView;

public class MetionsPageUpTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "MetionsPageUpTask";	
	public static ReentrantLock lock = new ReentrantLock();
	
    private Context context;
    private LocalAccount account;
	private Weibo microBlog = null;
	private UnreadCount unreadCount;
	private MentionsListAdapter adapter;
	private PullToRefreshListView listView;
	private List<com.cattong.entity.Status> listStatus;
    private String resultMsg = null;
    private boolean isAutoUpdate = false;
    private boolean isEmptyAdapter = false;
    private boolean isUpdateConflict = false;
	public MetionsPageUpTask(MentionsListAdapter adapter) {
		this.adapter = adapter;
		this.context = adapter.getContext();

		microBlog = GlobalVars.getMicroBlog(adapter.getAccount());
		listStatus = new ArrayList<com.cattong.entity.Status>(Constants.PAGING_DEFAULT_COUNT);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isEmptyAdapter = (adapter.getMax() == null);
		boolean isCancel = false;
        if (!isAutoUpdate) {

        }
        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	isCancel = true;
        	if (!isAutoUpdate) {
	    		resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, context);
        	}
        }
        if (unreadCount != null && unreadCount.getMetionCount() <= 0) {
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
			return isSuccess;
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

		Paging<com.cattong.entity.Status> paging = new Paging<com.cattong.entity.Status>();
		paging.setPageSize(GlobalVars.UPDATE_COUNT);
		com.cattong.entity.Status since = adapter.getMax();
		if (since instanceof LocalStatus
			&& ((LocalStatus)since).isDivider()) {
			since = null;
		}
		paging.setGlobalSince(since);

		if (unreadCount == null) {
		    try {
			     unreadCount = microBlog.getUnreadCount();
		    } catch(LibException e) {}
		}

		if (paging.hasNext()) {
			paging.moveToNext();
			try {
				List<com.cattong.entity.Status> statuses = microBlog.getMentionTimeline(paging);
				listStatus.addAll(statuses);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}

		ResponseCountUtil.getResponseCounts(listStatus, microBlog);

		isSuccess = ListUtil.isNotEmpty(listStatus);
		if (isSuccess && paging.hasNext()) {
			LocalStatus localStatus = StatusUtil.createDividerStatus(listStatus, account);
			listStatus.add(localStatus);
		}
		//添加到适配器中，此处没有更新ui
		if (isSuccess) {
			adapter.addNewBlogs(listStatus);
		}

		lock.unlock();
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		int cacheSize = adapter.getListNewBlogs().size();
		if (result || cacheSize > 0) {
			if (isAutoUpdate) {
				if (unreadCount == null ||
					(unreadCount != null && unreadCount.getMetionCount() > 0)
				) {
	                sendBroadcast();
				} else {
					addToAdapter();
				}
			} else {
				addToAdapter();
				if (unreadCount != null && unreadCount.getMetionCount() > 0) {
					sendBroadcast();
				}
			}
			//清空提醒
			ResetUnreadCountTask task = new ResetUnreadCountTask(
			    context, adapter.getAccount(), UnreadType.MENTION);
			task.execute();
		} else {
			if (resultMsg != null && !isAutoUpdate) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else if (resultMsg == null && !isAutoUpdate) {
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
		//发送更新广播;
		Intent intent = new Intent(Constants.ACTION_RECEIVER_AUTO_UPDATE_NOTIFY);
		NotificationEntity entity = adapter.getNotificationEntity(unreadCount);
		intent.putExtra("NOTIFICATION_ENTITY", entity);
		intent.putExtra("ACCOUNT", adapter.getAccount());
		context.sendBroadcast(intent);
	}

	private void addToAdapter() {
		List<com.cattong.entity.Status> listNewBlog = adapter.getListNewBlogs();
		int cacheSize = listNewBlog.size();
		//如果通知已经存在;
		if (cacheSize > 0) {
			NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notiManager.cancel(adapter.getAccount().getAccountId().intValue() * 100 + Skeleton.TYPE_MENTION);
		}

		if (cacheSize > 0 &&
			listNewBlog.get(cacheSize - 1) instanceof LocalStatus
		) {
			cacheSize--;
		}

		adapter.refresh();
		if (!isAutoUpdate && !isEmptyAdapter) {
			Toast.makeText(
				adapter.getContext(),
				adapter.getContext().getString(R.string.msg_refresh_metion, cacheSize),
				Toast.LENGTH_LONG
			).show();
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
		listStatus.add(divider);
		adapter.addCacheToFirst(listStatus);
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
