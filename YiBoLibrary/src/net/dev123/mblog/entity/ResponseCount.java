package net.dev123.mblog.entity;

import java.io.Serializable;

import net.dev123.entity.BaseEntity;

public class ResponseCount extends BaseEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -5821721245226087741L;

	/** 微博消息ID */
	private String statusId;
	/** 评论数量 */
	private int commentsCount;
	/** 转发数量 */
	private int retweetCount;

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

}
