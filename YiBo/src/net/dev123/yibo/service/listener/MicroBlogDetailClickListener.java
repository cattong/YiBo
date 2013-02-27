package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.MicroBlogActivity;
import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroBlogDetailClickListener implements OnClickListener {
	private Context context;
	private Status status;
	
	public MicroBlogDetailClickListener(Context context) {
		this.context = context;
	}
	
	public MicroBlogDetailClickListener(Context context, Status status) {
		this.context = context;
		this.status = status;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);
		intent.setClass(context, MicroBlogActivity.class);

		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_MY_HOME);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
