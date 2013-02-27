package net.dev123.mblog.entity;


/**
 * 两个用户的关系信息;
 */
public class Relationship implements java.io.Serializable {
	private static final long serialVersionUID = 2414322167493381574L;

	private String sourceUserId;
	private String sourceScreenName;

	private boolean isFollowing; //sourceFollowingTarget;
	private boolean isFollowed;  //sourceFollowedByTarget;

	private String targetUserId;
	private String targetScreenName;

	private boolean isBlocking; //sourceBlockingTarget
	private boolean isSourceNotificationsEnabled;

	public String getSourceUserId() {
		return sourceUserId;
	}

	public void setSourceUserId(String sourceUserId) {
		this.sourceUserId = sourceUserId;
	}

	public String getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

	public Relation getRelation() {
		if (isFollowing && isFollowed) {
			return Relation.Friendship;
		}
		if (isFollowing) {
			return Relation.Followingship;
		}
		if (isFollowed) {
			return Relation.Followedship;
		}
		return Relation.Noneship;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public boolean isSourceBlockingTarget() {
		return isBlocking;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public void setSourceNotificationsEnabled(boolean sourceNotificationsEnabled) {
		this.isSourceNotificationsEnabled = sourceNotificationsEnabled;
	}

	public String getSourceScreenName() {
		return sourceScreenName;
	}

	public void setSourceScreenName(String sourceScreenName) {
		this.sourceScreenName = sourceScreenName;
	}

	public String getTargetScreenName() {
		return targetScreenName;
	}

	public void setTargetScreenName(String targetScreenName) {
		this.targetScreenName = targetScreenName;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((sourceScreenName == null) ? 0 : sourceScreenName.hashCode());
		result = prime * result	+ ((sourceUserId == null) ? 0 : sourceUserId.hashCode());
		result = prime * result	+ ((targetScreenName == null) ? 0 : targetScreenName.hashCode());
		result = prime * result	+ ((targetUserId == null) ? 0 : targetUserId.hashCode());
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
		Relationship other = (Relationship) obj;
		if (sourceScreenName == null) {
			if (other.sourceScreenName != null)
				return false;
		} else if (!sourceScreenName.equals(other.sourceScreenName))
			return false;
		if (sourceUserId == null) {
			if (other.sourceUserId != null)
				return false;
		} else if (!sourceUserId.equals(other.sourceUserId))
			return false;
		if (targetScreenName == null) {
			if (other.targetScreenName != null)
				return false;
		} else if (!targetScreenName.equals(other.targetScreenName))
			return false;
		if (targetUserId == null) {
			if (other.targetUserId != null)
				return false;
		} else if (!targetUserId.equals(other.targetUserId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Relationship{"
				+ "sourceUserId=" + sourceUserId
				+ ", targetUserId=" + targetUserId
				+ ", sourceUserScreenName='" + sourceScreenName + '\''
				+ ", targetUserScreenName='" + targetScreenName + '\''
				+ ", isFollowingship=" + isFollowing
				+ ", isFollowedship=" + isFollowed
				+ ", sourceNotificationsEnabled=" + isSourceNotificationsEnabled
				+ '}';
	}


}
