package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

import com.cattong.commons.ServiceProvider;
import com.cattong.entity.BaseUser;


public class Account implements Serializable {
	private static final long serialVersionUID = -966939247139934134L;

	public static final int STATE_APPLY  = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_EXPIRED = 2;
	public static final int STATE_INVALID = 3;

	private Long accountId;

	private Integer authVersion;
	
	private String accessToken;

	private String accessSecret;

	private String userId;

	private BaseUser user;

	private Integer serviceProviderNo;

	private ServiceProvider serviceProvider;

	private String appKey;
	
	private String appSecret;

	private Integer state;

	private boolean isDefault;

	private Long passportId;
	
	private Date createdAt;

	private Date tokenExpiredAt;

	private String restProxyUrl;
	private String searchProxyUrl;
	
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}


	public Integer getAuthVersion() {
		return authVersion;
	}

	public void setAuthVersion(Integer authVersion) {
		this.authVersion = authVersion;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getServiceProviderNo() {
		return serviceProviderNo;
	}

	public void setServiceProviderNo(int serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
		this.serviceProvider = ServiceProvider
				.getServiceProvider(serviceProviderNo);
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		this.serviceProviderNo = serviceProvider.getSpNo();
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdTime) {
		this.createdAt = createdTime;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
		if (user != null) {
			this.userId = user.getUserId();
			this.setServiceProvider(user.getServiceProvider());
		}
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public Long getPassportId() {
		return passportId;
	}

	public void setPassportId(Long passportId) {
		this.passportId = passportId;
	}

	public Date getTokenExpiredAt() {
		return tokenExpiredAt;
	}

	public void setTokenExpiredAt(Date tokenExpiredAt) {
		this.tokenExpiredAt = tokenExpiredAt;
	}

	public String getRestProxyUrl() {
		return restProxyUrl;
	}

	public void setRestProxyUrl(String restProxyUrl) {
		this.restProxyUrl = restProxyUrl;
	}

	public String getSearchProxyUrl() {
		return searchProxyUrl;
	}

	public void setSearchProxyUrl(String searchProxyUrl) {
		this.searchProxyUrl = searchProxyUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result	+ ((serviceProviderNo == null) ? 0 : serviceProviderNo.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		Account other = (Account) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (serviceProviderNo == null) {
			if (other.serviceProviderNo != null)
				return false;
		} else if (!serviceProviderNo.equals(other.serviceProviderNo))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accessToken=" + accessToken
				+ ", accessSecret=" + accessSecret + ", authVersion=" + authVersion
				+ ", userId=" + userId + ", serviceProviderNo="
				+ serviceProviderNo + ", serviceProvider=" + serviceProvider
				+ ", appKey=" + appKey + ", state=" + state
				+ ", createdAt=" + createdAt + "]";
	}
	
}
