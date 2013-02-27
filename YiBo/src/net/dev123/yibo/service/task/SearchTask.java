package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.SearchActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.StatusSearchResultAdapter;
import net.dev123.yibo.service.adapter.UserSearchResultAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

		YiBoApplication yibo = (YiBoApplication)context.getApplication();
		MicroBlog microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
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
				if (Constants.DEBUG) Log.e("SearchTask", e.getMessage(), e);
				message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			}
		}
		if (searchResult != null &&
			searchResult.size() > 0 &&
			searchResult.get(0) instanceof net.dev123.mblog.entity.Status
		) {
			Util.getResponseCounts((List<net.dev123.mblog.entity.Status>)searchResult, microBlog);
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
