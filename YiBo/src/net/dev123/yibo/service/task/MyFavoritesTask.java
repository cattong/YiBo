package net.dev123.yibo.service.task;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.yibo.MyFavoritesActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.MyFavoriteListAdapter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MyFavoritesTask extends AsyncTask<Void, Void, List<net.dev123.mblog.entity.Status>> {
	private static String TAG  = "MyFavoritesTask";
	private MicroBlog microBlog = null;

	private MyFavoritesActivity context;
	private MyFavoriteListAdapter adapter;
	private Paging<net.dev123.mblog.entity.Status> paging;
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
	protected List<net.dev123.mblog.entity.Status> doInBackground(Void... params) {
		if (microBlog == null) {
			return null;
		}

		List<net.dev123.mblog.entity.Status> listStatus = null;
		paging = adapter.getPaging();
		net.dev123.mblog.entity.Status max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				listStatus = microBlog.getFavorites(paging);
			} catch (LibException e) {
				if (Constants.DEBUG) Log.e(TAG, "Task", e);
				message = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
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
