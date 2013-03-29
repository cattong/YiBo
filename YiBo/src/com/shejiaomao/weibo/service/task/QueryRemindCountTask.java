package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.UnreadCount;
import com.cattong.weibo.entity.UnreadType;
import com.shejiaomao.common.NetType;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.NotificationEntity;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.adapter.MyHomeListAdapter;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.widget.Skeleton;

public class QueryRemindCountTask extends AsyncTask<Void, Void, UnreadCount> {
	private static final String TAG = "QueryRemindCountTask";
	private SheJiaoMaoApplication sheJiaoMao;
	private Context context;
	private Weibo microBlog;
    private AdapterCollectionCache adapterCache;

	public QueryRemindCountTask(AdapterCollectionCache adapterCache) {
		this.adapterCache = adapterCache;
		microBlog = GlobalVars.getMicroBlog(adapterCache.getAccount());
		if (adapterCache.getMyHomeListAdapter() != null) {
			context = adapterCache.getMyHomeListAdapter().getContext();
			sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (GlobalVars.NET_TYPE == NetType.NONE) {
			cancel(true);
		}
	}

	@Override
	protected UnreadCount doInBackground(Void... params) {
		UnreadCount unreadCount = null;
		if (microBlog == null) {
			return unreadCount;
		}

		try {
			unreadCount = microBlog.getUnreadCount();
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
			if (e.getErrorCode() == LibResultCode.API_UNSUPPORTED) {
				updateIfUnreadCountUnsupport();
			}
		}

		if (unreadCount != null) {
			updateByUnreadCount(unreadCount);
		}
		return null;
	}

	private void updateByUnreadCount(UnreadCount unreadCount) {
		if (sheJiaoMao == null 
			|| adapterCache == null 
			|| unreadCount == null
		) {
			return;
		}

		//新粉丝提醒;
		if (sheJiaoMao.isCheckFollowers() && unreadCount.getFollowerCount() > 0){
			sendNewFollowersBroadcast(unreadCount);
		}

		LocalAccount account = adapterCache.getAccount();
		if (Logger.isDebug()) Log.v(TAG, "accountId:" + account.getAccountId() + " updateByRemindCount");
		MyHomeListAdapter myHomeAdapter = adapterCache.getMyHomeListAdapter();
		if (myHomeAdapter != null && sheJiaoMao.isCheckStatuses()) {
			MyHomePageUpTask task = new MyHomePageUpTask(myHomeAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update myhome");
		}

		MentionsListAdapter metionsAdapter = adapterCache.getMentionsListAdapter();
		if (metionsAdapter != null 
			&& sheJiaoMao.isCheckMentions() 
			&& unreadCount.getMetionCount() > 0) {
			MetionsPageUpTask task = new MetionsPageUpTask(metionsAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update metions");
		}

		CommentsListAdapter commentsAdapter = adapterCache.getCommentsListAdapter();
		if (commentsAdapter != null 
			&& sheJiaoMao.isCheckComments() 
			&& unreadCount.getCommentCount() > 0) {
			CommentsPageUpTask task = new CommentsPageUpTask(commentsAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update comments");
		}

		DirectMessagesListAdapter messageAdapter = adapterCache.getDirectMessagesListAdapter();
		if (messageAdapter != null 
			&& sheJiaoMao.isCheckDirectMesages() 
			&& unreadCount.getDireceMessageCount() > 0) {
			DirectMessagePageUpTask task = new DirectMessagePageUpTask(messageAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update inbox");
		}
	}

	private void updateIfUnreadCountUnsupport() {
		if (sheJiaoMao == null || adapterCache == null) {
			return;
		}

		LocalAccount account = adapterCache.getAccount();
		if (Logger.isDebug()) Log.v(TAG, "accountId:" + account.getAccountId() + " updateIfunreadCountUnsupport");
		MyHomeListAdapter myHomeAdapter = adapterCache.getMyHomeListAdapter();
		if (myHomeAdapter != null && sheJiaoMao.isCheckStatuses()) {
			MyHomePageUpTask task = new MyHomePageUpTask(myHomeAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update myhome");
		}

		MentionsListAdapter metionsAdapter = adapterCache.getMentionsListAdapter();
		if (metionsAdapter != null && sheJiaoMao.isCheckMentions()) {
			MetionsPageUpTask task = new MetionsPageUpTask(metionsAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update metions");
		}

		CommentsListAdapter commentsAdapter = adapterCache.getCommentsListAdapter();
		if (commentsAdapter != null && sheJiaoMao.isCheckComments()) {
			CommentsPageUpTask task = new CommentsPageUpTask(commentsAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update comments");
		}

		DirectMessagesListAdapter inboxAdapter = adapterCache.getDirectMessagesListAdapter();
		if (inboxAdapter != null && sheJiaoMao.isCheckDirectMesages()) {
			DirectMessagePageUpTask task = new DirectMessagePageUpTask(inboxAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update inbox");
		}
	}

	private void sendNewFollowersBroadcast(UnreadCount unreadCount) {
		if (unreadCount == null || unreadCount.getFollowerCount() <= 0) {
			return;
		}

		LocalAccount account = adapterCache.getAccount();
		//发送更新广播;
		Intent intent = new Intent(Constants.ACTION_RECEIVER_AUTO_UPDATE_NOTIFY);
		NotificationEntity entity = new NotificationEntity();
		entity.setTickerText(context.getString(R.string.msg_followers_ticker_text));
		String screenName = (account.getUser() == null ? "" : account.getUser().getScreenName());
		entity.setContentTitle(context.getString(
			R.string.msg_followers_content_title,
			screenName,	unreadCount.getFollowerCount()));
		
		String contentText = context.getString(
			R.string.msg_followers_content_text,
			unreadCount.getFollowerCount());
		entity.setContentText(
			account.getServiceProvider().getSpName() + ": " 
			+ contentText);
		entity.setContentType(Skeleton.TYPE_MORE);

		intent.putExtra("NOTIFICATION_ENTITY", entity);
		intent.putExtra("ACCOUNT", account);
		context.sendBroadcast(intent);

		//清空提醒
		ResetUnreadCountTask task = new ResetUnreadCountTask(
		    context, account, UnreadType.FOLLOWER);
		task.execute();
	}
}
