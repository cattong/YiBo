package com.shejiaomao.weibo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;

public class ListChooseDialog {
	private Context context;
	private View parent;
	private PopupWindow listWindow;
	private ListView lvItem;
	private ListAdapter listAdapter;
	private OnItemClickListener itemClickListener;
	public ListChooseDialog(Context context, View parent) {
		this.context = context;
		this.parent = parent;
		initComponents();
	}
	
	private void initComponents() {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.widget_dialog_list_choose, null);
	    lvItem = (ListView)contentView.findViewById(R.id.lvItem);
	    View emptyView = contentView.findViewById(R.id.llLoadingView);
	    lvItem.setEmptyView(emptyView);
	    
	    listWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    listWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(100, 158, 158, 158)));
	    listWindow.setFocusable(true);
	    listWindow.setOutsideTouchable(true);
	    listWindow.setAnimationStyle(android.R.anim.fade_in);
	}
	
	public void show() {
		listWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}
	
	public void dismiss() {
		listWindow.dismiss();
	}
	
	public boolean isShowing() {
		return listWindow.isShowing();
	}

	public ListAdapter getListAdapter() {
		return listAdapter;
	}

	public void setListAdapter(ListAdapter listAdapter) {
		this.listAdapter = listAdapter;
		if (listAdapter != null) {
			lvItem.setAdapter(listAdapter);
		}
	}

	public OnItemClickListener getItemClickListener() {
		return itemClickListener;
	}

	public void setItemClickLitener(OnItemClickListener itemClickListener) {
		if (itemClickListener == null) {
			return;
		}
		this.itemClickListener = itemClickListener;
		lvItem.setOnItemClickListener(itemClickListener);
	}
}
