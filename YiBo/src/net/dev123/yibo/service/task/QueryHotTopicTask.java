package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Trend;
import net.dev123.mblog.entity.Trends;
import net.dev123.yibo.HotTopicsActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.TopicListAdapter;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class QueryHotTopicTask extends AsyncTask<Void, Void, Trends> {
	private static final String TAG = QueryHotTopicTask.class.getSimpleName();

	private HotTopicsActivity context;
	private TopicListAdapter topicsAdapter;
	private String message;

	public QueryHotTopicTask(HotTopicsActivity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		topicsAdapter = context.getTopicsAdapter();
	}

	@Override
	protected Trends doInBackground(Void... params) {
		YiBoApplication yibo = (YiBoApplication) ((Activity) context).getApplication();
		MicroBlog microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
		if (microBlog == null) {
			return null;
		}

		Trends trends = null;
		try {
			trends = microBlog.getCurrentTrends();
		} catch (LibException e) {
			Log.d(TAG, e.getMessage(), e);
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}

		return trends;
	}

	@Override
	protected void onPostExecute(Trends trends) {
		super.onPreExecute();
		context.hideLoadingView();
		if (trends != null && trends.getTrends() != null) {
			Trend[] topics = trends.getTrends();			
			List<String> topicList = new ArrayList<String>(topics.length);
			for (Trend topic : topics) {
				topicList.add(topic.getName());
			}
			topicsAdapter.addCacheToLast(topicList);
			topicsAdapter.notifyDataSetChanged();
		} else {
			if (StringUtil.isNotEmpty(message)) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		}
	}
}
