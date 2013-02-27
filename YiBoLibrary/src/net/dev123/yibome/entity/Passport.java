package net.dev123.yibome.entity;

import java.io.Serializable;
import java.util.Date;

public class Passport implements Serializable{
	private static final long serialVersionUID = 7751252188195985517L;

	public static final int STATE_INACTIVE = 1; // 未激活
	public static final int STATE_ACTIVE = 2; // 激活
	public static final int STATE_FROZEN = 3; // 冻结
	public static final int STATE_REVOKED = 4; // 已作废

	private String username;

	private String email;

	private Integer state;

	private Boolean isVip;

	private Date createdAt;

	private String authToken;

	private String authSecret;
	
	private PointLevel pointLevel;

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

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getAuthSecret() {
		return authSecret;
	}

	public void setAuthSecret(String authSecret) {
		this.authSecret = authSecret;
	}

	public PointLevel getPointLevel() {
		return pointLevel;
	}

	public void setPointLevel(PointLevel pointLevel) {
		this.pointLevel = pointLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authSecret == null) ? 0 : authSecret.hashCode());
		result = prime * result
				+ ((authToken == null) ? 0 : authToken.hashCode());
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
		if (authSecret == null) {
			if (other.authSecret != null)
				return false;
		} else if (!authSecret.equals(other.authSecret))
			return false;
		if (authToken == null) {
			if (other.authToken != null)
				return false;
		} else if (!authToken.equals(other.authToken))
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
				+ createdAt + ", authToken=" + authToken + ", authSecret="
				+ authSecret + ", pointLevel=" + pointLevel.toString() + "]";
	}


}
