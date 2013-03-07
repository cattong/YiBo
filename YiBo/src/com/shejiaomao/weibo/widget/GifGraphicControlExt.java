package com.shejiaomao.weibo.widget;

public class GifGraphicControlExt {

	public int hold;           //保留字段
	
	public int disposal;       //处置方法:0 - 不使用处置方法;1 - 不处置图形，把图形从当前位置移去;
	                           // 2 - 回复到背景色; 3 - 回复到先前状态; 4-7 - 自定义
	public boolean inputFlag;  //用户输入标识
	
	public boolean transparentColorFlag;  //透明颜色标志
	
	public int delay;                     //延迟时间;
	
	public int 	transparentColorIndex;    //透明色索引值;
}
