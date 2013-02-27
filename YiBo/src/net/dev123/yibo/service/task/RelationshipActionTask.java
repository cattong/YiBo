package net.dev123.yibo.service.task;

import net.dev123.commons.util.StringUtil;
import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Relationship;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.ResourceBook;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.listener.ProfileFollowClickListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RelationshipActionTask extends AsyncTask<Void, Void, Boolean> {
	private static final String  LOG = "RelationshipActionTask";
	private Context context;
	private View view;
	private User targetUser;
	private YiBoApplication yibo = null;

	private ProgressDialog dialog;
    private String resultMsg;

	public RelationshipActionTask(Context context, User targetUser) {
		this.context = context;
		this.yibo = (YiBoApplication)context.getApplicationContext();
		this.targetUser = targetUser;
	}

	public RelationshipActionTask(View view, User targetUser) {
		this.context = view.getContext();
		this.yibo = (YiBoApplication)context.getApplicationContext();
		this.targetUser = targetUser;
		this.view = view;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (view != null && view instanceof Button) {
        	Button btnFollow =(Button)view;
        	btnFollow.setEnabled(false);
		}
		if (targetUser.isFollowing()) {
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
		MicroBlog microBlog = GlobalVars.getMicroBlog(yibo.getCurrentAccount());
		if (targetUser == null || microBlog == null) {
			return isSuccess;
		}

		try {
			if (targetUser.isFollowing()) {
				microBlog.destroyFriendship(targetUser.getId());
				targetUser.setFollowing(false);
				resultMsg = context.getString(R.string.msg_personal_unfollow_success);
			} else {
				microBlog.createFriendship(targetUser.getId());
			    targetUser.setFollowing(true);
				targetUser.setBlocking(false);
				resultMsg = context.getString(R.string.msg_personal_follow_success);
			}
			isSuccess = true;
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(LOG, "Task", e);
			resultMsg = ResourceBook.getStatusCodeValue(e.getExceptionCode(), context);
		}
		
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (dialog != null && dialog.getContext() != null) {
			try {
			    dialog.dismiss();
			} catch(Exception e){}
		}

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
    		Relationship relationship = profileActivity.getRelationship();
    		if (relationship != null) {
    			relationship.setFollowing(targetUser.isFollowing());
    			relationship.setBlocking(targetUser.isBlocking());
    			profileActivity.setRelationship(relationship);
    		}
    	} else if (btnFollow != null) {
    		btnFollow.setEnabled(true);
        	if (targetUser.isFollowing()) {
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
