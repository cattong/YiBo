package net.dev123.mblog.entity;

import java.util.Date;

public class RateLimitStatus implements java.io.Serializable {
	private static final long serialVersionUID = 832355052293658614L;
	private int remainingHits;
	private int hourlyLimit;
	private int resetTimeInSeconds;
	private int secondsUntilReset;
	private Date resetTime;

	public void setRemainingHits(int remainingHits) {
		this.remainingHits = remainingHits;
	}

	public void setHourlyLimit(int hourlyLimit) {
		this.hourlyLimit = hourlyLimit;
	}

	public void setResetTimeInSeconds(int resetTimeInSeconds) {
		this.resetTimeInSeconds = resetTimeInSeconds;
	}

	public void setSecondsUntilReset(int secondsUntilReset) {
		this.secondsUntilReset = secondsUntilReset;
	}

	public void setResetTime(Date resetTime) {
		this.resetTime = resetTime;
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

	public int getSecondsUntilReset() {
		return secondsUntilReset;
	}

	public Date getResetTime() {
		return resetTime;
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
		if (secondsUntilReset != that.secondsUntilReset)
			return false;
		if (resetTime != null ? !resetTime.equals(that.resetTime) : that.resetTime != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = remainingHits;
		result = 31 * result + hourlyLimit;
		result = 31 * result + resetTimeInSeconds;
		result = 31 * result + secondsUntilReset;
		result = 31 * result + (resetTime != null ? resetTime.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RateLimitStatusJSONImpl{"
				+ "remainingHits=" + remainingHits
				+ ", hourlyLimit=" + hourlyLimit
				+ ", resetTimeInSeconds=" + resetTimeInSeconds
				+ ", secondsUntilReset=" + secondsUntilReset
				+ ", resetTime=" + resetTime
				+ '}';
	}
}
