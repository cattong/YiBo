package net.dev123.mblog.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import net.dev123.entity.BaseUser;
import net.dev123.entity.Gender;

/**
 * 微博用户类
 *
 * @version
 * @author 马庆升
 * @time 2010-7-23 下午04:54:02
 */
public class User extends BaseUser implements java.io.Serializable {

	private static final long serialVersionUID = -6345893237975349030L;

	/** 用户微博地址 */
	private String url;

	/** 跟随者数量，即粉丝数 */
	private int followersCount;

	/** 最新一条微博消息 */
	private Status status;

	/** 跟随的数量，即关注数 */
	private int friendsCount;
	/** 创建时间 */
	private Date createdAt;
	/** 收藏数量 */
	private int favouritesCount;

	/** 性别 */
	private Gender gender;

	/** 微博数 */
	private int statusesCount;
	private boolean isGeoEnabled;
	/** 是否经过认证 */
	private boolean isVerified;
	/** 是否正关注 */
	private boolean isFollowing;
	/** 是否正在黑名单中 */
	private boolean isBlocking;
	/** 是否被该用户关注 */
	private boolean isFollowedBy;
	/** 是否已检查过同该用户的关系 */
	private boolean isRelationChecked;

	private boolean isProtected;
	private boolean isContributorsEnabled;

	private int utcOffset;
	private String timeZone;
	private String lang;

	private String profileBackgroundImageUrl;
	private boolean profileBackgroundTiled;

	private String profileBackgroundColor;
	private String profileTextColor;
	private String profileLinkColor;
	private String profileSidebarFillColor;
	private String profileSidebarBorderColor;

	public String getGlobalName() {
		return screenName + "@" + serviceProvider.getServiceProviderName();
	}

	public String getProfileName() {
		String profileName = null;
		switch(serviceProvider) {
		case Sina:
		case Sohu:
		case NetEase:
		case Fanfou:
			profileName = screenName;
			break;
		case Tencent:
			profileName = screenName + "(@" + name + ")";
			break;
		case Twitter:
			profileName = screenName + "(@" + name + ")";
			break;
		}
		return profileName;
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
			displayName = id;
			break;
		}
		return displayName;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public boolean isContributorsEnabled() {
		return isContributorsEnabled;
	}

	public URL getProfileImageURL() {
		try {
			return new URL(profileImageUrl);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public URL getURL() {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public boolean isProtected() {
		return isProtected;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public Date getStatusCreatedAt() {
		return status.getCreatedAt();
	}

	public String getStatusId() {
		return status.getId();
	}

	public String getStatusText() {
		return status.getText();
	}

	public String getStatusSource() {
		return status.getSource();
	}

	public boolean isStatusTruncated() {
		return status.isTruncated();
	}

	public String getStatusInReplyToStatusId() {
		return status.getInReplyToStatusId();
	}

	public String getStatusInReplyToUserId() {
		return status.getInReplyToUserId();
	}

	public boolean isStatusFavorited() {
		return status.isFavorited();
	}

	public String getStatusInReplyToScreenName() {
		return status.getInReplyToScreenName();
	}

	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	public String getProfileTextColor() {
		return profileTextColor;
	}

	public String getProfileLinkColor() {
		return profileLinkColor;
	}

	public String getProfileSidebarFillColor() {
		return profileSidebarFillColor;
	}

	public String getProfileSidebarBorderColor() {
		return profileSidebarBorderColor;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public Status getStatus() {
		return status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}

	public boolean isProfileBackgroundTiled() {
		return profileBackgroundTiled;
	}

	public String getLang() {
		return lang;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public boolean isGeoEnabled() {
		return isGeoEnabled;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public void setGeoEnabled(boolean isGeoEnabled) {
		this.isGeoEnabled = isGeoEnabled;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public void setContributorsEnabled(boolean isContributorsEnabled) {
		this.isContributorsEnabled = isContributorsEnabled;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setProfileBackgroundImageUrl(String profileBackgroundImageUrl) {
		this.profileBackgroundImageUrl = profileBackgroundImageUrl;
	}

	public void setProfileBackgroundTiled(boolean profileBackgroundTiled) {
		this.profileBackgroundTiled = profileBackgroundTiled;
	}

	public void setProfileBackgroundColor(String profileBackgroundColor) {
		this.profileBackgroundColor = profileBackgroundColor;
	}

	public void setProfileTextColor(String profileTextColor) {
		this.profileTextColor = profileTextColor;
	}

	public void setProfileLinkColor(String profileLinkColor) {
		this.profileLinkColor = profileLinkColor;
	}

	public void setProfileSidebarFillColor(String profileSidebarFillColor) {
		this.profileSidebarFillColor = profileSidebarFillColor;
	}

	public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
		this.profileSidebarBorderColor = profileSidebarBorderColor;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public boolean isFollowedBy() {
		return isFollowedBy;
	}

	public void setFollowedBy(boolean isFollowedBy) {
		this.isFollowedBy = isFollowedBy;
	}

	public boolean isRelationChecked() {
		return isRelationChecked;
	}

	public void setRelationChecked(boolean isRelationChecked) {
		this.isRelationChecked = isRelationChecked;
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
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User{"
				+ " sp=" + serviceProvider
				+ ", userId=" + id
				+ ", name='" + name + '\''
				+ ", screenName='" + screenName + '\''
				+ ", location='" + location + '\''
				+ ", description='" + description + '\''
				+ ", profileImageUrl='" + profileImageUrl + '\''
				+ ", url='" + url + '\''
				+ ", isProtected=" + isProtected
				+ ", followersCount=" + followersCount
				+ ", status=" + (status == null ? "null" : status.getText())
				+ ", profileBackgroundColor='" + profileBackgroundColor + '\''
				+ ", profileTextColor='" + profileTextColor + '\''
				+ ", profileLinkColor='" + profileLinkColor + '\''
				+ ", profileSidebarFillColor='"	+ profileSidebarFillColor + '\''
				+ ", profileSidebarBorderColor='" + profileSidebarBorderColor + '\''
				+ ", friendsCount=" + friendsCount
				+ ", createdAt=" + createdAt
				+ ", favouritesCount=" + favouritesCount
				+ ", utcOffset=" + utcOffset
				+ ", timeZone='" + timeZone + '\''
				+ ", profileBackgroundImageUrl='" + profileBackgroundImageUrl + '\''
				+ ", profileBackgroundTile='" + profileBackgroundTiled + '\''
				+ ", statusesCount=" + statusesCount
				+ ", geoEnabled=" + isGeoEnabled
				+ ", verified=" + isVerified
				+ ", following=" + isFollowing
				+ '}';
	}

}
