package net.dev123.yibo.service.listener;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.service.task.ToggleFavoriteTask;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class MicroBlogFavoriteClickListener implements OnClickListener {
    private Context context;
    private Status status;
	public MicroBlogFavoriteClickListener(Context context) {
	    this.context = context;	
	}
	
	public MicroBlogFavoriteClickListener(Context context, Status status) {
	    this.context = context;	
	    this.status = status;
	}
	
	@Override
	public void onClick(View v) {		
		ToggleFavoriteTask task = null;

	    task = new ToggleFavoriteTask(context, status);
	    task.execute();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
