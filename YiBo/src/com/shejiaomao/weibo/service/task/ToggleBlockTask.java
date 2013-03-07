package com.shejiaomao.weibo.service.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.db.LocalAccount;

public class ToggleBlockTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG   = "ToggleBlockTask";

	private Context context;
    private User targetUser;
    private View view;

	private ProgressDialog dialog = null;
	private String resultMsg = null;;
	public ToggleBlockTask(Context context, User user) {
		this.context = context;
        this.targetUser = user;
	}

	public ToggleBlockTask(View view, User user) {
		this.view = view;
		this.context = view.getContext();
        this.targetUser = user;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

//		if (targetUser.isBlocking()) {
//			dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_personal_unblocking));
//		} else {
//		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_personal_blocking));
//		}
//		dialog.setCancelable(true);
//		dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
        LocalAccount account = ((SheJiaoMaoApplication)context.getApplicationContext()).getCurrentAccount();
		Weibo microBlog = GlobalVars.getMicroBlog(account);

		boolean isSuccess = false;
		if (microBlog == null) {
			return isSuccess;
		}

//		try {
//			User resultUser = null;
//			if (targetUser.isBlocking()) {
//				resultUser = microBlog.destroyBlock(targetUser.getUserId());
//				isSuccess = (resultUser.isBlocking() == false);
//			} else {
//				resultUser = microBlog.createBlock(targetUser.getUserId());
//			    isSuccess = (resultUser.isBlocking() == true);
//			}
//			if (isSuccess) {
//				targetUser.setBlocking(resultUser.isBlocking());
//			}
//		} catch (LibException e) {
//			if (Constants.DEBUG) Log.e(TAG, "Task", e);
//			resultMsg = ResourceBook.getResultCodeValue(e.getExceptionCode(), context);
//		}

		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (dialog != null
				&& dialog.getContext() != null) {
			try {
			    dialog.dismiss();
			} catch (Exception e) {}
		}

//		if (result) {
//			if (targetUser.isBlocking()) {
//				resultMsg = context.getString(R.string.msg_personal_block_success);
//			} else {
//				resultMsg = context.getString(R.string.msg_personal_unblock_success);
//			}
//			if (context instanceof ProfileActivity) {
//				final ProfileActivity profileActivity = (ProfileActivity)context;
//				Relationship relationship = profileActivity.getRelationship();
//				if (relationship != null) {
//					relationship.setBlocking(targetUser.isBlocking());
//					relationship.setFollowing(false); //无论是拉入还是移出黑名单，都不再是following关系
//				}
//				profileActivity.setRelationship(relationship);
//			} else if ( view != null && view instanceof Button) {
//				Button btnBlock = (Button)view;
//				btnBlock.setEnabled(true);
//	        	if (targetUser.isBlocking()) {
//	        		btnBlock.setText(R.string.btn_personal_unblock);
//	        		btnBlock.setTextColor(R.color.selector_btn_action_negative);
//	        		btnBlock.setTextAppearance(context, R.style.btn_action_negative);
//	        		btnBlock.setBackgroundResource(R.drawable.selector_btn_action_negative);
//	        	} else {
//	        		btnBlock.setText(R.string.btn_personal_block);
//	        		btnBlock.setTextColor(R.color.selector_btn_action_positive);
//	        		btnBlock.setTextAppearance(context, R.style.btn_action_positive);
//	        		btnBlock.setBackgroundResource(R.drawable.selector_btn_action_positive);
//	        	}
//	        	btnBlock.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						new ToggleBlockTask(view, targetUser).execute();
//					}
//				});
//			}
//		}

		Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
	};
}
