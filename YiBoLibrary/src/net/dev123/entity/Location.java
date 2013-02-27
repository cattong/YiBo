package net.dev123.entity;

import net.dev123.commons.util.StringUtil;

public class Location extends BaseEntity {

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
	/** 邮编 */
	private String zipCode;

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
		return buffer.toString();
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
