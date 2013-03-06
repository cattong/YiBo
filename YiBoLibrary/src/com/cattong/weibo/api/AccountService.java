package com.cattong.weibo.api;

import java.io.File;

import com.cattong.commons.LibException;
import com.cattong.entity.User;
import com.cattong.weibo.entity.RateLimitStatus;


public interface AccountService {
	/**
	 * 验证用户认证信息的有效性，有效则返回用户对象数据，
	 * 无效或网络出现问题，则返回HTTP 401状态码及错误信息<br>
	 *
	 * @return 用户对象
	 * @throws LibException
	 *             当网络不可用或提供的认证信息错误时
	 */
	User verifyCredentials() throws LibException;

	/**
	 * 返回当前时段内剩余可用的API请求数。
	 * <p>此接口不受调用次数限制</p>
	 *
	 * @return 次数限制信息
	 * @throws LibException
	 */
	RateLimitStatus getRateLimitStatus() throws LibException;

	/**
	 * 更新用户头像
	 *
	 * @param image
	 *            必须是有效的GIF，JPG或者PNG格式图片，小于700K. 图片宽度大于500像素将会被按比例缩小。
	 * @return 更新后用户对象
	 * @throws LibException
	 *             网络不可用，或者指定文件未找到，或者指定文件对象不代表一个文件
	 */
	User updateProfileImage(File image) throws LibException;

	/**
	 * 更新用户的信息，只有指定的参数定不为空才会被更新<br>
	 *
	 * @param screenName
	 *            昵称，可选，不超过20字。
	 * @param email
	 *            电子邮件，可选，不超过40字。必须是有效的电子邮件地址。
	 * @param url
	 *            网站网址，可选，不超过100字 ，会自动追加"http://"
	 * @param location
	 *            位置，可选. 不超过30字。
	 * @param description
	 *            个人描述，可选，不超过160字。
	 * @return 更新后的用户对象
	 * @throws LibException
	 */
	User updateProfile(String screenName, String email,
		String url, String location, String description) throws LibException;

}
