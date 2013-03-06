package com.cattong.sns.entity;

import com.cattong.entity.BaseSocialEntity;

public class Photo extends BaseSocialEntity {
	private static final long serialVersionUID = 6750080173297921475L;
	
	/** 照片id */
	private String id;
	/** 相册id */
	private String albumId;
	/** 发布相片的人或专页 */
	private String userId;
	/** 照片描述信息 */
	private String caption;
	/** 原图 */
	private String originalPicture;
	/** 中图 */
	private String middlePicture;
	/** 缩略图 */
	private String thumbnailPicture;
	/** 评论数 */
	private long commentsCount;
	/** 赞数 */
	private long lieksCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
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

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getLieksCount() {
		return lieksCount;
	}

	public void setLieksCount(long lieksCount) {
		this.lieksCount = lieksCount;
	}

}
