package com.shejiaomao.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cattong.entity.StatusCatalog;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.adapter.HotStatusesListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GoHomeClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogContextMenuListener;
import com.shejiaomao.weibo.service.listener.MicroBlogItemClickListener;
import com.shejiaomao.weibo.service.listener.StatusRecyclerListener;
import com.shejiaomao.weibo.service.listener.StatusScrollListener;
import com.shejiaomao.weibo.service.task.HotStatusesTask;

public class HotStatusesActivity extends BaseActivity {
	
	private SheJiaoMaoApplication sheJiaoMao;
	private HotStatusesListAdapter adapter;

	private ListView lvMicroBlog;
	private View listFooter;

	private int type;
	
	private StatusRecyclerListener recyclerListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mystatuses);

		sheJiaoMao = (SheJiaoMaoApplication)getApplication();

		initComponents();
		bindEvent();

		executeTask();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		lvMicroBlog = (ListView)this.findViewById(R.id.lvMicroBlog);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(lvMicroBlog);
		ThemeUtil.setListViewStyle(lvMicroBlog);
		
		Intent intent = this.getIntent();
		type = intent.getIntExtra("STATUS_CATALOG", StatusCatalog.Hot_Retweet.getCatalogNo());

		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		int titleId = R.string.title_hot_retweets;
		if (type == StatusCatalog.Hot_Comment.getCatalogNo()) {
			titleId = R.string.title_hot_comments;
		}
		tvTitle.setText(titleId);

		adapter = new HotStatusesListAdapter(this, sheJiaoMao.getCurrentAccount());
		showMoreFooter();
		lvMicroBlog.setAdapter(adapter);
		lvMicroBlog.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		lvMicroBlog.setOnScrollListener(new StatusScrollListener());
        setBack2Top(lvMicroBlog);
        
		recyclerListener = new StatusRecyclerListener();
		lvMicroBlog.setRecyclerListener(recyclerListener);
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());
		
		ListView  lvMicroBlog = (ListView)this.findViewById(R.id.lvMicroBlog);
		lvMicroBlog.setOnItemClickListener(new MicroBlogItemClickListener(this));
		MicroBlogContextMenuListener contextMenuListener =
			new MicroBlogContextMenuListener(lvMicroBlog);
		lvMicroBlog.setOnCreateContextMenuListener(contextMenuListener);
	}

	private void executeTask() {
		new HotStatusesTask(this, adapter, type).execute();
	}

	public void showLoadingFooter() {
		if (listFooter != null) {
			lvMicroBlog.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_loading, null);
		ThemeUtil.setListViewLoading(listFooter);
		lvMicroBlog.addFooterView(listFooter);
	}

	public void showMoreFooter() {
		if (listFooter != null) {
			lvMicroBlog.removeFooterView(listFooter);
		}

		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		listFooter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				executeTask();
			}
		});
		lvMicroBlog.addFooterView(listFooter);
	}

	public void showNoMoreFooter() {
		if (listFooter != null) {
			lvMicroBlog.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
        ThemeUtil.setListViewMore(listFooter);
        
		TextView tvFooter = (TextView) listFooter.findViewById(R.id.tvFooter);
		tvFooter.setText(R.string.label_no_more);
		lvMicroBlog.addFooterView(listFooter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		adapter.clear();
	}
}
