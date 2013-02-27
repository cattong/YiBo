package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalStatus;
import net.dev123.yibo.service.adapter.MentionsListAdapter;
import net.dev123.yibo.service.adapter.StatusUtil;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MetionsPageDownTask extends AsyncTask<net.dev123.mblog.entity.Status, Void, Boolean> {
	private static final String TAG = "MetionsTask";
	private MicroBlog microBlog;
	private LocalAccount account;

	private MentionsListAdapter adapter;
	private LocalStatus divider;
	private List<net.dev123.mblog.entity.Status> listStatus = null;
    private String resultMsg;
	public MetionsPageDownTask(MentionsListAdapter adapter, LocalStatus divider) {
		this.adapter = adapter;
		this.account = adapter.getAccount();
		this.divider = divider;
		microBlog = GlobalVars.getMicroBlog(account);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(true);
        }

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	cancel(true);
        	resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, adapter.getContext());
        	Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();

        	if (divider != null) {
        		divider.setLoading(false);
        	}
        	adapter.notifyDataSetChanged();
        }
	}

	@Override
	protected Boolean doInBackground(net.dev123.mblog.entity.Status... params) {
		boolean isSuccess = false;
		if (microBlog == null 
			|| params == null 
		    || params.length != 2) {
			return isSuccess;
		}

		net.dev123.mblog.entity.Status max = params[0];
		net.dev123.mblog.entity.Status since = params[1];

		Paging<net.dev123.mblog.entity.Status> paging = new Paging<net.dev123.mblog.entity.Status>();
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getMentions(paging);
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), adapter.getContext());
				paging.moveToPrevious();
			} finally {
		    	ListUtil.truncate(listStatus, paging.getMax(), paging.getSince());
		    }
		}
		Util.getResponseCounts(listStatus, microBlog);

		isSuccess = listStatus != null && listStatus.size() > 0;
		if (isSuccess && paging.hasNext()) {
			LocalStatus localStatus = StatusUtil.createDividerStatus(listStatus, account);
			listStatus.add(localStatus);
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
        if (divider != null) {
        	divider.setLoading(false);
        }

		if (result) {
            adapter.addCacheToDivider(divider, listStatus);
		} else {
			if (resultMsg != null) {
				Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(adapter.getContext(), R.string.msg_no_divider_data, Toast.LENGTH_LONG).show();
				adapter.remove(divider);
			}

			// 如果没有的话,修改状态
			adapter.notifyDataSetChanged();
		}
	}
}
