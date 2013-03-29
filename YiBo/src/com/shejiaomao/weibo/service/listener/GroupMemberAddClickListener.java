package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.UserQuickSelectorActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SelectMode;

public class GroupMemberAddClickListener implements OnClickListener {

	public GroupMemberAddClickListener() {
		
	}
	
	@Override
	public void onClick(View v) {
		Activity context = (Activity)v.getContext();

		Intent intent = new Intent();
		intent.putExtra("SELECT_MODE", SelectMode.Multiple.toString());
		intent.setClass(context, UserQuickSelectorActivity.class);
		intent.putExtra("TITLE_ID", R.string.title_group_member_add);
		context.startActivityForResult(intent, Constants.REQUEST_CODE_USER_SELECTOR);
	}

}
