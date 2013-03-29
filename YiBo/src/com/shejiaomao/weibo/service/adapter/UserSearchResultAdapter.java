package com.shejiaomao.weibo.service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.SearchActivity;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class UserSearchResultAdapter extends ArrayAdapter<User> {
	private SearchActivity context;

	public UserSearchResultAdapter(SearchActivity context) {
		super(context, R.layout.list_item_social_graph_search);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_social_graph_search, null);
		    UserHolder holder = new UserHolder(convertView);
		    convertView.setTag(holder);
		}

		User user = getItem(position);
        UserHolder holder = (UserHolder)convertView.getTag();
        if (holder == null || user == null) {
        	return convertView;
        }
        holder.reset();

        String profileUrl = user.getProfileImageUrl();
        if(StringUtil.isNotEmpty(profileUrl)){
            ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(holder.ivProfilePicture, profileUrl, true);
            holder.headTask = headTask;
            headTask.execute();
        }
		if (user.isVerified()) {
			holder.ivVerify.setVisibility(View.VISIBLE);
		}

		holder.tvScreenName.setText(user.getScreenName());

		String impress = "";
		if (user.getGender() != null) {
			impress += ResourceBook.getGenderValue(user.getGender(), context) + ", ";
		}
		if(user.getLocation() != null){
			impress += user.getLocation();
		}
		holder.tvImpress.setText(impress);

		return convertView;
	}
}
