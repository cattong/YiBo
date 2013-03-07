package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;
import android.widget.Toast;

import com.cattong.commons.Paging;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.activity.StatusSubscribeActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.StatusSubscribeListAdapter;

public class StatusSubscribeTask extends AsyncTask<Void, Void, List<com.cattong.entity.Status>> {
	private static final String TAG = "StatusSubscribeTask";
	private LocalAccount account;
	private Weibo microBlog = null;

	private StatusSubscribeActivity context;
	private StatusSubscribeListAdapter adapter;
	private Paging<com.cattong.entity.Status> paging;
	private String resultMsg;

	public StatusSubscribeTask(StatusSubscribeActivity context, StatusSubscribeListAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		this.account = adapter.getAccount();
		this.paging = adapter.getPaging();
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	@Override
	protected List<com.cattong.entity.Status> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}
		
		List<com.cattong.entity.Status> statusList = null;
//		SocialCat socialCat = Util.getSocialCat(context);
//		if (socialCat == null) {
//			return null;
//		}
//		
//		List<com.cattong.entity.Status> statusList = null;
//		com.cattong.entity.Status max = adapter.getMin();
//		paging.setGlobalMax(max);
//
//		if (paging.moveToNext()) {
//			try {
//				statusList = socialCat.getStatusCatalog(context.getCatalog(),
//					account.getServiceProvider(), paging);
//			} catch (LibException e) {
//				if (Constants.DEBUG) Log.e(TAG, "Task", e);
//				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
//				paging.moveToPrevious();
//			}
//		}
//		ResponseCountUtil.getResponseCounts(statusList, microBlog);

		return statusList;
	}

	@Override
	protected void onPostExecute(List<com.cattong.entity.Status> result) {
		if (result != null && result.size() > 0) {
			adapter.addCacheToDivider(null, result);
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		if (paging.hasNext()) {
			context.showMoreFooter();
		} else {
			context.showNoMoreFooter();
		}
	}

}
