package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;


public interface TimelineService {
	/**
	 * 返回最新更新的N条微博消息，具体数量为各平台接口默认数量
	 * 
	 * @return 最新更新的公共微博消息列表
	 * @throws LibException
	 */
	List<Status> getPublicTimeline() throws LibException;

	/**
	 * 返回登陆用户及其关注的用户最新的微博消息（包括转发），与用户“我的首页”返回内容相同。
	 * 分页方式：自由模式
	 * @param paging
	 *     分页控制，支持since_id，max_id，count和page参数.
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getHomeTimeline(Paging<Status> paging) throws LibException;

	/**
	 * 返回登陆用户所关注的用户发布的微博消息。 <br>
	 * 分页方式：自由模式
	 * @param paging
	 *     分页控制，支持since_id，max_id，count和page参数.
	 * @return 微博信息列表
	 * @throws LibException
	 */
	List<Status> getFriendsTimeline(Paging<Status> paging) throws LibException;

	/**
	 * 返回指定用户名的用户及其关注的用户发布的微博消息。 <br>
	 * 分页方式：单向方式
	 * @param userId
	 *            指定用户的用户名,如果为空，则获得是登录用户自己的微博消息
	 * @param paging
	 *            分页控制，支持since_id，max_id，count和page参数.
	 * @return 微博信息列表
	 * @throws LibException
	 */
	List<Status> getUserTimeline(String userId, Paging<Status> paging) throws LibException;

	/**
	 * 返回登陆用户的提到他的微博消息，包括含转发他的微博和在微博中@我的微博<br>
	 * 分页方式：自由模式
	 * @param paging
	 *            分页控制，支持since_id，max_id，count和page参数。
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getMentionTimeline(Paging<Status> paging) throws LibException;

	/**
	 * 返回由登陆用户转发的微博列表。 <br>
	 *
	 * @param paging
	 *            分页控制，支持since_id，max_id，count和page参数。
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException;

	/**
	 * 按时间顺序返回发送及收到的评论列表 <br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 评论列表
	 * @throws LibException
	 */
	List<Comment> getCommentTimeline(Paging<Comment> paging) throws LibException;
	
	/**
	 * 由我发送的评论列表
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 评论列表
	 * @throws LibException
	 */
	List<Comment> getCommentsByMe(Paging<Comment> paging) throws LibException;

	/**
	 * 我收到的评论列表
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 评论列表
	 * @throws LibException
	 */
	List<Comment> getCommentsToMe(Paging<Comment> paging) throws LibException;
}
