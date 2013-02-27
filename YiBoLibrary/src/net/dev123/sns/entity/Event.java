package net.dev123.sns.entity;

import java.util.Date;

import net.dev123.entity.BaseEntity;
import net.dev123.entity.GeoLocation;

public class Event extends BaseEntity {
	private static final long serialVersionUID = 5899312466818657026L;

	public static enum Privacy {
		OPEN, SECRET, CLOSED
	}

	/** EventId */
	private String id;

	private String name;

	private String description;

	private Profile owner;

	private Date startTime;

	private Date endTime;

	private String location;

	private GeoLocation venue;

	private Privacy privacy;

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

	public Profile getOwner() {
		return owner;
	}

	public void setOwner(Profile owner) {
		this.owner = owner;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public GeoLocation getVenue() {
		return venue;
	}

	public void setVenue(GeoLocation venue) {
		this.venue = venue;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

}
