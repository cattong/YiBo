package com.cattong.entity;

import java.io.Serializable;

import com.cattong.commons.util.StringUtil;

public class Location implements Serializable {
	private static final long serialVersionUID = -1413858119414305535L;
	
	/** 国家 */
	private String country;
	/** 省、州 */
	private String province;
	/** 城市 */
	private String city;
	/** 区 */
	private String district;
	/** 街道 */
	private String street;
	/** 店名或地标名*/
	private String landmark;
	
	/** 邮编 */
	private String zipCode;

	/** 此位置的坐标**/
	private double latitude;
	private double longitude;
	
	public Location() {
		
	}
	
	public Location(double latitude, double longitude) {
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmake(String landmark) {
		this.landmark = landmark;
	}

	public String getFormatedAddress() {
		StringBuffer buffer = new StringBuffer();
		if (StringUtil.isNotEmpty(province)) {
			buffer.append(province);
			buffer.append(" ");
		}
		if (StringUtil.isNotEmpty(city)) {
			buffer.append(city);
			buffer.append(" ");
		}
		if (StringUtil.isNotEmpty(district)) {
			buffer.append(district);
			buffer.append(" ");
		}
		if (StringUtil.isNotEmpty(street)) {
			buffer.append(street);
		}
		if (StringUtil.isNotEmpty(landmark)) {
			buffer.append(landmark);
		}
		return buffer.toString();
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
