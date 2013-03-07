package com.shejiaomao.weibo.service.adapter;

import java.util.Date;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.weibo.entity.DirectMessage;

import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalDirectMessage;

public class DirectMessageUtil {

	public static LocalDirectMessage createDividerDirectMessage(List<DirectMessage> messageList, LocalAccount account) {
		if (ListUtil.isEmpty(messageList) || account == null) {
			return null;
		}
		
		DirectMessage message = messageList.get(messageList.size() - 1);
		StringBuffer newId = new StringBuffer(message.getId());
		char c = newId.charAt(newId.length() - 1);
		byte b = (byte)((int)c - 1);
		newId.setCharAt(newId.length() - 1, (char)b);
		
		LocalDirectMessage dividerMessage = new LocalDirectMessage();
		dividerMessage.setId(newId.toString());
		dividerMessage.setAccountId(account.getAccountId());
		dividerMessage.setText("divider");
		dividerMessage.setSenderId(message.getSenderId());
		dividerMessage.setRecipientId(message.getRecipientId());
		dividerMessage.setSenderScreenName(message.getSenderScreenName());
		dividerMessage.setRecipientScreenName(message.getRecipientScreenName());
		dividerMessage.setServiceProvider(account.getServiceProvider());
		Date createdAt = new Date(message.getCreatedAt().getTime() -1);
		dividerMessage.setCreatedAt(createdAt);
		dividerMessage.setDivider(true);

		return dividerMessage;
	}
}
