package net.dev123.yibo.service.task;

import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.UnreadCount;
import net.dev123.mblog.entity.UnreadType;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.NotificationEntity;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.CommentsListAdapter;
import net.dev123.yibo.service.adapter.DirectMessagesListAdapter;
import net.dev123.yibo.service.adapter.MentionsListAdapter;
import net.dev123.yibo.service.adapter.MyHomeListAdapter;
import net.dev123.yibo.service.cache.AdapterCollectionCache;
import net.dev123.yibo.widget.Skeleton;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class QueryRemindCountTask extends AsyncTask<Void, Void, UnreadCount> {
	private static final String TAG = "QueryRemindCountTask";
	private YiBoApplication yibo;
	private Context context;
	private MicroBlog microBlog;
    private AdapterCollectionCache adapterCache;

	public QueryRemindCountTask(AdapterCollectionCache adapterCache) {
		this.adapterCache = adapterCache;
		microBlog = GlobalVars.getMicroBlog(adapterCache.getAccount());
		if (adapterCache.getMyHomeListAdapter() != null) {
			context = adapterCache.getMyHomeListAdapter().getContext();
			yibo = (YiBoApplication)context.getApplicationContext();
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
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
			if (e.getExceptionCode() == ExceptionCode.UNSUPPORTED_API) {
				updateIfUnreadCountUnsupport();
			}
		}

		if (unreadCount != null) {
			updateByUnreadCount(unreadCount);
		}
		return null;
	}

	private void updateByUnreadCount(UnreadCount unreadCount) {
		if (yibo == null 
			|| adapterCache == null 
			|| unreadCount == null
		) {
			return;
		}

		//新粉丝提醒;
		if (yibo.isCheckFollowers() && unreadCount.getFollowerCount() > 0){
			sendNewFollowersBroadcast(unreadCount);
		}

		LocalAccount account = adapterCache.getAccount();
		if (Constants.DEBUG) Log.v(TAG, "accountId:" + account.getAccountId() + " updateByRemindCount");
		MyHomeListAdapter myHomeAdapter = adapterCache.getMyHomeListAdapter();
		if (myHomeAdapter != null && yibo.isCheckStatuses()) {
			MyHomePageUpTask task = new MyHomePageUpTask(myHomeAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update myhome");
		}

		MentionsListAdapter metionsAdapter = adapterCache.getMentionsListAdapter();
		if (metionsAdapter != null 
			&& yibo.isCheckMentions() 
			&& unreadCount.getMetionCount() > 0) {
			MetionsPageUpTask task = new MetionsPageUpTask(metionsAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update metions");
		}

		CommentsListAdapter commentsAdapter = adapterCache.getCommentsListAdapter();
		if (commentsAdapter != null 
			&& yibo.isCheckComments() 
			&& unreadCount.getCommentCount() > 0) {
			CommentsPageUpTask task = new CommentsPageUpTask(commentsAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update comments");
		}

		DirectMessagesListAdapter messageAdapter = adapterCache.getDirectMessagesListAdapter();
		if (messageAdapter != null 
			&& yibo.isCheckDirectMesages() 
			&& unreadCount.getDireceMessageCount() > 0) {
			DirectMessagePageUpTask task = new DirectMessagePageUpTask(messageAdapter);
			task.setAutoUpdate(true);
			task.setUnreadCount(unreadCount);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update inbox");
		}
	}

	private void updateIfUnreadCountUnsupport() {
		if (yibo == null || adapterCache == null) {
			return;
		}

		LocalAccount account = adapterCache.getAccount();
		if (Constants.DEBUG) Log.v(TAG, "accountId:" + account.getAccountId() + " updateIfunreadCountUnsupport");
		MyHomeListAdapter myHomeAdapter = adapterCache.getMyHomeListAdapter();
		if (myHomeAdapter != null && yibo.isCheckStatuses()) {
			MyHomePageUpTask task = new MyHomePageUpTask(myHomeAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update myhome");
		}

		MentionsListAdapter metionsAdapter = adapterCache.getMentionsListAdapter();
		if (metionsAdapter != null && yibo.isCheckMentions()) {
			MetionsPageUpTask task = new MetionsPageUpTask(metionsAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update metions");
		}

		CommentsListAdapter commentsAdapter = adapterCache.getCommentsListAdapter();
		if (commentsAdapter != null && yibo.isCheckComments()) {
			CommentsPageUpTask task = new CommentsPageUpTask(commentsAdapter);
			task.setAutoUpdate(true);
			task.execute();
			Log.v(TAG, "accountId:" + account.getAccountId() + " auto update comments");
		}

		DirectMessagesListAdapter inboxAdapter = adapterCache.getDirectMessagesListAdapter();
		if (inboxAdapter != null && yibo.isCheckDirectMesages()) {
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
			account.getServiceProvider().getServiceProviderName() + ": " 
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
