package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.User;

/**
 * BlockMethods 黑名单相关操作接口
 *
 * @version
 * @author 马庆升
 * @time 2010-9-27 上午11:12:43
 */
public interface BlockMethods {

	/**
	 * 将某用户加入黑名单
	 *
	 * @param userId
	 *            用户唯一标识
	 * @return 用户对象
	 * @throws LibException
	 */
	User createBlock(String userId) throws LibException;

	/**
	 * 将某用户移出黑名单
	 *
	 * @param userId
	 *            用户唯一标识
	 * @return 用户对象
	 * @throws LibException
	 */
	User destroyBlock(String userId) throws LibException;

	/**
	 * 检测某用户是否是黑名单用户
	 *
	 * @param userId
	 *            用户唯一标识
	 * @return 用户是否在黑名单中
	 * @throws LibException
	 */
	boolean existsBlock(String userId) throws LibException;

	/**
	 * 列出黑名单用户(输出用户详细信息)
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 黑名单用户列表
	 * @throws LibException
	 */
	List<User> getBlockingUsers(Paging<User> paging) throws LibException;

	/**
	 * 列出分页黑名单用户ID
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 黑名单用户ID
	 * @throws LibException
	 */
	List<String> getBlockingUsersIDs(Paging<String> paging) throws LibException;
}
