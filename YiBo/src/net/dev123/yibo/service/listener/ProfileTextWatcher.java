package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileEditActivity;
import net.dev123.yibo.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Weiping Ye
 * @version 创建时间：2011-8-18 下午8:12:46
 **/
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
