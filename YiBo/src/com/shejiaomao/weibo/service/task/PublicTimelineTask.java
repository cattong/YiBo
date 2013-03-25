package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.PublicTimelineActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.PublicTimelineListAdapter;

public class PublicTimelineTask extends AsyncTask<Void, Void, List<com.cattong.entity.Status>> {
	private static final String TAG = "PublicTimelineTask";
	private Weibo microBlog = null;

	private PublicTimelineListAdapter adapter;
	private String message;
	private PublicTimelineActivity context;

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	public PublicTimelineTask(PublicTimelineActivity context, PublicTimelineListAdapter adapter, LocalAccount account) {
		this.adapter = adapter;
		this.context = context;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected List<com.cattong.entity.Status> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

		List<com.cattong.entity.Status> listStatus = null;
		try {
			listStatus = microBlog.getPublicTimeline();
		} catch (LibException e) {
			if (Logger.isDebug()) Log.v(TAG, e.getMessage(), e);
			message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}
        ResponseCountUtil.getResponseCounts(listStatus, microBlog);
		return listStatus;
	}

	@Override
	protected void onPostExecute(List<com.cattong.entity.Status> result) {
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				adapter.add(result.get(i));
			}
			adapter.notifyDataSetChanged();
		} else {
			// 如果没有的话，界面加上提示!
			if (message != null) {
				Toast.makeText(adapter.getContext(), message, Toast.LENGTH_LONG).show();
			}
		}
		context.showMoreFooter();
	}

}
