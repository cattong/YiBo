package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AccountsActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.widget.AccountSelectorWindow;

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
