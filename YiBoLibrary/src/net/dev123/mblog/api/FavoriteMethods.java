package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;

/**
 * FavoriteMethods
 *
 * @version
 * @author 马庆升
 * @time 2010-7-28 下午02:38:09
 */
public interface FavoriteMethods {

	/**
	 * 收藏指定ID的微博消息
	 *
	 * @param statusId
	 *            微博消息ID
	 * @return 微博消息对象
	 * @throws LibException
	 */
	Status createFavorite(String statusId) throws LibException;

	/**
	 * 取消收藏指定ID的微博消息
	 *
	 * @param statusId
	 *            微博消息ID
	 * @return 微博消息对象
	 * @throws LibException
	 */
	Status destroyFavorite(String statusId) throws LibException;

	/**
	 * 返回当前登录用户收藏的微博列表
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getFavorites(Paging<Status> paging) throws LibException;

	/**
	 * 返回指定用户收藏的微博列表
	 *
	 * @param userId
	 *            用户唯一标识名
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getFavorites(String userId, Paging<Status> paging) throws LibException;
}
