package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.ResponseCount;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;

public class QueryResponseCountTask extends AsyncTask<Void, Void, ResponseCount> {
    private static final String LOG_TAG  = "QueryResponseCountTask";

	private Context context;
	private Weibo weibo = null;
	private String resultMsg = null;

	//更新列表的转发和评论
	com.cattong.entity.Status status;
	TextView tvResponse;
	public QueryResponseCountTask(Context context, com.cattong.entity.Status status) {
		this.context = context;
		this.status = status;
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		weibo = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	public QueryResponseCountTask(Context context, com.cattong.entity.Status status, TextView tvResponse) {
		this.context = context;
		this.status = status;
		this.tvResponse = tvResponse;
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		weibo = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, context);
        	cancel(true);
        	onPostExecute(null);
        }

		if (context instanceof MicroBlogActivity) {
			int retweetCount = (status.getRetweetCount() == null ? 0 : status.getRetweetCount());
			int commentCount = (status.getCommentCount() == null ? 0 : status.getCommentCount());
			fillClickableResponseCount(retweetCount, commentCount);
    	} else {
			if (CompatibilityUtil.isSdk1_5()) {
				if (Logger.isDebug()) Log.v(LOG_TAG, "sdk1.5");
				cancel(true);
			}
    	}
	}

	@Override
	protected ResponseCount doInBackground(Void... params) {
		if (weibo == null || status == null) {
			return null;
		}

		if (status.getServiceProvider() == ServiceProvider.Twitter) {
			//Twitter不支持Count接口
			return null;
		}

		ResponseCount count = null;
		try {
			count = weibo.getResponseCount(status);
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(LOG_TAG, resultMsg, e);
			if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
			    resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
			}
		}

		return count;
	}

	@Override
	protected void onPostExecute(ResponseCount result) {
	    super.onPreExecute();

	    if (result != null) {
	    	status.setRetweetCount(result.getRetweetCount());
	    	status.setCommentCount(result.getCommentCount());
	    	String responseFormat = GlobalResource.getStatusResponseFormat(context);
            if (context instanceof MicroBlogActivity) {
            	fillClickableResponseCount(result.getRetweetCount(), result.getCommentCount());
	    	} else if (tvResponse != null) {
	    		String responseText = String.format(
	    			responseFormat,	result.getRetweetCount(),
	    			result.getCommentCount());
	    		tvResponse.setText(responseText);
	    	}
	    } else {
	    	if (tvResponse == null && resultMsg != null) {
	    	    //Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	    	}
	    }
	}

	private void fillClickableResponseCount(int retweetCount, int commentCount) {
		TextView tvRetweetCount = (TextView)((Activity)context).findViewById(R.id.tvRetweetCount);
		TextView tvCommentCount = (TextView)((Activity)context).findViewById(R.id.tvCommentCount);

		String retweetCountText = context.getString(
			R.string.label_blog_retweet_count, retweetCount);
		String commentCountText = context.getString(
			R.string.label_blog_comment_count, commentCount);

		tvRetweetCount.setText(retweetCountText);
		tvCommentCount.setText(commentCountText);
	}
}
