package net.dev123.yibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.util.ListUtil;
import net.dev123.mblog.entity.Trend;
import net.dev123.yibo.db.LocalAccount;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-23 上午12:32:58
 **/
public class UserTopicListAdapter extends CacheAdapter<Trend> {
	 private Activity context;
	 private List<Trend> trendList = new ArrayList<Trend>();
	 
	 public UserTopicListAdapter(Activity context, LocalAccount account) {
		 super(context, account);
		 this.context = context;
	 }
	@Override
	public int getCount() {
		return trendList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= trendList.size()) {
			return null;
		}
		return trendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Trend trend = trendList.get(position);
		if (trend == null) {
			return null;
		}
		TextView textView = new TextView(context);
		textView.setText(trend.getName());
		if (convertView == null) {
			convertView = new TextView(context);
		}
		
		((TextView)convertView).setText(trend.getName());
		
		return convertView;
	}

	@Override
	public boolean addCacheToFirst(List<Trend> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}
		trendList.addAll(0, list);
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean addCacheToDivider(Trend value, List<Trend> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}
		trendList.addAll(list);
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean addCacheToLast(List<Trend> list) {
		if (ListUtil.isEmpty(list)) {
			return false;
		}
		trendList.addAll(list);
		this.notifyDataSetChanged();
		return true;
	}

	@Override
	public Trend getMax() {
		Trend max = null;
		if (ListUtil.isNotEmpty(trendList)) {
			max = trendList.get(0);
		}
		return max;
	}

	@Override
	public Trend getMin() {
		Trend min = null;
		if (ListUtil.isNotEmpty(trendList)) {
			min = trendList.get(trendList.size() - 1);
		}
		return min;
	}

	@Override
	public void clear() {
		if (trendList != null) {
			trendList.clear();
		}
	}

}
