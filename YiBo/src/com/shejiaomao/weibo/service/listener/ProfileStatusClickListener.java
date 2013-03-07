package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.Constants;

public class ProfileStatusClickListener implements OnClickListener {
	private Activity context;
	private Status status;
	public ProfileStatusClickListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("STATUS", status);
		intent.putExtras(bundle);
		intent.setClass(context, MicroBlogActivity.class);

		context.startActivityForResult(intent, Constants.REQUEST_CODE_MY_HOME);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
