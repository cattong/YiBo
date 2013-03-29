package com.shejiaomao.weibo.service.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;

public class AccountSelectorListAdapter extends BaseAdapter implements Filterable{
	private LayoutInflater inflater;
	private SelectMode mode;
	private boolean isShowSnsAccount;
	private Filter filter;
	private List<LocalAccount> listAccount;
	private List<LocalAccount> listSelectedAccount;
	private Context context ;

	public AccountSelectorListAdapter(Context context, SelectMode mode, boolean isShowSnsAccount) {
		this.context = context;
		this.mode = mode;
		this.listSelectedAccount = new ArrayList<LocalAccount>();
		this.isShowSnsAccount = isShowSnsAccount;
		this.filter = new AcountFilter();
		initComponents(context);
	}

	private void initComponents(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.listAccount = GlobalVars.getAccountList(context, false);
	}

	@Override
	public int getCount() {
		if (ListUtil.isEmpty(listAccount)) {
			return 0;
		}
		return listAccount.size();
	}

	@Override
	public Object getItem(int position) {
		if (ListUtil.isEmpty(listAccount)) {
			return null;
		}
		return listAccount.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AccountSelectorHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_account_selector, null);
			holder = new AccountSelectorHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (AccountSelectorHolder)convertView.getTag();
		}
		
		final LocalAccount account = (LocalAccount)getItem(position);
        if (account == null) {
        	return convertView;
        }
        
        holder.reset(account.getServiceProvider());
		String profileImageUrl = account.getUser().getProfileImageUrl();
		if (StringUtil.isNotEmpty(profileImageUrl)) {
			new ImageLoad4HeadTask(holder.ivProfilePicture, profileImageUrl, true).execute();
		}

		CheckBox cbAccountSelector = holder.cbAccountSelector;
        if (mode == SelectMode.Single) {
		    cbAccountSelector.setButtonDrawable(holder.theme.getDrawable("selector_checkbox_group"));
        } else {
        	cbAccountSelector.setButtonDrawable(holder.theme.getDrawable("selector_checkbox"));
        }

		if (listSelectedAccount.contains(account)) {
			cbAccountSelector.setChecked(true);
		} else {
			cbAccountSelector.setChecked(false);
		}

		holder.tvScreenName.setText(account.getUser().getScreenName());
		String snNameText = account.getServiceProvider().getSpName();
		holder.tvSPName.setText(snNameText);

		return convertView;
	}

	private void addSelectedAccount(LocalAccount account) {
		if (account == null || listSelectedAccount.contains(account)) {
			return;
		}

		if (mode == SelectMode.Single) {
			listSelectedAccount.clear();
		}
		listSelectedAccount.add(account);
	}

	public void addSelectedAccounts(List<LocalAccount> listAccount) {
		if (ListUtil.isEmpty(listAccount)) {
			return;
		}
		for (LocalAccount account : listAccount) {
			addSelectedAccount(account);
		}
		this.notifyDataSetChanged();
	}

	public List<LocalAccount> getListSelectedAccount() {
		return listSelectedAccount;
	}

	public List<LocalAccount> getListAccount() {
		return listAccount;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	private class AcountFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			List<LocalAccount> resultList = GlobalVars.getAccountList(context, false);
			if (!isShowSnsAccount) {
				List<LocalAccount> tmpList = resultList;
				resultList = new ArrayList<LocalAccount>();
				Iterator<LocalAccount> iterator = tmpList.iterator();
				LocalAccount tmpAccount = null;
				while (iterator.hasNext()) {
					tmpAccount = iterator.next();
					if (!tmpAccount.isSnsAccount()) {
						resultList.add(tmpAccount);
					}
				}
				filterResults.values = resultList;
			}
			filterResults.values = resultList;
			filterResults.count = resultList.size();
			return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			listAccount = (List<LocalAccount>)results.values;
			notifyDataSetChanged();
		}

	}
}
