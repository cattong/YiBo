package net.dev123.yibome.entity;

import java.io.Serializable;
import java.util.Date;

public class PointOrderInfo implements Serializable {
	private static final long serialVersionUID = 6533302465323891949L;

	private Long orderId;
	
	private int orderType;
	
	private String thirdpartyOrderId;
	
	private int count;
	
	private int points;
	
	private int state;
	
	private Date createdAt;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getThirdpartyOrderId() {
		return thirdpartyOrderId;
	}

	public void setThirdpartyOrderId(String thirdpartyOrderId) {
		this.thirdpartyOrderId = thirdpartyOrderId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@Override
	public String toString() {
		return "PointOrderInfo [orderId=" + orderId + ", thirdpartyOrderId=" + thirdpartyOrderId
		    + ", count=" + count + ", points=" + points + ", state=" + state
		    + ", createdAt=" + createdAt + "]";
	}
}
