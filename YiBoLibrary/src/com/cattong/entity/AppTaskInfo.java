package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class AppTaskInfo extends BaseEntity {
	private static final long serialVersionUID = -9098530146145008258L;

	public final static int INSTALL_TYPE_SYSTEM = 1; //内置安装
	public final static int INSTALL_TYPE_USER   = 2; //外置安装
	
	public final static int STATE_APPLY    = 1; //申请
	public final static int STATE_ONLINE   = 2; //上线
	public final static int STATE_REJECT   = 3; //拒绝
	public final static int STATE_OFFLINE  = 4; //上架
	
	private Long taskId;
	
	private Integer taskType;
	
	private Integer installType;
	
	private String appId;
	private AppInfo appInfo;
	
	private String dowloadUrl;
	
	private Integer points;
	
	private Integer state;
	
	private Date createdAt;
	
	private Date startedAt;
	
	private Date endedAt;
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getInstallType() {
		return installType;
	}

	public void setInstallType(Integer installType) {
		this.installType = installType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public String getDowloadUrl() {
		return dowloadUrl;
	}

	public void setDowloadUrl(String dowloadUrl) {
		this.dowloadUrl = dowloadUrl;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
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

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(Date endedAt) {
		this.endedAt = endedAt;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final AppTaskInfo obj = (AppTaskInfo) o;
        if (taskId == null 
        	|| taskType == null
        	|| installType == null
        	|| appId == null) {
        	return false;
        }
        
        if (taskId.equals(obj.getTaskId())
        	&& taskType.equals(obj.getTaskType())
        	&& installType.equals(obj.getInstallType())
        	&& appId.equals(obj.getAppId())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendLong(taskId).appendInt(taskType).appendInt(installType);
		helper.appendObj(appId);
		return helper.getHashCode();
	}

}
