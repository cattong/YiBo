package com.shejiaomao.weibo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.adapter.PublicTimelineListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogContextMenuListener;
import com.shejiaomao.weibo.service.listener.MicroBlogItemClickListener;
import com.shejiaomao.weibo.service.listener.StatusRecyclerListener;
import com.shejiaomao.weibo.service.listener.StatusScrollListener;
import com.shejiaomao.weibo.service.task.PublicTimelineTask;

public class PublicTimelineActivity extends BaseActivity {
	private PublicTimelineListAdapter adapter = null;
	private SheJiaoMaoApplication sheJiaoMao;

	private ListView lvMicroBlog  = null;
	private View listFooter = null;

	private StatusRecyclerListener recyclerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_timeline);

		sheJiaoMao = (SheJiaoMaoApplication) getApplication();
		adapter = new PublicTimelineListAdapter(this, sheJiaoMao.getCurrentAccount());
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
		
        TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_public_timeline);

		showMoreFooter();
		lvMicroBlog.setAdapter(adapter);
		lvMicroBlog.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		lvMicroBlog.setOnScrollListener(new StatusScrollListener());
        setBack2Top(lvMicroBlog);
        
		recyclerListener = new StatusRecyclerListener();
		lvMicroBlog.setRecyclerListener(recyclerListener);
	}

	private void bindEvent() {
		MicroBlogItemClickListener itemClickListener = new MicroBlogItemClickListener(this);
		lvMicroBlog.setOnItemClickListener(itemClickListener);

		MicroBlogContextMenuListener contextMenuListener =
			new MicroBlogContextMenuListener(lvMicroBlog);
		lvMicroBlog.setOnCreateContextMenuListener(contextMenuListener);

		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

	}

	private void executeTask() {
		new PublicTimelineTask(this, adapter, sheJiaoMao.getCurrentAccount()).execute();
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
	
}
