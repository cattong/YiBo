package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class BaseUser extends BaseSocialEntity {
	private static final long serialVersionUID = 6924492479904061650L;
	
	/** 用户ID */
	protected String userId;
	/** 用户名 */
	protected String name;
	/** 显示名称 */
	protected String screenName;
	/** 头像图片地址 */
	protected String profileImageUrl;
	/** 性别 */
	protected Gender gender;
	
	/** 好友数或关注数 */
	private int friendsCount;
	/** 被关注数量或粉丝数 */
	private int followersCount;
	/** 签名或微博数 */
	private int statusesCount;
	
	/** 简单描述 */
	protected String description;
	/** 当前所在地 */
	protected String location;
	/** 是否经过认证 */
	protected boolean isVerified;

	protected Date createdAt;

    /** 用户关系*/
	private Relationship relationship;

	public String getDisplayName() {
		return screenName;
	}
	
	public String getMentionName() {
		return "@" + screenName;
	}

	public String getMentionTitleName() {
		return "@" + screenName;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getFriendsCount() {
		return friendsCount;
	}
	
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	
	public int getFollowersCount() {
		return followersCount;
	}
	
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	
	public int getStatusesCount() {
		return statusesCount;
	}
	
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public Relationship getRelationship() {
		return relationship;
	}

	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(userId).appendObj(serviceProvider)
		.appendObj(name).appendObj(screenName).appendBoolean(isVerified);
		return helper.getHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseUser other = (BaseUser) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User{"
				+ " sp=" + serviceProvider
				+ ", userId=" + userId
				+ ", name='" + name + '\''
				+ ", screenName='" + screenName + '\''
				+ ", description='" + description + '\''
				+ ", location='" + location + '\''
				+ ", profileImageUrl='" + profileImageUrl + '\''
				+ ", verified=" + isVerified
				+ '}';
	}

}
