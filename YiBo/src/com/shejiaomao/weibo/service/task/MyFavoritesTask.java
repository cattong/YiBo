package com.shejiaomao.weibo.service.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.MyFavoritesActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.MyFavoriteListAdapter;

public class MyFavoritesTask extends AsyncTask<Void, Void, List<com.cattong.entity.Status>> {
	private static String TAG  = "MyFavoritesTask";
	private Weibo microBlog = null;

	private MyFavoritesActivity context;
	private MyFavoriteListAdapter adapter;
	private Paging<com.cattong.entity.Status> paging;
	private String message;

	ArrayList<Status> listStatus = null;

	public MyFavoritesTask(MyFavoritesActivity context, MyFavoriteListAdapter adapter, LocalAccount account) {
		this.context = context;
		this.adapter = adapter;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	public MyFavoritesTask(MyFavoritesActivity context, MyFavoriteListAdapter adapter, int accountID) {
		this.context = context;
		this.adapter = adapter;
		microBlog = GlobalVars.getMicroBlog(accountID);
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

		List<com.cattong.entity.Status> listStatus = null;
		paging = adapter.getPaging();
		com.cattong.entity.Status max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getFavorites(paging);
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e(TAG, "Task", e);
				message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
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
			// 如果没有的话，界面加上提示!
			if (message != null) {
				Toast.makeText(adapter.getContext(), message, Toast.LENGTH_LONG).show();
			}
		}
		if(paging.hasNext()){
			context.showMoreFooter();
		}else{
			context.showNoMoreFooter();
		}

	}

}
