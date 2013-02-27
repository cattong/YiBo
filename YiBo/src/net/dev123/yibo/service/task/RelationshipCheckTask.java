package net.dev123.yibo.service.task;

import net.dev123.exception.LibException;
import net.dev123.mblog.MicroBlog;
import net.dev123.mblog.entity.Relationship;
import net.dev123.mblog.entity.User;
import net.dev123.yibo.ProfileActivity;
import net.dev123.yibo.R;
import net.dev123.yibo.YiBoApplication;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalVars;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.listener.ProfileFollowClickListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RelationshipCheckTask extends AsyncTask<Void, Void, Relationship> {
	private static final String TAG = "RelationshipCheckTask";

	private User targetUser;
	private Context context;
	private View view;

	public RelationshipCheckTask(Context context, User user) {
		this.context = context;
		this.targetUser = user;
	}

	public RelationshipCheckTask(View v, User user) {
		this.context = v.getContext();
		this.targetUser = user;
		this.view = v;
	}

	@Override
	protected void onPreExecute() {
		if (view != null && view instanceof Button) {
			Button btn = (Button)view;
			btn.setVisibility(View.VISIBLE);
			btn.setText(R.string.btn_loading);
			btn.setTextAppearance(context, R.style.btn_action_negative);
			ThemeUtil.setBtnActionNegative(btn);
			btn.setEnabled(false);
		}
	}

	@Override
	protected Relationship doInBackground(Void... params) {
		if (targetUser == null) {
			return null;
		}

		LocalAccount account = ((YiBoApplication)context.getApplicationContext()).getCurrentAccount();
		if (account == null
			|| targetUser.getId().equals(account.getUser().getId())) {
			return null;
		}

		MicroBlog microBlog = GlobalVars.getMicroBlog(account);
		if (microBlog == null) {
			return null;
		}

		Relationship relationship = null;
		try {
			relationship = microBlog.showRelationship(
				account.getUser().getId(),
				targetUser.getId());
			targetUser.setFollowing(relationship.isFollowing());
			targetUser.setFollowedBy(relationship.isFollowed());
			targetUser.setBlocking(relationship.isBlocking());
			targetUser.setRelationChecked(true);
			boolean isBlocking = microBlog.existsBlock(targetUser.getId());
			targetUser.setBlocking(isBlocking);
			relationship.setBlocking(isBlocking);
		} catch (LibException e) {
			if (Constants.DEBUG) Log.e(TAG, "Task", e);
		}

		return relationship;
	}

	@Override
	protected void onPostExecute(Relationship result) {
		if (result != null) {
			if (context instanceof ProfileActivity) {
				((ProfileActivity)context).setRelationship(result);
			} else  {
				updateView(view, targetUser);
			}
		}
	}

	public static void updateView(View view, final User user) {
		if (view == null || user == null) {
			return;
		}

		Button btn = null;
		ImageView ivFriendship = null;
		if (view.getId() == R.id.llSocialGraphItem) {
			btn = (Button)view.findViewById(R.id.btnSocialGraphOperate);
			ivFriendship = (ImageView)view.findViewById(R.id.ivFriendship);
		} else if (view instanceof Button) {
			btn = (Button)view;
		}

		if (btn == null) {
			return;
		}
		View.OnClickListener btnClickListener;
		if (user.isFollowing()) {
			btn.setText(R.string.btn_personal_unfollow);
			btn.setTextAppearance(btn.getContext(), R.style.btn_action_negative);
			ThemeUtil.setBtnActionNegative(btn);
			btn.setEnabled(true);
			btnClickListener = new ProfileFollowClickListener(user);
			if (ivFriendship != null && user.isFollowedBy()) {
				ivFriendship.setVisibility(View.VISIBLE);
			}
		} else if (user.isBlocking()) {
			btn.setText(R.string.btn_personal_unblock);
			btn.setTextAppearance(btn.getContext(), R.style.btn_action_positive);
			ThemeUtil.setBtnActionPositive(btn);
			btn.setEnabled(true);
			btnClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new ToggleBlockTask(v, user).execute();
				}
			};
		} else {
			btn.setText(R.string.btn_personal_follow);
			btn.setTextAppearance(btn.getContext(), R.style.btn_action_positive);
			ThemeUtil.setBtnActionPositive(btn);
			btn.setEnabled(true);
			btnClickListener = new ProfileFollowClickListener(user);
		}
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(btnClickListener);
	}
}
