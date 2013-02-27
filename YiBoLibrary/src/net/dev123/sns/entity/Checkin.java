package net.dev123.sns.entity;

import net.dev123.entity.BaseEntity;

public class Checkin extends BaseEntity {

	private static final long serialVersionUID = 5905862929781814858L;
	/** 签到id */
	private String id;
	/** 签到信息 */
	private String message;
	/** 签到的用户 */
	private User user;
	/** 应用 */
	private Application application;
	/** 签到地点 */
	private Place place;
	/** 签到时间 */
	private String createdTime;
	/** 评论数量 */
	private long commentsCount;
	/** 赞数量 */
	private long likesCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

}
