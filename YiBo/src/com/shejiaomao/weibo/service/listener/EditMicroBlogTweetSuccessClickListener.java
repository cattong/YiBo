package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.widget.TweetProgressDialog;

public class EditMicroBlogTweetSuccessClickListener implements OnClickListener {
	private TweetProgressDialog dialog;
	public EditMicroBlogTweetSuccessClickListener(TweetProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	@Override
	public void onClick(View v) {
        dialog.dismiss();
        Activity context = (Activity)v.getContext();
        context.finish();
	}

}
