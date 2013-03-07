package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Status;
import android.app.Activity;
import android.view.View;

import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.task.ImageLoad4BigTask;

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
