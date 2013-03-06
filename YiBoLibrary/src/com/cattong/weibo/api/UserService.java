package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.User;


public interface UserService {
	/**
	 * 根据用户唯一标识返回用户信息<br>
	 *
	 * @param userId
	 *            唯一标识
	 * @return 用户对象
	 * @throws LibException
	 */
	User showUser(String userId) throws LibException;

	/**
	 * 根据与用户交互名返回用户信息
	 * 
	 * @param displayName
	 *            用户交互名
	 * @return 用户对象
	 * @throws LibException
	 */
	User showUserByDisplayName(String displayName) throws LibException;
	
	/**
	 * 搜索用户
	 *
	 * @param keyword
	 *            用户搜索关键字，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 用户列表
	 * @throws LibException
	 */
	List<User> searchUsers(String keyword, Paging<User> paging) throws LibException;

	/**
	 * 返回当前登陆用户关注对象列表<br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 关注列表
	 * @throws LibException
	 */
	List<User> getFriends(Paging<User> paging) throws LibException;

	/**
	 * 根据用户唯一标识，获取关注对象列表。<br>
	 *
	 * @param userId
	 *            用户唯一标识
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 关注列表
	 * @throws LibException
	 */
	List<User> getUserFriends(String userId, Paging<User> paging) throws LibException;

	/**
	 * 返回认证用户的跟随者，即粉丝，包含跟随者当前微博。<br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 粉丝列表
	 * @throws LibException
	 */
	List<User> getFollowers(Paging<User> paging) throws LibException;

	/**
	 * 返回指定用户的跟随者，包含跟随者最新一条微博。<br>
	 *
	 * @param userId
	 *            用户唯一标识
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 粉丝列表
	 * @throws LibException
	 */
	List<User> getUserFollowers(String userId, Paging<User> paging) throws LibException;

}
