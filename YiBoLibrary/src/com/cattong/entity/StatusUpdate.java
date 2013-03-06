package com.cattong.entity;

import java.io.File;


/**
 * StatusUpdate
 *
 * @version
 * @author cattong.com
 * @time 2010-7-25 上午09:47:16
 */
public class StatusUpdate implements java.io.Serializable {

	private static final long serialVersionUID = -3595502688477609916L;

	/** 微博消息内容 */
	private String status;
	/** 图片文件 */
	private File image;
	/** 所回复的微博消息ID */
	private String inReplyToStatusId;
	/** 地理位置 */
	private GeoLocation location;
	/** 地点名 */
	private String placeId;
	/** 是否显示坐标 */
	private boolean isDisplayCoordinates = true;

	public StatusUpdate(String status) {
		this.status = status;
	}

	/**
	 * 获取微博消息内容
	 *
	 * @return 微博消息内容
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置要更新的微博消息
	 *
	 * @param status 微博消息内容，不能为空
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取被转发的微博消息Id
	 *
	 * @return 被转发的微博消息Id
	 */
	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	/**
	 * 设置被转发的微博消息Id
	 *
	 * @param inReplyToStatusId
	 */
	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	/**
	 * 获取微博更新的地理位置信息
	 *
	 * @return 该微博更新的地理位置信息{@link GeoLocation}对象
	 */
	public GeoLocation getLocation() {
		return location;
	}

	/**
	 * 设置微博更新的地理位置信息
	 *
	 * @param location 地理位置信息{@link GeoLocation}对象
	 */
	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public boolean isDisplayCoordinates() {
		return isDisplayCoordinates;
	}

	public void setDisplayCoordinates(boolean isDisplayCoordinates) {
		this.isDisplayCoordinates = isDisplayCoordinates;
	}

	/**
	 * 获取微博更新的图片文件
	 *
	 * @return 图片文件对象
	 */
	public File getImage() {
		return image;
	}

	/**
	 * 设置微博更新的图片
	 *
	 * @param image 图片文件对象
	 */
	public void setImage(File image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result	+ ((inReplyToStatusId == null) ? 0 : inReplyToStatusId.hashCode());
		result = prime * result + (isDisplayCoordinates ? 1231 : 1237);
		result = prime * result	+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((placeId == null) ? 0 : placeId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusUpdate other = (StatusUpdate) obj;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (inReplyToStatusId == null) {
			if (other.inReplyToStatusId != null)
				return false;
		} else if (!inReplyToStatusId.equals(other.inReplyToStatusId))
			return false;
		if (isDisplayCoordinates != other.isDisplayCoordinates)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (placeId == null) {
			if (other.placeId != null)
				return false;
		} else if (!placeId.equals(other.placeId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("StatusUpdate{status=").append(status)
			.append(", inReplyToStatusId=").append(inReplyToStatusId)
			.append(", location=").append(location)
			.append(", placeId=").append(placeId)
			.append(", isDisplayCoordinates=").append(isDisplayCoordinates)
			.append(image == null ? ", no image" : (", image length: " + image.length()))
			.append("}");

		return sb.toString();
	}

}
