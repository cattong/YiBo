package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.task.RelationshipActionTask;

public class ProfileFollowClickListener implements OnClickListener {
	private User targetUser;

	public ProfileFollowClickListener(User targetUser) {
		this.targetUser = targetUser;
	}

	@Override
	public void onClick(View v) {
		if (targetUser == null) {
			return;
		}
		new RelationshipActionTask(v, targetUser).execute();
	}

	public User getUser() {
		return targetUser;
	}

	public void setUser(User user) {
		this.targetUser = user;
	}

}
