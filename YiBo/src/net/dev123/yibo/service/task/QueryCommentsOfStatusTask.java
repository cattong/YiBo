package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Comment;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.adapter.CommentsOfStatusListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class QueryCommentsOfStatusTask extends AsyncTask<Void, Void, List<Comment>> {
    private static final String TAG = "QueryCommentsOfStatusTask";
	private Context context;
	private CommentsOfStatusListAdapter adapter;
	private MicroBlog microBlog = null;
	private Paging<Comment> paging;
	private net.dev123.mblog.entity.Status status;

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
				listComment = microBlog.getCommentsOfStatus(status.getId(), paging);
				for(int i = 0, size = listComment.size(); i < size; i++) {
					Comment comment = listComment.get(i);
					if (comment.getInReplyToStatus() == null 
						|| comment.getInReplyToStatus().getId() == null) {
						comment.setInReplyToStatus(status);
					}
				}
			} catch (LibException e) {
				if(Constants.DEBUG) Log.e(TAG, TAG, e);
				if (e.getExceptionCode() != ExceptionCode.UNSUPPORTED_API) {
				    resultMsg = ResourceBook.getStatusCodeValue(
				    	e.getExceptionCode(), context);
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
