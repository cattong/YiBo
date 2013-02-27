package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

public class Link extends BaseEntity {

	private static final long serialVersionUID = -2766785193940216809L;
	/** 链接id */
	private String id;
	/** 所有者 */
	private Profile from;
	/** 链接名 */
	private String name;
	/** 用户添加的文本内容 */
	private String message;
	/** 缩略图 */
	private String picture;
	/** 链接地址 */
	private String link;
	/** 链接说明文字 */
	private String caption;
	/** 简介或描述性信息 */
	private String description;
	/** 链接的icon地址 */
	private String icon;
	/** 创建时间 */
	private Date createdTime;
	/** 评论数量 */
	private long commentsCount;
	/** 赞数量 */
	private long likesCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getUser() {
		return from;
	}

	public void setFrom(Profile from) {
		this.from = from;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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
