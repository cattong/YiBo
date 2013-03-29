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
import com.shejiaomao.weibo.service.adapter.SocialGraphListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GoHomeClickListener;
import com.shejiaomao.weibo.service.listener.SocialGraphItemClickListener;
import com.shejiaomao.weibo.service.listener.UserRecyclerListener;
import com.shejiaomao.weibo.service.task.SocialGraphTask;

public class SocialGraphActivity extends BaseActivity {
	private SheJiaoMaoApplication sheJiaoMao;
    private SocialGraphListAdapter adapter = null;

	private int socialGraphType = SocialGraphTask.TYPE_FOLLOWERS;
	private LocalAccount account;
	private User user;

	private ListView lvUser;
	private View listFooter;

	private UserRecyclerListener userRecyclerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_graph);

		sheJiaoMao = (SheJiaoMaoApplication) getApplication();
		initComponents();
		bindEvent();

		executeTask();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		lvUser = (ListView) findViewById(R.id.lvUser);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(lvUser);
		ThemeUtil.setListViewStyle(lvUser);
		
		Intent intent = this.getIntent();
		socialGraphType = intent.getIntExtra("SOCIAL_GRAPH_TYPE", socialGraphType);
		user = (User)intent.getSerializableExtra("USER");
        if (user == null) {
        	return;
        }
		account = (LocalAccount)intent.getSerializableExtra("ACCOUNT");
        if (account == null) {
        	account = sheJiaoMao.getCurrentAccount();
        }
        
		adapter = new SocialGraphListAdapter(this, account, socialGraphType);
		
		
		lvUser.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		showLoadingFooter();
		lvUser.setAdapter(adapter);

		userRecyclerListener = new UserRecyclerListener();
		lvUser.setRecyclerListener(userRecyclerListener);
		setBack2Top(lvUser);
		
		if (socialGraphType == SocialGraphTask.TYPE_FOLLOWERS) {
			tvTitle.setText(R.string.title_followers);
		} else if (socialGraphType == SocialGraphTask.TYPE_FRIENDS) {
			tvTitle.setText(R.string.title_friends);
		} else if (socialGraphType == SocialGraphTask.TYPE_BLOCKS) {
			tvTitle.setText(R.string.title_blocks);
		}
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());

		ListView lvUser = (ListView)this.findViewById(R.id.lvUser);
		lvUser.setOnItemClickListener(new SocialGraphItemClickListener(this));
	}

	private void executeTask() {
		new SocialGraphTask(adapter, user).execute();
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
		
		TextView tvFooter = (TextView)listFooter.findViewById(R.id.tvFooter);		
		tvFooter.setText(R.string.label_no_more);
		lvUser.addFooterView(listFooter);
	}
}
