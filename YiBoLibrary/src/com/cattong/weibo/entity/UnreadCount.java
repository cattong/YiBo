package com.cattong.weibo.entity;

public class UnreadCount implements java.io.Serializable {
	private static final long serialVersionUID = 565587235337396305L;

	private int statusCount; // 新微博数量
	private int metionCount; // 新的提到我的数量
	private int commentCount; // 新评论数量
	private int direceMessageCount; // 新私信数量
	private int followerCount; // 新粉丝数量

	public int getMetionCount() {
		return metionCount;
	}

	public void setMetionCount(int metionCount) {
		this.metionCount = metionCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getDireceMessageCount() {
		return direceMessageCount;
	}

	public void setDireceMessageCount(int direceMessageCount) {
		this.direceMessageCount = direceMessageCount;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	public int getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(int statusCount) {
		this.statusCount = statusCount;
	}

}
