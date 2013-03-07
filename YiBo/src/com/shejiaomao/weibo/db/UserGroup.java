package com.shejiaomao.weibo.db;

import java.io.Serializable;

import com.cattong.commons.ServiceProvider;

public class UserGroup implements Serializable {
	private static final long serialVersionUID = -410499562133710001L;

	public static final int STATE_SYNCED = 0;
	public static final int STATE_ADDED = 1;
	public static final int STATE_DELETED = 2;

	private Long groupId;

	private String userId;

	private Integer state;

	private Integer serviceProviderNo;
	private ServiceProvider serviceProvider;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getServiceProviderNo() {
		return serviceProviderNo;
	}

	public void setServiceProviderNo(Integer serviceProviderNo) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime
				* result
				+ ((serviceProviderNo == null) ? 0 : serviceProviderNo
						.hashCode());
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
		UserGroup other = (UserGroup) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
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
		return "UserGroup [groupId=" + groupId + ", userId=" + userId
				+ ", serviceProvider=" + serviceProvider + "]";
	}

}
