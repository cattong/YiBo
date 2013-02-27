package net.dev123.sns.qqzone;

import net.dev123.commons.oauth.config.OAuthConfigurationBase;

public class QQZoneOAuthConfiguration extends OAuthConfigurationBase {

	public QQZoneOAuthConfiguration() {
		this.setOAuthConsumerKey("221523");
		this.setOAuthConsumerSecret("887f5a7424c087bf6bc6393bc0696801");
		this.setOAuthCallbackURL("http://www.yibo.me/authorize/getAccessToken.do");
		this.setOAuthAccessTokenURL("https://graph.qq.com/oauth2.0/token");
		this.setOAuthAuthorizeURL("https://graph.qq.com/oauth2.0/authorize?display=mobile");

		String[] scopes = new String[]{
			"get_user_info", // 获取用户信息
			"list_album", // 获取用户QQ空间相册列表
			"add_share", // 同步动态到QQ空间
			"add_weibo", // 发表一条消息到腾讯微博
			"add_topic", // 发表一条说说到QQ空间
			"add_one_blog", // 发表一篇日志到QQ空间
			"add_album", // 创建一个QQ空间相册
			"upload_pic" // 上传一张照片到QQ空间相册
		};

		this.setOAuthScopes(scopes);
		this.setOAuthScopeSeparator(",");
	}

}
