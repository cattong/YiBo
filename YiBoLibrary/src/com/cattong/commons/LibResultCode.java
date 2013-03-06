package com.cattong.commons;

public final class LibResultCode {
	private LibResultCode() { }

	public static final int ACTION_SUCCESS             = 1;        //操作成功
	public static final int ACTION_FAILED              = 0;        //操作失败
	
	public static final int E_UNKNOWN_ERROR            = 2;        //未知异常
    public static final int E_PARAM_ERROR              = 3;        //参数错误
    public static final int E_PARAM_NULL               = 4;        //参数为空

	/** Http协议异常(保留与http状态码一致: 0-999) **/
    public static final int SC_BAD_REQUEST             = 400;       //错误请求，请检查请求地址及参数
	public static final int SC_UNAUTHORIZED            = 401;       //未通过身份验证(用户名或密码错误)
	public static final int SC_FORBIDDEN               = 403;       //服务器拒绝请求或非法访问
	public static final int SC_NOT_FOUND               = 404;       //请求资源未找到，请检查请求地址及参数
	public static final int SC_REQUEST_TIMEOUT         = 408;       //请求超时，请稍候重试
	public static final int SC_INTERNAL_SERVER_ERROR   = 500;       //服务器内部错误，无法完成请求，请稍候重试
	public static final int SC_BAD_GATEWAY             = 502;       //服务器宕机或正在升级
	public static final int SC_SERVICE_UNAVAILABLE     = 503;       //服务暂不可用，服务器过载或停机维护，请稍候重试

	/**（保留号: 1000-1999)**/
	// 自定义异常类型代码
	public static final int API_UNSUPPORTED            = 1000;     //API暂时没有支持
	public static final int API_VERSION_TOO_LOW        = 1001;     //您使用的版本过低，建议升级
	public static final int FILE_NOT_FOUND             = 1050;     //文件不存在
	public static final int FILE_TYPE_INVALID          = 1051;     //文件类型不正确

	// 网络问题异常代码
	public static final int NET_ISSUE                      = 1100;      //网络出现异常
	public static final int NET_SOCKET_TIME_OUT            = 1101;      //网络超时,请确认网络是否稳定
	public static final int NET_I_O_EXCEPTION              = 1102;      //网络出现异常,请确认网络是否稳定
	public static final int NET_UNCONNECTED                = 1103;      //网络未连接
	public static final int NET_CLIENT_PROTOCOL_EXCEPTION  = 1110;      //客户端协议异常
	public static final int NET_HTTPS_UNDER_CMWAP          = 1111;      //cmwap下无法使用https
	
	public static final int URL_MALFORMED_ERROR            = 2010;      //URL地址格式不正确
	public static final int URI_SYNTAX_ERROR               = 2011;      //URI地址语法错误
	public static final int JSON_PARSE_ERROR               = 2020;      //JSON解析错误
	public static final int DATE_PARSE_ERROR               = 2030;      //日期解析错误
	public static final int THIRDPARTY_SYSTEM_ERROR        = 2050;      //第三方系统交互异常
	
	public static final int SIGN_NOT_EXIST                 = 3001;      //参数未进行签名
	public static final int SIGN_VERIFY_ERROR              = 3002;      //签名验证错误，可能版本过低，建议升级
	
	public static final int OAUTH_EXCEPTION                          = 30000;  //进行OAuth认证授权出现错误
	public static final int OAUTH_AUTHORIZATION_REQUIRED             = 30001; //需要OAuth认证信息
	public static final int OAUTH_VERSION_REJECTED                   = 30002; //不支持的OAuth版本
	public static final int OAUTH_PARAMETER_ABSENT                   = 30003; //缺少OAuth参数
	public static final int OAUTH_PARAMETER_REJECTED                 = 30004; //包含不支持的OAuth参数
	public static final int OAUTH_TIMESTAMP_REFUSED                  = 30005; //时间戳被拒绝
	public static final int OAUTH_SIGNATURE_METHOD_REJECTED          = 30006; //不支持的OAuth签名算法
	public static final int OAUTH_NONCE_USED                         = 30007; //nonce已被使用过
	public static final int OAUTH_TOKEN_USED                         = 30008; //token已被使用过
	public static final int OAUTH_TOKEN_EXPIRED                      = 30009; //token已过期
	public static final int OAUTH_TOKEN_REVOKED                      = 30010; //token已被废除
	public static final int OAUTH_TOKEN_REJECTED                     = 30011; //不支持的token
	public static final int OAUTH_TOKEN_NOT_AUTHORIZED               = 30012; //未通过认证
	public static final int OAUTH_SIGNATURE_INVALID                  = 30013; //OAuth签名无效
	public static final int OAUTH_CONSUMER_KEY_UNKNOWN               = 30014; //未知的consumer_key
	public static final int OAUTH_CONSUMER_KEY_REJECTED              = 30015; //不支持的consumer_key
	public static final int OAUTH_ADDITIONAL_AUTHORIZATION_REQUIRED  = 30016; //需要附加的认证信息
	public static final int OAUTH_PERMISSION_UNKNOWN                 = 30017; //未知授权
	public static final int OAUTH_PERMISSION_DENIED                  = 30018; //拒绝访问
	public static final int OAUTH_USER_REFUSED                       = 30019; //用户拒绝
	public static final int OAUTH_CONSUMER_KEY_REFUSED               = 30020; //consumer_key被拒绝

	//微博或SNS平台api 提示的异常信息代码
	public static final int API_MB_PARAMS_ERROR           = 40001;    //参数错误，请参考API文档;
	public static final int API_MB_NOT_OWNER              = 40002;    //不是对象所属者，没有操作权限;
	public static final int API_MB_FORMAT_UNSUPPORT       = 40007;    //格式不支持，仅仅支持XML或JSON格式;
	public static final int API_MB_INTERNAL_ERROR         = 40028;    //内部接口错误;
	public static final int API_MB_DOMAIN_ERROR           = 40043;    //domain参数错误;
	public static final int API_MB_APPKEY_EMPTY           = 40044;    //appkey参数缺失;
	public static final int API_MB_APPKEY_SOURCE_EMPTY    = 40022;    //source参数(appkey)缺失;
	public static final int API_MB_PARAM_ERROR            = 40054;    //参数错误，请参考API文档;
	public static final int API_MB_SIGNATURE_INVALID      = 40107;    //使用的授权签名不合法 signature_invalid
	public static final int API_MB_HTTP_METHOD_UNSUPPORT  = 40307;    //请求的HTTP METHOD不支持;
	public static final int API_MB_RATE_LIMITED           = 40312;    //请求次数超限，请稍候重试  || API调用次数超限
	public static final int API_MB_INVOKE_RATE_TOO_QUICK  = 40401;    //发表太快，被频率限制
	public static final int API_MB_SEARCH_RATE_LIMITED    = 40402;    //搜索微博次数超限，请稍候重试 || Twitter搜索API调用超限


	public static final int API_MB_USER_NOT_EXIST         = 40023;    //用户不存在;
	public static final int API_MB_USER_PARAM_ERROR       = 40026;    //请传递正确的目标用户uid或者screen name;
	public static final int API_MB_USER_S_T_NOT_EXIST     = 40033;    //source_user或者target_user用户不存在 ;
	public static final int API_MB_UID_EMPTY              = 40041;    //uid参数为空;
	public static final int API_MB_USER_NOT_FOLLOWING     = 40071;    //关系错误，user_id必须是你关注的用户;
	public static final int API_MB_USER_ID_EMPTY          = 40069;    //userid是空值;
                              
	public static final int API_MB_CONTENT_EMPTY          = 40012;    //内容为空;
	public static final int API_MB_CONTENT_OVER_LENGTH    = 40013;    //微博内容过长，请确认不超过140个字符;
	public static final int API_MB_CONTENT_ILLEGAL        = 40076;    //含有非法词,比如脏话过多，包含垃圾信息、广告信息等;

	public static final int API_MB_TWEET_NOT_EXIST        = 40031;    //微博不存在或已经被删除了;
	public static final int API_MB_TWEET_NOT_OWNER        = 40036;    //您不是该微博作者;
	public static final int API_MB_TWEET_ILLEGAL          = 40038;    //不合法的微博;
	public static final int API_MB_TWEET_REPEAT           = 40025;    //不能重复发布内容相同的微博;
	public static final int API_MB_TWEET_ID_EMPTY         = 40016;    //微博ID为空;
	public static final int API_MB_TWEET_OVER_THRESHOLD   = 40308;    //发布微博超过上限;
	public static final int API_MB_RETWEET_SELF           = 40034;    //不能转发自己的微博;

	public static final int API_MB_IMAGE_UPLOAD_ERROR     = 40009;    //图片错误，请确保使用multipart上传了图片;
	public static final int API_MB_IMAGE_FORMAT_UNSUPPORT = 40045;    //不支持的图片类型,支持的图片类型有JPG,GIF,PNG;
	public static final int API_MB_IMAGE_OVER_SIZE        = 40008;    //上传的图片过大，图片大小上限为5M;

	public static final int API_MB_MESSAGE_NOT_EXIST      = 40010;    //私信不存在;
	public static final int API_MB_MESSAGE_LIMITED        = 40011;    //私信发布超过上限;
	public static final int API_MB_MESSAGE_RECEIVER_NOT_FOLLOWER   = 40017;    //不能给未关注您的人发私信;UNFOLLOW YOU
	public static final int API_MB_MESSAGE_ILLEGAL        = 40019;    //不合法的私信;
	public static final int API_MB_MESSAGE_NOT_OWNER      = 40021;    //您不是该私信作者;

	public static final int API_MB_COMMENT_NOT_OWNER      = 40015;    //您不是该评论作者;
	public static final int API_MB_COMMENT_ID_EMPTY       = 40020;    //评论ID为空;
	public static final int API_MB_COMMENT_ILLEGAL        = 40037;    //不合法的评论;
	public static final int API_MB_COMMENT_OVER_THRESHOLD = 40305;    //发布评论超过上限;

	public static final int API_MB_ALREADY_FOLLOWED       = 40303;    //已关注此用户;

	public static final int API_MB_IDS_EMPTY              = 40018;    //Ids参数为空;
	public static final int API_MB_IDS_TOO_MANY           = 40024;    //ids过多，请参考API文档;

	public static final int API_MB_LIST_NAME_TOO_LONG     = 40032;    //列表名太长，请确保输入的文本不超过10个字符;
	public static final int API_MB_LIST_DESC_TOO_LONG_    = 40030;    //列表描述太长，请确保输入的文本不超过70个字符;
	public static final int API_MB_LIST_NOT_EXIST         = 40035;    //列表不存在;
	public static final int API_MB_LIST_NAME_DUPLICATED   = 40061;    //列表名冲突;
	public static final int API_MB_LIST_ID_TOO_LONG       = 40062;    //id列表太长了;
	public static final int API_MB_LIST_CREATE_FAILED     = 40074;    //创建list失败;

	public static final int API_MB_GEO_PARAM_ERROR        = 40039;    //地理信息输入错误;
	public static final int API_MB_DB_RECORD_EXISTS       = 40059;    //插入失败，记录已存在;
	public static final int API_MB_DB_ERROR               = 40060;    //数据库错误，请联系系统管理员;
	public static final int API_MB_URLS_EMPTY             = 40063;    //urls是空的;
	public static final int API_MB_URLS_TOO_MANY          = 40064;    //urls太多了;
	public static final int API_MB_URL_EMPTY              = 40066;    //url是空值;
	public static final int API_MB_IP_LIMITED             = 40040;    //IP限制，不能请求该资源;
	public static final int API_MB_IP_EMPTY               = 40065;    //ip是空值;
	public static final int API_MB_TREND_NAME_EMPTY       = 40067;    //trend_name是空值;
	public static final int API_MB_TREND_ID_EMPTY         = 40068;    //trend_id是空值;
	public static final int API_MB_GROUP_PRIVATE_UNSUPPORT    = 40073;    //目前不支持私有分组;
	public static final int API_MB_GROUP_PRIVATE_SUPPORT_ONLY = 40084;    //目前只支持私有分组 ;
	public static final int API_MB_CATEGORY_INVALID       = 40082;    //无效分类!;
	public static final int API_MB_STATUS_CODE_INVALID    = 40083;    //无效状态码;
	public static final int API_MB_SOCIAL_GRAPH_OVER      = 40304;    //关系操作超过上限;
	public static final int API_MB_TAG_EMPTY              = 40027;    //标签参数为空;

	public static final int API_MB_AUTH_VERIFIER_ERROR        = 40029;    //授权验证时错误;
	public static final int API_MB_AUTH_TOKEN_EMPTY           = 40042;    //token参数为空;
	public static final int API_MB_AUTH_REMOVED               = 40072;    //授权关系已经被删除;
	public static final int API_MB_AUTH_TIMESTAMP_ERROR       = 40104;    //OAuth时间戳不正确;
	public static final int API_MB_AUTH_TOKEN_EXPIRED         = 40111;    //OAuth Token已经过期;
	public static final int API_MB_AUTH_PIN_VERIFY_FAILED     = 40114;    //OAuth PIN码认证失败;
	public static final int API_MB_AUTH_PASSWORD_INVALID      = 40309;    //密码不正确;
	public static final int API_MB_AUTH_VERIFY_OVER_THRESHOLD = 40306;    //用户名密码认证超过请求限制;

	public static final int API_MB_INSUFFICIENT_PRIVILEGES    = 40053;    //权限不足，只有创建者有相关权限;
	public static final int API_MB_PERMISSION_NEED_ADMIN      = 40075;    //需要系统管理员的权限;
	public static final int API_MB_PERMISSION_REMIND_FAILED   = 40084;    //提醒失败，需要权限;
	public static final int API_MB_PERMISSION_ACCESS_LIMITED  = 40070;    //第三方应用访问api接口权限受限制;
	public static final int API_MB_PERMISSION_NEED_MORE_AUTHORIZED = 40314;    //该资源需要更高级的授权;authorize

	/**扩展api错误代号**/
	public static final int API_MB_PERMISSION_ACCESS_DENIED   = 40500;     //api接口访问被禁止;
	public static final int API_MB_USER_IS_FOLLOWING          = 40510;     //用户已经关注了;
	public static final int API_MB_USER_SCREEN_NAME_EXIST     = 40511;     //此昵称已经被占用
	public static final int API_MB_ITEM_NOT_EXIST             = 40520;     //记录不存在或已被删除
	public static final int API_MB_ITEM_REVIEWING             = 40530;     //消息审核中



}
