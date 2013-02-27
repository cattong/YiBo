package net.dev123.yibo;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.adapter.UserTopicStatusListAdapter;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.listener.GoHomeClickListener;
import net.dev123.yibo.service.listener.MicroBlogContextMenuListener;
import net.dev123.yibo.service.listener.MicroBlogItemClickListener;
import net.dev123.yibo.service.listener.StatusRecyclerListener;
import net.dev123.yibo.service.task.UserTopicStatusTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserTopicStatusesActivity extends BaseActivity {

	private YiBoApplication yibo;
	private UserTopicStatusListAdapter adapter;

	private ListView lvMicroBlog;
	private View footerList;
	private View loadingView;
	private StatusRecyclerListener recyclerListener;
	private String trendName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		trendName = intent.getStringExtra("trendName");
		if (StringUtil.isEmpty(trendName)) {
			this.finish();
		}
		setContentView(R.layout.mystatuses);

		yibo = (YiBoApplication)getApplication();

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
		
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(this.getString(R.string.title_topic_statues, trendName));
		
		adapter = new UserTopicStatusListAdapter(this, yibo.getCurrentAccount(), trendName);
		showLoadingView();
		lvMicroBlog.setAdapter(adapter);
		lvMicroBlog.setFastScrollEnabled(yibo.isSliderEnabled());
		setBack2Top(lvMicroBlog);
		
		recyclerListener = new StatusRecyclerListener();
		lvMicroBlog.setRecyclerListener(recyclerListener);
	}
	
	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());
		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());
		
		lvMicroBlog.setOnItemClickListener(new MicroBlogItemClickListener(this));
		lvMicroBlog.setOnCreateContextMenuListener(
				new MicroBlogContextMenuListener(lvMicroBlog));
	}
	
	private void executeTask() {
		new UserTopicStatusTask(this, adapter, trendName).execute();
	}
	
	public void showLoadingView() {
		removeFooterView();
		if(loadingView == null) {
			loadingView = getLayoutInflater().inflate(R.layout.list_item_loading, null);
			ThemeUtil.setListViewLoading(loadingView);
		}
		
		loadingView.setOnClickListener(null);
		lvMicroBlog.addFooterView(loadingView);
	}
	
	public void removeFooterView() {
		if (footerList != null) {
			lvMicroBlog.removeFooterView(footerList);
		}
		if(loadingView != null) {
		    lvMicroBlog.removeFooterView(loadingView);
		}
	}
	
	public void showMoreFooter() {
		removeFooterView();
		if (footerList == null) {
		    footerList = getLayoutInflater().inflate(R.layout.list_item_more, null);
		    ThemeUtil.setListViewMore(footerList);
		}
		footerList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadingView();
				executeTask();
			}
		});
		lvMicroBlog.addFooterView(footerList);
	}
	
	public void showNoMoreFooter() {
		removeFooterView();
		if (footerList == null) {
		    footerList = getLayoutInflater().inflate(R.layout.list_item_more, null);
		    ThemeUtil.setListViewMore(footerList);
		}
		TextView tvFooter = (TextView) footerList.findViewById(R.id.tvFooter);
		tvFooter.setText(R.string.label_no_more);
		footerList.setOnClickListener(null);
		lvMicroBlog.addFooterView(footerList);
	}
}
