package net.dev123.yibo;

import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.adapter.TopicListAdapter;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.listener.GoHomeClickListener;
import net.dev123.yibo.service.task.QueryHotTopicTask;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HotTopicsActivity extends BaseActivity {
	private YiBoApplication yibo;
	private ListView lvTopics;
	private TopicListAdapter topicsAdapter;
	private View loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_topics);
		
		yibo = (YiBoApplication)getApplication();
		
		initCompoments();
		bindEvent();
	}

	private void initCompoments() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		lvTopics = (ListView)findViewById(R.id.lvTopics);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(lvTopics);
		ThemeUtil.setListViewStyle(lvTopics);
		
		loadingView = getLayoutInflater().inflate(R.layout.list_item_loading, null);
		ThemeUtil.setListViewLoading(loadingView);		
		lvTopics.addFooterView(loadingView);
		
		
		topicsAdapter = new TopicListAdapter(this, yibo.getCurrentAccount());
		lvTopics.setAdapter(topicsAdapter);
		lvTopics.setFastScrollEnabled(yibo.isSliderEnabled());
		setBack2Top(lvTopics);
		lvTopics.setOnItemClickListener(
			new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String topic = (String)topicsAdapter.getItem(position);
					Intent intent = new Intent();
					String uri = Constants.URI_TOPIC.toString() + "#" + topic + "#";
					intent.setData(Uri.parse(uri));
					intent.setClass(HotTopicsActivity.this, SearchActivity.class);
					HotTopicsActivity.this.startActivity(intent);
				}
			}
		);
		
		TextView tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_hot_topics);
		
		new QueryHotTopicTask(this).execute();
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());
        
		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());
	}
	
	public ListView getLvTopics() {
		return lvTopics;
	}

	public void hideLoadingView() {
		if (lvTopics.getFooterViewsCount() > 0
			&& loadingView != null) {
			lvTopics.removeFooterView(loadingView);
		}
	}

	public TopicListAdapter getTopicsAdapter() {
		return topicsAdapter;
	}
}
