package net.dev123.yibome.entity;

public enum SubscribeCatalog {

	NONE(0),                 //0.未分类
	HOT_RETWEET(1),          //1.热门转发
	HOT_COMMENT(2),          //2.热门评论
	DAILY_NEWS(3),           //3.今日新闻
	IMAGE(4),                //4.图说微博
	NEWS(10),                //10.新闻
	NEWS_TECHNOLOGY(11),     //11.科技新闻
	NEWS_IT(12),             //12.IT新闻
	NEWS_ECONOMY(13),        //13.经济新闻
	NEWS_ENTERTAINMENT(14),  //14.娱乐新闻
	ENTERTAINMENT(20),       //20.娱乐
	TECHNOLOGY(30),          //30.科技
	IT(40),                  //40.IT
	CULTURE(50),             //50.人文艺术
	PHILOSOPHY(60),          //60.哲理类
	JOKE(70);                //70.冷笑话
	
    private int subscribeCatalogNo;
	private SubscribeCatalog(int subscribeCatalogNo) {
		this.subscribeCatalogNo = subscribeCatalogNo;
	}
	
	public int getSubscribeCatalogNo() {
		return subscribeCatalogNo;
	}
	
	public static SubscribeCatalog getSubscribeCatalog(int subscribeCatalogNo) {
		SubscribeCatalog sc = SubscribeCatalog.NONE;
		switch (subscribeCatalogNo) {
		case 1: sc = SubscribeCatalog.HOT_RETWEET; break;
		case 2: sc = SubscribeCatalog.HOT_COMMENT; break;
		case 3: sc = SubscribeCatalog.DAILY_NEWS; break;
		case 4: sc = SubscribeCatalog.IMAGE; break;
		case 10: sc = SubscribeCatalog.NEWS; break;
		case 11: sc = SubscribeCatalog.NEWS_TECHNOLOGY; break;
		case 12: sc = SubscribeCatalog.NEWS_IT; break;
		case 13: sc = SubscribeCatalog.NEWS_ECONOMY; break;
		case 14: sc = SubscribeCatalog.NEWS_ENTERTAINMENT; break;
		case 20: sc = SubscribeCatalog.ENTERTAINMENT; break;
		case 30: sc = SubscribeCatalog.TECHNOLOGY; break;
		case 40: sc = SubscribeCatalog.IT; break;
		case 50: sc = SubscribeCatalog.CULTURE; break;
		case 60: sc = SubscribeCatalog.PHILOSOPHY; break;
		case 70: sc = SubscribeCatalog.JOKE; break;
		default : sc = SubscribeCatalog.NONE; break;
		}
		
		return sc;
	}
	
}
