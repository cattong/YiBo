package com.shejiaomao.weibo.widget;

public class GifImageDescriptor {

	public int offX;  //X方向偏移量
	
	public int offY;  //Y方向偏移量
	
	public int width;  //图像宽度
	
	public int height; //图像高度
	
	public boolean lctFlag;  //0: false; 1: true #1bit
	
	public boolean interlaceFlag;  //交织标志,置位时图象数据使用交织方式排列，否则使用顺序排列 #1bit。
	
	public int r;        //保留，必须初始化为0.  #2bit
	
	public int lctSize;   //局部颜色列表大小(Size of Local Color Table)，pixel+1就为颜色列表的位数 #3bit

}
