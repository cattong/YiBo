package com.shejiaomao.weibo.service.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.StatusUpdate;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.UpdateStatusTask;
import com.shejiaomao.weibo.service.task.UpdateStatusToMutiAccountsTask;

public class EditMicroBlogSendClickListener implements OnClickListener {
	private EditMicroBlogActivity context;

	public EditMicroBlogSendClickListener(EditMicroBlogActivity context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		EditText edText = (EditText)context.findViewById(R.id.etText);
		String text = edText.getText().toString().trim();
		if (StringUtil.isEmpty(text) 
			&& edText.getHint() != null) {
			text = edText.getHint().toString();
		}
		if (StringUtil.isEmpty(text)) {
        	Toast.makeText(v.getContext(), R.string.msg_blog_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		int byteLen = StringUtil.getLengthByByte(text);
		if (byteLen > Constants.STATUS_TEXT_MAX_LENGTH * 2) {
			text = StringUtil.subStringByByte(text, 0, Constants.STATUS_TEXT_MAX_LENGTH * 2);
		}

		List<LocalAccount> listUpdateAccount = context.getListUpdateAccount();
		if (ListUtil.isEmpty(listUpdateAccount)) {
			Toast.makeText(v.getContext(), R.string.title_accounts_selector, Toast.LENGTH_SHORT).show();
			return;
		}

		v.setEnabled(false);
		context.getEmotionViewController().hideEmotionView();
		//hide input method
		InputMethodManager inputMethodManager = (InputMethodManager)v.getContext().
		    getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(edText.getWindowToken(), 0);


		
		List<LocalAccount> sinaUpdateAccount = null;
		if (GlobalVars.IS_OBEY_SINA_AGREEMENT 
			&& isBreachOfAgreement(listUpdateAccount)) {
			context.setUpdateSinaAndPauseOthers(true);
			sinaUpdateAccount = new ArrayList<LocalAccount>();
			for (LocalAccount account : listUpdateAccount) {
				if (account.getServiceProvider() == ServiceProvider.Sina) {
					sinaUpdateAccount.add(account);
				}
			}
			//context.showDialog(EditMicroBlogActivity.DIALOG_AGREEMENT);
			Toast.makeText(v.getContext(), 
				R.string.msg_agreement_dispose, 
				Toast.LENGTH_LONG
			).show();
		} else {
			context.setUpdateSinaAndPauseOthers(false);
		}

		StatusUpdate statusUpdate = generateStatusUpdate(text);
		
		List<LocalAccount> accountRealUpdateList = 
			(context.isUpdateSinaAndPauseOthers() && sinaUpdateAccount != null)
				? sinaUpdateAccount : listUpdateAccount;

		if (accountRealUpdateList.size() == 1) {
			LocalAccount account = accountRealUpdateList.get(0);
		    UpdateStatusTask task = new UpdateStatusTask(context, statusUpdate, account);
		    if (statusUpdate.getImage() != null) {
		    	task.setRotateDegrees(context.getRotateDegrees());
		    }
			task.setShowDialog(true);
			task.execute();
			return;
		}
		
		if (accountRealUpdateList.size() > 1) {
			UpdateStatusToMutiAccountsTask updateMutiTask = 
				new UpdateStatusToMutiAccountsTask (
					context, statusUpdate, accountRealUpdateList);
            if (statusUpdate.getImage() != null) {
            	updateMutiTask.setRotateDegrees(context.getRotateDegrees());
            }
		    updateMutiTask.execute();
		}
			
	}

	private StatusUpdate generateStatusUpdate(String text) {
		StatusUpdate statusUpdate = new StatusUpdate(text);
		statusUpdate.setLocation(context.getGeoLocation());
		if (context.isHasImageFile() && StringUtil.isNotEmpty(context.getImagePath())) {
			statusUpdate.setImage(new File(context.getImagePath()));		    	
		}
		return statusUpdate;
	}

	private boolean isBreachOfAgreement(List<LocalAccount> listUpdateAccount) {
		boolean isBreachOfAgreement = false;
		if (ListUtil.isEmpty(listUpdateAccount) 
			&& listUpdateAccount.size() == 1) {
			return isBreachOfAgreement;
		}

		List<ServiceProvider> spList = new ArrayList<ServiceProvider>();
		for (LocalAccount account : listUpdateAccount) {
			if (!spList.contains(account.getServiceProvider())) {
				spList.add(account.getServiceProvider());
			}
		}

		if (spList.size() > 1 &&
				spList.contains(ServiceProvider.Sina)) {
			isBreachOfAgreement = true;
		}

		return isBreachOfAgreement;
	}
}
