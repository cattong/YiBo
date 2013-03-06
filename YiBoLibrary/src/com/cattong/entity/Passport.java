package com.cattong.entity;

import java.io.Serializable;
import java.util.Date;

public class Passport implements Serializable{
	private static final long serialVersionUID = 7751252188195985517L;

	public static final int TYPE_WEB_SITE  = 1; //官网注册用户
	public static final int TYPE_MOBILE    = 2; //手机注册用户
	public static final int TYPE_JIFENGBANG = 3; //积分邦用户
	public static final int TYPE_PRELOADER  = 4; //预装用户
	
	public static final int STATE_INACTIVE = 1; // 未激活
	public static final int STATE_ACTIVE = 2; // 激活
	public static final int STATE_FROZEN = 3; // 冻结
	public static final int STATE_REVOKED = 4; // 已作废

	public static final int ROLE_TYPE_JIFENBANG   = 20; //积分邦会员角色
	public static final int ROLE_TYPE_PRELOADER   = 30; //积分邦卖场角色
	
	private String passportId;
	
	private String email;
	
	private String username;

	private Integer state;

	private Boolean isVip;

	private Date createdAt;

	private String accessToken;
	
	private Integer points; //当前可用积分

	private Integer totalPoints; //历史总积分
	
	private PointsLevel pointsLevel;

	private Integer roleType;
	
	private Long parentPassportId;
	
	public String getPassportId() {
		return passportId;
	}

	public void setPassportId(String passportId) {
		this.passportId = passportId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Boolean isVip() {
		return isVip;
	}

	public void setVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public PointsLevel getPointsLevel() {
		return pointsLevel;
	}

	public void setPointsLevel(PointsLevel pointsLevel) {
		this.pointsLevel = pointsLevel;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public Long getParentPassportId() {
		return parentPassportId;
	}

	public void setParentPassportId(Long parentPassportId) {
		this.parentPassportId = parentPassportId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((isVip == null) ? 0 : isVip.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Passport other = (Passport) obj;

		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (isVip == null) {
			if (other.isVip != null)
				return false;
		} else if (!isVip.equals(other.isVip))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Passport [username=" + username + ", email=" + email
				+ ", state=" + state + ", isVip=" + isVip + ", createTime="
				+ createdAt + ", accessToken=" + accessToken + ", pointsLevel=" + pointsLevel.toString() + "]";
	}


}
