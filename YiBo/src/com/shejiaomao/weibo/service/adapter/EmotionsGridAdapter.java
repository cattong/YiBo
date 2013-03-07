package com.shejiaomao.weibo.service.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shejiaomao.weibo.common.EmotionLoader;

public class EmotionsGridAdapter extends BaseAdapter {
    private Context context;
    private String[] emotionArray;
    private String emotionTabName;

	public EmotionsGridAdapter(Context context) {
		this.context = context;
		emotionArray = EmotionLoader.getEmotionsArray();
	}

	@Override
	public int getCount() {
		return emotionArray.length;
	}

	@Override
	public Object getItem(int position) {
		return emotionArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = null;
		if (convertView instanceof ImageView) {
			iv = (ImageView)convertView;
		} else {
			iv = new ImageView(context);
		}

//		int drawableId = EmotionLoader.getDrawableFromEmontion(emotionArray[position]);
//        iv.setImageResource(drawableId);
		Drawable drawable = EmotionLoader.getDrawableByEmontion(emotionArray[position]);
		iv.setImageDrawable(drawable);
        return iv;
	}

	public String getEmotionTabName() {
		return emotionTabName;
	}

	public void setEmotionTabName(String emotionTabName) {
		this.emotionTabName = emotionTabName;
		this.emotionArray = EmotionLoader.getEmotionsArray(emotionTabName);
	}
}
