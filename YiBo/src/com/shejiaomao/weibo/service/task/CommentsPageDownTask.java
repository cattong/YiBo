package com.shejiaomao.weibo.service.task;

import java.util.List;

import com.shejiaomao.maobo.R;
import android.app.Activity;
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
import com.cattong.weibo.impl.tencent.Tencent;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalComment;
import com.shejiaomao.weibo.service.adapter.CommentUtil;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;

public class CommentsPageDownTask extends AsyncTask<Comment, Void, Boolean> {
	private static final String TAG = "CommentsPageDownTask";
	private Context context;
	private Weibo microBlog;
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

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)((Activity)context).getApplication();
		microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (divider != null) {
        	divider.setLoading(true);
        }

        if (GlobalVars.NET_TYPE == NetType.NONE) {
        	cancel(true);
        	resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, context);
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
				if(Logger.isDebug()) Log.e(TAG, "Task", e);
				resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
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