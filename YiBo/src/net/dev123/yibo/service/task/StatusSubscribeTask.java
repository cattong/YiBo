package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.StatusSubscribeActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.YiBoMeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.StatusSubscribeListAdapter;
import net.dev123.yibome.YiBoMe;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class StatusSubscribeTask extends AsyncTask<Void, Void, List<net.dev123.mblog.entity.Status>> {
	private static final String TAG = "StatusSubscribeTask";
	private LocalAccount account;
	private MicroBlog microBlog = null;
	private YiBoMe yiboMe = null;

	private StatusSubscribeActivity context;
	private StatusSubscribeListAdapter adapter;
	private Paging<net.dev123.mblog.entity.Status> paging;
	private String resultMsg;

	public StatusSubscribeTask(StatusSubscribeActivity context, StatusSubscribeListAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		this.account = adapter.getAccount();
		this.paging = adapter.getPaging();
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		context.showLoadingFooter();
	}

	@Override
	protected List<net.dev123.mblog.entity.Status> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}
		yiboMe = YiBoMeUtil.getYiBoMeNullAuth();
		if (yiboMe == null) {
			return null;
		}
		
		List<net.dev123.mblog.entity.Status> statusList = null;
		net.dev123.mblog.entity.Status max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				statusList = yiboMe.getStatusSubscribe(context.getCatalog(),
					account.getServiceProvider(), paging);
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
				paging.moveToPrevious();
			}
		}
		Util.getResponseCounts(statusList, microBlog);

		return statusList;
	}

	@Override
	protected void onPostExecute(List<net.dev123.mblog.entity.Status> result) {
		if (result != null && result.size() > 0) {
			adapter.addCacheToDivider(null, result);
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		if (paging.hasNext()) {
			context.showMoreFooter();
		} else {
			context.showNoMoreFooter();
		}
	}

}
