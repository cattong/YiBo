package net.dev123.mblog.entity;

import java.util.Date;

public class RetweetDetails implements java.io.Serializable {
	private long retweetId;
	private Date retweetedAt;
	private User retweetingUser;
	static final long serialVersionUID = 1957982268696560598L;

	public long getRetweetId() {
		return retweetId;
	}

	public Date getRetweetedAt() {
		return retweetedAt;
	}

	public User getRetweetingUser() {
		return retweetingUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (retweetId ^ (retweetId >>> 32));
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
		RetweetDetails other = (RetweetDetails) obj;
		if (retweetId != other.retweetId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RetweetDetails{"
				+ "retweetId=" + retweetId
				+ ", retweetedAt=" + retweetedAt
				+ ", retweetingUser=" + retweetingUser
				+ '}';
	}
}
