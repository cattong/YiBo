package net.dev123.yibo.service.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import net.dev123.commons.Constants;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.AppGridAdapter;
import net.dev123.yibo.widget.Skeleton;
import net.dev123.yibo.widget.ValueSetEvent;
import net.dev123.yibo.widget.ViewChangeEvent;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class AppChangeListener implements PropertyChangeListener {
	private Activity context;
	private YiBoApplication yibo;
	private AppGridAdapter adapter;

	private WeakReference<View> refView;
	private AppGridItemClickListener itemClickListener;
	public AppChangeListener(Context context) {
		this.context = (Activity)context;
		this.yibo = (YiBoApplication)context.getApplicationContext();
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
			if (Constants.DEBUG && contentView == null) {
				Log.v("AppChangeListener", "HomePage_App View recycle");
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
			if (Constants.DEBUG) {
				Log.v("AppChangeListener", "reclaim:" + this.getClass().getCanonicalName());
			}
		}
		gvApp.setAdapter(adapter);
		gvApp.setFastScrollEnabled(yibo.isSliderEnabled());
		
		return contentView;
	}
}
