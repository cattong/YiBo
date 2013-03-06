package com.cattong.entity;

import java.io.Serializable;

import com.cattong.commons.ServiceProvider;


public abstract class BaseSocialEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 988287172603888683L;

	/** 服务提供商 */
	protected ServiceProvider serviceProvider;
	/** 服务提供商代码，用于序列化时保存SP，枚举类型在序列化时丢失 */
	protected int serviceProviderNo;

	public ServiceProvider getServiceProvider() {
		if (serviceProviderNo > 0 && serviceProvider == null) {
			serviceProvider = ServiceProvider.getServiceProvider(serviceProviderNo);
		}
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		if (serviceProvider != null) {
			this.serviceProviderNo = serviceProvider.getSpNo();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + serviceProviderNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaseSocialEntity)) {
			return false;
		}
		BaseSocialEntity other = (BaseSocialEntity) obj;
		if (serviceProviderNo != other.serviceProviderNo)
			return false;
		return true;
	}
}
