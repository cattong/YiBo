package com.cattong.entity;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.HashCodeHelper;

public class ConfigApp extends BaseEntity {
	private static final long serialVersionUID = -2679069024604088707L;
	
	public static final int STATE_ENABLED = 1;
	public static final int STATE_DISABLED = 0;
	
	private Long appId;
	
	private String appKey;
	
	private int serviceProviderNo;
	private ServiceProvider serviceProvider;
	
	private String appSecret;
	
	private String appName;
	
	private int authVersion;
	
	private int authFlow;
	
	private String callbackUrl;
	
	private int state;
	
	private boolean isShared;
	
	private Long passportId;

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public int getServiceProviderNo() {
		return serviceProviderNo;
	}

	public void setServiceProviderNo(int serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
		this.serviceProvider = ServiceProvider.getServiceProvider(serviceProviderNo);
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		this.serviceProviderNo = serviceProvider == null ? null : serviceProvider.getSpNo();
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getAuthVersion() {
		return authVersion;
	}

	public void setAuthVersion(int authVersion) {
		this.authVersion = authVersion;
	}

	public int getAuthFlow() {
		return authFlow;
	}

	public void setAuthFlow(int authFlow) {
		this.authFlow = authFlow;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public Long getPassportId() {
		return passportId;
	}

	public void setPassportId(Long passportId) {
		this.passportId = passportId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ConfigApp) {
			return false;
		}
		if (this.appKey == null || this.serviceProviderNo <= 0) {
			return false;
		}
		final ConfigApp other = (ConfigApp) o;
		if (this.appId.equals(other.getAppId())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendLong(appId).appendObj(appKey).appendInt(serviceProviderNo);
		helper.appendObj(appSecret);
		return helper.hashCode();
	}
}
