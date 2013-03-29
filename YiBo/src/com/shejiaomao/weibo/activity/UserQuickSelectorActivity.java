package com.shejiaomao.weibo.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cattong.commons.util.ListUtil;
import com.cattong.entity.BaseUser;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.Relation;
import com.shejiaomao.weibo.db.TaskDao;
import com.shejiaomao.weibo.service.adapter.UserQuickSelectorListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.UserQuickSelectorTabChangeListener;
import com.shejiaomao.weibo.service.listener.UserQuickSelectorTextWatcher;
import com.shejiaomao.weibo.service.listener.UserSelectorRecyclerListener;
import com.shejiaomao.weibo.service.task.UserQuickSelectorRecentTask;
import com.shejiaomao.weibo.service.task.UserQuickSelectorTask;
import com.shejiaomao.widget.TabButton;

public class UserQuickSelectorActivity extends BaseActivity {
    private SelectMode selectMode;
	private String title;

    private UserQuickSelectorListAdapter selectorAdapter;
    private Relation relation;

	private TabButton tabButton;
	private Button btnFollowing;
	private Button btnRecentContact;
	
	private ListView lvUser;
	private View listFooter;

    private SheJiaoMaoApplication sheJiaoMao;
    private LocalAccount account;

    private UserSelectorRecyclerListener recyclerListener;
    private boolean isFirstLoad = false;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_quick_selector);

		sheJiaoMao = (SheJiaoMaoApplication)getApplication();
		initParams();
		initComponents();
		bindEvent();

		relation = Relation.Followingship;
		executeTask();
	}

	private void initParams() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String mode = bundle.getString("SELECT_MODE");
		int titleId = bundle.getInt("TITLE_ID", R.string.title_select_mention_user);
		title = this.getString(titleId);
		try {
			selectMode = SelectMode.valueOf(mode);
		} catch (Exception e) {
			selectMode = SelectMode.Multiple;
		}

		account = sheJiaoMao.getCurrentAccount();
	}

	private void initComponents() {
		LinearLayout llRoot = (LinearLayout)this.findViewById(R.id.llRoot);
		LinearLayout llHeaderBase = (LinearLayout)this.findViewById(R.id.llHeaderBase);
		
		LinearLayout llHeaderUserSelector = (LinearLayout)this.findViewById(R.id.llHeaderUserSelector);
		EditText etFilterName = (EditText)this.findViewById(R.id.etFilterName);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnFollowing = (Button) findViewById(R.id.btnFollowing);
		btnRecentContact = (Button) findViewById(R.id.btnRecentContact);
		lvUser = (ListView)this.findViewById(R.id.lvUser);		
		
		LinearLayout llToolbar = (LinearLayout)this.findViewById(R.id.llToolbar);
		Button btnConfirm = (Button)this.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
		
		ThemeUtil.setRootBackground(llRoot);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		
		llHeaderUserSelector.setBackgroundDrawable(theme.getDrawable("bg_header_corner_search"));
		int padding6 = theme.dip2px(6);
		int padding8 = theme.dip2px(8);
		llHeaderUserSelector.setPadding(padding6, padding8, padding6, padding8);
		etFilterName.setBackgroundDrawable(theme.getDrawable("bg_input_frame_left_half"));
		btnSearch.setBackgroundDrawable(theme.getDrawable("selector_btn_search"));
		btnFollowing.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_left"));
		btnFollowing.setPadding(0, 0, 0, 0);
		ColorStateList selectorBtnTab = theme.getColorStateList("selector_btn_tab");
		btnFollowing.setTextColor(selectorBtnTab);
		btnFollowing.setGravity(Gravity.CENTER);
		btnRecentContact.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_right"));
		btnRecentContact.setPadding(0, 0, 0, 0);
		btnRecentContact.setTextColor(selectorBtnTab);
		btnRecentContact.setGravity(Gravity.CENTER);
		
		ThemeUtil.setListViewStyle(lvUser);
		llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
		ThemeUtil.setBtnActionPositive(btnConfirm);
		ThemeUtil.setBtnActionNegative(btnCancel);
		
		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		tvTitle.setText(title);

		selectorAdapter = new UserQuickSelectorListAdapter(this, account, selectMode);
		showLoadingFooter();
		lvUser.setAdapter(selectorAdapter);
		lvUser.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		setBack2Top(lvUser);
		
        recyclerListener = new UserSelectorRecyclerListener();
        lvUser.setRecyclerListener(recyclerListener);

        lvUser.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == parent.getCount() - 1) {
					view.performClick();
				} else {
					CheckBox checkBox = (CheckBox)view.findViewById(R.id.cbUser);
					checkBox.performClick();
					UserQuickSelectorActivity.this.updateButtonState();
				}
			}
		});
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		EditText etFilterName = (EditText)this.findViewById(R.id.etFilterName);
		TextWatcher textWatcher = new UserQuickSelectorTextWatcher(selectorAdapter);
		etFilterName.addTextChangedListener(textWatcher);

		tabButton = new TabButton();
		tabButton.addButton(btnFollowing);
		tabButton.addButton(btnRecentContact);
		tabButton.toggleButton(btnFollowing);
		UserQuickSelectorTabChangeListener tabChangeListener = 
			new UserQuickSelectorTabChangeListener(this);
		tabButton.setOnTabChangeListener(tabChangeListener);
		
        Button btnConfirm = (Button)this.findViewById(R.id.btnConfirm);
        btnConfirm.setEnabled(false);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				ArrayList<BaseUser> userList = pickupSelectedUsers();
				//保存入最近联系人
				TaskDao dao = new TaskDao(v.getContext());
				dao.saveRecentContact(account, userList);
				
				bundle.putSerializable("LIST_USER", userList);
				intent.putExtras(bundle);
				((Activity)v.getContext()).setResult(Constants.RESULT_CODE_SUCCESS, intent);
				((Activity)v.getContext()).finish();
			}
		});

        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new GoBackClickListener());
	}

	private ArrayList<BaseUser> pickupSelectedUsers() {
		ArrayList<BaseUser> listUser= new ArrayList<BaseUser>();
		if (selectorAdapter != null) {
			for (BaseUser user : selectorAdapter.getListSelectedUser()) {
				if (!listUser.contains(user)) {
					listUser.add(user);
				}
			}
		}

		return listUser;
	}

	public void updateButtonState() {
		ArrayList<BaseUser> userList = pickupSelectedUsers();
		Button btnConfirm = (Button) this.findViewById(R.id.btnConfirm);
		if (ListUtil.isNotEmpty(userList)) {
			btnConfirm.setEnabled(true);
		} else {
			btnConfirm.setEnabled(false);
		}
	}

	public void executeTask() {
		if (btnRecentContact != null && btnRecentContact.isEnabled()) {
		    new UserQuickSelectorTask(selectorAdapter, relation).execute();
		} else {
			new UserQuickSelectorRecentTask(selectorAdapter).execute();
		}
	}

	public void showLoadingFooter() {
		if(listFooter != null){
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

	public boolean isFirstLoad() {
		return isFirstLoad;
	}

	public void setFirstLoad(boolean isFirstLoad) {
		this.isFirstLoad = isFirstLoad;
	}

	public UserQuickSelectorListAdapter getSelectorAdapter() {
		return selectorAdapter;
	}
}
