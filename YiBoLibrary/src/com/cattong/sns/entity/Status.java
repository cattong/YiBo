package com.cattong.sns.entity;

import java.util.Date;

import com.cattong.entity.BaseSocialEntity;


public class Status extends BaseSocialEntity {
	private static final long serialVersionUID = 3214366636579090783L;
	
	/** 状态消息ID */
	private String id;
	/** 状态信息内容 */
	private String text;
	/** 作者信息 */
	private String userId;
	/** 来源 */
	private String source;
	/** 评论数量 */
	private long commentsCount;
	/** 赞数量 */
	private long likesCount;
	/** 更新时间 */
	private Date updatedTime;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
