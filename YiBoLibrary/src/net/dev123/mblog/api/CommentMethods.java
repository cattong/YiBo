package net.dev123.mblog.api;

import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.exception.LibException;
import net.dev123.mblog.entity.Comment;

/**
 * CommentMethod
 *
 * @version
 * @author 马庆升
 * @time 2010-8-7 上午11:46:49
 */
public interface CommentMethods {

	/**
	 * 评论某条微博
	 *
	 * @param comment
	 *            评论内容，不能为空
	 * @param statusId
	 *            被评论的微博消息ID，不能为空
	 * @return 创建的评论
	 * @throws LibException
	 */
	Comment createComment(String comment, String statusId) throws LibException;

	/**
	 * 回复评论某条微博的评论
	 *
	 * @param comment
	 *            评论内容，不能为空
	 * @param statusId
	 *            被评论的微博消息ID，不能为空
	 * @param commentId
	 *            被评论的评论ID，不能为空
	 * @return 创建的评论
	 * @throws LibException
	 */
	Comment createComment(String comment, String statusId, String commentId) throws LibException;

	/**
	 * 删除评论
	 *
	 * @param commentId
	 *            评论ID，不能为空
	 * @return 删除的评论
	 * @throws LibException
	 */
	Comment destroyComment(String commentId) throws LibException;

	/**
	 * 返回指定微博的评论列表
	 *
	 * @param statusId
	 *            微博消息ID，不能为空
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 评论列表
	 * @throws LibException
	 */
	List<Comment> getCommentsOfStatus(String statusId, Paging<Comment> paging) throws LibException;

	/**
	 * 按时间顺序返回发送及收到的评论列表 <br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 评论列表
	 * @throws LibException
	 */
	List<Comment> getCommentsTimeline(Paging<Comment> paging) throws LibException;

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
