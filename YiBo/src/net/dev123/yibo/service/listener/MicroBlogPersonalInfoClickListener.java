package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroBlogPersonalInfoClickListener implements OnClickListener {
	private Context context;
	private User user;
	
	public MicroBlogPersonalInfoClickListener(Context context) {
		this.context = context;
	}
	
	public MicroBlogPersonalInfoClickListener(Context context, User user) {
		this.context = context;
		this.user = user;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
        
		bundle.putSerializable("USER", user);
		intent.putExtras(bundle);

		intent.setClass(context, ProfileActivity.class);
		((Activity)context).startActivity(intent);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
