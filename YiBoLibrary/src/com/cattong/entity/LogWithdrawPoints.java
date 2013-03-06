package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;


public class LogWithdrawPoints implements Serializable {
	private static final long serialVersionUID = 8308417741097357937L;

	private Long logId;
	
	private Long orderId;
	
	private int channelType;
	
	private int points;
	
	private Date createdAt;
	
	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public int getChannelType() {
		return channelType;
	}

	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
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
        if (!(o instanceof LogWithdrawPoints)) {
            return false;
        }
        
        final LogWithdrawPoints log = (LogWithdrawPoints)o;
        if (log == null 
        	|| logId != log.getLogId()
        	|| channelType != log.getChannelType()
        	|| orderId != log.getOrderId()) {
        	return false;
        }
        
        return true;
	}

	@Override
	public int hashCode() {
		String codeStr = String.valueOf(logId) + String.valueOf(orderId);
		return  codeStr.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	    sb.append("logId:" + this.logId)
	        .append("|orderId:" + this.orderId)
	        .append("|channelType:" + this.channelType)
	        .append("|points:" + this.points);
        return sb.toString();
	}

}
