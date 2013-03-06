package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

public class WithdrawOrderInfo implements Serializable {
	private static final long serialVersionUID = 3254258904310091131L;

	/**订单类型**/
	public static final int ORDER_TYPE_TELEPHONE = 1; //手机提现
	public static final int ORDER_TYPE_ALIPAY = 2; //支付宝提现
	public static final int ORDER_TYPE_QBI = 3; //Q币提现
	public static final int ORDER_TYPE_OTHER = 4; 

	/**订单状态**/
	public static final int STATE_APPLY = 0; //订单申请中
	public static final int STATE_AGREE = 1; //审核通过
	public static final int STATE_REJECT = 2; //拒绝
	
	private Long orderId;
	
	private int orderType;
	
	private String thirdpartyOrderId;
	
	private Long passportId;
	
	private int points;
	
	private int income;
	
	private String withdrawTo;
	
	private int state;
	
	private Date createdAt;
	
	private String description;
	
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
	
	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public String getWithdrawTo() {
		return withdrawTo;
	}

	public void setWithdrawTo(String withdrawTo) {
		this.withdrawTo = withdrawTo;
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

	public Long getPassportId() {
		return passportId;
	}

	public void setPassportId(Long passportId) {
		this.passportId = passportId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WithdrawOrderInfo)) {
            return false;
        }
        
        final WithdrawOrderInfo order = (WithdrawOrderInfo)o;
        if (order == null || orderId != order.getOrderId()) {
        	return false;
        }
        
        return true;
	}

	@Override
	public int hashCode() {
		return orderId != null ? orderId.hashCode() : 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	    sb.append("orderId:" + this.orderId)
	        .append("|orderType:" + this.orderType)
	        .append("|income:" + this.income)
	        .append("|point:" + this.points)
	        .append("|passportId:" + this.passportId);
        return sb.toString();
	}
}
