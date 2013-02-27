package net.dev123.yibo.service.listener;

import net.dev123.yibo.service.task.UpdateStatusToMutiAccountsTask;
import net.dev123.yibo.widget.TweetProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;

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
