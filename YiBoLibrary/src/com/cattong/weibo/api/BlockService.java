package com.cattong.weibo.api;

import java.util.List;

import com.cattong.commons.LibException;
import com.cattong.commons.Paging;
import com.cattong.entity.User;


/**
 * BlockMethods 黑名单相关操作接口
 *
 * @version
 * @author cattong.com
 * @time 2010-9-27 上午11:12:43
 */
public interface BlockService {

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
	 * 列出黑名单用户(输出用户详细信息)
	 *
	 * @param paging
	 *            分页控制参数
	 * @return 黑名单用户列表
	 * @throws LibException
	 */
	List<User> getBlockingUsers(Paging<User> paging) throws LibException;

}
