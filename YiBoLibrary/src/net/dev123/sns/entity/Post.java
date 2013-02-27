package net.dev123.sns.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dev123.entity.BaseEntity;

public class Post extends BaseEntity {

	private static final long serialVersionUID = -5690552820267012278L;

	public static enum PostType {
		POST, CHECKIN, LINK, NOTE, PHOTO, ALBUM, STATUS, VIDEO, SWF, MUSIC
	}

	/** id */
	private String id;
	/** 来自 */
	private Profile from;
	/** 接收者 */
	private List<Profile> to;
	/** 内容的最初发布者 */
	private Profile owner;
	/** 用户发布的文字内容 */
	private String message;
	/** 系统根据用户动作填充的描述性文字内容 */
	private String story;
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
	/** 视频或照片等媒体对象id */
	private String objectId;
	/** 视频源链接地址，照片原图片地址 */
	private String sourceLink;
	/** 其他属性 */
	private Map<String, Object> properties = new HashMap<String, Object>();
	/** Post类型 */
	private PostType postType;
	/** icon */
	private String icon;
	/** 来源应用 */
	private Application application;
	/** 可见性 */
	private Privacy privacy;
	/** 赞数量 */
	private long likesCount;
	/** 评论数量 */
	private long commentsCount;
	/** 创建时间 */
	private Date createdTime;
	/** 更新时间 */
	private Date updatedTime;
	/** 可进行的操作 */
	private List<Action> actions;

	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public static class Action {

		private String name;

		private String link;

		public String getName() {
			return name;
		}

		public String getLink() {
			return link;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setLink(String link) {
			this.link = link;
		}

	}

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

	public List<Profile> getTo() {
		return to;
	}

	public void setTo(List<Profile> to) {
		this.to = to;
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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String source) {
		this.sourceLink = source;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, ? extends Object> properties) {
		if (properties == null) {
			return;
		}
		this.properties.putAll(properties);
	}

	public PostType getPostType() {
		return postType;
	}

	public void setPostType(PostType postType) {
		this.postType = postType;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
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

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Profile getOwner() {
		if (owner == null) {
			return from;
		}
		return owner;
	}

	public void setOwner(Profile owner) {
		this.owner = owner;
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
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Post other = (Post) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", from=" + from + ", to=" + to
				+ ", message=" + message + ", story=" + story + ", picture="
				+ picture + ", link=" + link + ", linkName=" + linkName
				+ ", linkCaption=" + linkCaption + ", linkDescription="
				+ linkDescription + ", objectId=" + objectId + ", source="
				+ sourceLink + ", properties=" + properties + ", postType="
				+ postType + ", icon=" + icon + ", application=" + application
				+ ", privacy=" + privacy + ", likesCount=" + likesCount
				+ ", commentsCount=" + commentsCount + ", createdTime="
				+ createdTime + ", updatedTime=" + updatedTime + ", actions="
				+ actions + ", serviceProvider=" + serviceProvider + "]";
	}

}