package net.dev123.sns.entity;

import net.dev123.entity.BaseEntity;

public class Page extends BaseEntity implements Profile {
	private static final long serialVersionUID = 5924845777317010449L;
	/** 专页Id */
	private String id;
	/** 专页名 */
	private String name;
	/** 专页图片 */
	private String picture;
	/** 专页地址 */
	private String link;
	/** 专页分类 */
	private String category;
	/** 网址 */
	private String website;
	/** 描述或简介 */
	private String description;
	/** 关注者数量 */
	private long followersCount;
	/** 位置信息 */
	private String location;
	/** 联系电话 */
	private String phone;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Override
	public String getProfileId() {
		return id;
	}

	@Override
	public String getProfileName() {
		return name;
	}

	@Override
	public ProfileType getProfileType() {
		return ProfileType.PAGE;
	}

	@Override
	public String getProfilePicture() {
		return picture;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
