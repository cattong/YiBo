package net.dev123.yibome.entity;

import java.util.List;

public class AccountSyncResult {

	private List<Account> toBeAdded;
	private List<Account> toBeDeleted;
	private List<Account> toBeUpdated;

	public List<Account> getToBeAdded() {
		return toBeAdded;
	}

	public void setToBeAdded(List<Account> toBeAdded) {
		this.toBeAdded = toBeAdded;
	}

	public List<Account> getToBeDeleted() {
		return toBeDeleted;
	}

	public void setToBeDeleted(List<Account> toBeDeleted) {
		this.toBeDeleted = toBeDeleted;
	}

	public List<Account> getToBeUpdated() {
		return toBeUpdated;
	}

	public void setToBeUpdated(List<Account> toBeUpdated) {
		this.toBeUpdated = toBeUpdated;
	}

}
