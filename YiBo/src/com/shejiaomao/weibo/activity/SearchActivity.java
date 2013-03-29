package com.shejiaomao.weibo.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.Paging;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.adapter.StatusSearchResultAdapter;
import com.shejiaomao.weibo.service.adapter.UserSearchResultAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GoHomeClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogContextMenuListener;
import com.shejiaomao.weibo.service.listener.MicroBlogItemClickListener;
import com.shejiaomao.weibo.service.listener.SearchTextWatch;
import com.shejiaomao.weibo.service.listener.SocialGraphItemClickListener;
import com.shejiaomao.weibo.service.listener.StatusRecyclerListener;
import com.shejiaomao.weibo.service.listener.StatusScrollListener;
import com.shejiaomao.weibo.service.listener.UserRecyclerListener;
import com.shejiaomao.weibo.service.task.SearchTask;
import com.shejiaomao.widget.TabButton;

public class SearchActivity extends BaseActivity {
	public static final String SEARCH_SUGGEST_ACTION = "shejiaomao.search.suggest.VIEW";

	private ArrayAdapter<?> resultAdapter = null;
	private ListView lvSearchResult = null;
	private View listFooter = null;
	private SheJiaoMaoApplication sheJiaoMao;

	private TabButton tabButton;
	private Button btnSearchStatus;
	private Button btnSearchUser;

	private String keyword;
	private Paging<?> paging;
	private boolean isExecuteQuery;
    private int searchCatalog;

	private StatusRecyclerListener statusRecyclerListener;
	private UserRecyclerListener userRecyclerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		sheJiaoMao = (SheJiaoMaoApplication) getApplication();

		//对shejiaomao://topic/#xxxx# 地址的解析
		Intent intent = getIntent();
		Uri uri = intent.getData();
		if (uri != null
			&& Constants.URI_TOPIC.getScheme().equals(uri.getScheme())
			&& Constants.URI_TOPIC.getAuthority().equals(uri.getAuthority())) {
			keyword = uri.toString().replace(Constants.URI_TOPIC.toString(), "");
			searchCatalog = SearchCatalogProvider.SEARCH_CATALOG_STATUS;
			isExecuteQuery = true;
		}

		//来自Searchable对话框查询
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			keyword = intent.getStringExtra(SearchManager.QUERY);
			searchCatalog = SearchCatalogProvider.SEARCH_CATALOG_STATUS;
			isExecuteQuery = true;
		} else if (SEARCH_SUGGEST_ACTION.equals(intent.getAction())
			&& uri != null) {
			String rowId = uri.getLastPathSegment();
			keyword = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
			searchCatalog = SearchCatalogProvider.SEARCH_CATALOG_STATUS;
			try {
				searchCatalog = Integer.parseInt(rowId);
			} catch(Exception e) {}
            isExecuteQuery = true;
		}

		initComponents();
		bindEvent();
	}

	private void initComponents() {
		LinearLayout llRoot = (LinearLayout)this.findViewById(R.id.llRoot);
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		LinearLayout llHeaderSearch = (LinearLayout)findViewById(R.id.llHeaderSearch);
		EditText etKeyWord = (EditText) findViewById(R.id.etKeyWord);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearchStatus = (Button) findViewById(R.id.btnSearchStatus);
		btnSearchUser = (Button) findViewById(R.id.btnSearchUser);
		lvSearchResult = (ListView) findViewById(R.id.lvSearchResult);
		lvSearchResult.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		lvSearchResult.setOnScrollListener(new StatusScrollListener());
		
		ThemeUtil.setRootBackground(llRoot);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		llHeaderSearch.setBackgroundDrawable(theme.getDrawable("bg_header_corner_search"));
		int padding6 = theme.dip2px(6);
		int padding8 = theme.dip2px(8);
		llHeaderSearch.setPadding(padding6, padding8, padding6, padding8);
		ThemeUtil.setListViewStyle(lvSearchResult);
		
		etKeyWord.setBackgroundDrawable(theme.getDrawable("bg_input_frame_left_half"));
		btnSearch.setBackgroundDrawable(theme.getDrawable("selector_btn_search"));
		btnSearchStatus.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_left"));
		btnSearchStatus.setPadding(0, 0, 0, 0);
		ColorStateList selectorBtnTab = theme.getColorStateList("selector_btn_tab");
		btnSearchStatus.setTextColor(selectorBtnTab);
		btnSearchStatus.setGravity(Gravity.CENTER);
		btnSearchUser.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_right"));
		btnSearchUser.setPadding(0, 0, 0, 0);
		btnSearchUser.setTextColor(selectorBtnTab);
		btnSearchUser.setGravity(Gravity.CENTER);
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnOperate = (Button) this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_home);
		btnOperate.setOnClickListener(new GoHomeClickListener());

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_search);

		final Button btnSearch = (Button) findViewById(R.id.btnSearch);
		final EditText etKeyWord = (EditText) findViewById(R.id.etKeyWord);

		etKeyWord.addTextChangedListener(new SearchTextWatch(this));

		tabButton = new TabButton();
		tabButton.addButton(btnSearchStatus);
		tabButton.addButton(btnSearchUser);
		tabButton.toggleButton(btnSearchStatus);

		statusRecyclerListener = new StatusRecyclerListener();
		userRecyclerListener = new UserRecyclerListener();
		//默认搜索微博
		lvSearchResult.setRecyclerListener(statusRecyclerListener);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				keyword = etKeyWord.getText().toString().trim();
				if (StringUtil.isBlank(keyword)) {
					Toast.makeText(v.getContext(), R.string.msg_search_no_keyword, Toast.LENGTH_LONG).show();
				    return;
				}

				if (!btnSearchStatus.isEnabled()) {
					resultAdapter = new StatusSearchResultAdapter(SearchActivity.this);
					lvSearchResult.setRecyclerListener(statusRecyclerListener);
					bindStatusItemEvent();
				} else if (!btnSearchUser.isEnabled()) {
					resultAdapter = new UserSearchResultAdapter(SearchActivity.this);
					lvSearchResult.setRecyclerListener(userRecyclerListener);
					bindUserItemEvent();
				}
				InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				showMoreFooter();
				lvSearchResult.setAdapter(resultAdapter);
				paging = new Paging();
				executeTask();
			}

		});

		if (isExecuteQuery) {
			if (searchCatalog == SearchCatalogProvider.SEARCH_CATALOG_STATUS) {
				btnSearchStatus.performClick();
			} else if (searchCatalog == SearchCatalogProvider.SEARCH_CATALOG_USER) {
				btnSearchUser.performClick();
			}
			etKeyWord.setText(keyword);
			btnSearch.performClick();

			//hide input method
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}

	private void bindStatusItemEvent() {
		MicroBlogItemClickListener itemClickListener = new MicroBlogItemClickListener(this);
		lvSearchResult.setOnItemClickListener(itemClickListener);
		MicroBlogContextMenuListener contextMenuListener =
			new MicroBlogContextMenuListener(lvSearchResult);
		lvSearchResult.setOnCreateContextMenuListener(contextMenuListener);
		lvSearchResult.setOnItemClickListener(itemClickListener);
	}

	private void bindUserItemEvent() {
		SocialGraphItemClickListener itemClickListener = new SocialGraphItemClickListener(this);
		lvSearchResult.setOnItemClickListener(itemClickListener);
		lvSearchResult.setOnCreateContextMenuListener(null);
	}

	private void executeTask() {
		new SearchTask(this, paging, keyword, resultAdapter).execute();
	}

	public void showLoadingFooter() {
		if (listFooter != null) {
			lvSearchResult.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_loading, null);
		ThemeUtil.setListViewLoading(listFooter);
		lvSearchResult.addFooterView(listFooter);
	}

	public void showMoreFooter() {
		if (listFooter != null) {
			lvSearchResult.removeFooterView(listFooter);
		}

		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
		ThemeUtil.setListViewMore(listFooter);
		listFooter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 executeTask();
			}
		});
		lvSearchResult.addFooterView(listFooter);
	}

	public void showNoMoreFooter() {
		if (listFooter != null) {
			lvSearchResult.removeFooterView(listFooter);
		}
		listFooter = getLayoutInflater().inflate(R.layout.list_item_more, null);
        ThemeUtil.setListViewMore(listFooter);
		TextView tvFooter = (TextView) listFooter.findViewById(R.id.tvFooter);
		if (resultAdapter.getCount() == 0) {
			tvFooter.setText(R.string.label_search_no_result);
		} else {
			tvFooter.setText(R.string.label_no_more);
		}

		lvSearchResult.addFooterView(listFooter);
	}

}
