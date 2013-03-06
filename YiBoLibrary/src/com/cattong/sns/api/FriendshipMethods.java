package com.cattong.sns.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.sns.entity.User;


public interface FriendshipMethods {

	boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException;

	List<User> getFriends(Paging<User> paging) throws LibException;

}
