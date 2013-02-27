package net.dev123.yibo;

import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.adapter.StatusSubscribeListAdapter;
import net.dev123.yibo.service.listener.GoBackClickListener;
import net.dev123.yibo.service.listener.GoHomeClickListener;
import net.dev123.yibo.service.listener.MicroBlogContextMenuListener;
import net.dev123.yibo.service.listener.MicroBlogItemClickListener;
import net.dev123.yibo.service.listener.StatusRecyclerListener;
import net.dev123.yibo.service.task.StatusSubscribeTask;
import net.dev123.yibome.entity.SubscribeCatalog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StatusSubscribeActivity extends BaseActivity {
	private YiBoApplication yibo;
	private SubscribeCatalog catalog;
	private StatusSubscribeListAdapter adapter = null;
	
	private ListView lvMicroBlog  = null;
	private View listFooter = null;

	private StatusRecyclerListener recyclerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_subscribe);

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
		
		int temp = getIntent().getIntExtra("SUBSCRIBE_CATALOG", 
			SubscribeCatalog.DAILY_NEWS.getSubscribeCatalogNo());
		catalog = SubscribeCatalog.getSubscribeCatalog(temp);
		if (catalog == null) {
			return;
		}
        
		int titleId = getIntent().getIntExtra("TITLE_ID", R.string.label_app_daily);
		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		tvTitle.setText(titleId);

		adapter = new StatusSubscribeListAdapter(this, yibo.getCurrentAccount());
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

		ListView lvMicroBlog = (ListView)this.findViewById(R.id.lvMicroBlog);
		lvMicroBlog.setOnItemClickListener(new MicroBlogItemClickListener(this));
		MicroBlogContextMenuListener contextMenuListener =
			new MicroBlogContextMenuListener(lvMicroBlog);
		lvMicroBlog.setOnCreateContextMenuListener(contextMenuListener);
	}

	private void executeTask() {
		new StatusSubscribeTask(this, adapter).execute();
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

	public SubscribeCatalog getCatalog() {
		return catalog;
	}

}
