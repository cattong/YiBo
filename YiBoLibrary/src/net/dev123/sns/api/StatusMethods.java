package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.Status;

public interface StatusMethods {

	boolean createStatus(String status) throws LibException;

	boolean destroyStatus(String statusId) throws LibException;

	List<Status> getStatuses(String profileId, Paging<Status> paging)
			throws LibException;

	Status showStatus(String statusId, String ownerId)
			throws LibException;

}
