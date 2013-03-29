package com.shejiaomao.weibo.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;

public class AppGridAdapter extends BaseAdapter {
    private AppGrid[] appGrids;
    private LayoutInflater inflater;
    
    private Theme theme;
	public AppGridAdapter(Context context) {		
		appGrids = new AppGrid[] {
		    new AppGrid("icon_app_search", R.drawable.icon_app_search, R.string.label_app_search),
		    new AppGrid("icon_app_daily", R.drawable.icon_app_daily, R.string.label_app_daily),
		    new AppGrid("icon_app_image", R.drawable.icon_app_image, R.string.label_app_image),
		    new AppGrid("icon_app_hot_retweet", R.drawable.icon_app_hot_retweet, R.string.label_app_hot_retweet),
		    new AppGrid("icon_app_hot_comment", R.drawable.icon_app_hot_comment, R.string.label_app_hot_comment),
		    new AppGrid("icon_app_hot_topic", R.drawable.icon_app_hot_topic, R.string.label_app_hot_topic),
		    new AppGrid("icon_app_jokes", R.drawable.icon_app_jokes, R.string.label_app_jokes),
		    new AppGrid("icon_app_public_timeline", R.drawable.icon_app_public_timeline, R.string.label_app_public_timeline),
		    new AppGrid("icon_app_exchange", R.drawable.icon_app_exchange, R.string.label_app_exchange)
		};

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		theme = ThemeUtil.createTheme(context);
	}

	@Override
	public int getCount() {
		return appGrids.length;
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 && position >= appGrids.length) {
			return null;
		}
		return appGrids[position];
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 && position >= appGrids.length) {
			return -1;
		}
		return appGrids[position].appImageId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppGridHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_app, null);
            holder = new AppGridHolder(convertView);
            convertView.setTag(holder);
        } else {
             holder = (AppGridHolder)convertView.getTag();
        }

        AppGrid appGrid = (AppGrid)getItem(position);
        if (appGrid != null) {
        	holder.ivApp.setImageDrawable(theme.getDrawable(appGrid.appImageName));
            holder.tvAppName.setText(appGrid.appNameId);
        }
        return convertView;
	}

	private static class AppGrid {
		String appImageName;
		int appImageId;
		int appNameId;
		public AppGrid(String appImageName, int appImageId, int appNameId) {
			this.appImageName = appImageName;
			this.appImageId = appImageId;
			this.appNameId = appNameId;
		}
	}

	private static class AppGridHolder {
		ImageView ivApp;
        TextView tvAppName;
        
        public AppGridHolder(View convertView) {
        	this.ivApp = (ImageView)convertView.findViewById(R.id.ivApp);
            this.tvAppName = (TextView)convertView.findViewById(R.id.tvAppName);
             
            Theme theme = ThemeUtil.createTheme(convertView.getContext());
            tvAppName.setTextColor(theme.getColor("list_item_app_name"));
        }
	}
}
