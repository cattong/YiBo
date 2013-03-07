package com.shejiaomao.weibo.db;

import java.io.Serializable;
import java.util.Date;

import com.cattong.weibo.entity.Group;

public class LocalGroup extends Group implements Serializable{

	private static final long serialVersionUID = 6254691739100451587L;

	public static final int STATE_SYNCED = 0;
	public static final int STATE_ADDED = 1;
	public static final int STATE_DELETED = 2;
	public static final int STATE_UPDATED = 3;

	private Long groupId;
	private Long remoteGroupId = 0L;
	private String spGroupId;

	private Date createdAt;

	private Integer state = 0;

	private long accountId;
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getSpGroupId() {
		return spGroupId;
	}

	public void setSpGroupId(String spGroupId) {
		this.spGroupId = spGroupId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getRemoteGroupId() {
		return remoteGroupId;
	}

	public void setRemoteGroupId(Long remoteGroupId) {
		this.remoteGroupId = remoteGroupId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

}
