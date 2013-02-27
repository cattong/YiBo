package net.dev123.yibome.entity;

import java.util.List;

import net.dev123.commons.ServiceProvider;

public class UserGroupSyncResult {

	private Long groupId;
	private ServiceProvider serviceProvider;
	private List<String> toBeAdded;
	private List<String> toBeDeleted;

	private Integer serviceProviderNo;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<String> getToBeAdded() {
		return toBeAdded;
	}

	public void setToBeAdded(List<String> toBeAdded) {
		this.toBeAdded = toBeAdded;
	}

	public List<String> getToBeDeleted() {
		return toBeDeleted;
	}

	public void setToBeDeleted(List<String> toBeDeleted) {
		this.toBeDeleted = toBeDeleted;
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
		this.serviceProviderNo = serviceProvider.getServiceProviderNo();
	}
}
