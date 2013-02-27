package net.dev123.yibo.service.task;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.ResponseCount;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.EmotionLoader;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.widget.CommentClickableSpan;
import net.dev123.yibo.widget.RetweetClickableSpan;
import android.app.Activity;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class QueryRetweetResponseCountTask extends AsyncTask<Void, Void, ResponseCount> {
    private static final String LOG_TAG  = "QueryRetweetResponseCountTask";

	private MicroBlogActivity context;
	private MicroBlog microBlog;
	private String resultMsg;

	//被转发的原微博
	net.dev123.mblog.entity.Status status;
	public QueryRetweetResponseCountTask(MicroBlogActivity context,
		net.dev123.mblog.entity.Status status) {
		this.context = context;
		this.status = status;
		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
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
        	resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, context);
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
	    	fillRetweetResponseCount(result.getRetweetCount(), result.getCommentsCount());
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
