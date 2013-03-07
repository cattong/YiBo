package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.os.AsyncTask;

import com.cattong.weibo.Weibo;


public class QueryBatchStatusCountTask extends AsyncTask<Void, Void, Boolean> {

	private List<com.cattong.entity.Status> listStatus;
	private Weibo microBlog;
	public QueryBatchStatusCountTask(List<com.cattong.entity.Status> listStatus, Weibo microBlog) {
		this.listStatus = listStatus;
		this.microBlog = microBlog;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = ResponseCountUtil.getResponseCounts(listStatus, microBlog);
		return isSuccess;
	}

}
