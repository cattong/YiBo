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
import com.cattong.entity.Comment;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;
import com.cattong.weibo.impl.tencent.Tencent;
import com.cattong.weibo.impl.twitter.Twitter;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalComment;
import com.shejiaomao.weibo.service.adapter.CommentUtil;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.widget.PullToRefreshListView;

public class CommentsPageUpTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "CommentsPageUpTask";
	public static ReentrantLock lock = new ReentrantLock();
	private Context context;

	private CommentsListAdapter adapter;
	private PullToRefreshListView listView;
	private Weibo microBlog = null;
	private UnreadCount UnreadCount;
	private List<Comment> commentList;
	private Paging<Comment> paging;
	private String resultMsg = null;
	private boolean isAutoUpdate = false;
	private boolean isEmptyAdapter = false;
	private boolean isUpdateConflict = false;
	public CommentsPageUpTask(CommentsListAdapter adapter) {
		this.adapter = adapter;
		this.context = adapter.getContext();

		microBlog = GlobalVars.getMicroBlog(adapter.getAccount());
		commentList = new ArrayList<Comment>();
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
	    		Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
        	}
        }
        if (UnreadCount != null && UnreadCount.getCommentCount() <= 0) {
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

		paging = new Paging<Comment>();
		paging.setPageSize(GlobalVars.UPDATE_COUNT);
		Comment since = adapter.getMax();
		if (since instanceof LocalComment
			&& ((LocalComment)since).isDivider()) {
			since = null;
		}
		paging.setGlobalSince(since);

		if (UnreadCount == null) {
		    try {
			     UnreadCount = microBlog.getUnreadCount();
		    } catch(LibException e) {}
		}

		if (paging.hasNext()) {
			paging.moveToNext();
			try {
				List<Comment> tempCommentList = microBlog.getCommentsToMe(paging);
				commentList.addAll(tempCommentList);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				    resultMsg = ResourceBook.getResultCodeValue(
				    	e.getErrorCode(), context);
				}
			}
		}

		isSuccess = ListUtil.isNotEmpty(commentList);
		if (isSuccess && paging.hasNext()) {
			LocalComment localComment = CommentUtil.createDividerComment(
				commentList, adapter.getAccount());
			commentList.add(localComment);
		}
		if (isSuccess) {
			adapter.addNewComments(commentList);
		}

		lock.unlock();
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		int cacheSize = adapter.getListNewComments().size();
		if (result || cacheSize > 0) {
			if (isAutoUpdate) {
				if (UnreadCount == null ||
					(UnreadCount != null && UnreadCount.getCommentCount() > 0)
				) {
					sendBroadcast();
				} else {
					addToAdapter();
				}
			} else {
				addToAdapter();
				if (UnreadCount != null && UnreadCount.getCommentCount() > 0) {
					sendBroadcast();
				}
			}

			//清空提醒
			ResetUnreadCountTask task = new ResetUnreadCountTask(
				context, adapter.getAccount(), UnreadType.COMMENT);
		    task.execute();
		} else {
			boolean isTencent = microBlog instanceof Tencent;
			boolean isTwitter = microBlog instanceof Twitter;
			if (resultMsg != null && !isAutoUpdate && !isTencent && !isTwitter) {
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
		NotificationEntity entity = adapter.getNotificationEntity(UnreadCount);
		intent.putExtra("NOTIFICATION_ENTITY", entity);
		intent.putExtra("ACCOUNT", adapter.getAccount());
		context.sendBroadcast(intent);
	}

	private void addToAdapter() {
		List<Comment> listNewComment = adapter.getListNewComments();
		int cacheSize = listNewComment.size();
		//如果通知已经存在;
		if (cacheSize > 0) {
			NotificationManager notiManager = (NotificationManager) 
			    context.getSystemService(Context.NOTIFICATION_SERVICE);
			int accountId = adapter.getAccount().getAccountId().intValue();
			notiManager.cancel(accountId * 100 + Skeleton.TYPE_COMMENT);
		}


		if (cacheSize > 0 
			&& listNewComment.get(cacheSize - 1) instanceof LocalComment
		) {
			cacheSize--;
		}

		adapter.refresh();
		if (!isAutoUpdate && !isEmptyAdapter) {
			Toast.makeText(
				context,
				context.getString(R.string.msg_refresh_comment, cacheSize),
				Toast.LENGTH_LONG
			).show();
		}
	}

	private void setEmptyView() {
		LocalAccount account = adapter.getAccount();
		if (account == null) {
			return;
		}
		LocalComment divider = new LocalComment();
		divider.setDivider(true);
		divider.setLocalDivider(true);
		commentList.add(divider);
		adapter.addCacheToFirst(commentList);
	}

	public boolean isAutoUpdate() {
		return isAutoUpdate;
	}

	public void setAutoUpdate(boolean isAutoUpdate) {
		this.isAutoUpdate = isAutoUpdate;
	}

	public UnreadCount getUnreadCount() {
		return UnreadCount;
	}

	public void setUnreadCount(UnreadCount UnreadCount) {
		this.UnreadCount = UnreadCount;
	}

	public PullToRefreshListView getListView() {
		return listView;
	}

	public void setListView(PullToRefreshListView listView) {
		this.listView = listView;
	}
}
