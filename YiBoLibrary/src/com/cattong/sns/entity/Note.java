package com.cattong.sns.entity;

import java.util.Date;

import com.cattong.entity.BaseSocialEntity;


public class Note extends BaseSocialEntity {
	private static final long serialVersionUID = -1886527670111560488L;
	
	/** Id */
	private String id;
	/** 作者 */
	private String userId;
	/** 标题 */
	private String subject;
	/** 内容 */
	private String content;
	/** 评论数 */
	private long commentsCount;
	/** 喜欢数 */
	private long likesCount;
	/** 创建时间 */
	private Date createdTime;
	/** 更新时间 */
	private Date updatedTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
