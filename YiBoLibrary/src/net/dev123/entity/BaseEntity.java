package net.dev123.entity;

import java.io.Serializable;

import net.dev123.commons.ServiceProvider;

public abstract class BaseEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 988287172603888683L;

	/** 服务提供商 */
	protected ServiceProvider serviceProvider;
	/** 服务提供商代码，用于序列化时保存SP，枚举类型在序列化时丢失 */
	protected int sp;

	public ServiceProvider getServiceProvider() {
		if (sp > 0 && serviceProvider == null) {
			serviceProvider = ServiceProvider.getServiceProvider(sp);
		}
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		if (serviceProvider != null) {
			this.sp = serviceProvider.getServiceProviderNo();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sp;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaseEntity)) {
			return false;
		}
		BaseEntity other = (BaseEntity) obj;
		if (sp != other.sp)
			return false;
		return true;
	}
}
