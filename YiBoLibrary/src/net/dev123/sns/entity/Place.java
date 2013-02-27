package net.dev123.sns.entity;

import net.dev123.entity.BaseEntity;
import net.dev123.entity.GeoLocation;

public class Place extends BaseEntity {

	private static final long serialVersionUID = -6875158386938881780L;
	/** 地点Id */
	private String id;
	/** 地点名 */
	private String name;
	/** 描述 */
	private String description;
	/** 地理位置 */
	private GeoLocation location;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

}
