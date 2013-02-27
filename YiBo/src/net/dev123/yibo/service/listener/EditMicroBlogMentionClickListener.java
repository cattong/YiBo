package net.dev123.yibo.service.listener;

import net.dev123.yibo.R;
import net.dev123.yibo.UserQuickSelectorActivity;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.SelectMode;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class EditMicroBlogMentionClickListener implements OnClickListener {
    private Integer requestCode;
    private SelectMode selectMode;
    private Integer titleId;
	public EditMicroBlogMentionClickListener() {		
	}
	
	@Override
	public void onClick(View v) {
		Activity context = (Activity)v.getContext();

		Intent intent = new Intent();
		if (selectMode == null) {
			selectMode = SelectMode.Multiple;
		}
		intent.putExtra("SELECT_MODE", selectMode.toString());
		intent.setClass(context, UserQuickSelectorActivity.class);
		if (requestCode == null) {
			requestCode = Constants.REQUEST_CODE_USER_SELECTOR;
		}
		if (titleId == null) {
			titleId = R.string.title_select_mention_user;
		}
		intent.putExtra("TITLE_ID", titleId);
		context.startActivityForResult(intent, requestCode);
	}

	public Integer getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(Integer requestCode) {
		this.requestCode = requestCode;
	}

	public SelectMode getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(SelectMode selectMode) {
		this.selectMode = selectMode;
	}

	public Integer getTitleId() {
		return titleId;
	}

	public void setTitleId(Integer titleId) {
		this.titleId = titleId;
	}

}
