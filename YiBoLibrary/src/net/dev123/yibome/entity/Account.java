package net.dev123.yibome.entity;

import java.io.Serializable;
import java.util.Date;

import net.dev123.commons.ServiceProvider;
import net.dev123.entity.BaseUser;

public class Account implements Serializable {

	private static final long serialVersionUID = -966939247139934134L;

	public static final int STATE_SYNCED = 0;
	public static final int STATE_ADDED = 1;
	public static final int STATE_DELETED = 2;

	private Long accountId;

	private String authToken;

	private String authSecret;

	private Integer authVersion;

	private String userId;

	private BaseUser user;

	private Integer serviceProviderNo;

	private ServiceProvider serviceProvider;

	private String appKey;
	
	private String appSecret;

	private Integer state;

	private boolean isDefault;

	private String restProxyUrl;

	private String searchProxyUrl;

	private Date createdAt;

	private Date tokenExpiresAt;

	private String tokenScopes;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getAuthSecret() {
		return authSecret;
	}

	public void setAuthSecret(String authSecret) {
		this.authSecret = authSecret;
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
		this.serviceProviderNo = serviceProvider.getServiceProviderNo();
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

	public void setServiceProviderNo(Integer serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
		if (user != null) {
			this.userId = user.getId();
		}
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", authToken=" + authToken
				+ ", authSecret=" + authSecret + ", authVersion=" + authVersion
				+ ", userId=" + userId + ", serviceProviderNo="
				+ serviceProviderNo + ", serviceProvider=" + serviceProvider
				+ ", appKey=" + appKey + ", state=" + state
				+ ", createdAt=" + createdAt + "]";
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

	public Date getTokenExpiresAt() {
		return tokenExpiresAt;
	}

	public void setTokenExpiresAt(Date expiresAt) {
		this.tokenExpiresAt = expiresAt;
	}

	public String getTokenScopes() {
		return tokenScopes;
	}

	public void setTokenScopes(String tokenScopes) {
		this.tokenScopes = tokenScopes;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
}
