package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.mblog.entity.Status;
import net.dev123.yibo.service.adapter.StatusUtil;
import net.dev123.yibo.service.task.ImageLoad4BigTask;
import android.app.Activity;
import android.view.View;

public 	class ImageClickListener implements View.OnClickListener {
	private Status status;
	public ImageClickListener(Status status) {
		this.status = status;
	}
	
	@Override
	public void onClick(View v) {
		String imageUrl = StatusUtil.getBigImageUrl(status);
		if (StringUtil.isEmpty(imageUrl)) {
			return;
		}
		
		ImageLoad4BigTask task = new ImageLoad4BigTask((Activity)v.getContext(), imageUrl);
		task.execute();
	}
}
