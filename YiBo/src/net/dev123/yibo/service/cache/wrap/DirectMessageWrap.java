package net.dev123.yibo.service.cache.wrap;

import java.util.Date;

import net.dev123.mblog.entity.DirectMessage;

public class DirectMessageWrap extends Wrap<DirectMessage> {

	private DirectMessage message;
	private int coversationCount;
	private Date readedTime;
	private boolean isReaded;	
	public DirectMessageWrap(DirectMessage message) {
		this.message = message;
		this.setLocalCached(false);
		isReaded = false;
		coversationCount = 1;
	}
	
	@Override
	public DirectMessage getWrap() {		
		return message;
	}

	@Override
	public void setWrap(DirectMessage t) {
		message = t;		
	}

	public Date getReadedTime() {
		return readedTime;
	}

	public void setReadedTime(Date readedTime) {
		this.readedTime = readedTime;
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public int getCoversationCount() {
		return coversationCount;
	}

	public void setCoversationCount(int coversationCount) {
		this.coversationCount = coversationCount;
	}

}
