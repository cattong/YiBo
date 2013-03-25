package com.shejiaomao.weibo.common;

import java.util.regex.Pattern;

import com.cattong.commons.oauth.OAuth;
import com.cattong.commons.util.FileUtil;
import android.app.Activity;
import android.net.Uri;

public final class Constants {
	private Constants() { }

	//public static final boolean DEBUG  = false;   // 调试模式

    //应用特有配置:intent-filter action名
	public static final String ACTION_WEIBO_MAIN = "com.shejiaomao.weibo.MAIN";
	public static final String ACTION_RECEIVER_AUTO_UPDATE  = "com.shejiaomao.weibo.AUTO_UPDATE";
    public static final String ACTION_RECEIVER_AUTO_UPDATE_NOTIFY = "com.shejiaomao.weibo.AUTO_UPDATE_NOTIFY";

    //应用设置数据存储 SharedPreferences名称/
	public static final String PREFS_NAME_APP_SETTING   = "WEIBO_APP_SETTING";
	public static final String PREFS_NAME_EMAIL = "EMAIL";
	public static final String PREFS_NAME_USERNAME = "USERNAME";
	public static final String PREFS_NAME_POINTS = "POINTS";
	public static final String PREFS_NAME_TOTAL_POINTS = "TOTAL_POINTS";
	public static final String PREFS_NAME_MILITARY_RANK = "MILITARY_RANK";
	public static final String PREFS_NAME_POINTS_TITLE = "POINTS_TITLE";
	public static final String PREFS_NAME_ACCESS_TOKEN = "ACCESS_TOKEN";
	
	public static final String PREFS_KEY_LOCALE         = "LOCALE";          // 语言设置
	public static final String PREFS_KEY_FONT_SIZE      = "FONT_SIZE";          // 设置字体大小
    public static final String PREFS_KEY_SHOW_HEAD      = "SHOW_HEAD";          // 微博列表是否显示头像
    public static final String PREFS_KEY_SHOW_THUMBNAIL = "SHOW_THUMBNAIL";     // 微博列表是否显示缩略图
	public static final String PREFS_KEY_UPDATE_COUNT   = "UPDATE_COUNT";       // 每次更新的数量
    public static final String PREFS_KEY_IMAGE_UPLOAD_QUALITY     = "IMAGE_UPLOAD_QUALITY";      // 上传图片的质量
	public static final String PREFS_KEY_IMAGE_DOWNLOAD_QUALITY   = "IMAGE_DOWNLOAD_QUALITY"; //下载图片的质量
	public static final String PREFS_KEY_AUTO_SCREEN_ORIENTATION  = "AUTO_SCREEN_ORIENTATION"; // 自动横竖屏转换
	public static final String PREFS_KEY_AUTO_LOCATE       = "AUTO_LOCATE";     // 自动定位
	public static final String PREFS_KEY_CACHE_STRATEGY    = "CACHE_STRATEGY";  // 缓存策略
	public static final String PREFS_KEY_CLEAR_CACHE       = "CLEAR_CACHE";     // 清理缓存
	public static final String PREFS_KEY_CLEAR_IMAGE_CACHE = "CLEAR_IMAGE_CACHE";  // 清理图片缓存
	public static final String PREFS_KEY_DEFAULT_ACCOUNT   = "DEFAULT_ACCOUNT_ID"; // 默认帐户ID
	public static final String PREFS_KEY_EXIT_ON_BACK      = "EXIT_ON_BACK";    //首页下，按返回键退出应用，而非在后运行
	public static final String PREFS_KEY_VERSION_CHECK_ON_STARTUP  = "VERSION_CHECK_ON_STARTUP";    //启动时检查版本更新
	public static final String PREFS_KEY_ENABLE_UPDATES      = "ENABLE_UPDATES";   // 启用更新
	public static final String PREFS_KEY_UPDATE_INTERVAL     = "UPDATE_INTERVAL";  // 检测时间间隔
	public static final String PREFS_KEY_CHECK_STATUSES      = "CHECK_STATUSES";   // 检查微博更新
	public static final String PREFS_KEY_CHECK_COMMENTS      = "CHECK_COMMENTS";   // 检查评论更新
	public static final String PREFS_KEY_CHECK_MENTIONS      = "CHECK_MENTIONS";   // 检查提到我的更新
	public static final String PREFS_KEY_CHECK_MESSAGES      = "CHECK_MESSAGESS";  // 检查私信更新
	public static final String PREFS_KEY_CHECK_FOLLOWERS     = "CHECK_FOLLOWERS";  // 检查新的关注者
	public static final String PREFS_KEY_SHOW_NOTIFICATIONS  = "SHOW_NOTIFICATIONS";  // 启用提醒
	public static final String PREFS_KEY_REFRESH_ON_FIRST_ENTER = "REFRESH_ON_FIRST_ENTER";  // 首次进入帐号时刷新
	public static final String PREFS_KEY_USE_SLIDER         = "USE_SLIDER";        // 启用列表滑块
	public static final String PREFS_KEY_ENABLE_GESTURE     = "ENABLE_GESTURE";    // 启用手势操作
	public static final String PREFS_KEY_SYNC_TO_ALL        = "SYNC_TO_ALL";       // 默认同步至所有帐户
	public static final String PREFS_KEY_VIBRATE            = "VIBRATE";           // 振动提醒
	public static final String PREFS_KEY_RINGTONE           = "RINGTONE";          // 铃声提醒
	public static final String PREFS_KEY_RINGTONE_URI       = "RINGTONE_URI";      // 铃声
	public static final String PREFS_KEY_LED                = "LED";               // LED灯闪烁提醒
	public static final String PREFS_KEY_SHOW_STATUS_ICON   = "SHOW_STATUS_ICON";  // 显示通知栏图标
	public static final String PREFS_KEY_AUTO_LOAD_MORE     = "AUTO_LOAD_MORE";    // 显示通知栏图标
	public static final String PREFS_KEY_REFRESH_ON_SHAKE   = "REFRESH_ON_SHAKE";  // 晃动刷新
	public static final String PREFS_KEY_IMAGE_FOLDER       = "IMAGE_FOLDER";      // 图片保存位置
	public static final String PREFS_KEY_DETECT_IMAGE_INFO  = "DETECT_IMAGE_INFO"; // 检测大图信息
	public static final String PREFS_KEY_AUTO_LOAD_COMMENTS = "AUTO_LOAD_COMMENTS"; // 检测大图信息

	public static final String PREFS_NAME_APP_TEMP          = "SHEJIAOMAO_APP_TEMP";	   //临时存储数据SharedPreferences名称
	public static final String PREFS_KEY_TEMP_EDIT_BLOG     = "TEMP_EDIT_BLOG";    // 写博客临时保存内容
	public static final String PREFS_KEY_IMAGE_PATH         = "IMAGE_PATH";        //图片路径
	public static final String PREFS_KEY_IMAGE_ROTATION     = "IMAGE_ROTATION";    //图片旋转角度
	public static final String PREFS_KEY_CURRENT_ACCOUNT    = "CURRENT_ACCOUNT";        //图片路径
	public static final String PREFS_KEY_OAUTH_TOKEN        = OAuth.OAUTH_TOKEN;
	public static final String PREFS_KEY_OAUTH_TOKEN_SECRET = OAuth.OAUTH_TOKEN_SECRET;
	public static final String PREFS_KEY_MAKE_DEFAULT       = "MAKE_DEFAULT";     //设置为默认帐户
	public static final String PREFS_KEY_FOLLOW_OFFICAL     = "FOLLOW_OFFICAL";   //关注官方微博
	public static final String PREFS_KEY_ACCOUNT_ADDED      = "NEW_ACCOUNT_ID";     //新增的帐户ID


	public static final int DEFAULT_UPDATE_INTERVAL       = 180;    // 默认检测时间间隔，以秒为单位
	public static final long IMAGE_SIZE_THRESHOLD         = 100 * FileUtil.ONE_KB; //图片尺寸显示阀值

	//网络连接设置
	public static final int CONNECTION_POOL_SIZE          = 128; // HTTP连接连接池大小
	public static final int CONNECTION_TIME_OUT           = 30000; // 连接池连接超时时间，以毫秒为单位
	public static final int CONNECTION_EVICT_INTERVAL     = 120000; // 连接池回收连接时间间隔，以毫秒为单位

	//微博api设置参数
	public static final int EDIT_TEXT_MAX_LENGTH        = 180;    //编辑框可写的最大长度;
	public static final int STATUS_TEXT_MAX_LENGTH      = 140;    //微博内容允许最大长度
	public static final int PAGING_DEFAULT_COUNT        = 20;     //分页默认的页数量

	//加密配置
	public static final byte[] KEY_BYTES  = { 0x6f, 0x68, 0x6d, 0x79, 0x67, 0x6f, 0x64, 0x21 };

    //Activity 跳转的request code
	//首页跳转请求
	public static final int REQUEST_CODE_MY_HOME                = 1000;
	public static final int REQUEST_CODE_METIONS                = 1001;
	public static final int REQUEST_CODE_DIRECT_MESSAGE         = 1002;
	public static final int REQUEST_CODE_BROADCAST_HALL         = 1003;
	public static final int REQUEST_CODE_MORE                   = 1004;
    public static final int REQUEST_CODE_NET_SETTINGS           = 1005;    //网络设置;
    public static final int REQUEST_CODE_MICRO_BLOG             = 1006;
    public static final int REQUEST_CODE_SETTINGS               = 1007;    //设置
    public static final int REQUEST_CODE_ACCOUNTS               = 1008;    //帐号：帐号管理;
    public static final int REQUEST_CODE_PROFILE_EDIT           = 1009;    //编辑个人资料
    //写微博
    public static final int REQUEST_CODE_IMG_SELECTOR           = 1010;    //博客编辑时，选择图片请求
    public static final int REQUEST_CODE_CAMERA                 = 1011;    //照相请求;
    public static final int REQUEST_CODE_IMAGE_EDIT             = 1012;    //编辑图片
    public static final int REQUEST_CODE_IMAGE_CROP             = 1013;    //裁剪图片
    //私信操作
    public static final int REQUEST_CODE_USER_SELECTOR          = 1040;    //选择用户DisplayName
    public static final int REQUEST_CODE_USER_SELECTOR_MESSAGE  = 1041;    //私信中选择用户DisplayName
    public static final int REQUEST_CODE_SPLASH                 = 1050;    //splash跳转
    //微博详细
    public static final int REQUEST_CODE_COMMENT_OF_STATUS      = 1060;    //评论一条微博
	//Notification的request code
	public static final int NOTIFICATION_NEW_MICRO_BLOG         = 2000;
	public static final int NOTIFICATION_NEW_METION             = 2001;
	public static final int NOTIFICATION_NEW_DIRECT_MESSAGE     = 2002;

	public static final int REQUEST_CODE_OAUTH_AUTHORIZE        = 3000;    //OAuth授权
	public static final int REQUEST_CODE_PASSPORT_LOGIN         = 3001;    //通行证登录
	public static final int REQUEST_CODE_ACCOUNT_ADD            = 3002;    //帐号添加
	public static final int REQUEST_CODE_CONFIG_APP_ADD         = 3003;    //添加自定义尾巴

	//Activity 跳回的result code
	public static final int RESULT_CODE_SUCCESS                 = Activity.RESULT_OK;
	public static final int RESULT_CODE_FAILED                  = Activity.RESULT_CANCELED;
	public static final int RESULT_CODE_MICRO_BLOG_DELETE       = 10060;
	public static final int RESULT_CODE_SETTING_ORIENTATION     = 10070;
	public static final int RESULT_CODE_SETTING_SLIDER          = 10071;
	public static final int RESULT_CODE_SETTING_FONT_SIZE       = 10072;
	public static final int RESULT_CODE_ACCOUNT_EXIT_APP        = 10080;
	public static final int RESULT_CODE_ACCOUNT_SWITCH          = 10081;
    public static final int RESULT_CODE_USER_SELECTOR           = 10400;
    public static final int RESULT_CODE_SPLASH_EXIT             = 10500;
    public static final int RESULT_CODE_IMAGE_ROTATED           = 10090; //图片经过旋转
    public static final int RESULT_CODE_IMAGE_CROPED            = 10091; //图片经过裁剪
    public static final int RESULT_CODE_IMAGE_DELETED           = 10092; //图片被删除

	//微博平台编号
	public static final int SP_NO_SINA                          = 1;     // 新浪的SP编号
	public static final int SP_NO_SOHU                          = 2;     // 搜狐的SP编号
	public static final int SP_NO_NETEASE                       = 3;     // 网易的SP编号
	public static final int SP_NO_TENCENT                       = 4;     // 腾讯的SP编号
	public static final int SP_NO_TWITTER                       = 5;     // 推特的SP编号

    //图片设置
	public static final int IMAGE_THUMBNAIL_WIDTH               = 150;   //上传图片，缩略图宽
	public static final int SETTING_POLICY_NO                   = 0;     //显示缩略策略,不显示
	public static final int SETTING_POLICY_YES                  = 1;     //显示缩略策略，总是显示
	public static final int SETTING_POLICY_ADAPTIVE             = 2;     //显示缩略策略,自适应2g/3g/wifi网络

    //编辑信息类型
    public static final int EDIT_TYPE_TWEET                     = 1;     //写微博
	public static final int EDIT_TYPE_RETWEET                   = 2;     //转发
	public static final int EDIT_TYPE_COMMENT                   = 3;     //评论
    public static final int EDIT_TYPE_RECOMMENT                 = 4;     //回复评论
	public static final int EDIT_TYPE_MESSAGE                   = 5;     //写私信
	public static final int EDIT_TYPE_REMESSAGE                 = 6;     //加复私信
	public static final int EDIT_TYPE_MENTION                   = 7;     //提到某人
	public static final int EDIT_TYPE_FEEDBACK                  = 8;     //意见反馈
    public static final int EDIT_TYPE_TO_MESSAGE                = 9;     //给某人私信
    //Activity跳转来源
    public static final int SOURCE_MY_HOME                      = 1;     //来自首页
    public static final int SOURCE_WIDGET                       = 10;    //来自部件
    public static final int SOURCE_WIDGET_EDIT                  = 11;    //来自部件的写新鲜事
    public static final int SOURCE_WIDGET_CAMERA                = 12;    //来自部件的拍照


	//应用定义的uri地址
	public static final Uri OAUTH_CALLBACK_SINA    = Uri.parse("shejiaomao://oauth/sina"); 	// 新浪  OAUTH 回调地址
	public static final Uri OAUTH_CALLBACK_SOHU    = Uri.parse("shejiaomao://oauth/sohu");    // 搜狐 OAUTH 回调地址
	public static final Uri OAUTH_CALLBACK_NETEASE = Uri.parse("shejiaomao://oauth/netease"); // 网易 OAUTH 回调地址
	public static final Uri URI_PERSONAL_INFO      = Uri.parse("shejiaomao://info/");         // 用户资料查看地址
	public static final Uri URI_TOPIC              = Uri.parse("shejiaomao://topic/");        // 话题查看地址


	//图片、图像和相册处理相关
	public static final int DISPLAY_LDPI_WIDTH           = 240;       //ldpi屏幕的宽度
	public static final int DISPLAY_MDPI_WIDTH           = 320;       //mdpi屏幕的宽度
	public static final int DISPLAY_HDPI_WIDTH           = 480;       //hdpi屏幕的宽度
	public static final int DISPLAY_XHDPI_WIDTH          = 640;       //xhdpi屏幕的宽度
	public static final int IMAGE_HEAD_MINI_SIZE_LDPI    = 24;        //ldpi小头像的大小
	public static final int IMAGE_HEAD_MINI_SIZE_MDPI    = 32;        //mdpi小头像的大小
	public static final int IMAGE_HEAD_MINI_SIZE_HDPI    = 48;        //hdpi小头像的大小
	public static final int IMAGE_HEAD_MINI_SIZE_XHDPI   = 64;        //xhdpi小头像的大小
	public static final int IMAGE_HEAD_NORMAL_SIZE_LDPI  = 36;        //ldpi资料头像的大小
	public static final int IMAGE_HEAD_NORMAL_SIZE_MDPI  = 48;        //mdpi资料头像的大小
	public static final int IMAGE_HEAD_NORMAL_SIZE_HDPI  = 72;        //hdpi资料头像的大小
	public static final int IMAGE_HEAD_NORMAL_SIZE_XHDPI = 96;        //xhdpi资料头像的大小
	public static final String DCIM_PATH                 = "/sdcard/DCIM";   // 用户保存图片时的存储目录
	public static final String PICTURE_NAME_PREFIX       = "sheJiaoMao_";          // 用户保存图片时图片名称前缀
	public static final String NET_EASE_IMAGE_URL_PREFIX = "http://126.fm/"; // 网易缩略图前缀标识

	//分隔符
	public static final String SEPARATOR_RECEIVER         = ",";      //收件人的分隔符

	//相关menu菜单
	public static final int CONTEXT_MENU_BLOG_COMMENT     = 001;
	public static final int CONTEXT_MENU_BLOG_RETWEET     = 002;
	public static final int CONTEXT_MENU_BLOG_FAVORITE    = 003;
	public static final int CONTEXT_MENU_BLOG_PERSONAL    = 004;
	public static final int CONTEXT_MENU_BLOG_RETWEET_ORIGIN = 005;
	public static final int CONTEXT_MENU_BLOG_COMMENT_ORIGIN  = 006;
	public static final int CONTEXT_MENU_BLOG_SHOW_ORIGIN = 007;
	public static final int CONTEXT_MENU_BLOG_URL         = 010;
	public static final int CONTEXT_MENU_BLOG_SHARE_TO_ACCOUNTS = 99;
	public static final int CONTEXT_MENU_BLOG_COPY        = 100;
	public static final int CONTEXT_MENU_BLOG_SHARE       = 101;
	public static final int CONTEXT_MENU_BLOG_DELETE      = 102;

	public static final String PASSPORT_EMAIL = "Passport_Email";
	public static final String PASSPORT_USERNAME = "Passport_Username";
	public static final String PASSPORT_TOKEN = "Passport_Token";
	public static final String PASSPORT_VIP = "Passport_Vip";
	public static final String PASSPORT_STATE = "Passport_State";
	public static final String PASSPORT_POINTS = "Passport_Points";
	public static final String PASSPORT_TITLE = "Passport_Title";

	public static final String LAST_SYNC_TIME = "Last_Sync_Time";

	public static final Pattern URL_PATTERN = Pattern.compile("[a-zA-z]+://[^\\s]*");

	//积分相关设置
	public static final int POINTS_SYNC_LEVEL = 400;
	public static final int POINTS_CUSTOM_SOURCE_LEVEL = 800;
}
