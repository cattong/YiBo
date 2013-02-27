package net.dev123.yibome.entity;

import java.io.Serializable;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-31 下午2:14:11
 **/
public class ConfigApp implements Serializable {
	private static final long serialVersionUID = -2679069024604088707L;
	public static final int STATE_ENABLED = 1;
	public static final int STATE_DISABLED = 0;
	
	private Long appId;
	private String appKey;
	private String appSecret;
	private String appName;
	private int authVersion;
	private int authFlow;
	private int state;
	private boolean isShared;
	private Long passportId;
	private int serviceProviderNo;

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
}
