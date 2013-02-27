package net.dev123.yibo.service.adapter;

import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.Paging;
import net.dev123.commons.util.ListUtil;
import net.dev123.mblog.entity.Group;
import net.dev123.yibo.R;
import net.dev123.yibo.db.GroupDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.HomePageGroupTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomePageGroupListAdapter extends BaseAdapter {	
    private Context context;
    private LocalAccount account;
    private List<Group> groupList;
    
    //first init
    private List<LocalAccount> firstInitAccountList;
	public HomePageGroupListAdapter(Context context) {
		this.context = context;
		this.groupList = new ArrayList<Group>();
		this.firstInitAccountList = new ArrayList<LocalAccount>();
	}
	
	private void readGroupList(LocalAccount account) {
		if (account == null) {
			return;
		}
		groupList.clear();
		
		GroupDao dao = new GroupDao(context);
		Paging<Group> paging = new Paging<Group>();
		paging.setPageSize(50);
		List<Group> tempGroupList = dao.getGroups(account, paging);
		if (ListUtil.isNotEmpty(tempGroupList)) {
			Group allGroup = new Group();
			allGroup.setId("all_group");
			allGroup.setName(context.getString(R.string.label_all_group));
			groupList.add(allGroup);
			
		    groupList.addAll(tempGroupList);
		} 
		if (ListUtil.isEmpty(tempGroupList) 
			|| !firstInitAccountList.contains(account)) {
			HomePageGroupTask task = new HomePageGroupTask(context, this);
			task.execute();
		}
		
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return groupList.size();
	}

	@Override
	public Object getItem(int position) {
		return groupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.list_item_dialog_list_choose, null);
		}
		
		Group group = (Group)getItem(position);
		
		TextView tvItemName = (TextView)convertView.findViewById(R.id.tvItemName);
		tvItemName.setText(group.getName());
		
		return convertView;
	}

	public boolean addGroupList(List<Group> list) {
		groupList.clear();
		Group allGroup = new Group();
		allGroup.setId("all_group");
		allGroup.setName(context.getString(R.string.label_all_group));
		groupList.add(allGroup);
		if (ListUtil.isNotEmpty(list)) {
			groupList.addAll(list);
		}
		this.notifyDataSetChanged();
		return true;
	}
	
	public LocalAccount getAccount() {
		return account;
	}

	public void setAccount(LocalAccount account) {
		if (account == null
			|| account.equals(this.account)) {
			return;
		}
		this.account = account;
		readGroupList(account);
	}
}
