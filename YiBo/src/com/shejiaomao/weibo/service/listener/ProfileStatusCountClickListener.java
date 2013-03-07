package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.activity.MyStatusesActivity;
import com.shejiaomao.weibo.common.Constants;

public class ProfileStatusCountClickListener implements OnClickListener {
	private Activity context;
	private User user;
	public ProfileStatusCountClickListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		if (user == null) {
			return;
		}
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("USER", user);
		intent.putExtras(bundle);
		intent.setClass(context, MyStatusesActivity.class);

		context.startActivityForResult(intent, Constants.REQUEST_CODE_MY_HOME);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
