package com.cattong.commons;

public enum ServiceProvider {

	None(-1, null, ServiceProvider.CATEGORY_WEIBO, null), // 空，仅用于非SP请求或无需指定SP的情况
	
	//微博
	Sina(ServiceProvider.SP_SINA, "新浪微博", ServiceProvider.CATEGORY_WEIBO, "外号猫哥"),
	Sohu(ServiceProvider.SP_SOHU, "搜狐微博", ServiceProvider.CATEGORY_WEIBO, "YiBo微博客户端"),
	NetEase(ServiceProvider.SP_NETEASE, "网易微博", ServiceProvider.CATEGORY_WEIBO, "YiBo微博客户端"),
	Tencent(ServiceProvider.SP_TENCENT, "腾讯微博", ServiceProvider.CATEGORY_WEIBO, "yiboclient"),
	Twitter(ServiceProvider.SP_TWITTER, "Twitter", ServiceProvider.CATEGORY_WEIBO, "yiboclient"),
	Fanfou(ServiceProvider.SP_FANFOU, "饭否", ServiceProvider.CATEGORY_WEIBO, "YiBo微博客户端"),

	//SNS
	RenRen(ServiceProvider.SP_RENREN, "人人网", ServiceProvider.CATEGORY_SNS, ""),
	KaiXin(ServiceProvider.SP_KAIXIN, "开心网", ServiceProvider.CATEGORY_SNS, ""),
	QQZone(ServiceProvider.SP_QQZONE, "QQ空间", ServiceProvider.CATEGORY_SNS, ""),
	Facebook(ServiceProvider.SP_FACEBOOK, "Facebook", ServiceProvider.CATEGORY_SNS, "");

	private ServiceProvider(int spNo, String spName, String spCategory, String officalName) {
		this.spNo = spNo;
		this.spName = spName;
		this.spCategory = spCategory;
		this.officalName = officalName;
	}

	/** 服务提供商编号 */
	private int spNo;
	private String spName;
	private String spCategory;
	private String officalName;

	public int getSpNo() {
		return spNo;
	}

	public String getSpName() {
		return spName;
	}

	public String getSpCategory() {
		return spCategory;
	}

	public String getOfficalName() {
		return officalName;
	}
	
	//平台编号
	public static final int SP_SINA            = 1;     // 新浪的SP编号
	public static final int SP_TENCENT         = 2;     // 腾讯的SP编号
	public static final int SP_QQZONE          = 3;     // QQ空间的SP编号
	public static final int SP_TWITTER         = 4;     // 推特的SP编号
	public static final int SP_SOHU            = 20;     // 搜狐的SP编号
	public static final int SP_NETEASE         = 21;     // 网易的SP编号	
	
	public static final int SP_FANFOU          = 22;     // 饭否的SP编号

	public static final int SP_RENREN          = 23;     // 人人网的SP编号
	public static final int SP_KAIXIN          = 24;     // 开心网的SP编号
	
	public static final int SP_FACEBOOK        = 25;     // Facebook的SP编号

	public static final String CATEGORY_WEIBO = "weibo";
	public static final String CATEGORY_SNS = "sns";

	public static ServiceProvider getServiceProvider(int spNo){
		ServiceProvider sp = null;
		switch(spNo){
		case SP_SINA:
			sp = Sina;
			break;
		case SP_SOHU:
			sp = Sohu;
			break;
		case SP_NETEASE:
			sp = NetEase;
			break;
		case SP_TENCENT:
			sp = Tencent;
			break;
		case SP_TWITTER:
			sp = Twitter;
			break;
		case SP_FANFOU:
			sp = Fanfou;
			break;
		case SP_RENREN:
			sp = RenRen;
			break;
		case SP_KAIXIN:
			sp = KaiXin;
			break;
		case SP_QQZONE:
			sp = QQZone;
			break;
		case SP_FACEBOOK:
			sp = Facebook;
			break;
		default:
			sp = None;
			break;
		}
		return sp;
	}

	public boolean isSns() {
		return CATEGORY_SNS.equals(spCategory);
	}

	public boolean isWeibo() {
		return CATEGORY_WEIBO.equals(spCategory);
	}
}
