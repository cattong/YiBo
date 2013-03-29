package com.shejiaomao.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import com.shejiaomao.maobo.R;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.StringUtil;
import com.cattong.commons.util.TimeSpanUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Location;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EmotionLoader;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.CommentsOfStatusListAdapter;
import com.shejiaomao.weibo.service.listener.AutoLoadMoreListener;
import com.shejiaomao.weibo.service.listener.CommentsOfStatusContextMenuListener;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GoHomeClickListener;
import com.shejiaomao.weibo.service.listener.ImageClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogCommentClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogFavoriteClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogMoreClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogPreviewClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogRetweetClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogShareClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogStatusContextMenuListener;
import com.shejiaomao.weibo.service.listener.ProfileHeaderClickListener;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.ImageLoad4ThumbnailTask;
import com.shejiaomao.weibo.service.task.QueryCommentsOfStatusTask;
import com.shejiaomao.weibo.service.task.QueryLocationTask;
import com.shejiaomao.weibo.service.task.QueryResponseCountTask;
import com.shejiaomao.weibo.service.task.QueryRetweetResponseCountTask;
import com.shejiaomao.widget.RichTextView;

public class MicroBlogActivity extends BaseActivity {
	private Status status;
	private int position;
	private int sourceType;
	private boolean isTencent;
    private LocalAccount currentAccount;

    private ListView lvCommentsOfStatus;
    private View listFooter;
    private CommentsOfStatusListAdapter commentsAdapter;

    private ProfileHeaderClickListener profileClickListener;
    private MicroBlogStatusContextMenuListener statusContextMenuListener;
    private CommentsOfStatusContextMenuListener commentsContextMenuListener;

    private MicroBlogCommentClickListener commentClickListener;
    private MicroBlogRetweetClickListener retweetClickListener;
    private MicroBlogFavoriteClickListener favoriteClickListener;
    private MicroBlogShareClickListener shareClickListener;
    private MicroBlogMoreClickListener moreClickListener;

    private QueryRetweetResponseCountTask retweetResponseCountTask;
    private QueryResponseCountTask responseCountTask;
    private ImageLoad4ThumbnailTask thumbnailTask;
    private ImageLoad4HeadTask headTask;
    private QueryLocationTask locationTask;

    private AutoLoadMoreListener autoLoadMoreListener;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.micro_blog);

        initParams(savedInstanceState);
		initComponent();
		bindEvent();
		fillInView(status);
	}

    private void initParams(Bundle savedInstanceState) {
        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)this.getApplication();
        currentAccount = sheJiaoMao.getCurrentAccount();

        if (currentAccount != null
        	&& currentAccount.getUser() != null) {
        	isTencent = currentAccount.getServiceProvider() == ServiceProvider.Tencent;
        }

        Bundle bundle = this.getIntent().getExtras();
        if (savedInstanceState != null) {
        	bundle = savedInstanceState;
        }
		status = (Status)bundle.getSerializable("STATUS");
		position = bundle.getInt("POSITION");
		sourceType = bundle.getInt("SOURCE");

		if (status != null) {
		    isTencent = status.getServiceProvider() == ServiceProvider.Tencent;

		    if (status instanceof LocalStatus) {
		    	long accountId = ((LocalStatus)status).getAccountId();
			    LocalAccount temp = GlobalVars.getAccount(accountId);
			    if (temp != null) {
			    	currentAccount = temp;
			    }
		    }
		}
    }

    public void initComponent() {
    	LinearLayout llRoot = (LinearLayout)findViewById(R.id.llRoot);
    	LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ThemeUtil.setRootBackground(llRoot);
    	ThemeUtil.setSecondaryMicroBlogHeader(llHeaderBase);
    	
    	//资料头部
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View headerView = inflater.inflate(R.layout.include_micro_blog_list_header, null);
    	LinearLayout llProfileHeader = (LinearLayout)headerView.findViewById(R.id.llProfileHeader);
    	TextView tvScreenName = (TextView)headerView.findViewById(R.id.tvScreenName);
    	ImageView ivVerify = (ImageView)headerView.findViewById(R.id.ivVerify);
    	TextView tvImpress = (TextView)headerView.findViewById(R.id.tvImpress);
    	ImageView ivMoreDetail = (ImageView)headerView.findViewById(R.id.ivMoreDetail);
    	ThemeUtil.setHeaderProfile(llProfileHeader);
    	int highlight = theme.getColor("highlight");
    	tvScreenName.setTextColor(highlight);
    	ivVerify.setImageDrawable(theme.getDrawable("icon_verification"));
    	tvImpress.setTextColor(theme.getColor("content"));
    	ivMoreDetail.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
    	
    	//微博内容
    	TextView tvText = (TextView)headerView.findViewById(R.id.tvText);
    	LinearLayout llThumbnailShape = (LinearLayout)headerView.findViewById(R.id.llThumbnailShape);
    	TextView tvImageInfo = (TextView)headerView.findViewById(R.id.tvImageInfo);
    	LinearLayout llRetweet = (LinearLayout)headerView.findViewById(R.id.llRetweet);
    	TextView tvRetweetText = (TextView)headerView.findViewById(R.id.tvRetweetText);
    	LinearLayout llRetweetThumbnailShape = (LinearLayout)headerView.findViewById(R.id.llRetweetThumbnailShape);
    	TextView tvRetweetImageInfo = (TextView)headerView.findViewById(R.id.tvRetweetImageInfo);
    	ImageView ivRetweetLocation = (ImageView)headerView.findViewById(R.id.ivRetweetLocation);
    	TextView tvRetweetLocation = (TextView)headerView.findViewById(R.id.tvRetweetLocation);
    	TextView tvRetweetCreatedAt = (TextView)headerView.findViewById(R.id.tvRetweetCreatedAt);
    	TextView tvRetweetSource = (TextView)headerView.findViewById(R.id.tvRetweetSource);
    	ImageView ivLocation = (ImageView)headerView.findViewById(R.id.ivLocation);
    	TextView tvLocation = (TextView)headerView.findViewById(R.id.tvLocation);
    	TextView tvCreatedAt = (TextView)headerView.findViewById(R.id.tvCreatedAt);
    	TextView tvSource = (TextView)headerView.findViewById(R.id.tvSource);
    	TextView tvRetweetCount = (TextView)headerView.findViewById(R.id.tvRetweetCount);
    	TextView tvCommentCount = (TextView)headerView.findViewById(R.id.tvCommentCount);
    	ImageView ivLineSeperator = (ImageView)headerView.findViewById(R.id.ivLineSeperator);
    	
    	tvText.setTextColor(theme.getColor("content"));
    	ColorStateList selectorTextLink = theme.getColorStateList("selector_text_link");
    	tvText.setLinkTextColor(selectorTextLink);
    	Drawable shapeAttachment = theme.getDrawable("shape_attachment");
    	llThumbnailShape.setBackgroundDrawable(shapeAttachment);
    	int quote = theme.getColor("quote");
        tvImageInfo.setTextColor(quote);
        llRetweet.setBackgroundDrawable(theme.getDrawable("bg_retweet_frame"));
        int padding10 = theme.dip2px(10);
        llRetweet.setPadding(padding10, padding10, padding10, theme.dip2px(6));        
        tvRetweetText.setTextColor(quote);
        tvRetweetText.setLinkTextColor(selectorTextLink);
        llRetweetThumbnailShape.setBackgroundDrawable(shapeAttachment);
        tvRetweetImageInfo.setTextColor(quote);
        Drawable iconLocation = theme.getDrawable("icon_location");
        ivRetweetLocation.setImageDrawable(iconLocation);
        tvRetweetLocation.setTextColor(quote);
        tvRetweetCreatedAt.setTextColor(quote);
        tvRetweetSource.setTextColor(quote);
        ivLocation.setImageDrawable(iconLocation);
        tvLocation.setTextColor(quote);
        tvCreatedAt.setTextColor(quote);
        tvSource.setTextColor(quote);       
        
        int emphasize = theme.getColor("emphasize");
        tvRetweetCount.setTextColor(emphasize);
        tvCommentCount.setTextColor(emphasize);
        ivLineSeperator.setBackgroundDrawable(theme.getDrawable("line_comment_of_status_normal"));
        
        //工具条
        LinearLayout llToolbar = (LinearLayout)findViewById(R.id.llToolbar);
        Button btnComment = (Button)findViewById(R.id.btnComment);
        Button btnRetweet = (Button)findViewById(R.id.btnRetweet);
        Button btnFavorite = (Button)findViewById(R.id.btnFavorite);
        Button btnShare = (Button)findViewById(R.id.btnShare);
        Button btnMore = (Button)findViewById(R.id.btnMore);
        llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
        btnComment.setBackgroundDrawable(theme.getDrawable("selector_toolbar_comment"));
        btnRetweet.setBackgroundDrawable(theme.getDrawable("selector_toolbar_retweet"));
        btnFavorite.setBackgroundDrawable(theme.getDrawable("selector_toolbar_favorite_add"));
        btnShare.setBackgroundDrawable(theme.getDrawable("selector_toolbar_share"));
        btnMore.setBackgroundDrawable(theme.getDrawable("selector_toolbar_more"));
        
    	lvCommentsOfStatus = (ListView) this.findViewById(R.id.lvCommentsOfStatus);
    	ThemeUtil.setListViewStyle(lvCommentsOfStatus);
    	lvCommentsOfStatus.addHeaderView(headerView);
        setBack2Top(lvCommentsOfStatus);
        
        //注册上下文菜单
        View statusView = this.findViewById(R.id.llStatus);
        statusContextMenuListener = new MicroBlogStatusContextMenuListener(status);
        statusView.setOnCreateContextMenuListener(statusContextMenuListener);

        autoLoadMoreListener = new AutoLoadMoreListener();
    }

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		ComponentName componentName = this.getCallingActivity();
		String className = (componentName != null ? componentName.getShortClassName() : null);
		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		if (!StringUtil.isEquals(".HomePageActivity", className)) {
			btnOperate.setVisibility(View.VISIBLE);
			btnOperate.setText(R.string.btn_home);
			btnOperate.setOnClickListener(new GoHomeClickListener());
		}

		if (sourceType == Constants.REQUEST_CODE_MY_HOME) {
			btnOperate.setVisibility(View.GONE);
			MicroBlogPreviewClickListener previewClickListener =
				new MicroBlogPreviewClickListener(currentAccount, position);
		    Button btnPrevious = (Button) this.findViewById(R.id.btnPrevious);
		    btnPrevious.setVisibility(View.VISIBLE);
		    btnPrevious.setOnClickListener(previewClickListener);

		    Button btnNext = (Button) this.findViewById(R.id.btnNext);
		    btnNext.setVisibility(View.VISIBLE);
		    btnNext.setOnClickListener(previewClickListener);
		}

		View llProfileHeader = this.findViewById(R.id.llProfileHeader);
		profileClickListener = new ProfileHeaderClickListener(this, status.getUser());
		llProfileHeader.setOnClickListener(profileClickListener);

		Button btnComment = (Button) this.findViewById(R.id.btnComment);
		commentClickListener = new MicroBlogCommentClickListener(this, status);
		btnComment.setOnClickListener(commentClickListener);

		Button btnRetweet = (Button) this.findViewById(R.id.btnRetweet);
		retweetClickListener = new MicroBlogRetweetClickListener(this, status);
		btnRetweet.setOnClickListener(retweetClickListener);

		Button btnFavorite = (Button) this.findViewById(R.id.btnFavorite);
		favoriteClickListener = new MicroBlogFavoriteClickListener(this, status);
		btnFavorite.setOnClickListener(favoriteClickListener);

		Button btnShare = (Button) this.findViewById(R.id.btnShare);
		shareClickListener = new MicroBlogShareClickListener(this, status);
		btnShare.setOnClickListener(shareClickListener);

		Button btnMore = (Button) this.findViewById(R.id.btnMore);
		moreClickListener = new MicroBlogMoreClickListener(this, currentAccount);
		moreClickListener.setStatus(status);
		btnMore.setOnClickListener(moreClickListener);

		TextView tvRetweetCount = (TextView)this.findViewById(R.id.tvRetweetCount);
		tvRetweetCount.setOnClickListener(retweetClickListener);
		TextView tvCommentCount = (TextView)this.findViewById(R.id.tvCommentCount);
		tvCommentCount.setOnClickListener(commentClickListener);
	}

    public void fillInView(Status status) {
    	if (status == null) {
    		return;
    	}
    	destroyTasks();

		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		ImageView ivProfilePicture = (ImageView) this.findViewById(R.id.ivProfilePicture);
		TextView tvScreenName = (TextView) this.findViewById(R.id.tvScreenName);
		ImageView ivVerify = (ImageView) this.findViewById(R.id.ivVerify);
		TextView tvImpress = (TextView) this.findViewById(R.id.tvImpress);
		ImageView ivNext = (ImageView) this.findViewById(R.id.ivMoreDetail);
		LinearLayout llLocation = (LinearLayout) this.findViewById(R.id.llLocation);
		TextView tvLocation = (TextView) this.findViewById(R.id.tvLocation);
		TextView tvCreatedAt = (TextView) this.findViewById(R.id.tvCreatedAt);
		TextView tvSource = (TextView) this.findViewById(R.id.tvSource);
		TextView tvText = (TextView) this.findViewById(R.id.tvText);
		LinearLayout llThumbnail = (LinearLayout) this.findViewById(R.id.llThumbnail);
		ImageView ivThumbnail = (ImageView) this.findViewById(R.id.ivThumbnail);
		View llRetweet = this.findViewById(R.id.llRetweet);
		TextView tvRetweetText = (TextView) this.findViewById(R.id.tvRetweetText);
		LinearLayout llRetweetThumbnail = (LinearLayout) this.findViewById(R.id.llRetweetThumbnail);
		ImageView ivRetweetThumbnail = (ImageView) this.findViewById(R.id.ivRetweetThumbnail);
		LinearLayout llRetweetLocation = (LinearLayout) this.findViewById(R.id.llRetweetLocation);
		TextView tvRetweetLocation = (TextView) this.findViewById(R.id.tvRetweetLocation);
		TextView tvRetweetCreateAt = (TextView) this.findViewById(R.id.tvRetweetCreatedAt);
		TextView tvRetweetSource = (TextView) this.findViewById(R.id.tvRetweetSource);
		if (tvText instanceof RichTextView) {
        	((RichTextView)tvText).setProvider(status.getServiceProvider());
        }
        if (tvRetweetText instanceof RichTextView) {
        	((RichTextView)tvRetweetText).setProvider(status.getServiceProvider());
        }

        //初始化控件信息:
        ivVerify.setVisibility(View.GONE);
		llThumbnail.setVisibility(View.GONE);
		ivThumbnail.setVisibility(View.GONE);
		((ViewGroup)llThumbnail.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
		llRetweet.setVisibility(View.GONE);
		llRetweetThumbnail.setVisibility(View.GONE);
		ivRetweetThumbnail.setVisibility(View.GONE);
		((ViewGroup)llRetweetThumbnail.getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);

        llLocation.setVisibility(View.GONE);
        llRetweetLocation.setVisibility(View.GONE);

        User user = status.getUser();
        if (user == null) {
        	return;
        }
        //标题栏
		tvTitle.setText(user.getScreenName());
		profileClickListener.setUser(user);

		//用户信息profile
		ivProfilePicture.setImageDrawable(GlobalResource.getDefaultNormalHeader(this));
		String profileUrl = user.getProfileImageUrl();
		if (StringUtil.isNotEmpty(profileUrl)) {
			headTask = new ImageLoad4HeadTask(ivProfilePicture, profileUrl, false);
			headTask.execute();
		}

		tvScreenName.setText(user.getScreenName());
		if (isTencent) {
			if (StringUtil.isNotEmpty(user.getLocation())) {
				String impress = getString(R.string.label_blog_post_in);
				tvImpress.setText(String.format(impress, status.getUser().getLocation()));
			}
		} else {
			String impress = ResourceBook.getGenderValue(user.getGender(), this);
			if (StringUtil.isNotEmpty(user.getLocation())) {
				impress += ("," + user.getLocation());
			}
			tvImpress.setText(impress);
		}
		ivNext.setVisibility(View.VISIBLE);

		//微博信息
		statusContextMenuListener.setStatus(status);
		Spannable textSpan = EmotionLoader.getEmotionSpannable(
			status.getServiceProvider(), status.getText());
		tvText.setText(textSpan);

		Status retweet = status.getRetweetedStatus();
		String thumbnailPicture = status.getThumbnailPictureUrl();
		ImageView ivTargetThumbnail = ivThumbnail;
		LinearLayout llTargetThumbnail = llThumbnail;
		if (retweet != null) {
			thumbnailPicture = retweet.getThumbnailPictureUrl();
			ivTargetThumbnail = ivRetweetThumbnail;
			llTargetThumbnail = llRetweetThumbnail;
		}
		if (StringUtil.isNotEmpty(thumbnailPicture)) {
			llTargetThumbnail.setVisibility(View.VISIBLE);
			ivTargetThumbnail.setOnClickListener(new ImageClickListener(status));

			thumbnailTask = new ImageLoad4ThumbnailTask(ivTargetThumbnail, thumbnailPicture);
			thumbnailTask.execute(status);
		}

        if (user.isVerified()) {
        	ivVerify.setVisibility(View.VISIBLE);
        }

        if (retweet != null) {
        	llRetweet.setVisibility(View.VISIBLE);

			String retweetText = retweet.getText();
			User sourceUser = retweet.getUser();
			if (sourceUser != null) {
				retweetText = (sourceUser.getMentionTitleName() + ": " + retweetText) ;
			}

			Spannable retweetTextSpan = EmotionLoader.getEmotionSpannable(
				status.getServiceProvider(), retweetText);
			tvRetweetText.setText(retweetTextSpan);

			retweetResponseCountTask = new QueryRetweetResponseCountTask(this, retweet);
			retweetResponseCountTask.execute();

			Location location = retweet.getLocation();
			if (location != null) {
				llRetweetLocation.setVisibility(View.VISIBLE);
				tvRetweetLocation.setText("");
				if (StringUtil.isNotEmpty(location.getProvince())) {
					tvRetweetLocation.setText(location.getFormatedAddress());
				} else {
					locationTask = new QueryLocationTask(location, tvRetweetLocation, currentAccount);
					locationTask.execute();
				}
			}
			if (retweet.getSource() != null) {
				this.findViewById(R.id.llRetweetState).setVisibility(View.VISIBLE);
				String retweetCreatedAt = TimeSpanUtil.toTimeSpanString(retweet.getCreatedAt());
				tvRetweetCreateAt.setText(retweetCreatedAt);
				String retweetSource = getString(R.string.label_status_source, retweet.getSource());
				retweetSource = Html.fromHtml(retweetSource).toString();
				tvRetweetSource.setText(retweetSource);
			}
		}

        Location location = status.getLocation();
		if (location != null) {
			llLocation.setVisibility(View.VISIBLE);
			tvLocation.setText("");
			if (StringUtil.isNotEmpty(location.getProvince())) {
				tvLocation.setText(location.getFormatedAddress());
			} else {
				locationTask = new QueryLocationTask(location, tvLocation, currentAccount);
				locationTask.execute();
			}
		}
        if (status.getCreatedAt() == null) {
        	tvCreatedAt.setVisibility(View.GONE);
        } else {
			String createdAt = TimeSpanUtil.toTimeSpanString(status.getCreatedAt());
			tvCreatedAt.setText(createdAt);
        }
        if (status.getSource() == null) {
        	tvSource.setVisibility(View.GONE);
        } else {
			String source = getString(R.string.label_status_source, status.getSource());
			source = Html.fromHtml(source).toString();
			tvSource.setText(source);
        }
		responseCountTask = new QueryResponseCountTask(this, status);
		responseCountTask.execute();

		//评论列表
    	commentsAdapter = new CommentsOfStatusListAdapter(this, currentAccount, status);
        lvCommentsOfStatus.setAdapter(commentsAdapter);
        commentsContextMenuListener = new CommentsOfStatusContextMenuListener(commentsAdapter);
		lvCommentsOfStatus.setOnCreateContextMenuListener(commentsContextMenuListener);

        //工具栏
        commentClickListener.setStatus(status);
        retweetClickListener.setStatus(status);
        initFavButton(status);
        shareClickListener.setStatus(status);
        moreClickListener.setStatus(status);
	}

	public void initFavButton(Status status) {
		Button btnFavorite = (Button) this.findViewById(R.id.btnFavorite);
		if (status.isFavorited()) {
			btnFavorite.setBackgroundDrawable(theme.getDrawable("selector_toolbar_favorite_remove"));
			status.setFavorited(true);
		} else {
			btnFavorite.setBackgroundDrawable(theme.getDrawable("selector_toolbar_favorite_add"));
			status.setFavorited(false);
		}
		favoriteClickListener.setStatus(status);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("STATUS", status);
		outState.putInt("POSITION", position);
		outState.putInt("SOURCE_TYPE", sourceType);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case Constants.REQUEST_CODE_COMMENT_OF_STATUS:
			if (resultCode == Constants.RESULT_CODE_SUCCESS) {
				Comment comment = (Comment) data.getSerializableExtra("RESULT_COMMENT");
				List<Comment> listComment = new ArrayList<Comment>();
				listComment.add(comment);
				commentsAdapter.addCacheToFirst(listComment);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		destroyTasks();
	}

	private void destroyTasks() {
	    if (headTask != null) {
	    	headTask.cancel(true);
	    	headTask = null;
	    }
	    if (thumbnailTask != null) {
	    	thumbnailTask.cancel(true);
	    	thumbnailTask = null;
	    }
		if (responseCountTask != null) {
			responseCountTask.cancel(true);
			responseCountTask = null;
		}

		if (retweetResponseCountTask != null) {
			retweetResponseCountTask.cancel(true);
			retweetResponseCountTask = null;
		}

		if (locationTask != null) {
			locationTask.cancel(true);
			locationTask = null;
		}
	}

	public void showLoadingFooter() {
		if (listFooter != null) {
			lvCommentsOfStatus.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_loading, null);
		ThemeUtil.setListViewLoading(listFooter);
		lvCommentsOfStatus.addFooterView(listFooter);
	}

	public void showLoadCommentsFooter() {
		if (listFooter != null) {
			lvCommentsOfStatus.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		TextView tvFooter = (TextView) listFooter.findViewById(R.id.tvFooter);
		tvFooter.setText(R.string.label_blog_load_comments);
		listFooter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QueryCommentsOfStatusTask task = new QueryCommentsOfStatusTask(commentsAdapter);
				task.execute();
			}
		});
		lvCommentsOfStatus.setOnScrollListener(null);
		lvCommentsOfStatus.addFooterView(listFooter);
	}

	public void showMoreFooter() {
		if (listFooter != null) {
			lvCommentsOfStatus.removeFooterView(listFooter);
		}

		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		listFooter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QueryCommentsOfStatusTask task = new QueryCommentsOfStatusTask(commentsAdapter);
				task.execute();
			}
		});
		lvCommentsOfStatus.setOnScrollListener(autoLoadMoreListener);
		lvCommentsOfStatus.addFooterView(listFooter);
	}

	public void showNoMoreFooter() {
		if (listFooter != null) {
			lvCommentsOfStatus.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		TextView tvFooter = (TextView) listFooter.findViewById(R.id.tvFooter);
		tvFooter.setText(R.string.label_blog_no_more_comments);
		lvCommentsOfStatus.addFooterView(listFooter);
	}
}
