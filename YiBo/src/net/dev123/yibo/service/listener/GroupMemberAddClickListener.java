package net.dev123.yibo.service.listener;

import net.dev123.yibo.R;
import net.dev123.yibo.UserQuickSelectorActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.SelectMode;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

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
