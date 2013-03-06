package com.cattong.entity;

public enum TaskType {

	Passport_Register(TaskType.TYPE_PASSPORT_REGISTER), 
	Passport_Login(TaskType.TYPE_PASSPORT_LOGIN), 
	Passport_Check_In(TaskType.TYPE_PASSPORT_CHECK_IN),
	Sina_Weibo_Login(TaskType.TYPE_SINA_WEIBO_LOGIN), 
	QQ_Login(TaskType.TYPE_QQ_LOGIN), 
	App_Preloader(TaskType.TYPE_APP_PRELOADER),
	Discount_Share(TaskType.TYPE_DISCOUNT_SHARE), 
	Weibo_Retweet(TaskType.TYPE_WEIBO_RETWEET),
	Tencent_Weibo_Login(TaskType.TYPE_TENCENT_WEIBO_LOGIN),
	Hot_Apps(TaskType.TYPE_HOT_APPS),
	Hot_Games(TaskType.TYPE_HOT_GAMES),
	Daily_Recommend_Apps(TaskType.TYPE_DAILY_RECOMMEND_APPS),
	App_Offer_1(TaskType.TYPE_APP_OFFER_1), 
	App_Offer_2(TaskType.TYPE_APP_OFFER_2), 
	App_Offer_3(TaskType.TYPE_APP_OFFER_3),
	App_Offer_4(TaskType.TYPE_APP_OFFER_4),
	App_Offer_5(TaskType.TYPE_APP_OFFER_5);
	
	private int taskTypeNo;
	private TaskType(int taskTypeNo) {
		this.taskTypeNo = taskTypeNo;
	}
	
	public int getTaskTypeNo() {
		return taskTypeNo;
	}
	
	public void setTaskTypeNo(int taskTypeNo) {
		this.taskTypeNo = taskTypeNo;
	}
	
	public static final int TYPE_PASSPORT_REGISTER = 1;//通行证完善资料
	public static final int TYPE_PASSPORT_LOGIN    = 2;//通行证登陆
	public static final int TYPE_PASSPORT_CHECK_IN = 3;//通行证签到
	public static final int TYPE_SINA_WEIBO_LOGIN  = 4;//新浪微博登陆
	public static final int TYPE_QQ_LOGIN          = 5;//QQ登陆
	public static final int TYPE_APP_PRELOADER     = 6;//应用预装
	public static final int TYPE_DISCOUNT_SHARE    = 7;//优惠打折分享
	public static final int TYPE_WEIBO_RETWEET     = 8;//微博转发
	public static final int TYPE_TENCENT_WEIBO_LOGIN = 9;//腾讯微博登陆
	public static final int TYPE_HOT_APPS          = 10;//装机必备软件
	public static final int TYPE_HOT_GAMES         = 11;//不得不推荐的游戏
	public static final int TYPE_DAILY_RECOMMEND_APPS = 12;//邦主每日推荐
	public static final int TYPE_APP_OFFER_1       = 51;//积分墙频道1
	public static final int TYPE_APP_OFFER_2       = 52;//积分墙频道2
	public static final int TYPE_APP_OFFER_3       = 53;//积分墙频道3
	public static final int TYPE_APP_OFFER_4       = 54;//积分墙频道4
	public static final int TYPE_APP_OFFER_5       = 55;//积分墙频道5
	
	public static TaskType getTaskType(int taskTypeNo) {
		TaskType type = null;
		switch (taskTypeNo) {
		case TYPE_PASSPORT_REGISTER:
			type = Passport_Register;
			break;
		case TYPE_PASSPORT_LOGIN:
			type = Passport_Login;
			break;
		case TYPE_PASSPORT_CHECK_IN:
			type = Passport_Check_In;
			break;
		case TYPE_SINA_WEIBO_LOGIN:
			type = Sina_Weibo_Login;
			break;
		case TYPE_QQ_LOGIN:
			type = QQ_Login;
			break;
		case TYPE_APP_PRELOADER:
			type = App_Preloader;
			break;
		case TYPE_DISCOUNT_SHARE:
			type = Discount_Share;
			break;
		case TYPE_WEIBO_RETWEET:
			type = Weibo_Retweet;
			break;			
		case TYPE_TENCENT_WEIBO_LOGIN:
			type = Tencent_Weibo_Login;
			break;
		case TYPE_HOT_APPS:
			type = Hot_Apps;
			break;
		case TYPE_HOT_GAMES:
			type = Hot_Games;
			break;
		case TYPE_DAILY_RECOMMEND_APPS:
			type = Daily_Recommend_Apps;
			break;			
		case TYPE_APP_OFFER_1:
			type = App_Offer_1;
			break;
		case TYPE_APP_OFFER_2:
			type = App_Offer_2;
			break;	
		case TYPE_APP_OFFER_3:
			type = App_Offer_3;
			break;
		case TYPE_APP_OFFER_4:
			type = App_Offer_4;
			break;	
		case TYPE_APP_OFFER_5:
			type = App_Offer_5;
			break;				
		}
		
		return type;
	}
}
