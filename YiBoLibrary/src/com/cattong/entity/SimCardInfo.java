package com.cattong.entity;

import com.cattong.commons.util.HashCodeHelper;

public class SimCardInfo extends BaseEntity {
	private static final long serialVersionUID = 8158302491962962284L;

	private String simImsi;
	
	private String simSerialNumber;
	
	private String simCountryIso;
	
	private String simOperator;
	
	private String simOperatorName;
	
	private Integer phoneType;
	
	private String phoneNum;
	
	public String getSimImsi() {
		return simImsi;
	}

	public void setSimImsi(String simImsi) {
		this.simImsi = simImsi;
	}

	public String getSimSerialNumber() {
		return simSerialNumber;
	}

	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}

	public String getSimCountryIso() {
		return simCountryIso;
	}

	public void setSimCountryIso(String simCountryIso) {
		this.simCountryIso = simCountryIso;
	}

	public String getSimOperator() {
		return simOperator;
	}

	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}

	public String getSimOperatorName() {
		return simOperatorName;
	}

	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}

	public Integer getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(Integer phoneType) {
		this.phoneType = phoneType;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final SimCardInfo obj = (SimCardInfo) o;
        if (simImsi == null) {
        	return false;
        }
        
        if (simImsi.equals(obj.getSimImsi())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(simImsi).appendObj(simSerialNumber);
		return helper.getHashCode();
	}

}
