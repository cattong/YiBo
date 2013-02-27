package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.service.task.RelationshipActionTask;
import android.view.View;
import android.view.View.OnClickListener;

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
