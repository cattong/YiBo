package com.cattong.weibo.entity;

import java.io.Serializable;

import com.cattong.entity.BaseSocialEntity;


public class ResponseCount extends BaseSocialEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5821721245226087741L;

	/** 微博消息ID */
	private String statusId;
	/** 评论数量 */
	private int commentCount;
	/** 转发数量 */
	private int retweetCount;

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentsCount) {
		this.commentCount = commentsCount;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

}
