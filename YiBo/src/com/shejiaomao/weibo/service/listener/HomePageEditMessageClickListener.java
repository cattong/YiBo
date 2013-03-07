package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.activity.EditDirectMessageActivity;
import com.shejiaomao.weibo.common.Constants;

public class HomePageEditMessageClickListener implements OnClickListener {
    private Context context;
    
	public HomePageEditMessageClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();		
		bundle.putInt("TYPE", Constants.EDIT_TYPE_MESSAGE);
		bundle.putBoolean("IS_INBOX", false);
		intent.putExtras(bundle);

		intent.setClass(context, EditDirectMessageActivity.class);
		((Activity)context).startActivity(intent);
	}

}
