package net.dev123.yibo;

import net.dev123.commons.Paging;
import net.dev123.mblog.entity.Trend;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.adapter.TopicListAdapter;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.listener.GoHomeClickListener;
import net.dev123.yibo.service.task.UserTopicTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserTopicsActivity extends BaseActivity {
	private YiBoApplication yibo;
	
	private ListView lvUserTopics;
	private TopicListAdapter topicsAdapter;
	private View loadingView;
	private Paging<Trend> paging;
	private View footerList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_topics);
		
		yibo = (YiBoApplication)getApplication();
		
		initComponents();
		bindEvent();
	}
	
	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		lvUserTopics = (ListView) findViewById(R.id.lvUserTopics);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(lvUserTopics);
		ThemeUtil.setListViewStyle(lvUserTopics);
		
		lvUserTopics.setFastScrollEnabled(yibo.isSliderEnabled());
		
		showLoadingView();
		topicsAdapter = new TopicListAdapter(this, yibo.getCurrentAccount());
		lvUserTopics.setAdapter(topicsAdapter);
		setBack2Top(lvUserTopics);
		lvUserTopics.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String trendName = (String)topicsAdapter.getItem(position);
					Intent intent = new Intent();
					intent.putExtra("trendName", trendName);
					intent.setClass(UserTopicsActivity.this, UserTopicStatusesActivity.class);
					UserTopicsActivity.this.startActivity(intent);
				}
			}
		);

		TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.label_personal_topics);
		paging = new Paging<Trend>();
		executeTask();
	}
	
	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());
		
		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());
	}
	
	public Paging<Trend> getPaging() {
		return paging;
	}
	
	private void executeTask() {
		new UserTopicTask(this).execute();
	}
	
	public void showLoadingView() {
		removeFooterView();
		if (loadingView == null) {
			loadingView = getLayoutInflater().inflate(R.layout.list_item_loading, null);
			ThemeUtil.setListViewLoading(loadingView);
		}
		loadingView.setOnClickListener(null);
		lvUserTopics.addFooterView(loadingView);
	}
	
	public void removeFooterView() {
		if (footerList != null) {
			lvUserTopics.removeFooterView(footerList);
		}
		if(loadingView != null) {
			lvUserTopics.removeFooterView(loadingView);
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
		lvUserTopics.addFooterView(footerList);
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
		lvUserTopics.addFooterView(footerList);
	}

	public TopicListAdapter getTopicsAdapter() {
		return topicsAdapter;
	}
}
