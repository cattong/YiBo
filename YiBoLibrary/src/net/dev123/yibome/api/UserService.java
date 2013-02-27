package net.dev123.yibome.api;

import net.dev123.commons.ServiceProvider;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.yibome.entity.UserExtInfo;

public interface UserService {

	User getUserBaseInfo(String userId, ServiceProvider sp) throws LibException;
	
	UserExtInfo getUserExtInfo(String userId, ServiceProvider sp) throws LibException;
}
