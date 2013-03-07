package com.shejiaomao.weibo.db;

import java.io.Serializable;
import java.util.Date;

import com.cattong.commons.ServiceProvider;

public class Task implements Serializable {
	private static final long serialVersionUID = -1191273884662190749L;

	public static final int TYPE_RECENT_CONTACK = 1;
	public static final int TYPE_TWEET = 2;
	public static final int TYPE_RETWEET = 3;
	public static final int TYPE_COMMENT = 4;
	public static final int TYPE_DIRECT_MESSAGE = 5;
	
	public static final int STATE_INIT = 1;
	public static final int STATE_ = 2;
	public static final int STATE_FINISHED = 5;
	
	private Long taskId;
	
	private int type;
	
	private String content;
	
	private String resultId;
	
	private Date createdAt;
	
	private Date finishedAt;
	
	private int state;
	
	private ServiceProvider serviceProvider;
	
	private Long accountId;

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
}
