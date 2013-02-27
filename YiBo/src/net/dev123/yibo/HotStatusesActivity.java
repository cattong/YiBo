package net.dev123.yibo;

import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.adapter.HotStatusesListAdapter;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.listener.GoHomeClickListener;
import net.dev123.yibo.service.listener.MicroBlogContextMenuListener;
import net.dev123.yibo.service.listener.MicroBlogItemClickListener;
import net.dev123.yibo.service.listener.StatusRecyclerListener;
import net.dev123.yibo.service.task.HotStatusesTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HotStatusesActivity extends BaseActivity {
	public static final int HOT_RETWEET = 0;
	public static final int HOT_COMMENT = 1;
	
	private YiBoApplication yibo;
	private HotStatusesListAdapter adapter;

	private ListView lvMicroBlog;
	private View listFooter;

	private int type;
	
	private StatusRecyclerListener recyclerListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		Intent intent = this.getIntent();
		type = intent.getIntExtra("TYPE", HOT_RETWEET);

		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		int titleId = R.string.title_hot_retweets;
		if (type == HOT_COMMENT) {
			titleId = R.string.title_hot_comments;
		}
		tvTitle.setText(titleId);

		adapter = new HotStatusesListAdapter(this, yibo.getCurrentAccount());
		showMoreFooter();
		lvMicroBlog.setAdapter(adapter);
		lvMicroBlog.setFastScrollEnabled(yibo.isSliderEnabled());
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
