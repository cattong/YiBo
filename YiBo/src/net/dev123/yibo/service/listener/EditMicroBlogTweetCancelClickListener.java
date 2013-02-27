package net.dev123.yibo.service.listener;

import net.dev123.yibo.R;
import net.dev123.yibo.widget.TweetProgressDialog;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
