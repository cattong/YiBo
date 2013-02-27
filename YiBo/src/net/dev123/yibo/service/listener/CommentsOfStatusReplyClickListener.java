package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Comment;
import net.dev123.yibo.EditCommentActivity;
import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

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
