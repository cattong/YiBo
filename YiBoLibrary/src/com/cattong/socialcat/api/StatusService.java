package com.cattong.socialcat.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.Status;
import com.cattong.entity.StatusCatalog;
import com.cattong.entity.StatusExtInfo;


public interface StatusService {

	StatusExtInfo getStatusExtInfo(ServiceProvider sp, String statusId) throws LibException;
	
	List<Status> getStatusCatalog(StatusCatalog catalog, ServiceProvider sp, 
		Paging<Status> paging) throws LibException;

	List<Status> getMobilePhoto(ServiceProvider sp, Paging<Status> paging) throws LibException;
}
