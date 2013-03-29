package com.shejiaomao.weibo.service.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.activity.MyFavoritesActivity;
import com.shejiaomao.weibo.activity.ProfileEditActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;
import com.shejiaomao.weibo.service.task.QueryResponseCountTask;
import com.shejiaomao.weibo.service.task.QueryUserTask;
import com.shejiaomao.weibo.service.task.SocialGraphTask;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.weibo.widget.ValueSetEvent;
import com.shejiaomao.weibo.widget.ViewChangeEvent;

public class ProfileChangeListener implements PropertyChangeListener {
	private Activity context;
	private WeakReference<View> refView;

	private ProfileSocialGraphClickListener friendsClickListener;
	private ProfileSocialGraphClickListener followersClickListener;
	private ProfileStatusCountClickListener statusesCountClickListener;
	private ProfileSocialGraphClickListener blocksClickListener;

	private boolean isTencent;
	private boolean isSohu;

	public ProfileChangeListener(Context context) {
		this.context = (Activity)context;
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
		if (!(event instanceof ViewChangeEvent
		    && event.getNewValue().equals(Skeleton.TYPE_PROFILE))) {
			return;
		}

		ViewChangeEvent changeEvent = (ViewChangeEvent) event;
		ViewGroup viewGroup = (ViewGroup) changeEvent.getView();

		viewGroup.removeAllViews();

		LocalAccount account = changeEvent.getAccount();
		if (account == null) {
			return;
		}
		if (!(account.getUser() instanceof User)) {
			return;
		}
		User user = (User)account.getUser();

		View view = updateContentView(user);
		viewGroup.addView(view);

		isSohu = account.getServiceProvider() == ServiceProvider.Sohu;
		isTencent = account.getServiceProvider() == ServiceProvider.Tencent;

		if (user.getStatus() == null) {
			QueryUserTask queryUserTask = new QueryUserTask(context, user, this);
			queryUserTask.execute();
		} else if (user.getStatus().getUser() == null) {
			//处理个人资料修改时，user对象更新，导致status.getUser()为空
			user.getStatus().setUser(user);
		}
	}

	private void valueSet(PropertyChangeEvent event) {
		ValueSetEvent setEvent = (ValueSetEvent)event;
		//LocalAccount account = setEvent.getAccount();

		switch (setEvent.getAction()) {
		case ACTION_INIT_ADAPTER:
			break;
		case ACTION_RECLAIM_MEMORY:
			refView = null;
			break;
		default:
			break;
		}
	}

	public View updateContentView(User user) {
		View contentView = null;
		if (refView != null) {
			contentView = refView.get();
			if (Logger.isDebug() && contentView == null) {
				Log.v("AppChangeListener", "HomePage_App View recycle");
			}
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = inflater.inflate(R.layout.home_page_content_profile, null);
			refView = new WeakReference<View>(contentView);
			if (Logger.isDebug()) {
				Log.v("AppChangeListener", "reclaim:" + this.getClass().getCanonicalName());
			}
		}
		if (user == null) {
			return contentView;
		}

		updateHeader();
		bindEvent(contentView, user);

		updateProfileView(contentView, user);
		updateStatusView(contentView, user);

		followersClickListener.setUser(user);
		friendsClickListener.setUser(user);
		statusesCountClickListener.setUser(user);
		if (!isSohu) {
			blocksClickListener.setUser(user);
		}

		return contentView;
	}

	private void updateHeader() {
	    View llHeaderBase = ((Activity)context).findViewById(R.id.llHeaderBase);
	    llHeaderBase.setVisibility(View.VISIBLE);
	    View llHeaderMessage = ((Activity)context).findViewById(R.id.llHeaderMessage);
	    llHeaderMessage.setVisibility(View.GONE);

		TextView tvTitle = (TextView) context.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_tab_profile);
		ImageButton ibProfileImage = (ImageButton) context.findViewById(R.id.ibProfileImage);
		ibProfileImage.setVisibility(View.VISIBLE);

		ImageButton ibGroup = (ImageButton) context.findViewById(R.id.ibGroup);
		ibGroup.setVisibility(View.GONE);
		ImageButton ibEdit = (ImageButton) context.findViewById(R.id.ibEdit);
		ibEdit.setVisibility(View.VISIBLE);
		ibEdit.setOnClickListener(new HomePageEditStatusClickListener(context));
	}

	private void bindEvent(View contentView, final User user) {
		View llProfileHeader = contentView.findViewById(R.id.llProfileHeader);
		Button btnEditProfile = (Button) contentView.findViewById(R.id.btnEditProfile);
		LinearLayout llFavsAndBlocks = (LinearLayout) contentView.findViewById(R.id.llFavsAndBlocks);
		Button btnFollow = (Button) contentView.findViewById(R.id.btnFollow);

		llProfileHeader.setEnabled(false);
		btnFollow.setVisibility(View.GONE);
		btnEditProfile.setVisibility(View.VISIBLE);
		llFavsAndBlocks.setVisibility(View.VISIBLE);

		btnEditProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("USER", user);
				intent.setClass(context, ProfileEditActivity.class);
				((Activity)v.getContext()).startActivityForResult(intent, Constants.REQUEST_CODE_PROFILE_EDIT);
			}
		});

		LinearLayout llStatusesCount = (LinearLayout) contentView.findViewById(R.id.llStatusesCount);
		statusesCountClickListener = new ProfileStatusCountClickListener(context);
		statusesCountClickListener.setUser(user);
		llStatusesCount.setOnClickListener(statusesCountClickListener);

		LinearLayout llFriendsCount = (LinearLayout) contentView.findViewById(R.id.llFriendsCount);
		friendsClickListener = new ProfileSocialGraphClickListener(context);
		friendsClickListener.setType(SocialGraphTask.TYPE_FRIENDS);
		friendsClickListener.setUser(user);
		llFriendsCount.setOnClickListener(friendsClickListener);

		LinearLayout llFollowersCount = (LinearLayout) contentView.findViewById(R.id.llFollowersCount);
		followersClickListener = new ProfileSocialGraphClickListener(context);
		followersClickListener.setType(SocialGraphTask.TYPE_FOLLOWERS);
		followersClickListener.setUser(user);
		llFollowersCount.setOnClickListener(followersClickListener);

		LinearLayout llFavorites = (LinearLayout) contentView.findViewById(R.id.llFavorites);
		llFavorites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), MyFavoritesActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		LinearLayout llTopics = (LinearLayout) contentView.findViewById(R.id.llTopics);
		llTopics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});

		LinearLayout llBlocks = (LinearLayout) contentView.findViewById(R.id.llBlocks);
		if (!isSohu) {
			blocksClickListener = new ProfileSocialGraphClickListener(context);
			blocksClickListener.setType(SocialGraphTask.TYPE_BLOCKS);
			blocksClickListener.setUser(user);
			llBlocks.setOnClickListener(blocksClickListener);
		}

	}

	private void updateProfileView(View contentView, User user) {
		LinearLayout llProfileHeader = (LinearLayout) contentView.findViewById(R.id.llProfileHeader);
		TextView tvScreenName = (TextView) contentView.findViewById(R.id.tvScreenName);
		ImageView ivVerify = (ImageView) contentView.findViewById(R.id.ivVerify);
		TextView tvImpress = (TextView) contentView.findViewById(R.id.tvImpress);
		Button btnEditProfile = (Button) contentView.findViewById(R.id.btnEditProfile);
		ScrollView llContentPanel = (ScrollView) contentView.findViewById(R.id.llContentPanel);
		LinearLayout llDescription = (LinearLayout) contentView.findViewById(R.id.llDescription);
		TextView tvDescription = (TextView) contentView.findViewById(R.id.tvDescription);
		LinearLayout llSocialGraph = (LinearLayout) contentView.findViewById(R.id.llSocialGraph);
		LinearLayout llFriendsCount = (LinearLayout) contentView.findViewById(R.id.llFriendsCount);
		TextView tvFriendsCount = (TextView) contentView.findViewById(R.id.tvFriendsCount);
		TextView tvFriendsLabel = (TextView) contentView.findViewById(R.id.tvFriendsLabel);
		LinearLayout llFollowersCount = (LinearLayout) contentView.findViewById(R.id.llFollowersCount);
		TextView tvFollowersCount = (TextView) contentView.findViewById(R.id.tvFollowersCount);
		TextView tvFollowersLabel = (TextView) contentView.findViewById(R.id.tvFollowersLabel);
		LinearLayout llStatusesCount = (LinearLayout) contentView.findViewById(R.id.llStatusesCount);
		TextView tvStatusesCount = (TextView) contentView.findViewById(R.id.tvStatusesCount);
		TextView tvStatusesLabel = (TextView) contentView.findViewById(R.id.tvStatusesLabel);
		
        ImageView ivLineSeperator_1 = (ImageView) contentView.findViewById(R.id.ivLineSeperator_1);
        ImageView ivLineSeperator_2 = (ImageView) contentView.findViewById(R.id.ivLineSeperator_2);
        ImageView ivLineSeperator_3 = (ImageView) contentView.findViewById(R.id.ivLineSeperator_3);
        ImageView ivLineSeperator_4 = (ImageView) contentView.findViewById(R.id.ivLineSeperator_4);
        
        LinearLayout llFavsAndBlocks = (LinearLayout) contentView.findViewById(R.id.llFavsAndBlocks);
        LinearLayout llFavorites = (LinearLayout) contentView.findViewById(R.id.llFavorites);
        LinearLayout llTopics = (LinearLayout) contentView.findViewById(R.id.llTopics);
        LinearLayout llBlocks = (LinearLayout) contentView.findViewById(R.id.llBlocks);

        TextView tvFavoritesLabel = (TextView) contentView.findViewById(R.id.tvFavoritesLabel);
        TextView tvFavoritesCount = (TextView) contentView.findViewById(R.id.tvFavoritesCount);
        ImageView ivFavoritesMore = (ImageView) contentView.findViewById(R.id.ivFavoritesMore);
        TextView tvTopicLabel = (TextView) contentView.findViewById(R.id.tvTopicLabel);
        ImageView ivTopicMore = (ImageView) contentView.findViewById(R.id.ivTopicMore);
        TextView tvBlocksLabel = (TextView) contentView.findViewById(R.id.tvBlocksLabel);
        ImageView ivBlocksMore = (ImageView) contentView.findViewById(R.id.ivBlocksMore);
        
		ThemeUtil.setHeaderProfile(llProfileHeader);
		Theme theme = ThemeUtil.createTheme(context);
		tvScreenName.setTextColor(theme.getColor("highlight"));
		ivVerify.setImageDrawable(GlobalResource.getIconVerification(context));
		tvImpress.setTextColor(theme.getColor("content"));
		ThemeUtil.setBtnActionPositive(btnEditProfile);
		llContentPanel.setBackgroundColor(theme.getColor("background_content"));
		int content = theme.getColor("content");
		llDescription.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		int padding8 = theme.dip2px(8);
		llDescription.setPadding(padding8, padding8, padding8, padding8);
		tvDescription.setTextColor(content);
		llSocialGraph.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		int padding1 = theme.dip2px(1);
		llSocialGraph.setPadding(padding1, padding1, padding1, padding1);
		llFriendsCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_left_corner"));
		int fivePadding = theme.dip2px(5);
		llFriendsCount.setPadding(fivePadding, fivePadding, fivePadding, fivePadding);
		int personalCount = theme.getColor("personal_count");
		tvFriendsCount.setTextColor(personalCount);
		llFollowersCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
		llFollowersCount.setPadding(fivePadding, fivePadding, fivePadding, fivePadding);
		tvFollowersCount.setTextColor(personalCount);
		llStatusesCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_right_corner"));
		llStatusesCount.setPadding(fivePadding, fivePadding, fivePadding, fivePadding);
		tvStatusesCount.setTextColor(personalCount);
		tvFriendsLabel.setTextColor(content);
		tvFollowersLabel.setTextColor(content);
		tvStatusesLabel.setTextColor(content);
		
		llFavsAndBlocks.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		llFavsAndBlocks.setPadding(padding1, padding1, padding1, padding1);
		llFavorites.setBackgroundDrawable(theme.getDrawable("selector_frame_item_top_corner"));
		llFavorites.setPadding(padding8, padding8, padding8, padding8);
		llTopics.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
		llTopics.setPadding(padding8, padding8, padding8, padding8);
		llBlocks.setBackgroundDrawable(theme.getDrawable("selector_frame_item_bottom_corner"));
		llBlocks.setPadding(padding8, padding8, padding8, padding8);
		tvFavoritesLabel.setTextColor(content);
		tvFavoritesCount.setTextColor(personalCount);
		tvTopicLabel.setTextColor(content);
		tvBlocksLabel.setTextColor(content);
		ivFavoritesMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
		ivTopicMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
		ivBlocksMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
		
		ivLineSeperator_1.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_2.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_3.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_4.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		
		if (StringUtil.isEmpty(user.getLocation()) || ",".equals(user.getLocation())) {
			String gender = ResourceBook.getGenderValue(user.getGender(), context);
			tvImpress.setText(gender);
		} else {
			String gender = ResourceBook.getGenderValue(user.getGender(), context);
			tvImpress.setText(gender + "," + user.getLocation());
		}

		if (user.isVerified()) {
        	ivVerify.setVisibility(View.VISIBLE);
        }

		tvScreenName.setText(user.getScreenName());

		if (StringUtil.isEmpty(user.getDescription())) {
			tvDescription.setText(context.getString(R.string.hint_personal_default_description));
		} else {
			tvDescription.setText(Html.fromHtml(user.getDescription()));
		}

		tvFollowersCount.setText(String.valueOf(user.getFollowersCount()));
		tvFriendsCount.setText(String.valueOf(user.getFriendsCount()));
		tvStatusesCount.setText(String.valueOf(user.getStatusesCount()));

		updateProfileImage(contentView, user.getProfileImageUrl());

		if (isTencent) { //腾讯微博无法获取收藏数
			tvFavoritesCount.setText("");
		} else {
			String favoriteCount = context.getString(
				R.string.label_personal_count, user.getFavouritesCount()
			);
			tvFavoritesCount.setText(favoriteCount);
		}
	}

	private void updateStatusView(View contentView, User user) {
		LinearLayout llStatus = (LinearLayout) contentView.findViewById(R.id.llStatus);
		TextView tvText = (TextView) contentView.findViewById(R.id.tvText);
		ImageView ivThumbnail = (ImageView) contentView.findViewById(R.id.ivThumbnail);
		LinearLayout llRetweet = (LinearLayout) contentView.findViewById(R.id.llRetweet);
		TextView tvRetweetText = (TextView) contentView.findViewById(R.id.tvRetweetText);
		ImageView ivRetweetThumbnail = (ImageView) contentView.findViewById(R.id.ivRetweetThumbnail);
		TextView tvRetweetCreatedAt = (TextView) contentView.findViewById(R.id.tvRetweetCreatedAt);
		TextView tvRetweetSource = (TextView) contentView.findViewById(R.id.tvRetweetSource);
		TextView tvCreateAt = (TextView) contentView.findViewById(R.id.tvCreateAt);
		TextView tvSource = (TextView) contentView.findViewById(R.id.tvSource);
		TextView tvResponse = (TextView) contentView.findViewById(R.id.tvResponse);

		Theme theme = ThemeUtil.createTheme(context);
		llStatus.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		int eightPadding = theme.dip2px(8);
		llStatus.setPadding(eightPadding, eightPadding, eightPadding, eightPadding);
        tvText.setTextColor(theme.getColor("content"));
        tvText.setLinkTextColor(theme.getColorStateList("selector_text_link"));
        tvRetweetText.setTextColor(theme.getColor("quote"));
        tvRetweetText.setLinkTextColor(theme.getColorStateList("selector_text_link"));
        int quote = theme.getColor("quote");
        tvRetweetCreatedAt.setTextColor(quote);
        tvRetweetSource.setTextColor(quote);
        tvSource.setTextColor(quote);
        tvCreateAt.setTextColor(quote);
        tvResponse.setTextColor(theme.getColor("emphasize"));
        Drawable shapeAttachment = theme.getDrawable("shape_attachment");
        ivThumbnail.setBackgroundDrawable(shapeAttachment);
        ivRetweetThumbnail.setBackgroundDrawable(shapeAttachment);
        llRetweet.setBackgroundDrawable(GlobalResource.getBgRetweetFrame(context));
        llRetweet.setPadding(theme.dip2px(10), theme.dip2px(12), 
            theme.dip2px(10), theme.dip2px(6));
        
		final Status status = user.getStatus();
		if (status == null) {
			llStatus.setVisibility(View.GONE);
			return;
		}
		Status retweet = status.getRetweetedStatus();

		llStatus.setVisibility(View.VISIBLE);
		ProfileStatusClickListener statusClickListener = new ProfileStatusClickListener(context);
		statusClickListener.setStatus(status);
		llStatus.setOnClickListener(statusClickListener);

		if (StringUtil.isNotEmpty(status.getText())) {
			Spannable textSpan = EmotionLoader.getEmotionSpannable(status.getServiceProvider(), status.getText());
			tvText.setText(textSpan);
		}

		String thumbnailPicture = status.getThumbnailPictureUrl();
		ivThumbnail.setVisibility(View.GONE);
		ivThumbnail.setImageBitmap(null);
		ivRetweetThumbnail.setVisibility(View.GONE);
		ivRetweetThumbnail.setImageBitmap(null);
		if (retweet != null) {
			thumbnailPicture = retweet.getThumbnailPictureUrl();
			ivThumbnail =  ivRetweetThumbnail;
		}
		if (StringUtil.isNotEmpty(thumbnailPicture)) {
			ivThumbnail.setVisibility(View.VISIBLE);
			ivThumbnail.setImageDrawable(GlobalResource.getDefaultThumbnail(context));
			ivThumbnail.setOnClickListener(new ImageClickListener(status));

			ImageLoad4ThumbnailTask imageLoadTask = new ImageLoad4ThumbnailTask(ivThumbnail, thumbnailPicture);
			imageLoadTask.execute(status);
		}

        if (retweet != null) {
        	llRetweet.setVisibility(View.VISIBLE);

			String retweetText = retweet.getText();
			User sourceUser = retweet.getUser();
			if (sourceUser != null) {
				String retweetScreenName = sourceUser.getMentionTitleName();
				retweetText = (retweetScreenName + ": " + retweetText);
			}
			
			Spannable retweetTextSpan = EmotionLoader.getEmotionSpannable(
				status.getServiceProvider(), retweetText);
			tvRetweetText.setText(retweetTextSpan);

			if (retweet.getSource() != null) {
				contentView.findViewById(R.id.llRetweetState).setVisibility(View.VISIBLE);
				String retweetCreatedAt = TimeSpanUtil.toTimeSpanString(retweet.getCreatedAt());
				tvRetweetCreatedAt.setText(retweetCreatedAt);
				String retweetSource = context.getString(R.string.label_status_source, retweet.getSource());
				retweetSource = Html.fromHtml(retweetSource).toString();
				tvRetweetSource.setText(retweetSource);
			}
		} else {
			llRetweet.setVisibility(View.GONE);
		}

		String createdAt = TimeSpanUtil.toTimeSpanString(status.getCreatedAt());

		tvCreateAt.setText(createdAt);
		String source = context.getString(R.string.label_status_source, status.getSource());
		source = Html.fromHtml(source).toString();
		tvSource.setText(source);

		String responseFormat = GlobalResource.getStatusResponseFormat(context);
		String responseText = String.format(responseFormat, 0, 0);
    	tvResponse.setText(responseText);
		QueryResponseCountTask task = new QueryResponseCountTask(context, status, tvResponse);
		task.execute();
	}

	private void updateProfileImage(View contentView, String imageUrl) {
		ImageView ivProfilePicture = (ImageView) contentView.findViewById(R.id.ivProfilePicture);
		if (StringUtil.isNotEmpty(imageUrl)) {
			ImageLoad4HeadTask loadTask = new ImageLoad4HeadTask(ivProfilePicture, imageUrl, false);
			loadTask.execute();
		}
	}

}
