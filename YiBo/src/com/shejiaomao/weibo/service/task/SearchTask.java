package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.SearchActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.StatusSearchResultAdapter;
import com.shejiaomao.weibo.service.adapter.UserSearchResultAdapter;

public class SearchTask extends AsyncTask<Void, Void, List> {

	enum SearchType {
		Statuses, Users
	}

	private SearchActivity context;
	private ArrayAdapter adapter;
	private Paging paging;
	private String keyword;
	private String message;
	private SearchType type;

	public SearchTask(SearchActivity context, Paging paging, String keyword, ArrayAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		this.paging = paging;
		this.keyword = keyword;

		if (adapter instanceof StatusSearchResultAdapter) {
			type = SearchType.Statuses;
		} else if (adapter instanceof UserSearchResultAdapter) {
			type = SearchType.Users;
		}
	}

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	@Override
	protected List doInBackground(Void... params) {
		if (adapter == null) {
			return null;
		}

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)context.getApplication();
		Weibo microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
		if (microBlog == null) {
			return null;
		}

		List searchResult = null;
		if (paging.moveToNext()) {
			try {
				if (type == SearchType.Statuses) {
					searchResult = microBlog.searchStatuses(keyword, paging);
				} else if (type == SearchType.Users) {
					searchResult = microBlog.searchUsers(keyword, paging);
				}
			} catch (LibException e) {
				if (Logger.isDebug()) Log.e("SearchTask", e.getMessage(), e);
				message = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}
		if (searchResult != null &&
			searchResult.size() > 0 &&
			searchResult.get(0) instanceof com.cattong.entity.Status
		) {
			ResponseCountUtil.getResponseCounts((List<com.cattong.entity.Status>)searchResult, microBlog);
		}

		return searchResult;
	}

	@Override
	protected void onPostExecute(List result) {
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				adapter.add(result.get(i));
			}
			adapter.notifyDataSetChanged();
		} else {
			if (message != null) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		}
		if (paging.hasNext()) {
			context.showMoreFooter();
		} else {
			context.showNoMoreFooter();
		}

	}
}
