package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class Comment extends BaseSocialEntity {
	private static final long serialVersionUID = -7433820211193332608L;
	
	/** 评论ID */
	private String commentId;
	/** 评论内容 */
	private String text;
	/** 评论来源 */
	private String source;
	/** 是否被截断 */
	private boolean isTruncated;
	/** 是否已收藏 */
	private boolean isFavorited;

	/** 创建时间 */
	private Date createdAt;
	/** 评论者信息 */
	private User user;
	/** 被评论的微博消息 */
	private Status replyToStatus;
	/** 被评论的评论 */
	private Comment replyToComment;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Status getReplyToStatus() {
		return replyToStatus;
	}

	public void setReplyToStatus(Status replyToStatus) {
		this.replyToStatus = replyToStatus;
	}

	public Comment getReplyToComment() {
		return replyToComment;
	}

	public void setReplyToComment(Comment replyToComment) {
		this.replyToComment = replyToComment;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(commentId).appendInt(serviceProvider.getSpNo()).appendObj(createdAt);
		return helper.getHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Comment))
			return false;
		Comment other = (Comment) obj;
		if (commentId == null) {
			if (other.commentId != null)
				return false;
		} else if (!commentId.equals(other.commentId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Comment{"
				+ "createdAt=" + createdAt
				+ ", id=" + commentId
				+ ", text='" + text + '\''
				+ ", source='" + source	+ '\''
				+ ", isTruncated=" + isTruncated
				+ ", isFavorited=" + isFavorited
				+ ", user=" + user
				+ ", status=" + replyToStatus
				+ ", comment=" + replyToComment
				+ '}';
	}

}
