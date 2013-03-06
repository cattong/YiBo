package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.Comment;


/**
 * CommentMethod
 *
 * @version
 * @author cattong.com
 * @time 2010-8-7 上午11:46:49
 */
public interface CommentService {

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

}
