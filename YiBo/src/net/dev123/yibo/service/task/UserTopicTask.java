package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Trend;
import net.dev123.yibo.UserTopicsActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.TopicListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UserTopicTask extends AsyncTask<Void, Void, List<Trend>> {

	private static final String TAG = UserTopicTask.class.getSimpleName();
	private UserTopicsActivity userTopicsActivity;
	private TopicListAdapter topicsAdapter;
	private Paging<Trend> paging;
	private String message;
	
	public UserTopicTask(Context context) {
		this.userTopicsActivity = (UserTopicsActivity) context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		topicsAdapter = userTopicsActivity.getTopicsAdapter();
		paging = userTopicsActivity.getPaging();
	}
	
	@Override
	protected List<Trend> doInBackground(Void... params) {
		YiBoApplication yibo = (YiBoApplication) userTopicsActivity.getApplication();
		MicroBlog mBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
		if (mBlog == null) {
			return null;
		}
		
		List<Trend> trendList = null;
		if (paging.moveToNext()) {
			try {
				trendList = mBlog.getUserTrends(
						yibo.getCurrentAccount().getUser().getId(), paging);
			} catch (LibException e) {
				if (Constants.DEBUG) Log.d(TAG, e.getMessage(), e);
				message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), 
						userTopicsActivity);
				paging.moveToPrevious();
			}
		}
		return trendList;
	}
	
	@Override
	protected void onPostExecute(List<Trend> trendList) {
		super.onPostExecute(trendList);
		if (trendList != null && trendList.size() > 0) {
			List<String> topicList = new ArrayList<String>(trendList.size());
			for(Trend trend : trendList) {
				topicList.add(trend.getName());
			}
			topicsAdapter.addCacheToLast(topicList);
			topicsAdapter.notifyDataSetChanged();
		} else {
			if (StringUtil.isNotEmpty(message)) {
				Toast.makeText(userTopicsActivity, message, Toast.LENGTH_LONG).show();
			}
		}
		
		if (paging.hasNext()) {
			userTopicsActivity.showMoreFooter();
		} else {
			userTopicsActivity.showNoMoreFooter();
		}
	}

}
