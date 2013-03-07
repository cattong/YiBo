package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.GroupActivity;
import com.shejiaomao.weibo.activity.SocialGraphActivity;
import com.shejiaomao.weibo.service.task.SocialGraphTask;

public class ProfileSocialGraphClickListener implements OnClickListener {
    private Activity context;
    private User user;
    private int type;
	public ProfileSocialGraphClickListener(Activity context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		if (type != SocialGraphTask.TYPE_FOLLOWERS &&
			type != SocialGraphTask.TYPE_FRIENDS &&
			type != SocialGraphTask.TYPE_BLOCKS
		) {
			return;
		}
		if (user == null) {
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("SOCIAL_GRAPH_TYPE", type);
		intent.putExtra("USER", user);
		if (type == SocialGraphTask.TYPE_FRIENDS && isCurrentUser(user)) {
			intent.setClass(context, GroupActivity.class);
			intent.putExtra("TAB_TYPE", GroupActivity.TAB_TYPE_ALL);
		} else {
		   intent.setClass(context, SocialGraphActivity.class);
		}
		
		context.startActivity(intent);
	}

	private boolean isCurrentUser(User user) {
		if (user == null) {
			return false;
		}
		
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication) context.getApplication();
		return user.equals(sheJiaoMao.getCurrentAccount().getUser());
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
