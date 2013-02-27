package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;

/**
 * SocialGraphMethods
 *
 * @version
 * @author 马庆升
 * @time 2010-7-27 下午08:52:45
 */
public interface SocialGraphMethods {

	/**
	 * 返回当前登录用户的关注对象的ID列表<br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return List<String> ID列表
	 * @throws LibException
	 */
	List<String> getFriendsIDs(Paging<String> paging) throws LibException;

	/**
	 * 返回指定用户的关注对象的ID列表<br>
	 *
	 * @param userId
	 *            用户唯一标识，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return List<String> ID列表
	 * @throws LibException
	 */
	List<String> getFriendsIDs(String userId, Paging<String> paging) throws LibException;

	/**
	 * 返回当前登录用户粉丝的ID列表<br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return List<String> ID列表
	 * @throws LibException
	 */
	List<String> getFollowersIDs(Paging<String> paging) throws LibException;

	/**
	 * 返回指定用户粉丝的ID列表 <br>
	 *
	 * @param userId
	 *            用户唯一标识，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return List<String> ID列表
	 * @throws LibException
	 */
	List<String> getFollowersIDs(String userId, Paging<String> paging) throws LibException;

}
