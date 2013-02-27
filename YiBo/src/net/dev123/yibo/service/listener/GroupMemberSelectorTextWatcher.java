package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.service.adapter.GroupMemberListAdapter;
import android.text.Editable;
import android.text.TextWatcher;

public class GroupMemberSelectorTextWatcher implements TextWatcher {
    private GroupMemberListAdapter listAdapter;

	public GroupMemberSelectorTextWatcher(GroupMemberListAdapter listAdapter) {
		this.listAdapter = listAdapter;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
        String filterName = s.toString();
        if (StringUtil.isEmpty(filterName)) {
        	filterName = " ";
        }

        listAdapter.getFilter().filter(filterName);
        listAdapter.notifyDataSetChanged();
	}

}
