package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Relationship;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.listener.ProfileFollowClickListener;

public class RelationshipActionTask extends AsyncTask<Void, Void, Boolean> {
	private static final String  LOG = "RelationshipActionTask";
	
	private SheJiaoMaoApplication sheJiaoMao;
	private Context context;
	private View view;
	private User targetUser;
	private Relationship relationship;

	private ProgressDialog dialog;
    private String resultMsg;

	public RelationshipActionTask(Context context, User targetUser) {
		this.context = context;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
		this.targetUser = targetUser;
		this.relationship = targetUser.getRelationship();
	}

	public RelationshipActionTask(View view, User targetUser) {
		this(view.getContext(), targetUser);
		this.context = view.getContext();
		this.view = view;		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (view != null && view instanceof Button) {
        	Button btnFollow =(Button)view;
        	btnFollow.setEnabled(false);
		}
		if (relationship == null) {
			cancel(true);
			return;
		}
		
		if (relationship.isSourceFollowingTarget()) {
		    dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_personal_unfollowing));
		} else {
			dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_personal_following));
		}
		dialog.setCancelable(true);
		dialog.setOnCancelListener(onCancelListener);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSuccess = false;
		Weibo microBlog = GlobalVars.getMicroBlog(sheJiaoMao.getCurrentAccount());
		if (targetUser == null || microBlog == null) {
			return isSuccess;
		}

		try {
			if (relationship.isSourceFollowingTarget()) {
				microBlog.destroyFriendship(targetUser.getUserId());
				relationship.setSourceFollowingTarget(false);
				relationship.setSourceFollowingTarget(false);
				resultMsg = context.getString(R.string.msg_personal_unfollow_success);
			} else {
				microBlog.createFriendship(targetUser.getUserId());
				relationship.setSourceFollowingTarget(true);
				resultMsg = context.getString(R.string.msg_personal_follow_success);
			}
			isSuccess = true;
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(LOG, "Task", e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), context);
		}
		
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
	    try {
			if (dialog != null) {
			    dialog.dismiss();
			}
		} catch(Exception e) {}

        if (StringUtil.isNotEmpty(resultMsg)) {
        	Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT).show();
        }
        
		Button btnFollow = null;
		if (view != null && view instanceof Button) {
        	btnFollow =(Button)view;
        	btnFollow.setEnabled(false);
		}
        
        if (!result) {
            //关注操作失败时，恢复按钮。
            if (btnFollow != null) {
            	btnFollow.setEnabled(true);
            }
        	return;
        }


        if (context instanceof ProfileActivity) {
    		final ProfileActivity profileActivity = (ProfileActivity)context;
    		if (relationship != null) {
    			profileActivity.setRelationship(relationship);
    		}
    	} else if (btnFollow != null) {
    		btnFollow.setEnabled(true);
        	if (relationship.isSourceFollowingTarget()) {
    			btnFollow.setText(R.string.btn_personal_unfollow);
    			btnFollow.setTextAppearance(context, R.style.btn_action_negative);
    			ThemeUtil.setBtnActionNegative(btnFollow);
        	} else {
    			btnFollow.setText(R.string.btn_personal_follow);
    			btnFollow.setTextAppearance(context, R.style.btn_action_positive);
    			ThemeUtil.setBtnActionPositive(btnFollow);
        	}
        	btnFollow.setOnClickListener(new ProfileFollowClickListener(targetUser));
        }
	}

	private OnCancelListener onCancelListener = new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			RelationshipActionTask.this.cancel(true);
		}
	};
}
