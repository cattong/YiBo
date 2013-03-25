package com.shejiaomao.weibo.service.task;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.Logger;
import com.cattong.commons.Paging;
import com.cattong.commons.util.ListUtil;
import com.cattong.entity.Comment;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.CommentsOfStatusListAdapter;

public class QueryCommentsOfStatusTask extends AsyncTask<Void, Void, List<Comment>> {
    private static final String TAG = "QueryCommentsOfStatusTask";
	private Context context;
	private CommentsOfStatusListAdapter adapter;
	private Weibo microBlog = null;
	private Paging<Comment> paging;
	private com.cattong.entity.Status status;

	private String resultMsg = null;
	public QueryCommentsOfStatusTask(CommentsOfStatusListAdapter adapter) {
		this.adapter = adapter;
		this.status = adapter.getStatus();
		this.context = adapter.getContext();
        this.paging = adapter.getPaging();

		microBlog = GlobalVars.getMicroBlog(adapter.getAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (context instanceof MicroBlogActivity) {
			((MicroBlogActivity)context).showLoadingFooter();
		}
	}

	@Override
	protected List<Comment> doInBackground(Void... params) {
		List<Comment> listComment = null;
        if (microBlog == null || status == null) {
        	return listComment;
        }

		Comment max = adapter.getMin();
		paging.setGlobalMax(max);

		if (paging.moveToNext()) {
			try {
				listComment = microBlog.getCommentsOfStatus(status.getStatusId(), paging);
				for(int i = 0, size = listComment.size(); i < size; i++) {
					Comment comment = listComment.get(i);
					if (comment.getReplyToStatus() == null 
						|| comment.getReplyToStatus().getStatusId() == null) {
						comment.setReplyToStatus(status);
					}
				}
			} catch (LibException e) {
				if(Logger.isDebug()) Log.e(TAG, TAG, e);
				if (e.getErrorCode() != LibResultCode.API_UNSUPPORTED) {
				    resultMsg = ResourceBook.getResultCodeValue(
				    	e.getErrorCode(), context);
				}
				paging.moveToPrevious();
			}
		}

		return listComment;
	}

	@Override
	protected void onPostExecute(List<Comment> result) {
	    super.onPreExecute();

	    if (ListUtil.isNotEmpty(result)) {
	    	adapter.addCacheToLast(result);
	    } else if (resultMsg != null){
	    	Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	    }

	    if (paging.hasNext()) {
			if (context instanceof MicroBlogActivity) {
				((MicroBlogActivity)context).showMoreFooter();
			}
	    } else {
			if (context instanceof MicroBlogActivity) {
				((MicroBlogActivity)context).showNoMoreFooter();
			}
	    }
	}
}
