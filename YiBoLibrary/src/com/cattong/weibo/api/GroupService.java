package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.Status;
import com.cattong.weibo.entity.Group;


public interface GroupService {
	/**
	 * 创建新的分组，每个用户最多能够创建20个。<br>
	 *
	 * @param groupName
	 *            分组名，不能为空.
	 * @param isPublicList
	 *            是否公开，默认为私有
	 * @param description
	 *            分组描述，可为空.
	 * @return 创建的分组
	 * @throws LibException
	 */
	Group createGroup(String groupName, boolean isPublicList,
			String description) throws LibException;

	/**
	 * 更新指定分组 <br>
	 *
	 * @param groupId
	 *            分组Id
	 * @param newGroupName
	 *            新的分组名
	 * @param isPublicList
	 *            是否公开
	 * @param newDescription
	 *            新的分组描述.
	 * @return 更新后的分组
	 * @throws LibException
	 */
	Group updateGroup(String groupId, String newGroupName,
			boolean isPublicList, String newDescription) throws LibException;

	/**
	 * 获取指定用户的分组列表，如果是当前用户则会包含私有分组<br>
	 *
	 * @param groupOwnerIdentifyName
	 *            分组的所有者微博唯一识别名
	 * @param paging
	 *            分页控制对象，不能为空
	 * @return 分组列表
	 * @throws LibException
	 */
	List<Group> getGroups(String groupOwnerUserId,
			Paging<Group> paging) throws LibException;

	/**
	 * 显示指定分组的信息，私有列表只有自己才可以访问<br>
	 *
	 * @param groupId
	 *            分组Id
	 * @return 分组信息
	 * @throws TwitterException
	 */
	Group showGroup(String groupId) throws LibException;

	/**
	 * 删除指定分组，要求必须是该分组的所有这 <br>
	 *
	 * @param groupId
	 *            分组Id
	 * @return 已被删除的分组
	 * @throws LibException
	 */
	Group destroyGroup(String groupId) throws LibException;

	/**
	 * 获取指定分组中成员微博更新列表<br>
	 *
	 * @param groupId
	 *            分组Id，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 分组中成员的微博更新列表
	 * @throws LibException
	 */
	List<Status> getGroupStatuses(String groupId, Paging<Status> paging)
			throws LibException;

	/**
	 * 获取指定用户的所有分组，包括订阅的和自己的<br>
	 *
	 * @param userId
	 *            用户唯一识别名
	 * @return 用户的分组列表
	 * @throws LibException
	 */
	List<Group> getAllGroups(String userId) throws LibException;
}
