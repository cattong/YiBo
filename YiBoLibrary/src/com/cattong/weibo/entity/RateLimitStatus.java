package com.cattong.weibo.entity;

import java.util.Date;

public class RateLimitStatus implements java.io.Serializable {
	private static final long serialVersionUID = 832355052293658614L;
	
	private int hourlyLimit;
	private int remainingHits;
	
	private Date resetedAt;
	private int resetTimeInSeconds;

	public void setRemainingHits(int remainingHits) {
		this.remainingHits = remainingHits;
	}

	public void setHourlyLimit(int hourlyLimit) {
		this.hourlyLimit = hourlyLimit;
	}

	public void setResetTimeInSeconds(int resetTimeInSeconds) {
		this.resetTimeInSeconds = resetTimeInSeconds;
	}

	public void setResetedAt(Date resetedAt) {
		this.resetedAt = resetedAt;
	}

	public int getRemainingHits() {
		return remainingHits;
	}

	public int getHourlyLimit() {
		return hourlyLimit;
	}

	public int getResetTimeInSeconds() {
		return resetTimeInSeconds;
	}

	public Date getResetedAt() {
		return resetedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RateLimitStatus))
			return false;

		RateLimitStatus that = (RateLimitStatus) o;

		if (hourlyLimit != that.hourlyLimit)
			return false;
		if (remainingHits != that.remainingHits)
			return false;
		if (resetTimeInSeconds != that.resetTimeInSeconds)
			return false;
		if (resetedAt != null ? !resetedAt.equals(that.resetedAt) : that.resetedAt != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = remainingHits;
		result = 31 * result + hourlyLimit;
		result = 31 * result + resetTimeInSeconds;
		result = 31 * result + (resetedAt != null ? resetedAt.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RateLimitStatusJSONImpl{"
				+ "remainingHits=" + remainingHits
				+ ", hourlyLimit=" + hourlyLimit
				+ ", resetTimeInSeconds=" + resetTimeInSeconds
				+ ", resetTime=" + resetedAt
				+ '}';
	}
}
