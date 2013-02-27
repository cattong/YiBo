package net.dev123.mblog.entity;

import java.util.Arrays;

import net.dev123.commons.CursorSupport;

public class IDs implements CursorSupport, java.io.Serializable {

	private static final long serialVersionUID = 5935567339579257526L;
	private int[] ids;
	private long previousCursor = -1;
	private long nextCursor = -1;

	public int[] getIDs() {
		return ids;
	}

	public boolean hasPrevious() {
		return 0 != previousCursor;
	}

	public long getPreviousCursor() {
		return previousCursor;
	}

	public boolean hasNext() {
		return 0 != nextCursor;
	}

	public long getNextCursor() {
		return nextCursor;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}

	public void setPreviousCursor(long previousCursor) {
		this.previousCursor = previousCursor;
	}

	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof IDs))
			return false;
		IDs iDs = (IDs) o;
		if (!Arrays.equals(ids, iDs.getIDs()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return ids != null ? Arrays.hashCode(ids) : 0;
	}

	@Override
	public String toString() {
		return "IDs{"
				+ "ids=" + Arrays.toString(ids)
				+ ", previousCursor=" + previousCursor
				+ ", nextCursor=" + nextCursor
				+ '}';
	}

}
