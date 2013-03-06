package com.cattong.sns.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.sns.entity.User;


public interface UserMethods {

	User showUser(String userId) throws LibException;

    List<User> showUsers(List<String> listUserId) throws LibException;

}
