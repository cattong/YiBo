package net.dev123.sns.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.sns.entity.FriendList;
import net.dev123.sns.entity.User;

public interface FriendshipMethods {

	boolean areFriends(String sourceUserId, String targetUserId)
			throws LibException;

	List<Boolean> areFriends(List<String> listSourceUserId,
			List<String> listTargetUserId) throws LibException;

	List<String> getFriendsIds(Paging<String> paging) throws LibException;

	List<User> getFriends(Paging<User> paging) throws LibException;

	/**
	 * 获取两个用户的共同好友
	 *
	 * @param userIdA
	 *            用户A的Id
	 * @param userIdB
	 *            用户B的id
	 * @param paging
	 *            分页控制参数
	 * @return 两用户的共同好友列表
	 * @throws LibException
	 */
	List<User> getMutualFriends(String userIdA, String userIdB,
			Paging<User> paging) throws LibException;

	boolean createFriendList(String listName) throws LibException;

	boolean createFriendListMember(String listId, String userId)
			throws LibException;

	boolean destroyFriendList(String listId) throws LibException;

	boolean destroyFriendListMember(String listId, String userId)
			throws LibException;

	List<FriendList> getFriendLists(Paging<FriendList> paging)
			throws LibException;

	List<User> getFriendListMember(String listId, Paging<User> paging)
			throws LibException;
}
