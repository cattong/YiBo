package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.service.adapter.UserQuickSelectorListAdapter;
import android.text.Editable;
import android.text.TextWatcher;

public class UserQuickSelectorTextWatcher implements TextWatcher {
    private UserQuickSelectorListAdapter usersAdapter;

	public UserQuickSelectorTextWatcher(UserQuickSelectorListAdapter usersAdapter) {
		this.usersAdapter = usersAdapter;
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

        usersAdapter.getFilter().filter(filterName);
        usersAdapter.notifyDataSetChanged();
	}

}
