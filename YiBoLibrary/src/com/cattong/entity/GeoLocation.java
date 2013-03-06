package com.cattong.entity;

import java.util.List;

public class GeoLocation {
	private static final long serialVersionUID = -4847567157651889935L;
	private double latitude;
	private double longitude;

	//geo 对应的多个地址 
	private List<Location> locationList;
	
	public GeoLocation() {
		
	}
	public GeoLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoLocation other = (GeoLocation) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeoLocation{"
				+ "latitude=" + latitude
				+ ", longitude=" + longitude
				+ '}';
	}

}
