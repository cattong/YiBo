package com.shejiaomao.weibo.widget;

import android.graphics.Bitmap;

public class GifFrame {

	public GifFrame(int frameIndex, int delay) {
		this.frameIndex = frameIndex;
		this.delay = delay;
	}
	
	/**frame info**/
	public int frameIndex;     //第几帧
	public int[] pixels;       //帧的像素值
	
	public GifGraphicControlExt gcExt;
	public GifImageDescriptor imgDescriptor;
	public GifImageData imgData;
	
	public GifFrame nextFrame;  //下一帧
	public int delay;           //下一帧延迟显示时间 ms;
	
	/**图片*/
	public Bitmap image;	
}
