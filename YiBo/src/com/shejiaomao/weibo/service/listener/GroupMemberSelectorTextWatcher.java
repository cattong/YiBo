package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.util.StringUtil;
import android.text.Editable;
import android.text.TextWatcher;

import com.shejiaomao.weibo.service.adapter.GroupMemberListAdapter;

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
