package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.widget.TweetProgressDialog.State;

public class TweetProgressListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<LocalAccount> listUpdateAccount;
	private Map<LocalAccount, State> mapState;
	
	private Animation rotateAnimation;
	public TweetProgressListAdapter(Context context) {
		this.mapState = new HashMap<LocalAccount, State>();
		this.listUpdateAccount = new ArrayList<LocalAccount>();
		
		initComponents(context);
	}

	private void initComponents(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_progress);
	}

	@Override
	public int getCount() {
		if (ListUtil.isEmpty(listUpdateAccount)) {
			return 0;
		}
		return listUpdateAccount.size();
	}

	@Override
	public Object getItem(int position) {
		if (ListUtil.isEmpty(listUpdateAccount)) {
			return null;
		}
		return listUpdateAccount.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_dialog_tweet_progress, null);
		}
		final LocalAccount account = (LocalAccount)getItem(position);
		
		ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageDrawable(GlobalResource.getDefaultMinHeader(context));
        
		String profileImageUrl = account.getUser().getProfileImageUrl();
		if (StringUtil.isNotEmpty(profileImageUrl)) {
			new ImageLoad4HeadTask(ivProfileImage, profileImageUrl, true).execute();
		}
		
		TextView screenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		TextView spName = (TextView) convertView.findViewById(R.id.tvSPName);
		ImageView ivTweetState = (ImageView) convertView.findViewById(R.id.ivTweetState);
		
		State state = mapState.get(account);
		if (state == null) {
			state = State.Waiting;
			mapState.put(account, state);
		}
		ivTweetState.setImageLevel(state.getState());
        if (state == State.Loading) {
        	ivTweetState.startAnimation(rotateAnimation);
        } else {
        	ivTweetState.clearAnimation();
        }
        
		screenName.setText(account.getUser().getScreenName());
		String snNameText = account.getServiceProvider().getSpName();
		spName.setText(snNameText);
		
		return convertView;
	}

	public void setListUpdateAccount(List<LocalAccount> listAccount) {
		if (ListUtil.isEmpty(listAccount)) {
			return;
		}
		
		for (LocalAccount account : listAccount) {
			if (listUpdateAccount.contains(account)) {
				continue;
			}
			listUpdateAccount.add(account);
		}
		this.notifyDataSetChanged();
	}
	
	public boolean updateState(LocalAccount account, State state) {
		boolean isSuccess = false;
		if (account == null || state == null) {
			return isSuccess;
		}
		
		if (listUpdateAccount.contains(account)) {
			mapState.put(account, state);
			isSuccess = true;
			this.notifyDataSetChanged();
		}
		
		return isSuccess;
	}
}
