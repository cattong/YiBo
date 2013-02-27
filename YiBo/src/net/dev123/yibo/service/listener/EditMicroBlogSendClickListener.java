package net.dev123.yibo.service.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dev123.commons.ServiceProvider;
import net.dev123.commons.util.ListUtil;
import net.dev123.commons.util.StringUtil;
import net.dev123.entity.StatusUpdate;
import net.dev123.yibo.EditMicroBlogActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.db.ConfigSystemDao;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.task.UpdateStatusTask;
import net.dev123.yibo.service.task.UpdateStatusThroughServerTask;
import net.dev123.yibo.service.task.UpdateStatusToMutiAccountsTask;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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

		boolean isUpdateThroughServer = false;
		if (listUpdateAccount.size() > 1) {
			ConfigSystemDao configDao = new ConfigSystemDao(context);
			if (configDao.getInt(Constants.PASSPORT_POINTS) >= Constants.POINTS_SYNC_LEVEL) {
				isUpdateThroughServer = true;
			}
		}
		
		List<LocalAccount> sinaUpdateAccount = null;
		if (GlobalVars.IS_OBEY_SINA_AGREEMENT 
			&& !isUpdateThroughServer
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
		
		if (accountRealUpdateList.size() > 1
			&& isUpdateThroughServer) {
		    UpdateStatusThroughServerTask task = new UpdateStatusThroughServerTask(
		    		context, statusUpdate, accountRealUpdateList);
		    if (statusUpdate.getImage() != null) {
		    	task.setRotateDegrees(context.getRotateDegrees());
            }
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

		List<ServiceProvider> listProvider = new ArrayList<ServiceProvider>();
		for (LocalAccount account : listUpdateAccount) {
			if (!listProvider.contains(account.getServiceProvider())) {
				listProvider.add(account.getServiceProvider());
			}
		}

		if (listProvider.size() > 1 &&
			listProvider.contains(ServiceProvider.Sina)) {
			isBreachOfAgreement = true;
		}

		return isBreachOfAgreement;
	}
}
