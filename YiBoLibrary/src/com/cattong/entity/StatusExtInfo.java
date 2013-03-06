package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.ServiceProvider;

public class StatusExtInfo extends BaseSocialEntity {
	private static final long serialVersionUID = -3100496103423471398L;

	private String globalStatusId;
	
    private Integer serviceProviderNo;    
    private ServiceProvider serviceProvider;
    
    private String statusId;
	
    private Status status;
    
    private Integer statusCatalogNo;
    private StatusCatalog statusCatalog;
    
	private Integer retweetCount;
	
	private Integer commentCount;
	
	private Integer likeCount;
	
	private Integer hateCount;
	
	private boolean isContainPicture;
	
	private Integer pictureCatalogNo = 0;
	
	private String tags;
	
	private Date createdAt;

	public String getGlobalStatusId() {
		return globalStatusId;
	}

	public void setGlobalStatusId(String globalStatusId) {
		this.globalStatusId = globalStatusId;
	}
	
	public Integer getServiceProviderNo() {
		return serviceProviderNo;
	}

	public void setServiceProviderNo(Integer serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
		this.serviceProvider = ServiceProvider.getServiceProvider(serviceProviderNo);
	}
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		if (serviceProvider != null) {
		    this.serviceProviderNo = serviceProvider.getSpNo();
		}
	}
	
	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getStatusCatalogNo() {
		return statusCatalogNo;
	}

	public void setStatusCatalogNo(Integer statusCatalogNo) {
		this.statusCatalogNo = statusCatalogNo;
		this.statusCatalog = StatusCatalog.getStatusCatalog(statusCatalogNo);
	}

	public StatusCatalog getStatusCatalog() {
		return statusCatalog;
	}

	public void setStatusCatalog(StatusCatalog statusCatalog) {
		this.statusCatalog = statusCatalog;
		if (statusCatalog != null) {
			this.statusCatalogNo = statusCatalog.getCatalogNo();
		}
	}

	public Integer getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	
	public Integer getLikeCount() {
		return likeCount;
	}
	
	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getHateCount() {
		return hateCount;
	}

	public void setHateCount(Integer hateCount) {
		this.hateCount = hateCount;
	}

	public boolean isContainPicture() {
		return isContainPicture;
	}

	public void setContainPicture(boolean isContainPicture) {
		this.isContainPicture = isContainPicture;
	}

	public Integer getPictureCatalogNo() {
		return pictureCatalogNo;
	}

	public void setPictureCatalogNo(Integer pictureCatalogNo) {
		this.pictureCatalogNo = pictureCatalogNo;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Status)) {
            return false;
        }
        
        final Status status = (Status) o;
        if (statusId == null || serviceProvider == null) {
        	return false;
        }
        
        if (statusId.equals(status.getStatusId()) &&
        	serviceProvider == status.getServiceProvider()
        ) {
        	return true;
        }
        return false;
	}
	
	@Override
	public int hashCode() {
		String codeStr = "ext_" + statusId + 
	        (serviceProvider != null ? serviceProvider.getSpNo() : 0);
	    return codeStr.hashCode();
	}
	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
	    sb.append("statusId:" + this.statusId)
	        .append("|serviceProvider:" + this.serviceProviderNo)
	        .append("|createdAt:" + this.createdAt);
        return sb.toString();
	}
}
