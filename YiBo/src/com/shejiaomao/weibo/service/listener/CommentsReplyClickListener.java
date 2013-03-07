package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cattong.entity.Comment;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.common.Constants;

public class CommentsReplyClickListener implements OnClickListener {
	private Context context;
	private Comment comment;
	
	public CommentsReplyClickListener(Context context) {
		this.context = context;
	}
	
	public CommentsReplyClickListener(Context context, Comment comment) {
		this.context = context;
		this.comment = comment;
	}
	
	@Override
	public void onClick(View v) {
		if (comment == null) {
			return;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		
		bundle.putInt("TYPE", Constants.EDIT_TYPE_RECOMMENT);
        bundle.putSerializable("COMMENT", comment);
		intent.putExtras(bundle);

		intent.setClass(v.getContext(), EditCommentActivity.class);
		((Activity)context).startActivity(intent);

	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

}
