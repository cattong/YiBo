package com.cattong.commons;

public interface CursorSupport {

    boolean hasPrevious();

    long getPreviousCursor();

    boolean hasNext();

    long getNextCursor();
}
