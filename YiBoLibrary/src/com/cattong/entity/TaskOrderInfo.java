package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class TaskOrderInfo extends BaseEntity {
	private static final long serialVersionUID = -7121242919818469403L;

	public static final int STATE_APPLY  = 0; //订单申请
	public static final int STATE_AGREE  = 1; //订单成交
	public static final int STATE_REJECT = 2; //订单拒绝
	public static final int STATE_REPEAL = 4; //订单撤销
	
	private Long orderId;
	
	private Integer taskTypeNo;
	private TaskType taskType;
	
	private Long taskId;
	
	private String deviceId;
	
	private Integer points;
	
	private Integer state;
	
	private Date createdAt;
	
	private Long passportId;
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getTaskTypeNo() {
		return taskTypeNo;
	}

	public void setTaskTypeNo(Integer taskTypeNo) {
		this.taskTypeNo = taskTypeNo;
		this.taskType = TaskType.getTaskType(taskTypeNo);
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
		this.taskTypeNo = taskType == null ? 0 : taskType.getTaskTypeNo();
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public Long getPassportId() {
		return passportId;
	}

	public void setPassportId(Long passportId) {
		this.passportId = passportId;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final TaskOrderInfo obj = (TaskOrderInfo) o;
        if (orderId == null
        	|| taskType == null
        	|| taskId == null
        	|| deviceId == null) {
        	return false;
        }
        
        if (orderId.equals(obj.getOrderId())
        	&& taskType == obj.getTaskType()
        	&& taskId.equals(obj.getTaskId())
        	&& deviceId.equals(obj.getDeviceId())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendLong(orderId).appendInt(taskTypeNo).appendLong(taskId).appendObj(deviceId);
		return helper.getHashCode();
	}


}
