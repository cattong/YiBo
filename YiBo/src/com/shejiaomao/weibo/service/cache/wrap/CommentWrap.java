package com.shejiaomao.weibo.service.cache.wrap;

import java.util.Date;

import com.cattong.entity.Comment;

public class CommentWrap extends Wrap<Comment> {

	private Comment comment;	
	private Date readedTime;
	private boolean isReaded;
	
	public CommentWrap(Comment comment) {
		this.comment = comment;
		
		setLocalCached(false);
		isReaded = false;
	}
	
	@Override
	public Comment getWrap() {		
		return comment;
	}

	@Override
	public void setWrap(Comment t) {
		this.comment = t;
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
