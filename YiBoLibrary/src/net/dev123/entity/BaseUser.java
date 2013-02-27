package net.dev123.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * 用户基类
 *
 * @version
 * @author 马庆升
 * @time 2010-7-23 下午04:54:02
 */
public abstract class BaseUser extends BaseEntity implements java.io.Serializable {

	private static final long serialVersionUID = -6345893237975349030L;

	/** 用户ID */
	protected String id;
	/** 用户名 */
	protected String name;
	/** 显示名称 */
	protected String screenName;
	/** 头像图片地址 */
	protected String profileImageUrl;
	/** 性别 */
	protected Gender gender;
	/** 简单描述 */
	protected String description;
	/** 当前所在地 */
	protected String location;
	/** 是否经过认证 */
	protected boolean isVerified;

	protected Date createdAt;

	public abstract String getGlobalName();

	public abstract String getProfileName();

	public abstract String getMentionName();

	public abstract String getMentionTitleName();

	public abstract String getDisplayName();

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public URL getProfileImageURL() {
		try {
			return new URL(profileImageUrl);
		} catch (MalformedURLException ex) {
			return null;
		}
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

	public void setId(String id) {
		this.id = id;
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
		BaseUser other = (BaseUser) obj;
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
				+ ", description='" + description + '\''
				+ ", location='" + location + '\''
				+ ", profileImageUrl='" + profileImageUrl + '\''
				+ ", verified=" + isVerified
				+ '}';
	}

}
