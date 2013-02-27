package net.dev123.yibo.widget;

import net.dev123.yibo.R;
import net.dev123.yibo.service.adapter.EmotionsGridAdapter;
import net.dev123.yibo.service.listener.EditMicroBlogEmotionItemClickListener;
import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-10-10 上午12:54:05
 **/
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
