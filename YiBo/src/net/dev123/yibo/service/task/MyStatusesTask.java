package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.MyStatusesActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.MyStatusesListAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MyStatusesTask extends AsyncTask<Void, Void, List<net.dev123.mblog.entity.Status>> {
	private static final String TAG = "MyStatusesTask";
	private MicroBlog microBlog = null;

	private MyStatusesActivity context;
	private MyStatusesListAdapter adapter;
	private Paging<net.dev123.mblog.entity.Status> paging;
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
	protected List<net.dev123.mblog.entity.Status> doInBackground(Void... params) {
		if (microBlog == null || user == null) {
			return null;
		}

		List<net.dev123.mblog.entity.Status> listStatus = null;
		paging = adapter.getPaging();
		net.dev123.mblog.entity.Status max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getUserTimeline(user.getId(), paging);
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
				message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
				paging.moveToPrevious();
			}
		}
		Util.getResponseCounts(listStatus, microBlog);

		return listStatus;
	}

	@Override
	protected void onPostExecute(List<net.dev123.mblog.entity.Status> result) {
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
