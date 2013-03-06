package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.Status;
import com.cattong.entity.StatusUpdate;


/**
 * 微博消息相关接口方法
 *
 * @version
 */
public interface StatusService {
	/**
	 * 根据微博消息ID返回单个微博信息。<br>
	 *
	 * @param statusId
	 *            微博消息ID，不能为空
	 * @return 获取到的微博消息
	 * @throws LibException
	 *
	 */
	Status showStatus(String statusId) throws LibException;

	/**
	 * 发布新微博，微博内容 超过140字将会被强制截断<br>
	 *
	 * @param latestStatus
	 *            微博更新{@link StatusUpdate}对象，不能为空 ;
	 *
	 * @return 用户最新微博消息对象
	 * @throws LibException
	 */
	Status updateStatus(StatusUpdate latestStatus) throws LibException;

	/**
	 * 根据微博消息ID删除微博消息，当前登录用户必须是微博作者。<br>
	 *
	 * @param statusId
	 *            要删除的微博消息ID，不能为空
	 * @return 已删除的微博消息对象
	 * @throws LibException
	 */
	Status destroyStatus(String statusId) throws LibException;

	/**
	 * 转发微博，支持添加转发附注，以及同时发布为评论
	 *
	 * @param statusId
	 *            转发的微博消息的ID，不能为空
	 * @param status
	 *            转发附注，可以为空
	 * @param isComment
	 *            是否同时作为评论发布
	 * @return 转发后的微博消息对象
	 * @throws LibException
	 */
	Status retweetStatus(String statusId, String status, boolean isComment) throws LibException;

	/**
	 * 获取指定微博的转发列表<br>
	 *
	 * @param statusId
	 *            微博消息ID，不能为空
	 * @param paging
	 *            分页控制参数{@link Paging}对象，不能为空
	 * @return 转发的微博消息列表
	 * @throws LibException
	 */
	List<Status> getRetweetsOfStatus(String statusId, Paging<Status> paging) throws LibException;

	/**
	 * 搜索微博信息<br>
	 *
	 * @param keyword
	 *            搜索关键字，不能为空
	 * @param paging
	 *            分页控制参数{@link Paging}对象，不能为空
	 * @return 微博搜索结果列表
	 * @throws LibException
	 */
	List<Status> searchStatuses(String keyword, Paging<Status> paging) throws LibException;

	/**
	 * 获取当天热门转发<br>
	 *
	 * @param count
	 *            获取数量
	 * @return 热门转发列表
	 * @throws LibException
	 */
	List<Status> getDailyHotRetweets(Paging<Status> paging) throws LibException;

	/**
	 * 获取一周热门转发<br>
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 热门转发列表
	 * @throws LibException
	 */
	List<Status> getWeeklyHotRetweets(Paging<Status> paging) throws LibException;

	/**
	 * 获取当天热门评论的微博列表<br>
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 热门评论的微博列表
	 * @throws LibException
	 */
	List<Status> getDailyHotComments(Paging<Status> paging) throws LibException;

	/**
	 * 获取一周热门评论的微博列表<br>
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 热门评论的微博列表
	 * @throws LibException
	 */
	List<Status> getWeeklyHotComments(Paging<Status> paging) throws LibException;

}
