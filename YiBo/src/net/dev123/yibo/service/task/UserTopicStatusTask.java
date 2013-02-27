package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Status;
import net.dev123.yibo.UserTopicStatusesActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.UserTopicStatusListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-24 上午12:34:46
 **/
public class UserTopicStatusTask extends AsyncTask<Void, Void, List<Status>> {

	private static final String TAG = UserTopicStatusTask.class.getSimpleName();
	private String message;
	private UserTopicStatusesActivity userTopicStatusesActivity;
	private UserTopicStatusListAdapter adapter;
	private String trendName;
	private Paging<net.dev123.mblog.entity.Status> paging;
	
	public UserTopicStatusTask(Context context, 
			UserTopicStatusListAdapter adapter, String trendName) {
		if (!(context instanceof UserTopicStatusesActivity)
			|| adapter == null
			|| StringUtil.isEmpty(trendName)) {
			this.cancel(true);
		}
		this.userTopicStatusesActivity = (UserTopicStatusesActivity)context;
		this.adapter = adapter;
		this.trendName = trendName;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected List<net.dev123.mblog.entity.Status> doInBackground(
			Void... params) {
		YiBoApplication yibo = (YiBoApplication) userTopicStatusesActivity.getApplication();
		MicroBlog mBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
		if (mBlog == null) {
			return null;
		}
		paging = adapter.getPaging();
		paging.setGlobalMax(adapter.getMin());
		List<net.dev123.mblog.entity.Status> statusList = null;
		
		if (paging.moveToNext()) {
			try {
				statusList = mBlog.getUserTrendsStatus(trendName, paging);
			} catch (LibException e) {
				if (Constants.DEBUG)
					Log.d(TAG, e.getMessage(), e);
				message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), 
						userTopicStatusesActivity);
				paging.moveToPrevious();
			}
		}
		return statusList;
	}

	@Override
	protected void onPostExecute(List<net.dev123.mblog.entity.Status> result) {
		super.onPostExecute(result);
		if (result != null && result.size() > 0) {
			adapter.addCacheToDivider(null, result);
		} else {
			if (message != null) {
				Toast.makeText(adapter.getContext(), message, Toast.LENGTH_LONG).show();
			}
		}
		
		if (paging.hasNext()) {
			userTopicStatusesActivity.showMoreFooter();
		}else{
			userTopicStatusesActivity.showNoMoreFooter();
		}
	}
}
