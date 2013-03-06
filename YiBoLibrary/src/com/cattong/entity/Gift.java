package com.cattong.entity;

import java.util.Date;

import com.cattong.commons.util.HashCodeHelper;

public class Gift extends BaseEntity {
	private static final long serialVersionUID = 6279207823988122996L;

	private long giftId;
	
	private int giftType;
	
	private String giftName;
	
	private String giftDescr;
	
	private int points;
	
	private int state;
	
	private int issueMode;
	
	private int issueAmount;
	
	private int orderNum;
	
	private Date createdAt;
	
	private Date startedAt;
	
	private Date endedAt;

	public long getGiftId() {
		return giftId;
	}

	public void setGiftId(long giftId) {
		this.giftId = giftId;
	}

	public int getGiftType() {
		return giftType;
	}

	public void setGiftType(int giftType) {
		this.giftType = giftType;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public String getGiftDescr() {
		return giftDescr;
	}

	public void setGiftDescr(String giftDescr) {
		this.giftDescr = giftDescr;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIssueMode() {
		return issueMode;
	}

	public void setIssueMode(int issueMode) {
		this.issueMode = issueMode;
	}

	public int getIssueAmount() {
		return issueAmount;
	}

	public void setIssueAmount(int issueAmount) {
		this.issueAmount = issueAmount;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
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
        
        final Gift obj = (Gift) o;
        if (giftId <= 0 
        	|| giftName == null) {
        	return false;
        }
        
        if (giftId == obj.getGiftId()
        	&& giftName.equals(obj.getGiftName()) 
        	&& giftType == obj.getGiftType()) {
        	return true;
        }
        
        return false;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendLong(giftId).appendInt(giftType).appendObj(giftName);
		return helper.getHashCode();
	}
    
}