package com.cattong.sns.impl.renren;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class RenRenOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = 5128879009164019087L;

	public RenRenOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
		
		this.setConsumerKey("525209e407784e1099679ab7a6723438");
		this.setConsumerSecret("e4266569bcea4bdcb4aa00ce222d48df");
		this.setCallbackUrl("http://www.yibo.me/getAccessToken.do");
		this.setAccessTokenUrl("https://graph.renren.com/oauth/token");
		this.setAuthorizeUrl("https://graph.renren.com/oauth/authorize?display=touch");

		String scope = 
			"publish_blog,"+ // 发布日志
			"publish_checkin,"+ // 发布报到
			"publish_feed,"+ // 发送新鲜事
			"publish_share,"+ // 发送分享
			"write_guestbook,"+ // 留言
			"send_invitation,"+ //,"+送邀请
			"send_request,"+ // 发送好友申请、圈人请求等时
			"send_message,"+ // 发送站内信
			"photo_upload,"+ // 上传照片
			"status_update,"+ // 发布状态
			"create_album,"+ // 发布相册
			"publish_comment,"+ // 发布评论
			"operate_like,"+ // 执行喜欢操作

			"read_user_blog,"+ // 获取用户日志
			"read_user_checkin,"+ // 获取用户报到信息
			"read_user_feed,"+ // 获取用户新鲜事
			"read_user_feed,"+ // 获取用户新鲜事
			"read_user_guestbook,"+ // 获取用户留言板
			"read_user_invitation,"+ // 获取用户被邀请的状况
			"read_user_like_history,"+ // 获取用户喜欢的历史信息
			"read_user_message,"+ // 获取用户站内信
			"read_user_notification,"+ // 获取用户已收到的通知
			"read_user_photo,"+ // 获取用户相片相关信息
			"read_user_status,"+ // 获取用户状态相关信息
			"read_user_album,"+ // 获取用户相册相关信息
			"read_user_comment,"+ // 获取用户评论相关信息
			"read_user_share" //  获取用户分享相关信息
		;

		this.setOAuthScope(scope);
	}

}
