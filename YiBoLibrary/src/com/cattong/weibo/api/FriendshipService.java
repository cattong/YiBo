package com.cattong.weibo.api;

import com.cattong.commons.LibException;
import com.cattong.entity.Relationship;
import com.cattong.entity.User;

public interface FriendshipService  {

	/**
	 * 当前登陆用户follow指定用户， 返回被跟随用户的用户信息。<br>
	 *
	 * @param userId
	 *            被跟随用户的唯一标识
	 * @return 被跟随用户对象
	 * @throws LibException
	 */
	User createFriendship(String userId) throws LibException;

	/**
	 * 当前登陆用户取消follow指定用户，返回取消跟随的用户对象。<br>
	 *
	 * @param userId
	 *            取消跟随的用户唯一标识
	 * @return 用户对象
	 * @throws LibException
	 */
	User destroyFriendship(String userId) throws LibException;

	/**
	 * 获得源用户与目标用户之间的详细关系。<br>
	 *
	 * @param sourceUserId
	 *            源用户的唯一标识
	 * @param targetUserId
	 *            目标用户的唯一标识
	 * @return 关系对象
	 * @throws LibException
	 */
	Relationship showRelationship(String sourceUserId, String targetUserId) throws LibException;
}
