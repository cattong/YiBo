package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.PublicTimelineActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.PublicTimelineListAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PublicTimelineTask extends AsyncTask<Void, Void, List<net.dev123.mblog.entity.Status>> {
	private static final String TAG = "PublicTimelineTask";
	private MicroBlog microBlog = null;

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
	protected List<net.dev123.mblog.entity.Status> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

		List<net.dev123.mblog.entity.Status> listStatus = null;
		try {
			listStatus = microBlog.getPublicTimeline();
		} catch (LibException e) {
			if (Constants.DEBUG) Log.v(TAG, e.getMessage(), e);
			message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}
        Util.getResponseCounts(listStatus, microBlog);
		return listStatus;
	}

	@Override
	protected void onPostExecute(List<net.dev123.mblog.entity.Status> result) {
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
