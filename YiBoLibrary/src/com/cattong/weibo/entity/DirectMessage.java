package com.cattong.weibo.entity;

import java.util.Date;

import com.cattong.entity.BaseSocialEntity;
import com.cattong.entity.User;


/**
 * 私信
 *
 * @version
 * @author cattong.com
 * @time 2010-7-24 上午09:51:41
 */
public class DirectMessage extends BaseSocialEntity implements java.io.Serializable {

	/** 私信ID */
	private String id;
	/** 私信内容 */
	private String text;
	/** 发送人用户ID */
	private String senderId;
	/** 接收人用户ID */
	private String recipientId;
	/** 发送时间 */
	private Date createdAt;
	/** 发送人昵称 */
	private String senderScreenName;
	/** 接收人昵称 */
	private String recipientScreenName;
	/** 发送人信息 */
	private User sender;
	/** 接收人信息 */
	private User recipient;

	private static final long serialVersionUID = -3253021825891789737L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSenderScreenName() {
		return senderScreenName;
	}

	public void setSenderScreenName(String senderScreenName) {
		this.senderScreenName = senderScreenName;
	}

	public String getRecipientScreenName() {
		return recipientScreenName;
	}

	public void setRecipientScreenName(String recipientScreenName) {
		this.recipientScreenName = recipientScreenName;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof DirectMessage))
			return false;
		DirectMessage other = (DirectMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DirectMessage{"
				+ "id=" + id
				+ ", text='" + text + '\''
				+ ", sender_id=" + senderId
				+ ", recipient_id="	+ recipientId
				+ ", created_at=" + createdAt
				+ ", sender_screen_name='" 	+ senderScreenName + '\''
				+ ", recipient_screen_name='" + recipientScreenName + '\''
				+ ", sender=" + sender
				+ ", recipient=" + recipient
				+ '}';
	}

}
