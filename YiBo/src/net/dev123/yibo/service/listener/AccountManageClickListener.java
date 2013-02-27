package net.dev123.yibo.service.listener;

import net.dev123.yibo.AccountsActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.CompatibilityUtil;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.widget.AccountSelectorWindow;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class AccountManageClickListener implements OnClickListener {
	private AccountSelectorWindow selectorWindow;
	public AccountManageClickListener(AccountSelectorWindow selectorWindow) {
		this.selectorWindow = selectorWindow;
	}

	public AccountManageClickListener() {
	}
	
	@Override
	public void onClick(View v) {
		if (selectorWindow != null) {
		    selectorWindow.dismiss();
		}
		
		Activity context = (Activity)v.getContext();
		Intent intent = new Intent();
		intent.setClass(context, AccountsActivity.class);
		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_ACCOUNTS);
		CompatibilityUtil.overridePendingTransition(
			(Activity)context, R.anim.slide_in_left, android.R.anim.fade_out
		);
	}

}
