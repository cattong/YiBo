package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.cattong.weibo.entity.ResponseCount;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.widget.CommentClickableSpan;
import com.shejiaomao.weibo.widget.RetweetClickableSpan;

public class QueryRetweetResponseCountTask extends AsyncTask<Void, Void, ResponseCount> {
    private static final String LOG_TAG  = "QueryRetweetResponseCountTask";

	private MicroBlogActivity context;
	private Weibo microBlog;
	private String resultMsg;

	//被转发的原微博
	com.cattong.entity.Status status;
	public QueryRetweetResponseCountTask(MicroBlogActivity context,
		com.cattong.entity.Status status) {
		this.context = context;
		this.status = status;
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (status != null) {
			int retweetCount = status.getRetweetCount() == null ? 0 : status.getRetweetCount();
			int commentCount = status.getCommentCount() == null ? 0 : status.getCommentCount();
			fillRetweetResponseCount(retweetCount, commentCount);
		}

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, context);
        	cancel(true);
        	onPostExecute(null);
        }
	}

	@Override
	protected ResponseCount doInBackground(Void... params) {
		if (microBlog == null || status == null) {
			return null;
		}

		ResponseCount count = null;
		try {
			count = microBlog.getResponseCount(status);
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
	    	fillRetweetResponseCount(result.getRetweetCount(), result.getCommentCount());
	    } else {
	    	if (StringUtil.isNotEmpty(resultMsg)) {
	    	    //Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	    	}
	    }
	}

	private void fillRetweetResponseCount(int retweetCount, int commentCount) {
		TextView tvRetweetText = (TextView)context.findViewById(R.id.tvRetweetText);
		if (tvRetweetText.getVisibility() != View.VISIBLE) {
			return;
		}

		RetweetClickableSpan retweetClickableSpan = new RetweetClickableSpan(status);
		CommentClickableSpan commentClickableSpan = new CommentClickableSpan(status);

		String retweetCountText = context.getString(
			R.string.label_blog_retweet_retweet_count, retweetCount
		);
		String commentCountText = context.getString(
			R.string.label_blog_retweet_comment_count, commentCount
		);

		SpannableStringBuilder textSpan = new SpannableStringBuilder();

		User sourceUser = status.getUser();
		String mentionTitleName = sourceUser != null ? sourceUser.getMentionTitleName() : "@?";
		String retweetText = mentionTitleName + ": " + status.getText();
		Spannable retweetTextSpan = EmotionLoader.getEmotionSpannable(
			status.getServiceProvider(), retweetText);
		textSpan.append(retweetTextSpan);

		SpannableStringBuilder retweetCountSpan = new SpannableStringBuilder();
		retweetCountSpan.append(retweetCountText);
		retweetCountSpan.setSpan(
			retweetClickableSpan, 0,
			retweetCountText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textSpan.append(" ").append(retweetCountSpan);

		SpannableStringBuilder commentCountSpan = new SpannableStringBuilder();
		commentCountSpan.append(commentCountText);
		commentCountSpan.setSpan(
			commentClickableSpan, 0,
			commentCountText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	    textSpan.append(" ").append(commentCountSpan);
		tvRetweetText.setText(textSpan);
	}
}
