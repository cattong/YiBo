package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.EditCommentActivity;
import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroBlogCommentClickListener implements OnClickListener {
	private Context context;
	private Status status;
	public MicroBlogCommentClickListener(Context context) {
		this.context = context;
	}

	public MicroBlogCommentClickListener(Context context, Status status) {
		this.context = context;
		this.status = status;
	}

	@Override
	public void onClick(View v) {
		if (status == null) {
			return;
		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putInt("TYPE", Constants.EDIT_TYPE_COMMENT);
        bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);

		intent.setClass(v.getContext(), EditCommentActivity.class);
		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_COMMENT_OF_STATUS);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


}
