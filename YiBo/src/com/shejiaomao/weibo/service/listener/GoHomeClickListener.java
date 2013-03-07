package com.shejiaomao.weibo.service.listener;

import com.shejiaomao.weibo.common.Constants;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class GoHomeClickListener implements OnClickListener {

	@Override
	public void onClick(View v) {		
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_WEIBO_MAIN);
	    intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
	    Activity context = (Activity)v.getContext();
	    context.startActivity(intent);
	    context.finish();
	}

}
