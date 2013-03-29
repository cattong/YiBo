package com.shejiaomao.weibo.service.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AppGridAdapter;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.weibo.widget.ValueSetEvent;
import com.shejiaomao.weibo.widget.ViewChangeEvent;

public class AppChangeListener implements PropertyChangeListener {
	private Activity context;
	private SheJiaoMaoApplication sheJiaoMao;
	private AppGridAdapter adapter;

	private WeakReference<View> refView;
	private AppGridItemClickListener itemClickListener;
	public AppChangeListener(Context context) {
		this.context = (Activity)context;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
        adapter = new AppGridAdapter(context);

        itemClickListener = new AppGridItemClickListener();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
        if (event instanceof ViewChangeEvent) {
        	viewChange(event);
        } else if (event instanceof ValueSetEvent) {
        	valueSet(event);
        }
	}

	private void viewChange(PropertyChangeEvent event) {
		if (!(event instanceof ViewChangeEvent) 
			|| !event.getNewValue().equals(Skeleton.TYPE_APP)) {
			return;
		}

		ViewChangeEvent changeEvent = (ViewChangeEvent)event;
		ViewGroup viewGroup = (ViewGroup)changeEvent.getView();
		viewGroup.removeAllViews();

		LocalAccount account = changeEvent.getAccount();
		View contentView = getContentView(account);
	    viewGroup.addView(contentView);

	    View llHeaderBase = context.findViewById(R.id.llHeaderBase);
	    llHeaderBase.setVisibility(View.VISIBLE);
	    View llHeaderMessage = context.findViewById(R.id.llHeaderMessage);
	    llHeaderMessage.setVisibility(View.GONE);

		TextView tvTitle = (TextView) context.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_tab_app);

		ImageButton ibProfileImage = (ImageButton) context.findViewById(R.id.ibProfileImage);
		ibProfileImage.setVisibility(View.VISIBLE);
		ImageButton ibGroup = (ImageButton) context.findViewById(R.id.ibGroup);
		ibGroup.setVisibility(View.GONE);
		ImageButton ibEdit = (ImageButton) context.findViewById(R.id.ibEdit);
		ibEdit.setVisibility(View.VISIBLE);
		ibEdit.setOnClickListener(new HomePageEditStatusClickListener(context));

	}

	private void valueSet(PropertyChangeEvent event) {
		ValueSetEvent setEvent = (ValueSetEvent)event;
		LocalAccount account = setEvent.getAccount();

		switch(setEvent.getAction()) {
		case ACTION_INIT_ADAPTER:
			initAdapter(account);
			break;
		case ACTION_RECLAIM_MEMORY:
			refView = null;
			break;
		default:
			break;
		}
	}

	private BaseAdapter initAdapter(LocalAccount account) {
		if (account == null) {
			return adapter;
		}

		if (adapter == null) {
			adapter = new AppGridAdapter(context);
		}
		return adapter;
	}

	public View getContentView(LocalAccount account) {
		BaseAdapter adapter = initAdapter(account);
		GridView gvApp;
		
		View contentView = null;
		if (refView != null) {
			contentView = refView.get();
			if (contentView == null) {
				Logger.debug("AppChangeListener", "HomePage_App View recycle");
			}
		}
		if (contentView != null) {
			gvApp = (GridView)contentView.findViewById(R.id.gvApp);
		} else {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.home_page_content_app, null);
            ThemeUtil.setContentBackground(contentView);
            //refView = new WeakReference<View>(contentView);
            
            gvApp = (GridView)contentView.findViewById(R.id.gvApp);
            View emptyView = contentView.findViewById(R.id.llLoadingView);
            gvApp.setOnItemClickListener(itemClickListener);
            gvApp.setEmptyView(emptyView);
            
			if (Logger.level <= Logger.DEBUG) {
				Logger.debug("AppChangeListener", "reclaim:" + this.getClass().getCanonicalName());
			}
		}
		gvApp.setAdapter(adapter);
		gvApp.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		
		return contentView;
	}
}
