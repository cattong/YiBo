package net.dev123.yibome.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.ServiceProvider;
import net.dev123.entity.StatusUpdate;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;
import net.dev123.yibome.entity.Account;
import net.dev123.yibome.entity.StatusSyncResult;
import net.dev123.yibome.entity.SubscribeCatalog;

public interface StatusService {

	List<Status> getStatusSubscribe(SubscribeCatalog catalog, ServiceProvider sp, 
		Paging<Status> paging) throws LibException;

	List<StatusSyncResult> syncStatus(StatusUpdate updateStatus, 
		List<? extends Account> listAccount) throws LibException ;
	
	List<StatusSyncResult> syncStatus(StatusUpdate updateStatus, 
			String accountInfos) throws LibException ;
}
