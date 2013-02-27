package net.dev123.sns.api;

import net.dev123.exception.LibException;

public interface EventMethods {

	boolean createEvent(String name, String startTime, String endTime)
			throws LibException;

	boolean destroyEvent(String eventId) throws LibException;

}
