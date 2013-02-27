package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

public class Status extends BaseEntity {

	private static final long serialVersionUID = 2674848187824898426L;

	/** 状态消息ID */
	private String id;
	/** 状态信息内容 */
	private String text;
	/** 作者信息 */
	private Profile from;
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

	public Profile getFrom() {
		return from;
	}

	public void setFrom(Profile from) {
		this.from = from;
	}

}
