package com.shejiaomao.weibo.widget;

import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.service.adapter.EmotionsGridAdapter;
import com.shejiaomao.weibo.service.listener.EditMicroBlogEmotionItemClickListener;

public class EmotionViewController {

	private Activity activity;
	private LinearLayout llEmotion;
	private GridView emotionGrid;
	public EmotionViewController(Activity activity) {
		this.activity = activity;
		setLlEmotion();
	}
	
	private void setLlEmotion() {
		this.llEmotion = (LinearLayout) this.activity.findViewById(R.id.llEmotion);
	}
	
	public boolean showEmotionView() {
		if (llEmotion == null) {
			setLlEmotion();
		}
		if (llEmotion.getVisibility() == View.GONE) {
			llEmotion.setVisibility(View.VISIBLE);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hideEmotionView() {
		if (llEmotion == null) {
			setLlEmotion();
		}
		if (llEmotion.getVisibility() == View.VISIBLE) {
			llEmotion.setVisibility(View.GONE);
			return true;
		} else {
			return false;
		}
	}
	
	public int reverseEmotionView() {
		if (llEmotion == null) {
			setLlEmotion();
		}
		if (llEmotion.getVisibility() == View.GONE) {
			showEmotionView();
			return View.VISIBLE;
		} else {
			hideEmotionView();
			return View.GONE;
		}
	}
	
	private void setEmotionGridView() {
		this.emotionGrid = (GridView) activity.findViewById(R.id.emotionGrid);
	}
	
	public void setEmotionGridViewAdapter(EmotionsGridAdapter adapter) {
		if (emotionGrid == null) {
			setEmotionGridView();
		}
		emotionGrid.setAdapter(adapter);
	}
	
	public void setEmotionGridViewOnItemClickListener(EditMicroBlogEmotionItemClickListener listener) {
		if (emotionGrid == null) {
			setEmotionGridView();
		}
		emotionGrid.setOnItemClickListener(listener);
	}
	
	public int getEmotionViewVisibility() {
		if (llEmotion == null) {
			setLlEmotion();
		}
		return llEmotion.getVisibility();
	}
}
