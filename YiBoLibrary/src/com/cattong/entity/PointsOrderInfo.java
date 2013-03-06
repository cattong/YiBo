package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

public class PointsOrderInfo implements Serializable {
	private static final long serialVersionUID = 5300809412396404863L;

	/**订单类型**/
	public static final int ORDER_TYPE_ALIPAY = 1; //支付宝订单
	public static final int ORDER_TYPE_MEIZU  = 2; //魅族订单
	public static final int ORDER_TYPE_LOGIN  = 3; //登陆订单
	
	public static final int ORDER_TYPE_WAPS = 6; //万普订单
	public static final int ORDER_TYPE_MIIDI = 7; //米迪订单
	public static final int ORDER_TYPE_ADWO = 8; //安沃订单
	public static final int ORDER_TYPE_DIANJOY = 9; //点乐订单
	public static final int ORDER_TYPE_DIANJIN = 10; //点金订单
	public static final int ORDER_TYPE_MIJI = 11; //米积分订单
	public static final int ORDER_TYPE_DATOUNIAO = 12; //大头鸟订单
	
	/**订单状态**/
	public static final int STATE_APPLY = 0; //订单申请中
	public static final int STATE_AGREE = 1; //审核通过
	public static final int STATE_REJECT = 2; //拒绝
	
	private Long orderId;
	
	private int orderType;
	
	private String thirdpartyOrderId;
	
	private int amount;
	
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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
		    + ", amount=" + amount + ", points=" + points + ", state=" + state
		    + ", createdAt=" + createdAt + "]";
	}
}
