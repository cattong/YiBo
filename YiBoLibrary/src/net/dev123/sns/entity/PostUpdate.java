package net.dev123.sns.entity;

import java.util.List;

import net.dev123.entity.BaseEntity;

public class PostUpdate extends BaseEntity {
	private static final long serialVersionUID = -894362915358268142L;
	/** 用户发布的文字内容 */
	private String message;
	/** 一般为照片或视频的缩略图 */
	private String picture;
	/** 链接 */
	private String link;
	/** 链接名 */
	private String linkName;
	/** 链接说明文字，出现在链接下方 */
	private String linkCaption;
	/** 链接简介或描述性信息 */
	private String linkDescription;
	/** 视频源链接地址，照片原图片地址 */
	private String sourceLink;
	/** 可见性 */
	private Privacy privacy;
	/** 可进行的操作 */
	private List<Post.Action> actions;

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

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkCaption() {
		return linkCaption;
	}

	public void setLinkCaption(String linkCaption) {
		this.linkCaption = linkCaption;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String source) {
		this.sourceLink = source;
	}
	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

	public List<Post.Action> getActions() {
		return actions;
	}

	public void setActions(List<Post.Action> actions) {
		this.actions = actions;
	}

}