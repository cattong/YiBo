package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.ServiceProvider;
import com.cattong.weibo.entity.DirectMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.weibo.activity.EditDirectMessageActivity;
import com.shejiaomao.weibo.common.Constants;

public class DirectMessagesReplyClickListener implements OnClickListener {
	private Context context;
	private DirectMessage message;

	public DirectMessagesReplyClickListener(Context context) {
		this.context = context;
	}

	public DirectMessagesReplyClickListener(Context context, DirectMessage message) {
		this.context = context;
		this.message = message;
	}

	@Override
	public void onClick(View v) {
		if (message == null) {
			return;
		}

		boolean isInbox = false;
//	    Button btnInbox = (Button)((Activity)context).findViewById(R.id.btnInbox);
//		if (!btnInbox.isEnabled()) {
//			isInbox = true;
//		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		String receiverName = null;
		if (message.getServiceProvider() == ServiceProvider.Tencent){
			if (isInbox){
				receiverName = message.getSender().getName();
			} else {
				receiverName = message.getRecipient().getName();
			}
		} else {
			if (isInbox){
				receiverName = message.getSenderScreenName();
			} else {
				receiverName = message.getRecipientScreenName();
			}
		}

		bundle.putString("SCREEN_NAME", receiverName);

		bundle.putInt("TYPE", Constants.EDIT_TYPE_REMESSAGE);
		intent.putExtras(bundle);

		intent.setClass(v.getContext(), EditDirectMessageActivity.class);
		((Activity)context).startActivity(intent);

	}

	public DirectMessage getMessage() {
		return message;
	}

	public void setMessage(DirectMessage message) {
		this.message = message;
	}

}
