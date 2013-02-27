package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.service.adapter.StatusUtil;
import android.content.Context;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroBlogRetweetClickListener implements OnClickListener, OnMenuItemClickListener {
	private Context context;
	private Status status;

	public MicroBlogRetweetClickListener(Context context) {
		this.context = context;
	}

	public MicroBlogRetweetClickListener(Context context, Status status) {
		this.context = context;
		this.status = status;
	}

	@Override
	public void onClick(View v) {
		StatusUtil.retweet(context, status);
	}
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		StatusUtil.retweet(context, status);
		return false;
	}

}
