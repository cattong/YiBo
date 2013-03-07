package com.shejiaomao.weibo.service.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.task.UpdateStatusToMutiAccountsTask;
import com.shejiaomao.weibo.widget.TweetProgressDialog;

public class EditMicroBlogTweetRetryClickListener implements OnClickListener {
	private UpdateStatusToMutiAccountsTask task;
	private TweetProgressDialog dialog;
	public EditMicroBlogTweetRetryClickListener(UpdateStatusToMutiAccountsTask task) {
		this.task = task;
		this.dialog = task.getDialog();
	}
	
	@Override
	public void onClick(View v) {
		if (task != null) {
            task.execute();
		}
        if (dialog != null) {
            dialog.setPositiveClickListener(null);	
        }
	}

}
