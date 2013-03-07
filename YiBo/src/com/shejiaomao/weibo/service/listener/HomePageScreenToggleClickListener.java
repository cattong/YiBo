package com.shejiaomao.weibo.service.listener;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class HomePageScreenToggleClickListener implements OnClickListener {
    private ScreenToggle toggle;
	public HomePageScreenToggleClickListener(ScreenToggle toggle) {
		this.toggle = toggle;
	}
	
	@Override
	public void onClick(View v) {
		if (toggle == null) {
			return;
		}
		
        toggle.toggle();
	}

	public static class ScreenToggle {
		private PopupWindow toggleWindow;
		private View parent;
		private View headerView;
		private View footerView;
		private boolean isTurnOn;
		public ScreenToggle(PopupWindow toggleWindow, View parent, View headerView, View footerView) {
			this.toggleWindow = toggleWindow;
			this.parent = parent;
			this.headerView = headerView;
			this.footerView = footerView;
			this.isTurnOn = false;
			
			int bottom = 0;
			if (footerView != null) {
				bottom = footerView.getHeight();
			}
			
			try {
			    toggleWindow.setAnimationStyle(0);
			    toggleWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, 0, bottom);
			} catch (Exception e) {};
		}
		
		public void toggle() {
			if (isTurnOn) {
				turnOff();
			} else {
				turnOn();
			}
		}
		
		public void turnOn() {
			if (isTurnOn) {
				return;
			}
			if (headerView != null) {
				headerView.setVisibility(View.GONE);
			}
			if (footerView != null) {
				footerView.setVisibility(View.GONE);
			}
			if (toggleWindow != null) {
				try {
					if (toggleWindow.isShowing()) {
					    toggleWindow.dismiss();
					}
					toggleWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
				} catch (Exception e) {}
			}
			
			isTurnOn = true;
		}
		
		public void turnOff() {
			if (!isTurnOn) {
				return;
			}
			int bottom = 0;
			if (headerView != null) {
				headerView.setVisibility(View.VISIBLE);
			}
			if (footerView != null) {
				footerView.setVisibility(View.VISIBLE);
				bottom = footerView.getHeight();
			}
			if (toggleWindow != null) {
				try {
					if (toggleWindow.isShowing()) {
					    toggleWindow.dismiss();
					}
					toggleWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, 0, bottom);
					//toggleWindow.update(0, bottom, -1, -1);
				} catch (Exception e) {}
			}
			
			isTurnOn = false;
		}
		
		public void dismiss() {
			turnOff();
			if (toggleWindow != null) {
				try {
					if (toggleWindow.isShowing()) {
					    toggleWindow.dismiss();
					}
				} catch (Exception e) {}
			}
		}
	}
}
