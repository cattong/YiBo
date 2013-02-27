package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.R;
import net.dev123.yibo.service.task.DestroyStatusTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

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
