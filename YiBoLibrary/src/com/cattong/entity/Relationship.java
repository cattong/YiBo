package com.cattong.entity;

import com.cattong.commons.util.HashCodeHelper;


/**
 * 两个用户的关系信息;
 */
public class Relationship extends BaseEntity {
	private static final long serialVersionUID = 2414322167493381574L;

	private String sourceUserId;
	private String sourceScreenName;

	private boolean isSourceFollowingTarget; 
	private boolean isSourceFollowedByTarget; 

	private String targetUserId;
	private String targetScreenName;

	private boolean isSourceBlockingTarget;

	public String getSourceUserId() {
		return sourceUserId;
	}

	public void setSourceUserId(String sourceUserId) {
		this.sourceUserId = sourceUserId;
	}

	public boolean isSourceFollowingTarget() {
		return isSourceFollowingTarget;
	}

	public void setSourceFollowingTarget(boolean isSourceFollowingTarget) {
		this.isSourceFollowingTarget = isSourceFollowingTarget;
	}

	public boolean isSourceFollowedByTarget() {
		return isSourceFollowedByTarget;
	}

	public void setSourceFollowedByTarget(boolean isSourceFollowedByTarget) {
		this.isSourceFollowedByTarget = isSourceFollowedByTarget;
	}

	public boolean isSourceBlockingTarget() {
		return isSourceBlockingTarget;
	}

	public void setSourceBlockingTarget(boolean isSourceBlockingTarget) {
		this.isSourceBlockingTarget = isSourceBlockingTarget;
	}

	public String getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}

//	public Relation getRelation() {
//		if (isFollowing && isFollowed) {
//			return Relation.Friendship;
//		}
//		if (isFollowing) {
//			return Relation.Followingship;
//		}
//		if (isFollowed) {
//			return Relation.Followedship;
//		}
//		return Relation.Noneship;
//	}


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
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final Relationship obj = (Relationship) o;
        if (sourceUserId == null 
        	|| targetUserId == null) {
        	return false;
        }
        
        if (sourceUserId.equals(obj.getSourceUserId()) 
        	&& targetUserId.equals(obj.getTargetUserId())) {
        	return true;
        }
        
        return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(sourceUserId).appendObj(targetUserId)
		   .appendBoolean(isSourceFollowingTarget).appendBoolean(isSourceFollowedByTarget);
		return helper.getHashCode();
	}
}
