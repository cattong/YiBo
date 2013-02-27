package net.dev123.yibo.service.listener;

import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.common.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class HomePageEditStatusClickListener implements OnClickListener {
    private Context context;
    
	public HomePageEditStatusClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		
		bundle.putInt("TYPE", Constants.EDIT_TYPE_TWEET);
		intent.putExtras(bundle);

		intent.setClass(context, EditMicroBlogActivity.class);
		((Activity)context).startActivity(intent);
	}

}
