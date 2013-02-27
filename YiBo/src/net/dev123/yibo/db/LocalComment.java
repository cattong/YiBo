package net.dev123.yibo.db;

import java.io.Serializable;

import net.dev123.mblog.entity.Comment;

public class LocalComment extends Comment implements Serializable {

	private static final long serialVersionUID = -8894800436042346294L;

	/** 归属帐号 */
	private Long accountId;
	/** 是否作为分割线 */
	private boolean isDivider;

	/**定义该分隔是否处于加载中 */
	private transient boolean isLoading = false; 
	private transient boolean isLocalDivider = false;
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public boolean isDivider() {
		return isDivider;
	}

	public void setDivider(boolean isDivider) {
		this.isDivider = isDivider;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public boolean isLocalDivider() {
		return isLocalDivider;
	}

	public void setLocalDivider(boolean isLocalDivider) {
		this.isLocalDivider = isLocalDivider;
	}

}
