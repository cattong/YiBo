package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

public class PassportPointsInfo implements Serializable {
	private static final long serialVersionUID = -782398269301307000L;

	public static final int CHANNEL_TYPE_ALIPAY = 1; //支付宝渠道
	
	public static final int CHANNEL_TYPE_WAPS = 6; //万普渠道
	public static final int CHANNEL_TYPE_MIIDI = 7; //米迪渠道
	public static final int CHANNEL_TYPE_ADWO = 8; //安沃渠道
	public static final int CHANNEL_TYPE_DIANJOY = 9; //点乐渠道
	public static final int CHANNEL_TYPE_DIANJIN = 10; //点金渠道
	public static final int CHANNEL_TYPE_MIJI = 11; //米积分渠道
	public static final int CHANNEL_TYPE_DATOUNIAO = 12; //大头鸟渠道
	
	private Long passportId;
	
	private int channelType;
	
	private int points;
	
	private Date createdAt;
	
	private Date lastModifiedAt;
	
	public Long getPassportId() {
		return passportId;
	}

	public void setPassportId(Long passportId) {
		this.passportId = passportId;
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

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PassportPointsInfo)) {
            return false;
        }
        
        final PassportPointsInfo pointsInfo = (PassportPointsInfo)o;
        if (pointsInfo == null 
        	|| passportId != pointsInfo.getPassportId()
        	|| channelType != pointsInfo.getChannelType()) {
        	return false;
        }
        
        return true;
	}

	@Override
	public int hashCode() {
		String codeStr = passportId != null ? passportId.toString() : "0" + String.valueOf(channelType);
		return codeStr.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	    sb.append("passportId:" + this.passportId)
	        .append("|channelType:" + this.channelType)
	        .append("|points:" + this.points);
        return sb.toString();
	}

}
