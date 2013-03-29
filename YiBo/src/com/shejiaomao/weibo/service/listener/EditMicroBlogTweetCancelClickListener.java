package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.widget.TweetProgressDialog;

public class EditMicroBlogTweetCancelClickListener implements OnClickListener {
	private AsyncTask task;
	private TweetProgressDialog dialog;
	public EditMicroBlogTweetCancelClickListener(AsyncTask task, TweetProgressDialog dialog) {
		this.task = task;
		this.dialog = dialog;
	}
	
	@Override
	public void onClick(View v) {
		if (task != null) {
		    task.cancel(true);
		}
		if (dialog != null) {
		    dialog.dismiss();
		}
		Activity context = (Activity)v.getContext(); 
		Button btnSend = (Button)context.findViewById(R.id.btnOperate);
		if (btnSend != null) {
			btnSend.setEnabled(true);
		}
	}

}
