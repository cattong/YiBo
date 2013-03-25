package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.MyStatusesActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.MyStatusesListAdapter;

public class MyStatusesTask extends AsyncTask<Void, Void, List<com.cattong.entity.Status>> {
	private static final String TAG = "MyStatusesTask";
	private Weibo microBlog = null;

	private MyStatusesActivity context;
	private MyStatusesListAdapter adapter;
	private Paging<com.cattong.entity.Status> paging;
	private User user;
	private String message;

	public MyStatusesTask(MyStatusesActivity context, MyStatusesListAdapter adapter, User user) {
		this.context = context;
		this.adapter = adapter;
		this.user = user;
		microBlog = GlobalVars.getMicroBlog(adapter.getAccount());
	}

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	@Override
	protected List<com.cattong.entity.Status> doInBackground(Void... params) {
		if (microBlog == null || user == null) {
			return null;
		}

		List<com.cattong.entity.Status> listStatus = null;
		paging = adapter.getPaging();
		com.cattong.entity.Status max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getUserTimeline(user.getUserId(), paging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
				paging.moveToPrevious();
			}
		}
		ResponseCountUtil.getResponseCounts(listStatus, microBlog);

		return listStatus;
	}

	@Override
	protected void onPostExecute(List<com.cattong.entity.Status> result) {
		if (result != null && result.size() > 0) {
			adapter.addCacheToDivider(null, result);
		} else {
			if (message != null) {
				Toast.makeText(adapter.getContext(), message, Toast.LENGTH_LONG).show();
			}
		}

		if (paging.hasNext()) {
			context.showMoreFooter();
		}else{
			context.showNoMoreFooter();
		}
	}

}
