package com.cattong.entity;


public enum StatusCatalog {
	Unknow(0),                 //0.未分类
	Hot_Retweet(1),          //1.热门转发
	Hot_Comment(2),          //2.热门评论
	News(10),                //10.新闻
	News_Technology(11),     //11.科技新闻
	News_IT(12),             //12.IT新闻
	News_Economy(13),        //13.经济新闻
	News_Entertainment(14),  //14.娱乐新闻
	Entertainment(20),       //20.娱乐
	Technology(30),          //30.科技
	IT(40),                  //40.IT
	Culture(50),             //50.人文艺术
	Philosophy(60),          //60.哲理类
	Joke(70),                //70.冷笑话
	Picture(80),             //80.图片：自拍，风景
	Picture_Mobile(81),      //81.素人(移动自拍)
	Picture_Person(82),      //82.人物
	Picture_Scenery(83),     //83.风景
	Picture_Cartoon(84),      //84.动漫
	Picture_Product(85),       //85.产品
	Picture_Reading(86),       //86.阅读
	Picture_Screenshot(87);    //87.截图
	
    private int catalogNo;
	private StatusCatalog(int catalogNo) {
		this.catalogNo = catalogNo;
	}
	
	public int getCatalogNo() {
		return catalogNo;
	}
	
	public static StatusCatalog getStatusCatalog(int catalogNo) {
		StatusCatalog sc = StatusCatalog.Unknow;
		
		switch (catalogNo) {
		case 0: sc = StatusCatalog.Unknow; break;
		case 1: sc = StatusCatalog.Hot_Retweet; break;
		case 2: sc = StatusCatalog.Hot_Comment; break;
		case 10: sc = StatusCatalog.News; break;
		case 11: sc = StatusCatalog.News_Technology; break;
		case 12: sc = StatusCatalog.News_IT; break;
		case 13: sc = StatusCatalog.News_Economy; break;
		case 14: sc = StatusCatalog.News_Entertainment; break;
		case 20: sc = StatusCatalog.Entertainment; break;
		case 30: sc = StatusCatalog.Technology; break;
		case 40: sc = StatusCatalog.IT; break;
		case 50: sc = StatusCatalog.Culture; break;
		case 60: sc = StatusCatalog.Philosophy; break;
		case 70: sc = StatusCatalog.Joke; break;
		case 80: sc = StatusCatalog.Picture; break;
		case 81: sc = StatusCatalog.Picture_Mobile; break;
		case 82: sc = StatusCatalog.Picture_Person; break;
		case 83: sc = StatusCatalog.Picture_Scenery; break;
		case 84: sc = StatusCatalog.Picture_Cartoon; break;
		case 85: sc = StatusCatalog.Picture_Product; break;
		case 86: sc = StatusCatalog.Picture_Reading; break;
		case 87: sc = StatusCatalog.Picture_Screenshot; break;
		default : sc = StatusCatalog.Unknow; break;
		}
		
		return sc;
	}
}
