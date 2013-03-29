package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.User;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.service.task.ToggleBlockTask;

public class ProfileBlockClickListener implements OnClickListener {
    private User user;
    public ProfileBlockClickListener(User user) {
    	this.user = user;
    }
    
	@Override
	public void onClick(View v) {
		final Context context = v.getContext();
		String msg = null;
		//if (user.isBlocking()) {
		//	msg = context.getString(R.string.msg_personal_unblock);
		//} else {
			msg = context.getString(R.string.msg_personal_block);
		//}
		new AlertDialog.Builder(context)
	        .setTitle(R.string.title_dialog_alert)
	        .setMessage(msg)
	        .setNegativeButton(R.string.btn_cancel,
			    new AlertDialog.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    }
			    })
		    .setPositiveButton(R.string.btn_confirm, 
			    new AlertDialog.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        	new ToggleBlockTask(context, user).execute();
			    }
		    }).show();		
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
