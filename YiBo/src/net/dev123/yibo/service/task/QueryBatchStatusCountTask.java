package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.mblog.MicroBlog;
import android.os.AsyncTask;


public class QueryBatchStatusCountTask extends AsyncTask<Void, Void, Boolean> {

	private List<net.dev123.mblog.entity.Status> listStatus;
	private MicroBlog microBlog;
	public QueryBatchStatusCountTask(List<net.dev123.mblog.entity.Status> listStatus, MicroBlog microBlog) {
		this.listStatus = listStatus;
		this.microBlog = microBlog;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = Util.getResponseCounts(listStatus, microBlog);
		return isSuccess;
	}

}
