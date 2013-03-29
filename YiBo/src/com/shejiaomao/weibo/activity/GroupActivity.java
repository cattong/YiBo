package com.shejiaomao.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cattong.entity.User;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.GroupListAdapter;
import com.shejiaomao.weibo.service.adapter.SocialGraphListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GroupTabChangeListener;
import com.shejiaomao.weibo.service.listener.UserRecyclerListener;
import com.shejiaomao.weibo.service.task.GroupTask;
import com.shejiaomao.weibo.service.task.SocialGraphTask;
import com.shejiaomao.widget.TabButton;

public class GroupActivity extends BaseActivity {
	public static final int TAB_TYPE_ALL = 0;
	public static final int TAB_TYPE_GROUP = 1;

	private SheJiaoMaoApplication sheJiaoMao;
	private LocalAccount currentAccount;
    private SocialGraphListAdapter sgAdapter;
    private GroupListAdapter groupAdapter;

    private int tabType;
	private int socialGraphType = SocialGraphTask.TYPE_FRIENDS;
	private User user;

	private Button btnTabLeft;
	private Button btnTabRight;

	private ListView lvUser;
	private View listFooter;

	private UserRecyclerListener userRecyclerListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);

		sheJiaoMao = (SheJiaoMaoApplication) getApplication();
		currentAccount = sheJiaoMao.getCurrentAccount();
		initComponents();
		bindEvent();

		//executeTask();
	}

	private void initComponents() {
		LinearLayout llRoot = (LinearLayout)this.findViewById(R.id.llRoot);
		LinearLayout llHeaderBase = (LinearLayout)this.findViewById(R.id.llHeaderBase);
		LinearLayout llTabHeader = (LinearLayout)this.findViewById(R.id.llTabHeader);
		lvUser = (ListView)this.findViewById(R.id.lvUser);
		ThemeUtil.setRootBackground(llRoot);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setHeaderToggleTab(llTabHeader);
		ThemeUtil.setListViewStyle(lvUser);
		
		Intent intent = this.getIntent();
		user = (User)intent.getSerializableExtra("USER");
        if (user == null) {
        	return;
        }
        tabType = intent.getIntExtra("TAB_TYPE", TAB_TYPE_ALL);

		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		String title = this.getString(R.string.title_group, user.getFriendsCount());
		tvTitle.setText(title);

		btnTabLeft = (Button) this.findViewById(R.id.btnTabLeft);
		btnTabLeft.setText(R.string.label_tab_all);
		btnTabRight = (Button) this.findViewById(R.id.btnTabRight);
		btnTabRight.setText(R.string.label_tab_group);

		lvUser.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		userRecyclerListener = new UserRecyclerListener();
		lvUser.setRecyclerListener(userRecyclerListener);
        setBack2Top(lvUser);
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		btnTabLeft = (Button) this.findViewById(R.id.btnTabLeft);
		btnTabRight = (Button) this.findViewById(R.id.btnTabRight);
		TabButton tabButton = new TabButton();
		tabButton.addButton(btnTabLeft);
		tabButton.addButton(btnTabRight);

		GroupTabChangeListener tabChangeListener = new GroupTabChangeListener(this);
		tabButton.setOnTabChangeListener(tabChangeListener);

		if (tabType == TAB_TYPE_ALL) {
			tabButton.toggleButton(btnTabLeft);
		} else if (tabType == TAB_TYPE_GROUP) {
			tabButton.toggleButton(btnTabRight);
		}
	}

	private void executeTask() {
		if (!btnTabLeft.isEnabled()) {
		    new SocialGraphTask(sgAdapter, user).execute();
		} else {
			new GroupTask(groupAdapter).execute();
		}
	}

	public void showLoadingFooter() {
		if (listFooter != null) {
			lvUser.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_loading, null);
		ThemeUtil.setListViewLoading(listFooter);
		lvUser.addFooterView(listFooter);
	}

	public void showMoreFooter() {
		if (listFooter != null) {
			lvUser.removeFooterView(listFooter);
		}

		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		listFooter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				executeTask();
			}
		});
		lvUser.addFooterView(listFooter);
	}

	public void showNoMoreFooter() {
		if (listFooter != null) {
			lvUser.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		TextView tvFooter = (TextView) listFooter.findViewById(R.id.tvFooter);
		tvFooter.setText(R.string.label_no_more);
		lvUser.addFooterView(listFooter);
	}

	public SocialGraphListAdapter getSgAdapter() {
		return sgAdapter;
	}

	public void setSgAdapter(SocialGraphListAdapter sgAdapter) {
		this.sgAdapter = sgAdapter;
	}

	public GroupListAdapter getGroupAdapter() {
		return groupAdapter;
	}

	public void setGroupAdapter(GroupListAdapter groupAdapter) {
		this.groupAdapter = groupAdapter;
	}

	public int getSocialGraphType() {
		return socialGraphType;
	}

	public void setSocialGraphType(int socialGraphType) {
		this.socialGraphType = socialGraphType;
	}

	public LocalAccount getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(LocalAccount currentAccount) {
		this.currentAccount = currentAccount;
	}

	public User getUser() {
		return user;
	}
}
