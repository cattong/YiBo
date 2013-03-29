package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeEntry;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;

public class ThemeListAdapter extends CacheAdapter<ThemeEntry> {

    private List<ThemeEntry> themeList = null;
    public ThemeListAdapter(Activity context, LocalAccount account) {
    	super(context, account);

    	themeList = new ArrayList<ThemeEntry>();
    	initData();
    }

    private void initData() {
    	Theme theme = ThemeUtil.createTheme(context);
    	//标准皮肤
    	ThemeEntry entry1 = new ThemeEntry();
    	entry1.setName(context.getString(R.string.label_theme_default));
    	entry1.setFileSize(300);
    	entry1.setPackageName("net.dev123.yibo");
    	entry1.setThumbnailUrl("http://dl.yibo.me/img/standard.png");
    	entry1.setInstalled(true);
    	entry1.setState(ThemeEntry.getThemeEntryState(entry1));
    	themeList.add(entry1);
    	//夜间模式
    	ThemeEntry entry2 = new ThemeEntry();
    	entry2.setName(context.getString(R.string.label_theme_nightskin));
    	entry2.setFileSize(300);
    	entry2.setPackageName("net.dev123.yibo.nightskin");
    	entry2.setThumbnailUrl("http://dl.yibo.me/img/nightskin.png");
    	entry2.setFileUrl("http://dl.yibo.me/skins/NightSkin.apk");
    	entry2.setInstalled(theme.isInstalled(entry2.getPackageName()));
    	entry2.setState(ThemeEntry.getThemeEntryState(entry2));
    	themeList.add(entry2);
    }
    
	@Override
	public int getCount() {
		return themeList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= themeList.size()) {
			return null;
		}

		return themeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object obj = getItem(position);
		ThemeHolder holder = null;
        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.list_item_theme, null);
        	holder = new ThemeHolder(convertView);
        	convertView.setTag(holder);
        } else {
        	holder = (ThemeHolder)convertView.getTag();
        }

        ThemeEntry entry = (ThemeEntry)obj;
        holder.themePictureTask = new ImageLoad4ThumbnailTask(
        	holder.ivThemePicture, entry.getThumbnailUrl());
        holder.themePictureTask.execute();
        
        holder.tvThemeName.setText(entry.getName());
        holder.tvFileSize.setText(entry.getFileSize() + "KB");
        
    	holder.operateClickListener.setEntry(entry);
        switch (entry.getState()) {
        case ThemeEntry.STATE_UNINSTALLED:
        	ThemeUtil.setBtnActionPositive(holder.btnThemeOperate);
        	holder.btnThemeOperate.setText(R.string.label_theme_state_uninstalled);
        	holder.btnThemeOperate.setEnabled(true);
        	break;
        case ThemeEntry.STATE_INSTALLED:
        	ThemeUtil.setBtnActionPositive(holder.btnThemeOperate);
    		holder.btnThemeOperate.setText(R.string.label_theme_state_installed);
    		holder.btnThemeOperate.setEnabled(true);
        	break;
        case ThemeEntry.STATE_USING:
        	ThemeUtil.setBtnActionNegative(holder.btnThemeOperate);
    		holder.btnThemeOperate.setText(R.string.label_theme_state_using);
    		holder.btnThemeOperate.setEnabled(false);
        	break;
        }
       
		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<ThemeEntry> list) {
		return false;
	}

	@Override
	public boolean addCacheToDivider(ThemeEntry value, List<ThemeEntry> list) {
		if (list == null || list.size() == 0) {
			return false;
		}

		themeList.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}

	@Override
	public boolean addCacheToLast(List<ThemeEntry> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}

		themeList.addAll(list);

		this.notifyDataSetChanged();

		return true;
	}
	
	@Override
	public ThemeEntry getMax() {
		ThemeEntry max = null;
		if (themeList != null && themeList.size() > 0) {
			max = themeList.get(0);
		}
		return max;
	}

	@Override
	public ThemeEntry getMin() {
		ThemeEntry min = null;
		if (themeList != null && themeList.size() > 0) {
			min = themeList.get(0);
		}
		return min;
	}

	@Override
	public void clear() {
		themeList.clear();
	}
}
