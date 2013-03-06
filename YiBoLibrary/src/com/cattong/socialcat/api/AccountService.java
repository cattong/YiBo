package com.cattong.socialcat.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.entity.Account;

public interface AccountService {
	
	Account syncAccount(Account account) throws LibException;
	
	List<Account> syncAccountList() throws LibException;
	
}
