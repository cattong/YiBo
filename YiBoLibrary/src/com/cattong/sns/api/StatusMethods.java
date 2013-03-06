package com.cattong.sns.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.Status;


public interface StatusMethods {

	boolean createStatus(String status) throws LibException;

	boolean destroyStatus(String statusId) throws LibException;

	List<Status> getStatuses(String profileId, Paging<Status> paging)
			throws LibException;

	Status showStatus(String statusId, String ownerId)
			throws LibException;

}
