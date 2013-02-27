package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

public class Comment extends BaseEntity {
	private static final long serialVersionUID = 7081992479043201567L;
	/** 评论Id */
	private String id;
	/** 被评论对象Id */
	private String objectId;
	/** 评论作者 */
	private Profile from;
	/** 评论内容 */
	private String text;
	/** 创建时间 */
	private Date createdTime;
	/** 赞数量 */
	private long likesCount;

	/** 被回复的评论Id */
	private String inReplyToCommentId;
	/** 被回复的评论作者Id */
	private String inReplyToUserId;
	/** 被回复的评论作者名 */
	private String inReplyToUserName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getFrom() {
		return from;
	}

	public void setFrom(Profile from) {
		this.from = from;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getInReplyToCommentId() {
		return inReplyToCommentId;
	}

	public void setInReplyToCommentId(String inReplyToCommentId) {
		this.inReplyToCommentId = inReplyToCommentId;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(String inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getInReplyToUserName() {
		return inReplyToUserName;
	}

	public void setInReplyToUserName(String inReplyToUserName) {
		this.inReplyToUserName = inReplyToUserName;
	}

}
