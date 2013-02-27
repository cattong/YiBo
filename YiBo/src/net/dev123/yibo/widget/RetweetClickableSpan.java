package net.dev123.yibo.widget;

import net.dev123.mblog.entity.Status;
import net.dev123.yibo.service.adapter.StatusUtil;
import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

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
