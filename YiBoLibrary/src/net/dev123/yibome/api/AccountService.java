package net.dev123.yibome.api;

import java.util.List;

import net.dev123.exception.LibException;
import net.dev123.yibome.entity.Account;
import net.dev123.yibome.entity.AccountSyncResult;

public interface AccountService {
	AccountSyncResult syncAccounts(List<? extends Account> accounts) throws LibException;
}
