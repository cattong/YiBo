package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class WitkeyTaskInfo extends BaseEntity {
	private static final long serialVersionUID = 1812042687413226592L;

	public final static int LIMITED_TYPE_BY_ALL    = 1; //按总数的限制类型
	public final static int LIMITED_TYPE_BY_HOUR   = 2; //按小时的限制类型
	public final static int LIMITED_TYPE_BY_DAILY  = 3; //按日的限制类型
	public final static int LIMITED_TYPE_BY_WEEK   = 4; //按周的限制类型
	public final static int LIMITED_TYPE_BY_MONTH  = 5; //按月的限制类型
	public final static int LIMITED_TYPE_BY_YEAR   = 6; //按年的限制类型

	public final static int TASK_LEVEL_JUNIOR   = 1; //初级
	public final static int TASK_LEVEL_MIDDLE   = 2; //中级
	public final static int TASK_LEVEL_SENIOR   = 3; //高级
	
	public static final int STATE_APPLY = 1; //申请
	public static final int STATE_ONLINE = 2; //上线
	public static final int STATE_ONLINE_NOT_DISPLAY = 3; //上线但不显示
	public static final int STATE_PAUSE = 4; //暂停
	public static final int STATE_OFFLINE = 5; //下架
	
	private Long taskTypeNo;
	private TaskType taskType;
	
	private String taskName;
	
	private int taskLevel;
	
	private String taskDescr;
	
	private Integer points;
	
	private Integer state;
	
	private Boolean hasTaskList;
	
	private Integer limitedType;
	
	private Integer limitedCount;
	
	private Integer limitedPoints;
	
	private Integer orderNum;
	
	private Date createdAt;
	
	public Long getTaskTypeNo() {
		return taskTypeNo;
	}

	public void setTaskTypeNo(Long taskTypeNo) {
		this.taskTypeNo = taskTypeNo;
		this.taskType = taskTypeNo == null ? null : TaskType.getTaskType(taskTypeNo.intValue());
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
		this.taskTypeNo = taskType == null ? null : new Long(taskType.getTaskTypeNo());
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getTaskLevel() {
		return taskLevel;
	}

	public void setTaskLevel(int taskLevel) {
		this.taskLevel = taskLevel;
	}

	public String getTaskDescr() {
		return taskDescr;
	}

	public void setTaskDescr(String taskDescr) {
		this.taskDescr = taskDescr;
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

	public Boolean getHasTaskList() {
		return hasTaskList;
	}

	public void setHasTaskList(Boolean hasTaskList) {
		this.hasTaskList = hasTaskList;
	}

	public Integer getLimitedType() {
		return limitedType;
	}

	public void setLimitedType(Integer limitedType) {
		this.limitedType = limitedType;
	}

	public Integer getLimitedCount() {
		return limitedCount;
	}

	public void setLimitedCount(Integer limitedCount) {
		this.limitedCount = limitedCount;
	}

	public Integer getLimitedPoints() {
		return limitedPoints;
	}

	public void setLimitedPoints(Integer limitedPoints) {
		this.limitedPoints = limitedPoints;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
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
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final WitkeyTaskInfo obj = (WitkeyTaskInfo) o;
        if (taskTypeNo == null || taskName == null) {
        	return false;
        }
        
        if (taskTypeNo.equals(obj.getTaskTypeNo()) 
        	&& taskName.equals(obj.getTaskName())) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendLong(taskTypeNo).appendObj(taskName).appendObj(taskDescr);
		helper.appendInt(points).appendInt(state).appendBoolean(hasTaskList);
		return helper.getHashCode();
	}

}
