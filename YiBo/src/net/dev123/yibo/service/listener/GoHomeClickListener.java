package net.dev123.yibo.service.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class GoHomeClickListener implements OnClickListener {

	@Override
	public void onClick(View v) {		
        Intent intent = new Intent();
        intent.setAction("net.dev123.yibo.MAIN");
	    intent.addCategory("android.intent.category.DEFAULT");
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
	    Activity context = (Activity)v.getContext();
	    context.startActivity(intent);
	    context.finish();
	}

}
