package com.cattong.entity;

import com.cattong.commons.util.HashCodeHelper;


/**
 * 微博用户类
 *
 * @version
 * @author 
 */
public class User extends BaseUser implements java.io.Serializable {
	private static final long serialVersionUID = -6345893237975349030L;

	/** 是否经过认证 */
	private boolean isVerified;
	private String verifyInfo;
	
	/** 收藏数量 */
	private int favouritesCount;
	
	/** 最新一条微博消息 */
	private Status status;

	public String getDisplayName() {
		String displayName = null;
		switch(serviceProvider) {
		case Sina:
			displayName = screenName;
			break;
		case NetEase:
			displayName = screenName;
			break;
		case Sohu:
			displayName = screenName;
			break;
		case Tencent:
			displayName = name;
			break;
		case Twitter:
			displayName = name;
			break;
		case Fanfou:
			displayName = userId;
			break;
		}
		return displayName;
	}
	
	public String getMentionName() {
		String mentionName = null;
		switch(serviceProvider) {
		case Fanfou:
			mentionName = "@" + name;
			break;
		default:
			mentionName = "@" + getDisplayName();
		}
		return mentionName;
	}

	public String getMentionTitleName() {
		String titleName = null;
		switch(serviceProvider) {
		case Sina:
		case Sohu:
		case Fanfou:
			titleName = "@" + screenName;
			break;
		case NetEase:
			titleName = "@" + screenName;
			break;
		case Tencent:
			titleName = screenName + "(@" + name + ")";
			break;
		case Twitter:
			titleName = "@" + name;
			break;
		}
		return titleName;
	}

	public Status getStatus() {
		return status;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public String getVerifyInfo() {
		return verifyInfo;
	}

	public void setVerifyInfo(String verifyInfo) {
		this.verifyInfo = verifyInfo;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
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
		User other = (User) obj;
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
				+ ", location='" + location + '\''
				+ ", description='" + description + '\''
				+ ", profileImageUrl='" + profileImageUrl + '\''
				+ ", followersCount=" + this.getFollowersCount()
				+ ", status=" + (status == null ? "null" : status.getText())
				+ ", friendsCount=" + this.getFriendsCount()
				+ ", createdAt=" + createdAt
				+ ", favouritesCount=" + favouritesCount
				+ ", statusesCount=" + this.getStatusesCount()
				+ ", verified=" + isVerified
				+ '}';
	}

}
