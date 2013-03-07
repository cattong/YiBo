package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.cattong.entity.Comment;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.common.Constants;

public class CommentsOfStatusReplyClickListener implements OnClickListener {
    private Comment comment;
    
	@Override
	public void onClick(View v) {
		if (comment == null) {
			return;
		}
		Activity context = (Activity)v.getContext();
		Intent intent = new Intent();
    	intent.setClass(context, EditCommentActivity.class);
    	intent.putExtra("TYPE", Constants.EDIT_TYPE_RECOMMENT);
    	intent.putExtra("COMMENT", comment);
    	context.startActivityForResult(intent, Constants.REQUEST_CODE_COMMENT_OF_STATUS);
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

}
