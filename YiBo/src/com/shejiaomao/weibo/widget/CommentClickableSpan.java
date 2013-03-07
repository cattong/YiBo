package com.shejiaomao.weibo.widget;

import com.cattong.entity.Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;

import com.shejiaomao.weibo.activity.MicroBlogActivity;

public class CommentClickableSpan extends ClickableSpan {
    private Status status;
	public CommentClickableSpan(Status status) {
		this.status = status;
	}
	@Override
	public void onClick(View widget) {
		if (status == null) {
			return;
		}

		Context context = widget.getContext();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
//
//		bundle.putInt("TYPE", Constants.EDIT_TYPE_COMMENT);
//        bundle.putSerializable("STATUS", status);
//		intent.putExtras(bundle);
//
//		intent.setClass(context, EditCommentActivity.class);
		
        bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);

		intent.setClass(context, MicroBlogActivity.class);
		((Activity)context).startActivity(intent);
	}

}
