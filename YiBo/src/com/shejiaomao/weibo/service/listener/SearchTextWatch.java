package com.shejiaomao.weibo.service.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.SearchActivity;

public class SearchTextWatch implements TextWatcher {
    private EditText etKeyWord;
    private Button btnSearch;
	public SearchTextWatch(SearchActivity context) {
    	btnSearch = (Button)context.findViewById(R.id.btnSearch);
		etKeyWord = (EditText)context.findViewById(R.id.etKeyWord);
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
		if (etKeyWord == null || btnSearch == null) {
			return;
		}
		
		if (etKeyWord.getText().length() > 0 
			&& etKeyWord.getText().length() > 0) {
			btnSearch.setEnabled(true);
		} else {
			btnSearch.setEnabled(false);
		}

	}

}
