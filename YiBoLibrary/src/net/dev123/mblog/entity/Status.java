package net.dev123.mblog.entity;

import java.util.Arrays;
import java.util.Date;

import net.dev123.entity.BaseEntity;
import net.dev123.entity.GeoLocation;

/**
 * 微博信息内容类
 *
 * @version
 * @author 马庆升
 * @time 2010-7-23 下午04:49:16
 */
public class Status extends BaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = -6511327781776085472L;
	/** 创建时间 */
	private Date createdAt;
	/** 微博信息ID */
	private String id;
	/** 微博信息内容 */
	private String text;
	/** 微博信息来源 */
	private String source;
	/** 是否已收藏 */
	private boolean isFavorited;
	/** 是否被截断 */
	private boolean isTruncated;

	/** 被回复微博消息ID */
	private String inReplyToStatusId;
	/** 被回复人ID */
	private String inReplyToUserId;
	/** 被回复人昵称 */
	private String inReplyToScreenName;

	/** 转发的微博信息 */
	private Status retweetedStatus;
	/** 作者信息 */
	private User user = null;

	/** 原图 */
	private String originalPicture;
	/** 中型图片 */
	private String middlePicture;
	/** 缩略图 */
	private String thumbnailPicture;

	private GeoLocation geoLocation = null;

	private String[] contributors;

	/** 转发数 */
	private Integer retweetCount;
	/** 评论数 */
	private Integer commentCount;


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

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(String inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String[] getContributors() {
		return contributors;
	}

	public void setContributors(String[] contributors) {
		this.contributors = contributors;
	}

	public Status getRetweetedStatus() {
		return retweetedStatus;
	}

	public void setRetweetedStatus(Status retweetedStatus) {
		this.retweetedStatus = retweetedStatus;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOriginalPicture() {
		return originalPicture;
	}

	public void setOriginalPicture(String originalPicture) {
		this.originalPicture = originalPicture;
	}

	public String getMiddlePicture() {
		return middlePicture;
	}

	public void setMiddlePicture(String middlePicture) {
		this.middlePicture = middlePicture;
	}

	public String getThumbnailPicture() {
		return thumbnailPicture;
	}

	public void setThumbnailPicture(String thumbnailPicture) {
		this.thumbnailPicture = thumbnailPicture;
	}

	@Override
	public String toString() {
		return "Status{"
				+ " sp=" + serviceProvider
				+ ", id=" + id
				+ ", createdAt=" + createdAt
				+ ", text='" + text + '\''
				+ ", source='" + source	+ '\''
				+ ", isTruncated=" + isTruncated
				+ ", inReplyToStatusId=" + inReplyToStatusId
				+ ", inReplyToUserId=" + inReplyToUserId
				+ ", isFavorited=" + isFavorited
				+ ", inReplyToScreenName='"	+ inReplyToScreenName + '\''
				+ ", geoLocation=" + geoLocation
				+ ", contributors="	+ (contributors == null ? null : Arrays.asList(contributors))
				+ ", retweetedStatus=" + retweetedStatus
				+ ", user=" + user
				+ '}';
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
		if (!(obj instanceof Status)) {
			return false;
		}
		Status other = (Status) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

}
