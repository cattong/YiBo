package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.service.task.ToggleFavoriteTask;

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
