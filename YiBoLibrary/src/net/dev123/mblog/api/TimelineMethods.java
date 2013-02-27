package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Status;

public interface TimelineMethods {
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
	List<Status> getMentions(Paging<Status> paging) throws LibException;

	/**
	 * 返回由登陆用户转发的微博列表。 <br>
	 *
	 * @param paging
	 *            分页控制，支持since_id，max_id，count和page参数。
	 * @return 微博消息列表
	 * @throws LibException
	 */
	List<Status> getRetweetedByMe(Paging<Status> paging) throws LibException;

}
