package net.dev123.yibo.service.task;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.exception.ExceptionCode;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Comment;
import net.dev123.mblog.tencent.Tencent;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.NetType;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.db.LocalComment;
import net.dev123.yibo.service.adapter.CommentUtil;
import net.dev123.yibo.service.adapter.CommentsListAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CommentsPageDownTask extends AsyncTask<Comment, Void, Boolean> {
	private static final String TAG = "CommentsPageDownTask";
	private Context context;
	private MicroBlog microBlog;
	private LocalAccount account;

	private CommentsListAdapter adapter;
	private LocalComment divider;
	List<Comment> commentList = null;
	private String resultMsg = null;
	public CommentsPageDownTask(CommentsListAdapter adapter, LocalComment divider) {
		this.adapter = adapter;
		this.context = adapter.getContext();
		this.account = adapter.getAccount();
		this.divider = divider;

		YiBoApplication yibo = (YiBoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(true);
        }

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	cancel(true);
        	resultMsg = ResourceBook.getStatusCodeValue(ExceptionCode.NET_UNCONNECTED, context);
        	Toast.makeText(context, resultMsg, Toast.LENGTH_LONG).show();

        	if (divider != null) {
        		divider.setLoading(false);
        	}
        	adapter.notifyDataSetChanged();
        }
	}

	@Override
	protected Boolean doInBackground(Comment... params) {
		boolean isSuccess = false;
		if (microBlog == null ||
			params == null ||
			params.length != 2
		) {
			return isSuccess;
		}

		Comment max = params[0];
		Comment since = params[1];
		Paging<Comment> paging = new Paging<Comment>();
		paging.setGlobalMax(max);
		paging.setGlobalSince(since);

		if (paging.moveToNext()) {
			try {
				commentList = microBlog.getCommentsToMe(paging);
			} catch (LibException e) {
				if(Constants.DEBUG) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
				paging.moveToPrevious();
			}
		}

		isSuccess = ListUtil.isNotEmpty(commentList);
		if (isSuccess && paging.hasNext()) {
			LocalComment localComment = CommentUtil.createDividerComment(commentList, account);
			commentList.add(localComment);
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
	    super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(false);
        }

	    if (result) {
            adapter.addCacheToDivider(divider, commentList);
	    } else {
	    	boolean isTencent = microBlog instanceof Tencent;
			if (resultMsg != null) {
				if (!isTencent) {
				    Toast.makeText(adapter.getContext(), resultMsg, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(adapter.getContext(), R.string.msg_no_divider_data, Toast.LENGTH_LONG).show();
				adapter.remove(divider);
			}

			// 如果没有的话,修改状态
			adapter.notifyDataSetChanged();
	    }

	}

}