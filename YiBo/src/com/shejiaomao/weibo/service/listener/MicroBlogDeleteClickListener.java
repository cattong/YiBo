package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.service.task.DestroyStatusTask;

public class MicroBlogDeleteClickListener implements OnClickListener {
    private Context context;
    private Status status;
	public MicroBlogDeleteClickListener(Context context, Status status) {
	    this.context = context;	
	    this.status = status;
	}
	
	@Override
	public void onClick(View v) {
		
		new AlertDialog.Builder(context)
		    .setTitle(R.string.title_dialog_alert)
		    .setMessage(R.string.msg_blog_delete)
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
			        	DestroyStatusTask task = new DestroyStatusTask(context, status);
						task.execute();
			    }
		    }).show();

	}

}
