package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.QueryUserTask;

public class ProfileRefreshClickListener implements OnClickListener {
    private ProfileChangeListener changeListener;
	public ProfileRefreshClickListener(ProfileChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public void onClick(View v) {
        Activity activity = (Activity)v.getContext();
        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)activity.getApplication();
        LocalAccount currentAccount = sheJiaoMao.getCurrentAccount();
        User user = (User) currentAccount.getUser();
        if (user != null) {
        	QueryUserTask queryUserTask = new QueryUserTask(activity, user, changeListener);
        	queryUserTask.execute();
        }
	}

}
