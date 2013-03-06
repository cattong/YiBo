package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.weibo.entity.DirectMessage;


public interface DirectMessageService {

	/**
	 * 返回登陆用户收件箱的私信列表。<br>
	 *
	 * @param paging
	 *            分页控制参数，支持since_id，max_id，count和page参数
	 * @return 私信列表
	 * @throws LibException
	 */
	List<DirectMessage> getInboxDirectMessages(Paging<DirectMessage> paging) throws LibException;

	/**
	 * 返回登陆用户发件箱的私信列表。<br>
	 *
	 * @param paging
	 *            分页控制参数，不能为空
	 * @return 私信列表
	 * @throws LibException
	 */
	List<DirectMessage> getOutboxDirectMessages(Paging<DirectMessage> paging) throws LibException;

	/**
	 * 发送私信给指定用户，超过140字将被截断<BR>
	 *
	 * @param identifyName
	 *            用户交互名，不能为空
	 * @param message
	 *            私信内容，不能为空
	 * @return DirectMessage
	 * @throws LibException
	 */
	DirectMessage sendDirectMessage(String displayName, String message) throws LibException;

	/**
	 * 删除收件箱的私信，登录用户必须是被删除私信的接收者
	 *
	 * @param messageId
	 *            要删除的私信ID，不能为空
	 * @return 被删除的私信
	 * @throws LibException
	 */
	DirectMessage destroyInboxDirectMessage(String messageId) throws LibException;

	/**
	 * 删除发件箱的私信，登录用户必须是被删除私信的接收者
	 *
	 * @param messageId
	 *            要删除的私信ID，不能为空
	 * @return 被删除的私信
	 * @throws LibException
	 */
	DirectMessage destroyOutboxDirectMessage(String messageId) throws LibException;

}
