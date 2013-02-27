package net.dev123.yibo.service.listener;

import net.dev123.yibo.widget.TweetProgressDialog;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

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
