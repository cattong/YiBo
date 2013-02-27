package net.dev123.commons;

public interface CursorSupport {

    boolean hasPrevious();

    long getPreviousCursor();

    boolean hasNext();

    long getNextCursor();
}
