package com.shejiaomao.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RichImageView extends ImageView {

	private String imageName;
	
	public RichImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RichImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
