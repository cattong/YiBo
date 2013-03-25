package com.cattong.sns.impl.qqzone;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class QQZoneOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -9095380979242451311L;

	public QQZoneOAuthConfig() {
		this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
		
		this.setConsumerKey("221523");
		this.setConsumerSecret("887f5a7424c087bf6bc6393bc0696801");
		this.setCallbackUrl("http://www.yibo.me/authorize/getAccessToken.do");
		this.setAccessTokenUrl("https://graph.qq.com/oauth2.0/token");
		this.setAuthorizeUrl("https://graph.qq.com/oauth2.0/authorize?display=mobile");

		String scope = 
			"get_user_info," + // 获取用户信息
			"list_album," + // 获取用户QQ空间相册列表
			"add_share," + // 同步动态到QQ空间
			"add_weibo," + // 发表一条消息到腾讯微博
			"add_topic," + // 发表一条说说到QQ空间
			"add_one_blog," + // 发表一篇日志到QQ空间
			"add_album," + // 创建一个QQ空间相册
			"upload_pic" // 上传一张照片到QQ空间相册
		;

		this.setOAuthScope(scope);
	}

}
