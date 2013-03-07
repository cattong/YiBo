package com.shejiaomao.weibo.widget;

import com.cattong.entity.Status;
import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

import com.shejiaomao.weibo.service.adapter.StatusUtil;

public class RetweetClickableSpan extends ClickableSpan {
    private Status status;
	public RetweetClickableSpan(Status status) {
		this.status = status;
	}
	
	@Override
	public void onClick(View widget) {
		Context context = widget.getContext();
		StatusUtil.retweet(context, status);
	}
}
