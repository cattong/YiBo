package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;

public class Album extends BaseEntity {
	private static final long serialVersionUID = 4271324304362763110L;
	/** 相册Id */
	private String id;
	/** 相册名 */
	private String name;
	/** 相册所有者 */
	private Profile from;
	/** 描述信息 */
	private String description;
	/** 位置信息 */
	private String location;
	/** 相册地址 */
	private String link;
	/** 相片数量 */
	private Long photosCount;
	/** 封面照片 */
	private String coverPicture;
	/** 可见性 */
	private Privacy privacy;
	/** 创建时间 */
	private Date createdTime;
	/** 更新时间 */
	private Date updatedTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Profile getFrom() {
		return from;
	}

	public void setFrom(Profile profile) {
		this.from = profile;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getPhotosCount() {
		return photosCount;
	}

	public void setPhotosCount(Long photosCount) {
		this.photosCount = photosCount;
	}

	public String getCoverPicture() {
		return coverPicture;
	}

	public void setCoverPicture(String coverPicture) {
		this.coverPicture = coverPicture;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}