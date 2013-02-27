package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.GroupActivity;
import net.dev123.yibo.SocialGraphActivity;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.service.task.SocialGraphTask;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

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
		
		YiBoApplication yibo = (YiBoApplication) context.getApplication();
		return user.equals(yibo.getCurrentAccount().getUser());
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
