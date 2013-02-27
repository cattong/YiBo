package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.QueryUserTask;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class ProfileRefreshClickListener implements OnClickListener {
    private ProfileChangeListener changeListener;
	public ProfileRefreshClickListener(ProfileChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public void onClick(View v) {
        Activity activity = (Activity)v.getContext();
        YiBoApplication yibo = (YiBoApplication)activity.getApplication();
        LocalAccount currentAccount = yibo.getCurrentAccount();
        User user = (User) currentAccount.getUser();
        if (user != null) {
        	QueryUserTask queryUserTask = new QueryUserTask(activity, user, changeListener);
        	queryUserTask.execute();
        }
	}

}
