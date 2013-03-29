package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.AccountsActivity;
import com.shejiaomao.weibo.common.Constants;

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
