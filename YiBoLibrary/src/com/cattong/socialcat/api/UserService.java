package com.cattong.socialcat.api;

import com.cattong.commons.LibException;
import com.cattong.commons.ServiceProvider;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;

public interface UserService {

	User getUserBaseInfo(ServiceProvider sp, String userId) throws LibException;
	
	UserExtInfo getUserExtInfo(ServiceProvider sp, String userId) throws LibException;
}
