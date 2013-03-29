package com.shejiaomao.weibo.widget;

import java.util.ArrayList;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.AccountSelectorListAdapter;
import com.shejiaomao.weibo.service.listener.AccountManageClickListener;
import com.shejiaomao.weibo.service.listener.AccountSelectorOperateClickListener;

public class AccountSelectorWindow {
	private Context context;
	private View anchor;
    private PopupWindow popList;
    private SelectMode mode;
    private boolean isShowSnsAccount;

    private ListView lvAccountSelector;
    private AccountSelectorListAdapter listAdapter;
    private OnItemClickListener onItemClickListener;
	public AccountSelectorWindow(Context context, View anchor, SelectMode mode, boolean isShowSnsAccount) {
		this.context = context;
		this.anchor = anchor;
		this.mode = mode;
		this.isShowSnsAccount = isShowSnsAccount;
		initComponents();
	}

	private void initComponents() {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.widget_pop_account_selector, null);
	    lvAccountSelector = (ListView)contentView.findViewById(R.id.lvAccountSelector);
	    ImageView ivAccountSelectorFooter = (ImageView)contentView.findViewById(R.id.ivAccountSelectorFooter);
	    Theme theme = ThemeUtil.createTheme(context);
	    ThemeUtil.setContentBackground(lvAccountSelector);
	    ThemeUtil.setListViewStyle(lvAccountSelector);
	    ivAccountSelectorFooter.setBackgroundDrawable(theme.getDrawable("selector_bg_footer_account_selector"));
	    
	    View footerView = null;
	    if (mode == SelectMode.Single) {
	    	footerView = inflater.inflate(R.layout.list_item_account_manage, null);
	    	LinearLayout llAccountManage = (LinearLayout)footerView.findViewById(R.id.llAccountManage);
	    	ImageView ivProfileImage = (ImageView)footerView.findViewById(R.id.ivProfileImage);
	    	TextView tvProfileName = (TextView)footerView.findViewById(R.id.tvProfileName);
	    	TextView tvImpress = (TextView)footerView.findViewById(R.id.tvImpress);
	    	ImageView ivMoreDetail = (ImageView)footerView.findViewById(R.id.ivMoreDetail);
	    	llAccountManage.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
	    	llAccountManage.setPadding(theme.dip2px(8), theme.dip2px(4), 
	    		theme.dip2px(16), theme.dip2px(4));
	    	ivProfileImage.setImageDrawable(theme.getDrawable("icon_group"));
	    	tvProfileName.setTextColor(theme.getColor("content"));
	    	tvImpress.setTextColor(theme.getColor("remark"));
	    	ivMoreDetail.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
	    	
	    	footerView.setOnClickListener(new AccountManageClickListener(this));
	    } else {
	    	footerView = inflater.inflate(R.layout.list_item_account_selector_operate, null);
	    	Button btnSelectAll = (Button)footerView.findViewById(R.id.btnSelectAll);
	    	Button btnSelectInverse = (Button)footerView.findViewById(R.id.btnSelectInverse);
	    	ThemeUtil.setBtnActionPositive(btnSelectAll);
	    	ThemeUtil.setBtnActionNegative(btnSelectInverse);
	    	
	    	AccountSelectorOperateClickListener operateClickListener =
	    		new AccountSelectorOperateClickListener(this);
	    	btnSelectAll.setOnClickListener(operateClickListener);
	    	btnSelectInverse.setOnClickListener(operateClickListener);
	    }
	    lvAccountSelector.addFooterView(footerView);

	    listAdapter = new AccountSelectorListAdapter(context, mode, isShowSnsAccount);
	    lvAccountSelector.setAdapter(listAdapter);

	    popList = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    popList.setBackgroundDrawable(new BitmapDrawable());
	    popList.setFocusable(true);
	    popList.setOutsideTouchable(true);
	}

	public void show() {
		listAdapter.getFilter().filter(null);
		listAdapter.notifyDataSetChanged();
		popList.showAsDropDown(anchor);
	}

	public void dismiss() {
		popList.dismiss();
	}

	public boolean isShowing() {
		return popList.isShowing();
	}

	public List<LocalAccount> getSelectedAccounts() {
		return listAdapter.getListSelectedAccount();
	}

	public void selectAll() {
		List<LocalAccount> accountList = listAdapter.getListAccount();
		listAdapter.addSelectedAccounts(accountList);
	}

	public void selectInverse() {
		List<LocalAccount> accountList = listAdapter.getListAccount();
		List<LocalAccount> selectedAccountList = listAdapter.getListSelectedAccount();
		for (LocalAccount account : accountList) {
			if (selectedAccountList.contains(account)) {
				removeSelectedAccount(account);
			} else {
				addSelectedAccount(account);
			}
		}
	}

	public void addSelectedAccount(LocalAccount account) {
		if (account == null) {
			return;
		}
		List<LocalAccount> listAccount = new ArrayList<LocalAccount>();
		listAccount.add(account);
		listAdapter.addSelectedAccounts(listAccount);
	}

	public void addSelectedAccounts(List<LocalAccount> accountList) {
		if (ListUtil.isEmpty(accountList)) {
			return;
		}
		listAdapter.addSelectedAccounts(accountList);
	}

	public void removeSelectedAccount(LocalAccount account) {
		List<LocalAccount> listSelectedAccount = listAdapter.getListSelectedAccount();
		listSelectedAccount.remove(account);
		listAdapter.notifyDataSetChanged();
	}

	public boolean isSelected(LocalAccount account) {
		List<LocalAccount> listSelectedAccount = listAdapter.getListSelectedAccount();
		return listSelectedAccount.contains(account);
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
		lvAccountSelector.setOnItemClickListener(onItemClickListener);
	}
}
