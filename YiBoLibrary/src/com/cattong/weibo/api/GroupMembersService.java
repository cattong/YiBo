package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.User;
import com.cattong.weibo.entity.Group;


public interface GroupMembersService {

	/**
	 * 获取指定分组的成员列表<br>
	 *
	 * @param listId
	 *            分组Id，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 指定分组的成员列表
	 * @throws LibException
	 */
	List<User> getGroupMembers(String listId, Paging<User> paging)
			throws LibException;

	/**
	 * 添加成员到指定分组，当前用户必须是该分组的所有者<br>
	 *
	 * @param listId
	 *            分组Id，不能为空
	 * @param userId
	 *            用户唯一识别名，不能为空
	 * @return 更新后的分组
	 * @throws LibException
	 */
	Group createGroupMember(String listId, String userId)
			throws LibException;

	/**
	 * 批量添加用户到指定分组，当前用户必须是该分组的所有者<br>
	 *
	 * @param listId
	 *            分组名，不能为空
	 * @param userIds
	 *            用户唯一识别名数组
	 * @return 更新后的分组
	 */
	Group createGroupMembers(String listId, String[] userIds)
			throws LibException;

	/**
	 * 从分组中删除指定成员，当前用户必须是该分组的所有者<br>
	 *
	 * @param listId
	 *            分组Id，不能为空
	 * @param userId
	 *            用户唯一识别名，不能为空
	 * @return 更新的分组信息
	 * @throws LibException
	 */
	Group destroyGroupMember(String listId, String userId)
			throws LibException;

	/**
	 * 检查某用户是否在指定分组中<br>
	 *
	 * @param listId
	 *            分组Id，不能为空
	 * @param userId
	 *            用户唯一识别名，不能为空
	 * @return 用户信息
	 * @throws LibException
	 */
	User showGroupMember(String listId, String userId)
			throws LibException;

	/**
	 * 获取指定用户所归属的分组列表，用于查询指定用户都被添加到了哪些分组中<br>
	 *
	 * @param groupMemberUserId
	 *            微博成员的唯一识别名
	 * @param paging
	 *            分组控制参数，不能为空
	 * @return 该成员所属的分组列表
	 * @throws LibException
	 */
	List<Group> getGroupMemberships(String groupMemberUserId,
			Paging<Group> paging) throws LibException;
}
