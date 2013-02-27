package net.dev123.exception;

public final class ExceptionCode {
	private ExceptionCode() { }

	public static final int ACTION_SUCCESS             = 1;        //操作成功
	public static final int ACTION_FAILED              = 0;        //操作失败
	
	public static final int UNKNOWN_EXCEPTION          = 2;        //未知异常;
    public static final int PARAMETER_ERROR            = 3;        //参数错误;
    public static final int PARAMETER_NULL             = 4;        //参数为空

	/** Http协议异常(保留与http状态码一致: 0-999) **/
    public static final int SC_BAD_REQUEST             = 400;       //错误请求，请检查请求地址及参数
	public static final int SC_UNAUTHORIZED            = 401;       //未通过身份验证(用户名或密码错误)
	public static final int SC_FORBIDDEN               = 403;       //服务器拒绝请求或非法访问
	public static final int SC_NOT_FOUND               = 404;       //请求资源未找到，请检查请求地址及参数
	public static final int SC_REQUEST_TIMEOUT         = 408;       //请求超时，请稍候重试
	public static final int SC_INTERNAL_SERVER_ERROR   = 500;       //服务器内部错误，无法完成请求，请稍候重试
	public static final int SC_BAD_GATEWAY             = 502;       //服务器宕机或正在升级
	public static final int SC_SERVICE_UNAVAILABLE     = 503;       //服务暂不可用，服务器过载或停机维护，请稍候重试

	/** 微博异常（保留号: 1000-1999)**/
	// 自定义异常类型代码
	public static final int UNSUPPORTED_API            = 1000;     //未支持的API;
	public static final int FILE_NOT_FOUND             = 1001;     //文件不存在;
	public static final int NOT_A_FILE                 = 1002;     //不是一个文件;

	// 网络问题异常代码
	public static final int NET_ISSUE                  = 1100;      //网络出现异常;
	public static final int NET_SOCKET_TIME_OUT        = 1101;      //套接字超时;
	public static final int NET_I_O_EXCEPTION          = 1102;      //网络I/O异常;
	public static final int NET_UNCONNECTED            = 1103;      //网络未连接;
	public static final int CLIENT_PROTOCOL_EXCEPTION  = 1110;      //客户端协议异常;

	public static final int URL_MALFORMED_ERROR        = 2010;      //URL地址格式不正确;
	public static final int URI_SYNTAX_ERROR           = 2011;      //URI地址语法错误
	public static final int JSON_PARSE_ERROR           = 2020;      //JSON解析错误
	public static final int DATE_PARSE_ERROR           = 2030;      //日期解析错误
	public static final int OAUTH_EXCEPTION            = 2040;      //进行OAuth认证授权出现错误

	public static final int OAUTH_AUTHORIZATION_REQUIRED             = 60000; //需要OAuth认证信息
	public static final int OAUTH_VERSION_REJECTED                   = 60001; //不支持的OAuth版本
	public static final int OAUTH_PARAMETER_ABSENT                   = 60002; //缺少OAuth参数
	public static final int OAUTH_PARAMETER_REJECTED                 = 60003; //包含不支持的OAuth参数
	public static final int OAUTH_TIMESTAMP_REFUSED                  = 60004; //时间戳被拒绝
	public static final int OAUTH_SIGNATURE_METHOD_REJECTED          = 60005; //不支持的OAuth签名算法
	public static final int OAUTH_NONCE_USED                         = 60006; //nonce已被使用过
	public static final int OAUTH_TOKEN_USED                         = 60007; //token已被使用过
	public static final int OAUTH_TOKEN_EXPIRED                      = 60008; //token已过期
	public static final int OAUTH_TOKEN_REVOKED                      = 60009; //token已被废除
	public static final int OAUTH_TOKEN_REJECTED                     = 60010; //不支持的token
	public static final int OAUTH_TOKEN_NOT_AUTHORIZED               = 60011; //未通过认证
	public static final int OAUTH_SIGNATURE_INVALID                  = 60012; //OAuth签名无效
	public static final int OAUTH_CONSUMER_KEY_UNKNOWN               = 60013; //未知的consumer_key
	public static final int OAUTH_CONSUMER_KEY_REJECTED              = 60014; //不支持的consumer_key
	public static final int OAUTH_ADDITIONAL_AUTHORIZATION_REQUIRED  = 60015; //需要附加的认证信息
	public static final int OAUTH_PERMISSION_UNKNOWN                 = 60016; //未知授权
	public static final int OAUTH_PERMISSION_DENIED                  = 60017; //拒绝访问
	public static final int OAUTH_USER_REFUSED                       = 60018; //用户拒绝
	public static final int OAUTH_CONSUMER_KEY_REFUSED               = 60019; //consumer_key被拒绝

	public static final class MicroBlog{
		private MicroBlog() { }
		//微博或SNS平台api 提示的异常信息代码
		public static final int API_PARAMS_ERROR           = 40001;    //参数错误，请参考API文档;
		public static final int API_NOT_OWNER              = 40002;    //不是对象所属者，没有操作权限;
		public static final int API_FORMAT_UNSUPPORT       = 40007;    //格式不支持，仅仅支持XML或JSON格式;
		public static final int API_INTERNAL_ERROR         = 40028;    //内部接口错误;
		public static final int API_DOMAIN_ERROR           = 40043;    //domain参数错误;
		public static final int API_APPKEY_EMPTY           = 40044;    //appkey参数缺失;
		public static final int API_APPKEY_SOURCE_EMPTY    = 40022;    //source参数(appkey)缺失;
		public static final int API_PARAM_ERROR            = 40054;    //参数错误，请参考API文档;
		public static final int API_SIGNATURE_INVALID      = 40107;    //使用的授权签名不合法 signature_invalid
		public static final int API_HTTP_METHOD_UNSUPPORT  = 40307;    //请求的HTTP METHOD不支持;
		public static final int API_RATE_LIMITED           = 40312;    //请求次数超限，请稍候重试  || API调用次数超限
		public static final int API_INVOKE_RATE_TOO_QUICK  = 40401;    //发表太快，被频率限制
		public static final int API_SEARCH_RATE_LIMITED    = 40402;    //搜索微博次数超限，请稍候重试 || Twitter搜索API调用超限


		public static final int API_USER_NOT_EXIST         = 40023;    //用户不存在;
		public static final int API_USER_PARAM_ERROR       = 40026;    //请传递正确的目标用户uid或者screen name;
		public static final int API_USER_S_T_NOT_EXIST     = 40033;    //source_user或者target_user用户不存在 ;
		public static final int API_UID_EMPTY              = 40041;    //uid参数为空;
		public static final int API_USER_NOT_FOLLOWING     = 40071;    //关系错误，user_id必须是你关注的用户;
		public static final int API_USER_ID_EMPTY          = 40069;    //userid是空值;

		public static final int API_CONTENT_EMPTY          = 40012;    //内容为空;
		public static final int API_CONTENT_OVER_LENGTH    = 40013;    //微博内容过长，请确认不超过140个字符;
		public static final int API_CONTENT_ILLEGAL        = 40076;    //含有非法词,比如脏话过多，包含垃圾信息、广告信息等;

		public static final int API_TWEET_NOT_EXIST        = 40031;    //微博不存在或已经被删除了;
		public static final int API_TWEET_NOT_OWNER        = 40036;    //您不是该微博作者;
		public static final int API_TWEET_ILLEGAL          = 40038;    //不合法的微博;
		public static final int API_TWEET_REPEAT           = 40025;    //不能重复发布内容相同的微博;
		public static final int API_TWEET_ID_EMPTY         = 40016;    //微博ID为空;
		public static final int API_TWEET_OVER_THRESHOLD   = 40308;    //发布微博超过上限;
		public static final int API_RETWEET_SELF           = 40034;    //不能转发自己的微博;

		public static final int API_IMAGE_UPLOAD_ERROR     = 40009;    //图片错误，请确保使用multipart上传了图片;
		public static final int API_IMAGE_FORMAT_UNSUPPORT = 40045;    //不支持的图片类型,支持的图片类型有JPG,GIF,PNG;
		public static final int API_IMAGE_OVER_SIZE        = 40008;    //上传的图片过大，图片大小上限为5M;

		public static final int API_MESSAGE_NOT_EXIST      = 40010;    //私信不存在;
		public static final int API_MESSAGE_LIMITED        = 40011;    //私信发布超过上限;
		public static final int API_MESSAGE_RECEIVER_NOT_FOLLOWER   = 40017;    //不能给未关注您的人发私信;UNFOLLOW YOU
		public static final int API_MESSAGE_ILLEGAL        = 40019;    //不合法的私信;
		public static final int API_MESSAGE_NOT_OWNER      = 40021;    //您不是该私信作者;

		public static final int API_COMMENT_NOT_OWNER      = 40015;    //您不是该评论作者;
		public static final int API_COMMENT_ID_EMPTY       = 40020;    //评论ID为空;
		public static final int API_COMMENT_ILLEGAL        = 40037;    //不合法的评论;
		public static final int API_COMMENT_OVER_THRESHOLD = 40305;    //发布评论超过上限;

		public static final int API_ALREADY_FOLLOWED       = 40303;    //已关注此用户;

		public static final int API_IDS_EMPTY              = 40018;    //Ids参数为空;
		public static final int API_IDS_TOO_MANY           = 40024;    //ids过多，请参考API文档;

		public static final int API_LIST_NAME_TOO_LONG     = 40032;    //列表名太长，请确保输入的文本不超过10个字符;
		public static final int API_LIST_DESC_TOO_LONG_    = 40030;    //列表描述太长，请确保输入的文本不超过70个字符;
		public static final int API_LIST_NOT_EXIST         = 40035;    //列表不存在;
		public static final int API_LIST_NAME_DUPLICATED   = 40061;    //列表名冲突;
		public static final int API_LIST_ID_TOO_LONG       = 40062;    //id列表太长了;
		public static final int API_LIST_CREATE_FAILED     = 40074;    //创建list失败;

		public static final int API_GEO_PARAM_ERROR        = 40039;    //地理信息输入错误;
		public static final int API_DB_RECORD_EXISTS       = 40059;    //插入失败，记录已存在;
		public static final int API_DB_ERROR               = 40060;    //数据库错误，请联系系统管理员;
		public static final int API_URLS_EMPTY             = 40063;    //urls是空的;
		public static final int API_URLS_TOO_MANY          = 40064;    //urls太多了;
		public static final int API_URL_EMPTY              = 40066;    //url是空值;
		public static final int API_IP_LIMITED             = 40040;    //IP限制，不能请求该资源;
		public static final int API_IP_EMPTY               = 40065;    //ip是空值;
		public static final int API_TREND_NAME_EMPTY       = 40067;    //trend_name是空值;
		public static final int API_TREND_ID_EMPTY         = 40068;    //trend_id是空值;
		public static final int API_GROUP_PRIVATE_UNSUPPORT    = 40073;    //目前不支持私有分组;
		public static final int API_GROUP_PRIVATE_SUPPORT_ONLY = 40084;    //目前只支持私有分组 ;
		public static final int API_CATEGORY_INVALID       = 40082;    //无效分类!;
		public static final int API_STATUS_CODE_INVALID    = 40083;    //无效状态码;
		public static final int API_SOCIAL_GRAPH_OVER      = 40304;    //关系操作超过上限;
		public static final int API_TAG_EMPTY              = 40027;    //标签参数为空;

		public static final int API_AUTH_VERIFIER_ERROR        = 40029;    //授权验证时错误;
		public static final int API_AUTH_TOKEN_EMPTY           = 40042;    //token参数为空;
		public static final int API_AUTH_REMOVED               = 40072;    //授权关系已经被删除;
		public static final int API_AUTH_TIMESTAMP_ERROR       = 40104;    //OAuth时间戳不正确;
		public static final int API_AUTH_TOKEN_EXPIRED         = 40111;    //OAuth Token已经过期;
		public static final int API_AUTH_PIN_VERIFY_FAILED     = 40114;    //OAuth PIN码认证失败;
		public static final int API_AUTH_PASSWORD_INVALID      = 40309;    //密码不正确;
		public static final int API_AUTH_VERIFY_OVER_THRESHOLD = 40306;    //用户名密码认证超过请求限制;

		public static final int API_INSUFFICIENT_PRIVILEGES    = 40053;    //权限不足，只有创建者有相关权限;
		public static final int API_PERMISSION_NEED_ADMIN      = 40075;    //需要系统管理员的权限;
		public static final int API_PERMISSION_REMIND_FAILED   = 40084;    //提醒失败，需要权限;
		public static final int API_PERMISSION_ACCESS_LIMITED  = 40070;    //第三方应用访问api接口权限受限制;
		public static final int API_PERMISSION_NEED_MORE_AUTHORIZED = 40314;    //该资源需要更高级的授权;authorize

		/**扩展api错误代号**/
		public static final int API_PERMISSION_ACCESS_DENIED   = 40500;     //api接口访问被禁止;
		public static final int API_USER_IS_FOLLOWING          = 40510;     //用户已经关注了;
		public static final int API_USER_SCREEN_NAME_EXIST     = 40511;     //此昵称已经被占用
		public static final int API_ITEM_NOT_EXIST             = 40520;     //记录不存在或已被删除
		public static final int API_ITEM_REVIEWING             = 40530;     //消息审核中

	}

	public static final class YiBoMe{
		private YiBoMe() { }
		/**Service逻辑类异常*/
		public static final int SERVER_ERROR                   = 50000; // 服务器错误，服务的未知错误均以此为代号
		public static final int USERNAME_NULL                  = 50001; //用户名为空
		public static final int USERNAME_INVALID               = 50002; //无效的用户名
		public static final int USERNAME_EXISTS                = 50003; //用户名已存在
		public static final int PASSWORD_NULL                  = 50004; //密码为空
		public static final int PASSWORD_INVALID               = 50005; //无效密码
		public static final int CONFIRMED_PASSWORD_NULL        = 50006; //确认密码为空
		public static final int CONFIRMED_PASSWORD_UNMATCH     = 50007; //两次输入密码不匹配
		public static final int EMAIL_NULL                     = 50008; //Email地址为空
		public static final int EMAIL_INVALID                  = 50009; //Email地址无效
		public static final int USERNAME_NOT_EXISTS            = 50010; //用户名不存在
		public static final int PASSWORD_INCORRECT             = 50011; //密码错误
		public static final int PASSPORT_INACTIVE              = 50012; //通行证未激活
		public static final int PASSPORT_FROZEN                = 50013; //通行证被冻结
		public static final int PASSPORT_REVOKED               = 50014; //通行证被废除
		public static final int ACCOUNTS_NULL                  = 50015; //Accounts参数为空
		public static final int GROUPS_NULL                    = 50016; //Groups参数为空
		public static final int SP_NULL                        = 50017; //ServiceProvider参数为空
		public static final int GROUP_ID_NULL                  = 50018; //Group_Id参数为空
		public static final int USERS_NULL                     = 50019; //Users参数为空
		public static final int SP_INVALID                     = 50020; //无效的ServiceProvider值
		public static final int GROUP_NOT_FOUND                = 50021; //未找到指定组
		public static final int GROUP_ID_INVALID               = 50022; //组ID无效
		public static final int EMAIL_EXISTS                   = 50023; //Email已存在
		public static final int SYNC_ACCOUNT_NULL              = 50024; //同步的账户为空
		public static final int SYNC_ACCOUNT_NOT_EXIST         = 50025; //同步的账户不存在
		public static final int PASSPORT_POINTS_NOT_ENOUGH     = 50026; //积分不足
		public static final int SAME_PASSPORT_APP_CONFIG       = 50027; //该APP已经存在
		public static final int PASSPORT_CANNOT_RESET          = 50028; //不能重置密码
		
	    /**dao逻辑类异常*/
	    //Passport错误代号:80000~80099
	    public static final int PASSPORT_ID_NOT_EXIST          = 80000;	  //通行证Id不存在  
	    //Account错误代号:80100~80199
	    //POINT_ORDER_INFO错误代号:80200~80299
	    public static final int POINT_ORDER_TYPE_INVALID       = 80200;   //积分订单类型不合法
	    public static final int POINT_ORDER_COUNT_INVALID      = 80201;   //积分订单数量 不合法
	    public static final int POINT_ORDER_THIRDPARTY_ORDER_ID_NULL   = 80202; //积分订单的第三方订单id为空
	    public static final int POINT_ORDER_THIRDPARTY_ORDER_HAS_EXIST = 80203; //积分订单的第三方订单已经存在
	    public static final int POINT_ORDER_POINTS_INVALID     = 80204;   //积分订单积分数不合法
	    public static final int POINT_ORDER_TODAY_LOGIN_EXIST  = 80205;   //当日积分赠送已经存在
	}
}
