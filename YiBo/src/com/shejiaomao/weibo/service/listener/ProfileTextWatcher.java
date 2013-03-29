package com.shejiaomao.weibo.service.listener;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.ProfileEditActivity;

public class ProfileTextWatcher implements TextWatcher {

	private ProfileEditActivity profileEditActivity;
	private User user;
	private Button btnProfileUpdate;
	private EditText etScreenName;
	private EditText etDescription;

	public ProfileTextWatcher(Context context) {
		this.profileEditActivity = (ProfileEditActivity) context;
		btnProfileUpdate = (Button) profileEditActivity.findViewById(R.id.btnProfileUpdate);
		user = profileEditActivity.getUser();
		etScreenName = (EditText) profileEditActivity.findViewById(R.id.etProfileScreenName);
		etDescription = (EditText) profileEditActivity.findViewById(R.id.etProfileDescription);
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
		if (StringUtil.isEmpty(etScreenName.getText().toString())) {
			btnProfileUpdate.setEnabled(false);
		} else if (StringUtil.isEquals(user.getScreenName(),
				        	etScreenName.getText().toString())
				    && StringUtil.isEquals(user.getDescription(),
				    		etDescription.getText().toString())) {
			btnProfileUpdate.setEnabled(false);
		} else {
			btnProfileUpdate.setEnabled(true);
		}
	}

	public void setUser(User user) {
		this.user = user;
	}

}
