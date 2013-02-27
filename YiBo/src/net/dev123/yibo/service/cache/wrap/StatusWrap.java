package net.dev123.yibo.service.cache.wrap;

import java.util.Date;

import net.dev123.mblog.entity.Status;

public class StatusWrap extends Wrap<Status> {

	private Status status;	
	private Date readedTime;
	private boolean isReaded;
	
	public StatusWrap(Status status) {
		this.status = status;
		
		setLocalCached(false);
		isReaded = false;
	}
	
	@Override
	public Status getWrap() {
		return status;
	}

	@Override
	public void setWrap(Status t) {
        this.status = t;
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

}
