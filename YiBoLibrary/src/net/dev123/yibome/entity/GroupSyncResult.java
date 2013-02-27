package net.dev123.yibome.entity;

import java.util.List;

public class GroupSyncResult {

	private List<LocalGroup> toBeAdded;
	private List<LocalGroup> toBeDeleted;
	private List<LocalGroup> toUpdateName;
	private List<LocalGroup> toUpdateId;

	public List<LocalGroup> getToBeAdded() {
		return toBeAdded;
	}

	public void setToBeAdded(List<LocalGroup> toBeAdded) {
		this.toBeAdded = toBeAdded;
	}

	public List<LocalGroup> getToBeDeleted() {
		return toBeDeleted;
	}

	public void setToBeDeleted(List<LocalGroup> toBeDeleted) {
		this.toBeDeleted = toBeDeleted;
	}

	public List<LocalGroup> getToUpdateName() {
		return toUpdateName;
	}

	public void setToUpdateName(List<LocalGroup> toUpdateName) {
		this.toUpdateName = toUpdateName;
	}

	public List<LocalGroup> getToUpdateId() {
		return toUpdateId;
	}

	public void setToUpdateId(List<LocalGroup> toUpdateId) {
		this.toUpdateId = toUpdateId;
	}

}
