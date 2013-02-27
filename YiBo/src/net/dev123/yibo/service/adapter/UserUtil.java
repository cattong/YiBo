package net.dev123.yibo.service.adapter;

import net.dev123.commons.util.StringUtil;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.R;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.service.task.ImageLoad4HeadTask;
import net.dev123.yibo.service.task.RelationshipCheckTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class UserUtil {

	public static View initConvertView(Context context, View convertView) {
		if (convertView != null 
			&& isSocialGraphView(convertView)) {
			return convertView;
		}
		
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.list_item_social_graph, null);
		UserHolder holder = new UserHolder(convertView);
		convertView.setTag(holder);
		
		return convertView;
	}
	
	public static View fillConvertView(View convertView, User user) {
		if (convertView == null
			|| user == null) {
			return null;
		}
		
		Context context = convertView.getContext();
        UserHolder holder = (UserHolder)convertView.getTag();
        if (holder == null || user == null) {
        	return convertView;
        }
        holder.reset();

        String profileUrl = user.getProfileImageUrl();
        if (StringUtil.isNotEmpty(profileUrl)) {
            ImageLoad4HeadTask headTask = new ImageLoad4HeadTask(
            	holder.ivProfilePicture, profileUrl, true);
            holder.headTask = headTask;
            headTask.execute();
        }

		holder.tvScreenName.setText(user.getScreenName());
		if (user.isVerified()) {
			holder.ivVerify.setVisibility(View.VISIBLE);
		}
		String impress = "";
		if (user.getGender() != null) {
			impress += ResourceBook.getGenderValue(user.getGender(), context) + ", ";
		}
		if (user.getLocation() != null) {
			impress += user.getLocation();
		}
		holder.tvImpress.setText(impress);

		if (holder.btnOperate != null) {
			final User targetUser = user;
			if (targetUser.isRelationChecked()) {
				RelationshipCheckTask.updateView(convertView, targetUser);
			} else {
				if (targetUser.isFollowing() || targetUser.isBlocking()) {
					RelationshipCheckTask.updateView(convertView, targetUser);
				}
				RelationshipCheckTask relationshipCheckTask = new RelationshipCheckTask(convertView, targetUser);
				holder.relationshipCheckTask = relationshipCheckTask;
				relationshipCheckTask.execute();
			}
		}

		return convertView;
	}
	
	private static boolean isSocialGraphView(View convertView) {
		boolean isSocialGraphView = false;
		try {
			View view = convertView.findViewById(R.id.ivProfilePicture);
			if (view != null) {
				isSocialGraphView = true;
			}
		} catch (Exception e) {
		}

		return isSocialGraphView;
	}
}
