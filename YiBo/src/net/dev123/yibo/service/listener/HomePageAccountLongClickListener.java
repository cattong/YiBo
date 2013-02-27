package net.dev123.yibo.service.listener;

import net.dev123.yibo.AccountsActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CompatibilityUtil;
import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;

public class HomePageAccountLongClickListener implements OnLongClickListener {

	public HomePageAccountLongClickListener() {
	}

	@Override
	public boolean onLongClick(View v) {
		Intent intent = new Intent();

		Activity context = (Activity)v.getContext();
		intent.setClass(context, AccountsActivity.class);
		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_ACCOUNTS);
		CompatibilityUtil.overridePendingTransition(
			(Activity)context, R.anim.slide_in_left, android.R.anim.fade_out
		);
		return true;
	}

}
