package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;
import net.dev123.mblog.entity.Group;

public interface GroupSubscribersMethods {

	/**
	 * 返回指定分组的订阅者 <br>
	 *
	 * @param groupId
	 *            分组Id
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 指定分组的订阅者
	 * @throws LibException
	 */
	List<User> getGroupSubscribers(String groupId, Paging<User> paging)
			throws LibException;

	/**
	 * 订阅指定分组 <br>
	 *
	 * @param groupId
	 *            分组Id.
	 * @return 更新后的分组对象
	 * @throws LibException
	 */
	Group createGroupSubscriber(String groupId) throws LibException;

	/**
	 * 取消订阅指定分组<br>
	 *
	 * @param groupId
	 *            分组Id
	 * @return 更新后的分组对象
	 * @throws LibException
	 */
	Group destroyGroupSubscriber(String groupId) throws LibException;

	/**
	 * 判断用户是否订阅指定的分组<br>
	 *
	 * @param groupId
	 *            分组Id
	 * @param userId
	 *            用户唯一标识
	 * @return 用户对象
	 * @throws LibException
	 */
	User showGroupSubscriber(String groupId, String userId)
			throws LibException;

	/**
	 * 获取指定用户关注的分组列表<br>
	 *
	 * @param groupOwnerUserId
	 *            用户唯一识别名，不能为空
	 * @param paging
	 *            分组控制参数，不能为空
	 * @return 用户关注的分组列表
	 * @throws LibException
	 */
	List<Group> getGroupSubscriptions(String groupOwnerUserId,
			Paging<Group> paging) throws LibException;

}
