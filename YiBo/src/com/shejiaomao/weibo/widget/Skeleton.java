package com.shejiaomao.weibo.widget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.AppChangeListener;
import com.shejiaomao.weibo.service.listener.HomePageAccountClickListener;
import com.shejiaomao.weibo.service.listener.HomePageAccountLongClickListener;
import com.shejiaomao.weibo.service.listener.HomePageGroupClickListener;
import com.shejiaomao.weibo.service.listener.HomePageHeaderDoubleClickListener;
import com.shejiaomao.weibo.service.listener.MessagesChangeListener;
import com.shejiaomao.weibo.service.listener.MoreChangeListener;
import com.shejiaomao.weibo.service.listener.MyHomeChangeListener;
import com.shejiaomao.weibo.service.listener.ProfileChangeListener;
import com.shejiaomao.weibo.service.listener.ProfileRefreshClickListener;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.widget.ValueSetEvent.Action;
import com.shejiaomao.widget.PullToRefreshListView;

public class Skeleton extends LinearLayout {
    public static final int TYPE_MY_HOME  = 1;
    public static final int TYPE_MESSAGE  = 2;
    public static final int TYPE_PROFILE  = 3;
    public static final int TYPE_APP      = 4;
    public static final int TYPE_MORE     = 5;
    public static final int TYPE_MENTION  = 21;
    public static final int TYPE_COMMENT  = 22;
    public static final int TYPE_DIRECT_MESSAGE = 23;

	private int contentType;
	private LocalAccount currentAccount = null;

	protected PropertyChangeSupport propertySupport;

	private Drawable myHomeDrawable;
	private Drawable myHomeRefreshDrawable;
	private Drawable messageDrawable;
	private Drawable messageRefreshDrawable;
	private Drawable profileDrawable;
	private Drawable profileRefreshDrawable;

	private Button btnMyHome;
	private Button btnMessage;
	private Button btnProfile;
	private Button btnApp;
	private Button btnMore;
	private LinearLayout llFooter;

	private ImageButton ibProfileImage;
	private HomePageAccountClickListener accountClickListener;
	private HomePageGroupClickListener groupClickListener;
	private ProfileRefreshClickListener profileRefreshListener;
	public Skeleton(Context context) {
		super(context);
		initLayout();
		initEvent();
		initPropertyChangeListener(context);
	}

	public Skeleton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout();
		initEvent();
		initPropertyChangeListener(context);
	}

	private void initLayout() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_page, this, true);
        ThemeUtil.setRootBackground(this);
        propertySupport = new PropertyChangeSupport(this);

        ibProfileImage = (ImageButton) this.findViewById(R.id.ibProfileImage);
        TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
        ImageButton ibGroup = (ImageButton) this.findViewById(R.id.ibGroup);
        ImageButton ibEdit = (ImageButton) this.findViewById(R.id.ibEdit);
        
        Button btnMention = (Button) this.findViewById(R.id.btnMention);
        Button btnComment = (Button) this.findViewById(R.id.btnComment);
        Button btnDirectMessage = (Button) this.findViewById(R.id.btnDirectMessage);

        llFooter = (LinearLayout) this.findViewById(R.id.llFooter);
		btnMyHome = (Button) this.findViewById(R.id.btnMyHome);
		btnMessage = (Button) this.findViewById(R.id.btnMessage);
		btnProfile = (Button) this.findViewById(R.id.btnProfile);
		btnApp = (Button) this.findViewById(R.id.btnApp);
		btnMore = (Button) this.findViewById(R.id.btnMore);
		
		//主题
		Theme theme = ThemeUtil.createTheme(getContext());
		LinearLayout llHeader = (LinearLayout)this.findViewById(R.id.llHeader);
		llHeader.setBackgroundDrawable(theme.getDrawable("bg_header"));
		llHeader.setGravity(Gravity.CENTER_VERTICAL);
		ibProfileImage.setBackgroundDrawable(theme.getDrawable("bg_account_display"));
		ibGroup.setImageDrawable(theme.getDrawable("selector_btn_group"));
		Drawable bgHeaderDivider = theme.getDrawable("bg_header_divider");
		ibGroup.setBackgroundDrawable(bgHeaderDivider);
		ibEdit.setBackgroundDrawable(theme.getDrawable("selector_btn_status"));
		ibEdit.setImageDrawable(bgHeaderDivider);
		tvTitle.setTextColor(theme.getColorStateList("selector_header_title"));
		
		btnMention.setBackgroundDrawable(theme.getDrawable("selector_tab_mention"));
		btnMention.setTextColor(theme.getColorStateList("selector_header_title"));
		btnComment.setBackgroundDrawable(theme.getDrawable("selector_tab_comment"));
		btnComment.setTextColor(theme.getColorStateList("selector_header_title"));
		btnDirectMessage.setBackgroundDrawable(theme.getDrawable("selector_tab_direct_message"));
		btnDirectMessage.setTextColor(theme.getColorStateList("selector_header_title"));
		
        myHomeDrawable = theme.getDrawable("selector_tab_home");
        myHomeRefreshDrawable = theme.getDrawable("selector_tab_home_refresh");
        messageDrawable = theme.getDrawable("selector_tab_message");
        messageRefreshDrawable = theme.getDrawable("selector_tab_message_refresh");
        profileDrawable = theme.getDrawable("selector_tab_profile");
        profileRefreshDrawable = theme.getDrawable("selector_tab_profile_refresh");

        llFooter.setBackgroundDrawable(theme.getDrawable("bg_footer"));
        llFooter.setPadding(0, 0, 0, 0);
		btnMyHome.setBackgroundDrawable(myHomeDrawable);
		btnMessage.setBackgroundDrawable(messageDrawable);
		btnProfile.setBackgroundDrawable(profileDrawable);
		btnApp.setBackgroundDrawable(theme.getDrawable("selector_tab_app"));
		btnMore.setBackgroundDrawable(theme.getDrawable("selector_tab_more"));
	}

	private void initEvent() {
		btnMyHome.setEnabled(false);

		btnMyHome.setOnClickListener(buttonListener);
		btnMessage.setOnClickListener(buttonListener);
		btnProfile.setOnClickListener(buttonListener);
		btnApp.setOnClickListener(buttonListener);
		btnMore.setOnClickListener(buttonListener);

		ImageButton ibGroup = (ImageButton)this.findViewById(R.id.ibGroup);
		groupClickListener = new HomePageGroupClickListener(this);
		ibGroup.setOnClickListener(groupClickListener);

		//双击头部快速回到第一行;
		ViewGroup llHeader = (ViewGroup)this.findViewById(R.id.llHeader);
		llHeader.setOnTouchListener(new HomePageHeaderDoubleClickListener());
	}

	public void initPropertyChangeListener(Context context) {
		MyHomeChangeListener myHomeChangeListner = new MyHomeChangeListener(context);
		MessagesChangeListener messagesChangeListener = new MessagesChangeListener(context);
		ProfileChangeListener profileChangeListener = new ProfileChangeListener(context);
		AppChangeListener appChangeListener = new AppChangeListener(context);
		MoreChangeListener moreChangeListener = new MoreChangeListener(context);

		addPropertyChangeListener(myHomeChangeListner);
		addPropertyChangeListener(messagesChangeListener);
		addPropertyChangeListener(profileChangeListener);
		addPropertyChangeListener(appChangeListener);
		addPropertyChangeListener(moreChangeListener);

		profileRefreshListener = new ProfileRefreshClickListener(profileChangeListener);
	}

	private void initAccountSelectorWindow() {
		View headerView = this.findViewById(R.id.llHeader);
		accountClickListener = new HomePageAccountClickListener(this.getContext(), headerView);
		ibProfileImage.setOnClickListener(accountClickListener);
		ibProfileImage.setOnLongClickListener(new HomePageAccountLongClickListener());
	}
	
	public void addContent(View contentView) {
		((ViewGroup)this.findViewById(R.id.llContainer)).addView(contentView);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
	    propertySupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    propertySupport.removePropertyChangeListener(listener);
	}

	public void setContentType(int newContentType) {
		int oldContentType = contentType;
		if (newContentType == oldContentType) {
			oldContentType = 0;
		}

		btnMyHome.setEnabled(true);
		btnMessage.setEnabled(true);
		btnProfile.setEnabled(true);
		btnApp.setEnabled(true);
		btnMore.setEnabled(true);

		btnMyHome.setOnClickListener(buttonListener);
		btnMessage.setOnClickListener(buttonListener);
		btnProfile.setOnClickListener(buttonListener);
		switch (newContentType) {
		case Skeleton.TYPE_MY_HOME:
			//btnMyHome.setEnabled(false);
			btnMyHome.setBackgroundDrawable(myHomeRefreshDrawable);
			btnMessage.setBackgroundDrawable(messageDrawable);
			btnProfile.setBackgroundDrawable(profileDrawable);
			btnMyHome.setOnClickListener(goToTopAndRefreshListener);
			break;
		case Skeleton.TYPE_MESSAGE:
		case Skeleton.TYPE_MENTION:
		case Skeleton.TYPE_COMMENT:
		case Skeleton.TYPE_DIRECT_MESSAGE:
			//btnMessage.setEnabled(false);
			btnMyHome.setBackgroundDrawable(myHomeDrawable);
			btnMessage.setBackgroundDrawable(messageRefreshDrawable);
			btnProfile.setBackgroundDrawable(profileDrawable);
			btnMessage.setOnClickListener(goToTopAndRefreshListener);
			break;
		case Skeleton.TYPE_PROFILE:
			//btnProfile.setEnabled(false);
			btnMyHome.setBackgroundDrawable(myHomeDrawable);
			btnMessage.setBackgroundDrawable(messageDrawable);
			btnProfile.setBackgroundDrawable(profileRefreshDrawable);
			btnProfile.setOnClickListener(profileRefreshListener);
			break;
		case Skeleton.TYPE_APP:
			btnMyHome.setBackgroundDrawable(myHomeDrawable);
			btnMessage.setBackgroundDrawable(messageDrawable);
			btnProfile.setBackgroundDrawable(profileDrawable);
			btnApp.setEnabled(false);
			break;
		case Skeleton.TYPE_MORE:
			btnMyHome.setBackgroundDrawable(myHomeDrawable);
			btnMessage.setBackgroundDrawable(messageDrawable);
			btnProfile.setBackgroundDrawable(profileDrawable);
			btnMore.setEnabled(false);
			break;
		}

		contentType = newContentType;
		View container = this.findViewById(R.id.llContainer);
		PropertyChangeEvent event = new ViewChangeEvent(
			this,           "contentType", oldContentType,
			newContentType, container,     currentAccount
		);
		propertySupport.firePropertyChange(event);
	}

	public LocalAccount getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(LocalAccount currentAccount) {
		this.currentAccount = currentAccount;
		if (accountClickListener == null) {
			initAccountSelectorWindow();
		}
		accountClickListener.setSelectedAccount(currentAccount);
	}

	public void setCurrentAccount(LocalAccount currentAccount, boolean isSwitchAccount) {
		if (currentAccount == null) {
			return;
		}
		setCurrentAccount(currentAccount);

        if (isSwitchAccount) {
        	String profileImageUrl = currentAccount.getUser().getProfileImageUrl();
        	ImageLoad4HeadTask task = new ImageLoad4HeadTask(ibProfileImage, profileImageUrl, true);
        	task.execute();
        	//setContentType(Skeleton.TYPE_MY_HOME);
			ValueSetEvent event = new ValueSetEvent(
				this,  "contentType",  0,
				Skeleton.TYPE_MY_HOME, currentAccount
			);
			event.setAction(Action.ACTION_INIT_ADAPTER);

			propertySupport.firePropertyChange(event);
        }
	}

	public void reclaim() {
		ValueSetEvent event = new ValueSetEvent(
			this,  "contentType",  0,
			Skeleton.TYPE_MY_HOME, currentAccount);
		event.setAction(Action.ACTION_RECLAIM_MEMORY);

		propertySupport.firePropertyChange(event);
	}

	public void removeAccount(LocalAccount account) {
		GlobalVars.removeAccount(account);
	}

	public int getContentType() {
		return contentType;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (accountClickListener == null) {
			initAccountSelectorWindow();
		}
	}

	View.OnClickListener buttonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnMyHome:
				setContentType(Skeleton.TYPE_MY_HOME);
				break;
			case R.id.btnMessage:
				setContentType(Skeleton.TYPE_MESSAGE);
				break;
			case R.id.btnProfile:
				setContentType(Skeleton.TYPE_PROFILE);
				break;
			case R.id.btnApp:
				setContentType(Skeleton.TYPE_APP);
				break;
			case R.id.btnMore:
				setContentType(Skeleton.TYPE_MORE);
				break;
			}

		}
	};

	View.OnClickListener goToTopAndRefreshListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            Activity activity = (Activity)v.getContext();
            ListView lvMicroBlog = (ListView)activity.findViewById(R.id.lvMicroBlog);
            if (lvMicroBlog != null && lvMicroBlog instanceof PullToRefreshListView) {
            	PullToRefreshListView prListView = (PullToRefreshListView)lvMicroBlog;
            	prListView.setSelection(0);
            	prListView.prepareForRefresh();
            	prListView.onRefresh();
            }
		}
	};
}
