package com.cattong.commons;

import java.util.ArrayList;


/**
 * 带游标支持的ArrayList
 */
public final class PagableList<T> extends ArrayList<T> implements CursorSupport {
	private static final long serialVersionUID = 1531950333538983361L;

	private long previousCursor;
	private long nextCursor;

	public PagableList(int size, long previousCursor, long nextCursor){
		super(size);
		this.previousCursor = previousCursor;
		this.nextCursor = nextCursor;
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

	public void setPreviousCursor(long previousCursor) {
		this.previousCursor = previousCursor;
	}

	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}
}
