package com.shejiaomao.widget;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TabButton implements OnClickListener {
    private List<Button> listButton;
    private OnTabChangeListener onTabChangeListener;
	public TabButton() {
		listButton = new ArrayList<Button>();
	}
	
	@Override
	public void onClick(View v) {
        toggleButton(v);
	}
	
	public void addButton(Button button) {
		if (button == null) {
			return;
		}
		if (listButton != null) {
			listButton.add(button);
			button.setOnClickListener(this);
		}
	}

	public void toggleButton(View button) {
		if (button == null) {
			return;
		}
		if (listButton == null || listButton.size() == 0) {
			return;
		}
		if (!listButton.contains(button)) {
			return;
		}
		
		for (Button tempButton : listButton) {
			tempButton.setEnabled(true);
		}
		button.setEnabled(false);
		
        if (onTabChangeListener != null) {
        	int which = listButton.indexOf(button);
        	onTabChangeListener.onTabChange(button, which);
        }
	}
	
	public interface OnTabChangeListener {
		public void onTabChange(View v, int which);
	}

	public OnTabChangeListener getOnTabChangeListener() {
		return onTabChangeListener;
	}

	public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
		this.onTabChangeListener = onTabChangeListener;
	}
}
