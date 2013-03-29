package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.os.AsyncTask;
import android.widget.Button;

import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.GlobalVars;

public class BlockingCheckTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "BlockingCheckTask";

	private Weibo microBlog;
	private SheJiaoMaoApplication sheJiaoMao;
	private ProfileActivity context;
	private User user;
	private boolean isBlocking;

	public BlockingCheckTask(ProfileActivity context) {
		this.context = context;
		this.user = context.getUser();
		this.sheJiaoMao = (SheJiaoMaoApplication) context.getApplication();
		this.microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
	}

	@Override
	protected void onPreExecute() {
		Button btnBlock = (Button) context.findViewById(R.id.btnBlock);
		btnBlock.setEnabled(false);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (microBlog == null) {
			return Boolean.FALSE;
		}

		//try {
			//isBlocking = microBlog.existsBlock(user.getUserId());
			//user.setBlocking(isBlocking);
		//} catch (LibException e) {
		//	if (Constants.DEBUG) {
		//		Log.e(TAG, "Task", e);
		//	}
		//}

		return isBlocking;
	}

	@Override
	protected void onPostExecute(Boolean isBlocking) {

	}
}
