package net.dev123.yibo.service.task;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.ResponseCount;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.CompatibilityUtil;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalResource;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class QueryResponseCountTask extends AsyncTask<Void, Void, ResponseCount> {
    private static final String LOG_TAG  = "QueryStatusCount";

	private Context context;
	private MicroBlog microBlog = null;
	private String resultMsg = null;

	//更新列表的转发和评论
	net.dev123.mblog.entity.Status status;
	TextView tvResponse;
	public QueryResponseCountTask(Context context, net.dev123.mblog.entity.Status status) {
		this.context = context;
		this.status = status;
		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	public QueryResponseCountTask(Context context, net.dev123.mblog.entity.Status status, TextView tvResponse) {
		this.context = context;
		this.status = status;
		this.tvResponse = tvResponse;
		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, context);
        	cancel(true);
        	onPostExecute(null);
        }

		if (context instanceof MicroBlogActivity) {
			int retweetCount = (status.getRetweetCount() == null ? 0 : status.getRetweetCount());
			int commentCount = (status.getCommentCount() == null ? 0 : status.getCommentCount());
			fillClickableResponseCount(retweetCount, commentCount);
    	} else {
			if (CompatibilityUtil.isSdk1_5()) {
				if (Constants.DEBUG) Log.v(LOG_TAG, "sdk1.5");
				cancel(true);
			}
    	}
	}

	@Override
	protected ResponseCount doInBackground(Void... params) {
		if (microBlog == null || status == null) {
			return null;
		}

		if (status.getServiceProvider() == ServiceProvider.Twitter) {
			//Twitter不支持Count接口
			return null;
		}

		ResponseCount count = null;
		try {
			count = microBlog.getResponseCount(status);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(LOG_TAG, resultMsg, e);
			if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
			    resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
			}
		}

		return count;
	}

	@Override
	protected void onPostExecute(ResponseCount result) {
	    super.onPreExecute();

	    if (result != null) {
	    	status.setRetweetCount(result.getRetweetCount());
	    	status.setCommentCount(result.getCommentsCount());
	    	String responseFormat = GlobalResource.getStatusResponseFormat(context);
            if (context instanceof MicroBlogActivity) {
            	fillClickableResponseCount(result.getRetweetCount(), result.getCommentsCount());
	    	} else if (tvResponse != null) {
	    		String responseText = String.format(
	    			responseFormat,	result.getRetweetCount(),
	    			result.getCommentsCount());
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
