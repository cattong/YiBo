package net.dev123.mblog.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

/**
 * 评论
 *
 * @version
 * @author 马庆升
 * @time 2010-7-24 上午10:19:45
 */
public class Comment extends BaseEntity {

	/** serialVersionUID */
	private static final long serialVersionUID = -3927416051419615956L;

	/** 创建时间 */
	private Date createdAt;
	/** 评论ID */
	private String id;
	/** 评论内容 */
	private String text;
	/** 评论来源 */
	private String source;
	/** 是否被截断 */
	private boolean isTruncated;
	/** 是否已收藏 */
	private boolean isFavorited;

	/** 评论者信息 */
	private User user;
	/** 被评论的微博消息 */
	private Status inReplyToStatus;
	/** 被评论的评论 */
	private Comment inReplyToComment;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

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

	public Status getInReplyToStatus() {
		return inReplyToStatus;
	}

	public void setInReplyToStatus(Status inReplyToStatus) {
		this.inReplyToStatus = inReplyToStatus;
	}

	public Comment getInReplyToComment() {
		return inReplyToComment;
	}

	public void setInReplyToComment(Comment inReplyToComment) {
		this.inReplyToComment = inReplyToComment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Comment{"
				+ "createdAt=" + createdAt
				+ ", id=" + id
				+ ", text='" + text + '\''
				+ ", source='" + source	+ '\''
				+ ", isTruncated=" + isTruncated
				+ ", isFavorited=" + isFavorited
				+ ", user=" + user
				+ ", status=" + inReplyToStatus
				+ ", comment=" + inReplyToComment
				+ '}';
	}

}
