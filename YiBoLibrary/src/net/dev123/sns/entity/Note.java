package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

public class Note extends BaseEntity {
	private static final long serialVersionUID = -291142291707418660L;
	/** Id */
	private String id;
	/** 作者 */
	private Profile from;
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

	public Profile getFrom() {
		return from;
	}

	public void setFrom(Profile author) {
		this.from = author;
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

}
