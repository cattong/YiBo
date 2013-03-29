package com.shejiaomao.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
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
import com.cattong.weibo.entity.Group;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.GroupDao;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalGroup;
import com.shejiaomao.weibo.service.adapter.GroupMemberListAdapter;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.GroupMemberAddClickListener;
import com.shejiaomao.weibo.service.listener.GroupMemberSelectorTextWatcher;
import com.shejiaomao.weibo.service.listener.UserContextMenuListener;
import com.shejiaomao.weibo.service.listener.UserSelectorRecyclerListener;
import com.shejiaomao.weibo.service.task.GroupMemberAddTask;
import com.shejiaomao.weibo.service.task.GroupMemberDeleteTask;
import com.shejiaomao.weibo.service.task.GroupMemberTask;

public class GroupMemberActivity extends BaseActivity {
    private SelectMode selectMode;

	private LocalGroup group;
    private GroupMemberListAdapter selectorAdapter;

    private ListView lvUser;
    private View listFooter;

    private SheJiaoMaoApplication sheJiaoMao;
    private LocalAccount account;

    private UserSelectorRecyclerListener recyclerListener;
    private boolean isFirstLoad = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_member);

		sheJiaoMao = (SheJiaoMaoApplication)getApplication();
		initParams();
		initComponents();
		bindEvent();

		executeTask();
	}

	private void initParams() {
		account = sheJiaoMao.getCurrentAccount();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String mode = bundle.getString("SELECT_MODE");
		Group tempGroup = (Group)bundle.getSerializable("GROUP");
		if (tempGroup instanceof LocalGroup) {
			group = (LocalGroup)tempGroup;
		} else {
			GroupDao dao = new GroupDao(this);
			group = dao.getGroup(account, tempGroup);
		}

		try {
			selectMode = SelectMode.valueOf(mode);
		} catch (Exception e) {
			selectMode = SelectMode.Multiple;
		}
	}

	private void initComponents() {
		LinearLayout llRoot = (LinearLayout)this.findViewById(R.id.llRoot);
		LinearLayout llHeaderBase = (LinearLayout)this.findViewById(R.id.llHeaderBase);
		LinearLayout llHeaderUserSelector = (LinearLayout)this.findViewById(R.id.llHeaderUserSelector);
		lvUser = (ListView)this.findViewById(R.id.lvUser);		
		LinearLayout llToolbar = (LinearLayout)this.findViewById(R.id.llToolbar);
		Button btnDelete = (Button)this.findViewById(R.id.btnDelete);
		Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
		ThemeUtil.setRootBackground(llRoot);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setHeaderUserSelector(llHeaderUserSelector);
		ThemeUtil.setListViewStyle(lvUser);
		llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
		ThemeUtil.setBtnActionPositive(btnDelete);
		ThemeUtil.setBtnActionNegative(btnCancel);
		
		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		tvTitle.setText(group.getName());

		selectorAdapter = new GroupMemberListAdapter(this, account, selectMode);
		showLoadingFooter();
		lvUser.setAdapter(selectorAdapter);
		lvUser.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());

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
					GroupMemberActivity.this.updateButtonState();
				}
			}
		});

        lvUser.setOnCreateContextMenuListener(new UserContextMenuListener(lvUser));
        setBack2Top(lvUser);
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnOperate = (Button)this.findViewById(R.id.btnOperate);
		btnOperate.setVisibility(View.VISIBLE);
		btnOperate.setText(R.string.btn_add_member);
		btnOperate.setOnClickListener(new GroupMemberAddClickListener());

		EditText etFilterName = (EditText)this.findViewById(R.id.etFilterName);
		TextWatcher textWatcher = new GroupMemberSelectorTextWatcher(selectorAdapter);
		etFilterName.addTextChangedListener(textWatcher);

        Button btnDelete = (Button)this.findViewById(R.id.btnDelete);
        btnDelete.setEnabled(false);
        btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<BaseUser> listSelectedUser = selectorAdapter.getListSelectedUser();
				List<BaseUser> listUser = new ArrayList<BaseUser>(listSelectedUser);
				new GroupMemberDeleteTask(selectorAdapter, group, listUser).execute();
			}
		});

        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new GoBackClickListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUEST_CODE_USER_SELECTOR:
			if (resultCode == Constants.RESULT_CODE_SUCCESS) {
				List<BaseUser> listUser = (List<BaseUser>)data.getSerializableExtra("LIST_USER");
				new GroupMemberAddTask(selectorAdapter, group, listUser).execute();
			}
			break;
		}
	}

	public void updateButtonState() {
		List<BaseUser> listUser = selectorAdapter.getListSelectedUser();
		Button btnDelete = (Button)this.findViewById(R.id.btnDelete);
		if (ListUtil.isNotEmpty(listUser)) {
			btnDelete.setEnabled(true);
		} else {
			btnDelete.setEnabled(false);
		}
	}

	private void executeTask() {
		new GroupMemberTask(selectorAdapter, group).execute();
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
}
