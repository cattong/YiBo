package com.shejiaomao.weibo.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.entity.Relationship;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.entity.UserExtInfo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GoHomeClickListener;
import com.shejiaomao.weibo.service.listener.ImageClickListener;
import com.shejiaomao.weibo.service.listener.ProfileBlockClickListener;
import com.shejiaomao.weibo.service.listener.ProfileFollowClickListener;
import com.shejiaomao.weibo.service.listener.ProfileSocialGraphClickListener;
import com.shejiaomao.weibo.service.listener.ProfileStatusClickListener;
import com.shejiaomao.weibo.service.listener.ProfileStatusCountClickListener;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;
import com.shejiaomao.weibo.service.task.QueryResponseCountTask;
import com.shejiaomao.weibo.service.task.QueryUserExtInfoTask;
import com.shejiaomao.weibo.service.task.QueryUserTask;
import com.shejiaomao.weibo.service.task.RelationshipCheckTask;
import com.shejiaomao.weibo.service.task.SocialGraphTask;

public class ProfileActivity extends BaseActivity {
    private LocalAccount account;
	private User user;
    //private Relationship relationship;

	private TextView tvScreenName;
	private ImageView ivVerify;
	private TextView tvImpress;
	
	private LinearLayout llVerifyInfo;
	private TextView tvVerifyInfo;
	private TextView tvDescription;

	private TextView tvStatusesCount;
	private TextView tvFriendsCount;
	private TextView tvFollowersCount;

	private TextView tvText;
	private TextView tvRetweetText;
	private TextView tvRetweetCreatedAt;
	private TextView tvRetweetSource;
	private TextView tvCreateAt;
	private TextView tvSource;
	private TextView tvResponse;

	private ProfileFollowClickListener followClickListener;
	private ProfileBlockClickListener blockClickListener;
	private ProfileSocialGraphClickListener friendsClickListener;
	private ProfileSocialGraphClickListener followersClickListener;
	private ProfileStatusCountClickListener statusesCountClickListener;
	private boolean isLoading;
	private boolean isSohu;

	private boolean isSelfProfile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);

		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)getApplication();
		account = sheJiaoMao.getCurrentAccount();
		if (account != null) {
			isSohu = account.getServiceProvider() == ServiceProvider.Sohu;
		}
		initComponents();
		bindEvent();

		//对sheJiaoMao://info/@XX地址的解析
		Uri uri = this.getIntent().getData();
		if (uri != null && uri.getPath() != null) {
			String displayName = uri.getPath().substring(2);
			user = new User();
			user.setUserId(displayName);
			user.setName(displayName);
			user.setScreenName(displayName);
			user.setServiceProvider(account.getServiceProvider());
		} else {
			user = (User)getIntent().getExtras().getSerializable("USER");
		}

		if (user.getDescription() == null || user.getUserId() == null) {
			isLoading = true;
		}

		this.isSelfProfile = user.equals(account.getUser());

		if (isLoading) {
			new QueryUserTask(this, user).execute();
		} else if (user.getStatus() == null) {
			updateUI();
			new QueryUserTask(this, user).execute();
		} else {
			updateUI();
			if (user.isVerified()) {
				new QueryUserExtInfoTask(this, user).execute();
			}
		}

		checkRelationship();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	
    	//个人资料头部
    	LinearLayout llProfileHeader = (LinearLayout)findViewById(R.id.llProfileHeader);
		tvScreenName = (TextView)findViewById(R.id.tvScreenName);
		ivVerify = (ImageView)findViewById(R.id.ivVerify);
		tvImpress = (TextView)findViewById(R.id.tvImpress);
		Button btnFollow = (Button)findViewById(R.id.btnFollow);
		Button btnEditProfile = (Button)findViewById(R.id.btnEditProfile);
		ThemeUtil.setHeaderProfile(llProfileHeader);
		Theme theme = ThemeUtil.createTheme(this);
		tvScreenName.setTextColor(theme.getColor("highlight"));
		ivVerify.setImageDrawable(GlobalResource.getIconVerification(this));
		tvImpress.setTextColor(theme.getColor("content"));
		ThemeUtil.setBtnActionPositive(btnFollow);
		ThemeUtil.setBtnActionPositive(btnEditProfile);
		
		//个人资料内容
		ScrollView llContentPanel = (ScrollView)findViewById(R.id.llContentPanel);
		llVerifyInfo = (LinearLayout) this.findViewById(R.id.llVerifyInfo);
		tvVerifyInfo = (TextView) this.findViewById(R.id.tvVerifyInfo);
		LinearLayout llDescription = (LinearLayout)findViewById(R.id.llDescription);
		tvDescription = (TextView)findViewById(R.id.tvDescription);
		LinearLayout llSocialGraph = (LinearLayout)findViewById(R.id.llSocialGraph);
		LinearLayout llFriendsCount = (LinearLayout)findViewById(R.id.llFriendsCount);
		tvFriendsCount = (TextView)findViewById(R.id.tvFriendsCount);
		TextView tvFriendsLabel = (TextView)findViewById(R.id.tvFriendsLabel);
		LinearLayout llFollowersCount = (LinearLayout)findViewById(R.id.llFollowersCount);
		tvFollowersCount = (TextView)findViewById(R.id.tvFollowersCount);
		TextView tvFollowersLabel = (TextView)findViewById(R.id.tvFollowersLabel);
		LinearLayout llStatusesCount = (LinearLayout)findViewById(R.id.llStatusesCount);
		tvStatusesCount = (TextView)findViewById(R.id.tvStatusesCount);
		TextView tvStatusesLabel = (TextView)findViewById(R.id.tvStatusesLabel);
		
        ImageView ivLineSeperator_1 = (ImageView)findViewById(R.id.ivLineSeperator_1);
        ImageView ivLineSeperator_2 = (ImageView)findViewById(R.id.ivLineSeperator_2);
        ImageView ivLineSeperator_3 = (ImageView)findViewById(R.id.ivLineSeperator_3);
        ImageView ivLineSeperator_4 = (ImageView)findViewById(R.id.ivLineSeperator_4);
        
        LinearLayout llFavsAndBlocks = (LinearLayout)findViewById(R.id.llFavsAndBlocks);
        LinearLayout llFavorites = (LinearLayout)findViewById(R.id.llFavorites);
        LinearLayout llTopics = (LinearLayout)findViewById(R.id.llTopics);
        LinearLayout llBlocks = (LinearLayout)findViewById(R.id.llBlocks);

        TextView tvFavoritesLabel = (TextView)findViewById(R.id.tvFavoritesLabel);
        TextView tvFavoritesCount = (TextView)findViewById(R.id.tvFavoritesCount);
        TextView tvTopicLabel = (TextView)findViewById(R.id.tvTopicLabel);
        TextView tvBlocksLabel = (TextView)findViewById(R.id.tvBlocksLabel);
        
		llContentPanel.setBackgroundColor(theme.getColor("background_content"));
		int content = theme.getColor("content");
		llVerifyInfo.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		int padding8 = theme.dip2px(8);
		llVerifyInfo.setPadding(padding8, padding8, padding8, padding8);
		tvVerifyInfo.setTextColor(content);
		llDescription.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		llDescription.setPadding(padding8, padding8, padding8, padding8);
		tvDescription.setTextColor(content);
		llSocialGraph.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		int padding1 = theme.dip2px(1);
		llSocialGraph.setPadding(padding1, padding1, padding1, padding1);
		llFriendsCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_left_corner"));
		int padding5 = theme.dip2px(5);
		llFriendsCount.setPadding(padding5, padding5, padding5, padding5);
		int personalCount = theme.getColor("personal_count");
		tvFriendsCount.setTextColor(personalCount);
		llFollowersCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
		llFollowersCount.setPadding(padding5, padding5, padding5, padding5);
		tvFollowersCount.setTextColor(personalCount);
		llStatusesCount.setBackgroundDrawable(theme.getDrawable("selector_frame_item_right_corner"));
		llStatusesCount.setPadding(padding5, padding5, padding5, padding5);
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
		
		ivLineSeperator_1.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_2.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_3.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		ivLineSeperator_4.setBackgroundDrawable(theme.getDrawable("line_seperator"));
		
		//最新一条微博
		LinearLayout llStatus = (LinearLayout)findViewById(R.id.llStatus);
		tvText = (TextView)findViewById(R.id.tvText);
		ImageView ivThumbnail = (ImageView)findViewById(R.id.ivThumbnail);
		LinearLayout llRetweet = (LinearLayout)findViewById(R.id.llRetweet);
		tvRetweetText = (TextView)findViewById(R.id.tvRetweetText);
		ImageView ivRetweetThumbnail = (ImageView)findViewById(R.id.ivRetweetThumbnail);
		tvRetweetCreatedAt = (TextView)findViewById(R.id.tvRetweetCreatedAt);
		tvRetweetSource = (TextView)findViewById(R.id.tvRetweetSource);
		tvCreateAt = (TextView)findViewById(R.id.tvCreateAt);
		tvSource = (TextView)findViewById(R.id.tvSource);
		tvResponse = (TextView)findViewById(R.id.tvResponse);

		llStatus.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
		llStatus.setPadding(padding8, padding8, padding8, padding8);
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
        llRetweet.setBackgroundDrawable(GlobalResource.getBgRetweetFrame(this));
        llRetweet.setPadding(theme.dip2px(10), theme.dip2px(12), 
            theme.dip2px(10), theme.dip2px(6));
        
		//工具条
        LinearLayout llToolbar = (LinearLayout)findViewById(R.id.llToolbar);
        Button btnMention = (Button)findViewById(R.id.btnMention);
        Button btnMessage = (Button)findViewById(R.id.btnMessage);
        Button btnBlock = (Button)findViewById(R.id.btnBlock);
        llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
        llToolbar.setGravity(Gravity.CENTER);
        llToolbar.setPadding(0, 4, 0, 4);
        btnMention.setBackgroundDrawable(theme.getDrawable("selector_btn_profile_mention"));
        btnMessage.setBackgroundDrawable(theme.getDrawable("selector_btn_profile_message"));
        btnBlock.setBackgroundDrawable(theme.getDrawable("selector_btn_profile_block"));
        
		llProfileHeader.setEnabled(false);
	}
	
	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());

		followClickListener = new ProfileFollowClickListener(user);
		blockClickListener = new ProfileBlockClickListener(user);

		LinearLayout llStatusesCount = (LinearLayout) this.findViewById(R.id.llStatusesCount);
		statusesCountClickListener = new ProfileStatusCountClickListener(this);
		statusesCountClickListener.setUser(user);
		llStatusesCount.setOnClickListener(statusesCountClickListener);

		LinearLayout llFriendsCount = (LinearLayout) this.findViewById(R.id.llFriendsCount);
		friendsClickListener = new ProfileSocialGraphClickListener(this);
		friendsClickListener.setType(SocialGraphTask.TYPE_FRIENDS);
		friendsClickListener.setUser(user);
		llFriendsCount.setOnClickListener(friendsClickListener);

		LinearLayout llFollowersCount = (LinearLayout) this.findViewById(R.id.llFollowersCount);
		followersClickListener = new ProfileSocialGraphClickListener(this);
		followersClickListener.setType(SocialGraphTask.TYPE_FOLLOWERS);
		followersClickListener.setUser(user);
		llFollowersCount.setOnClickListener(followersClickListener);

		Button btnMention = (Button) findViewById(R.id.btnMention);
		btnMention.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("TYPE", Constants.EDIT_TYPE_MENTION);
				intent.putExtra("APPEND_TEXT", user.getMentionName());
				intent.setClass(v.getContext(), EditMicroBlogActivity.class);
				startActivity(intent);
			}
		});
	}

	private void updateUI() {
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(user.getScreenName());
		if (StringUtil.isEmpty(user.getLocation()) || ",".equals(user.getLocation())) {
			String gender = ResourceBook.getGenderValue(user.getGender(), this);
			tvImpress.setText(gender);
		} else {
			String gender = ResourceBook.getGenderValue(user.getGender(), this);
			tvImpress.setText(gender + "," + user.getLocation());
		}

		if (user.isVerified()) {
        	ivVerify.setVisibility(View.VISIBLE);
        	llVerifyInfo.setVisibility(View.VISIBLE);
        	if (StringUtil.isNotEmpty(user.getVerifyInfo())) {
        		String verifyInfo = this.getString(
        				R.string.label_profile_Verify_Info, 
        				user.getVerifyInfo());
        		tvVerifyInfo.setText(verifyInfo);
        	}
        }

		tvScreenName.setText(user.getScreenName());
		if (StringUtil.isEmpty(user.getDescription())) {
			tvDescription.setText(getString(R.string.hint_personal_default_description));
		} else {
			tvDescription.setText(Html.fromHtml(user.getDescription()));
		}

		followClickListener.setUser(user);
		blockClickListener.setUser(user);

		tvFollowersCount.setText(String.valueOf(user.getFollowersCount()));
		tvFriendsCount.setText(String.valueOf(user.getFriendsCount()));
		tvStatusesCount.setText(String.valueOf(user.getStatusesCount()));
		followersClickListener.setUser(user);
		friendsClickListener.setUser(user);
		statusesCountClickListener.setUser(user);

		updateProfileImage(user.getProfileImageUrl());

		if (isSelfProfile || account == null || user == null) {
			Button btnFollow = (Button) this.findViewById(R.id.btnFollow);
			btnFollow.setVisibility(View.GONE);
		}

		LinearLayout llStatus = (LinearLayout)findViewById(R.id.llStatus);
		final Status status = user.getStatus();
		if (status == null) {
			llStatus.setVisibility(View.GONE);
			return;
		}

		llStatus.setVisibility(View.VISIBLE);
		ProfileStatusClickListener statusClickListener = new ProfileStatusClickListener(this);
		status.setUser(user);
		statusClickListener.setStatus(status);
		llStatus.setOnClickListener(statusClickListener);

		if (StringUtil.isNotEmpty(status.getText())) {
			Spannable textSpan = EmotionLoader.getEmotionSpannable(
				status.getServiceProvider(), status.getText());
			tvText.setText(textSpan);
		}

		Status retweet = status.getRetweetedStatus();
		String thumbnailPicture = status.getThumbnailPictureUrl();
		ImageView ivThumbnail = (ImageView) this.findViewById(R.id.ivThumbnail);
		if (retweet != null) {
			thumbnailPicture = retweet.getThumbnailPictureUrl();
			ivThumbnail = (ImageView) this.findViewById(R.id.ivRetweetThumbnail);
		}
		if (StringUtil.isNotEmpty(thumbnailPicture)) {
			ivThumbnail.setVisibility(View.VISIBLE);
			ivThumbnail.setImageDrawable(GlobalResource.getDefaultThumbnail(this));
			ivThumbnail.setOnClickListener(new ImageClickListener(status));

			ImageLoad4ThumbnailTask imageLoadTask = new ImageLoad4ThumbnailTask(ivThumbnail, thumbnailPicture);
			imageLoadTask.execute(status);
		}

        if (retweet != null) {
			this.findViewById(R.id.llRetweet).setVisibility(View.VISIBLE);

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
				this.findViewById(R.id.llRetweetState).setVisibility(View.VISIBLE);
				String retweetCreatedAt = TimeSpanUtil.toTimeSpanString(retweet.getCreatedAt());
				tvRetweetCreatedAt.setText(retweetCreatedAt);
				String retweetSource = this.getString(R.string.label_status_source, retweet.getSource());
				retweetSource = Html.fromHtml(retweetSource).toString();
				tvRetweetSource.setText(retweetSource);
			}
		}

		String createdAt = TimeSpanUtil.toTimeSpanString(status.getCreatedAt());
		tvCreateAt.setText(createdAt);
		String source = getString(R.string.label_status_source, status.getSource());
		source = Html.fromHtml(source).toString();
        tvSource.setText(source);

		String responseFormat = GlobalResource.getStatusResponseFormat(this);
		String responseText = String.format(responseFormat, 0, 0);
    	tvResponse.setText(responseText);
		QueryResponseCountTask task = new QueryResponseCountTask(this, status, tvResponse);
		task.execute();
	}

	private void updateProfileImage(String imageUrl) {
		ImageView ivProfilePicture = (ImageView) this.findViewById(R.id.ivProfilePicture);
		if (StringUtil.isNotEmpty(imageUrl)) {
			ImageLoad4HeadTask loadTask = new ImageLoad4HeadTask(ivProfilePicture, imageUrl, false);
			loadTask.execute();
		}
	}

	private void updateRelationship() {
		if (user == null || user.getRelationship() == null) {
			return;
		}

		Relationship relationship = user.getRelationship();
		
		Button btnFollow = (Button) findViewById(R.id.btnFollow);
		Button btnMessage = (Button) findViewById(R.id.btnMessage);
		Button btnBlock = (Button) findViewById(R.id.btnBlock);

		View.OnClickListener btnFollowOnClickListener = followClickListener;
		btnFollow.setEnabled(true);		
		if (relationship.isSourceFollowingTarget()) {
			((ProfileFollowClickListener)btnFollowOnClickListener).setUser(user);
			btnFollow.setTextAppearance(this, R.style.btn_action_negative);
			btnFollow.setText(R.string.btn_personal_unfollow);
			btnFollow.setTextColor(theme.getColor("selector_btn_action_negative"));
			btnFollow.setBackgroundDrawable(theme.getDrawable("selector_btn_action_negative"));
		} else if (relationship.isSourceBlockingTarget()) {
			((ProfileBlockClickListener)btnFollowOnClickListener).setUser(user);
			btnFollow.setTextAppearance(this, R.style.btn_action_positive);
			btnFollow.setText(R.string.btn_personal_unblock);
			btnFollow.setTextColor(theme.getColor("selector_btn_action_positive"));
			btnFollow.setBackgroundDrawable(theme.getDrawable("selector_btn_action_positive"));
			btnFollowOnClickListener = blockClickListener;
		} else {
			((ProfileFollowClickListener)btnFollowOnClickListener).setUser(user);
			btnFollow.setTextAppearance(this, R.style.btn_action_positive);
			btnFollow.setText(R.string.btn_personal_follow);
			btnFollow.setTextColor(theme.getColor("selector_btn_action_positive"));
			btnFollow.setBackgroundDrawable(theme.getDrawable("selector_btn_action_positive"));
		}
		btnFollow.setOnClickListener(btnFollowOnClickListener);

		ImageView ivFriendship = (ImageView) this.findViewById(R.id.ivFriendship);
        if (relationship.isSourceFollowingTarget() && relationship.isSourceFollowedByTarget()) {
        	ivFriendship.setVisibility(View.VISIBLE);
        } else {
        	ivFriendship.setVisibility(View.GONE);
        }
        
		if (relationship.isSourceFollowedByTarget() || true) {
			btnMessage.setVisibility(View.VISIBLE);
			btnMessage.setEnabled(true);
			btnMessage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("TYPE", Constants.EDIT_TYPE_TO_MESSAGE);
					intent.putExtra("DISPLAY_NAME", user.getDisplayName());
					intent.setClass(v.getContext(), EditDirectMessageActivity.class);
					v.getContext().startActivity(intent);
				}
			});
		}

		if (isSohu) {
			return;
			//搜狐不支持黑名单接口，下面代码是黑名单相关的
		}

		if (relationship.isSourceBlockingTarget()) {
			btnBlock.setBackgroundDrawable(theme.getDrawable("selector_btn_profile_unblock"));
		} else {
			btnBlock.setBackgroundDrawable(theme.getDrawable("selector_btn_profile_block"));
		}

		btnBlock.setVisibility(View.VISIBLE);
		btnBlock.setEnabled(true);
		btnBlock.setOnClickListener(blockClickListener);
	}


	public void setRelationship(Relationship relationship) {
		if (relationship == null) {
			return;
		}
		if (user != null) {
			user.setRelationship(relationship);
		}
		updateRelationship();
	}

	public void checkRelationship() {
		if (isSelfProfile) {
			return;
		}
		new RelationshipCheckTask(this, user).execute();
	}

	public void setVerifyInfo(UserExtInfo userExtInfo) {
		if (userExtInfo != null) {
			llVerifyInfo.setVisibility(View.VISIBLE);
			String verifyInfo = this.getString(
				R.string.label_profile_Verify_Info, 
				userExtInfo.getVerifyInfo());
			tvVerifyInfo.setText(verifyInfo);
		} else {
			llVerifyInfo.setVisibility(View.GONE);
		}
		
	}
	
	public void setUser(User user) {
		if (user == null) {
			return;
		}
		User oldUser = this.user;
		this.user = user;
		if (!oldUser.getUserId().equals(user.getUserId())) {
			checkRelationship();
		}

		isLoading = false;
		updateUI();
	}

	public User getUser() {
		return this.user;
	}

}
