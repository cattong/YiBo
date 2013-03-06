package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

import com.cattong.commons.ServiceProvider;


public class UserExtInfo implements Serializable {
	private static final long serialVersionUID = -1034208678774999528L;

	private String userId;

	private ServiceProvider serviceProvider;
	
	private Date birthday;
	
	private String originalProfileImageUrl;
	
	private String verifyInfo;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getOriginalProfileImageUrl() {
		return originalProfileImageUrl;
	}

	public void setOriginalProfileImageUrl(String originalProfileImageUrl) {
		this.originalProfileImageUrl = originalProfileImageUrl;
	}

	public String getVerifyInfo() {
		return verifyInfo;
	}

	public void setVerifyInfo(String verifyInfo) {
		this.verifyInfo = verifyInfo;
	}
}
