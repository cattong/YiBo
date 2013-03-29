package com.shejiaomao.weibo.service.task;

import com.shejiaomao.maobo.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cattong.commons.LibException;
import com.cattong.commons.Logger;
import com.cattong.entity.BaseUser;
import com.cattong.entity.Relationship;
import com.cattong.entity.User;
import com.cattong.weibo.Weibo;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.ProfileFollowClickListener;

public class RelationshipCheckTask extends AsyncTask<Void, Void, Relationship> {
	private static final String TAG = "RelationshipCheckTask";

	private Context context;
	private User targetUser;
	
	private View view;

	public RelationshipCheckTask(Context context, User targetUser) {
		this.context = context;
		this.targetUser = targetUser;
	}

	public RelationshipCheckTask(View view, User user) {
		this(view.getContext(), user);
		this.view = view;
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

		LocalAccount account = ((SheJiaoMaoApplication)context.getApplicationContext()).getCurrentAccount();
		if (account == null
			|| targetUser.getUserId().equals(account.getUser().getUserId())) {
			return null;
		}

		Weibo microBlog = GlobalVars.getMicroBlog(account);
		if (microBlog == null) {
			return null;
		}

		Relationship relationship = null;
		try {
			BaseUser sourceUser = account.getUser();
			relationship = microBlog.showRelationship(
				sourceUser.getUserId(),	targetUser.getUserId());
		} catch (LibException e) {
			if (Logger.isDebug()) Log.e(TAG, "Task", e);
		}

		return relationship;
	}

	@Override
	protected void onPostExecute(Relationship result) {
		if (result != null) {
			targetUser.setRelationship(result);
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
		Relationship relationship = user.getRelationship();
		if (relationship != null && relationship.isSourceFollowingTarget()) {
			btn.setText(R.string.btn_personal_unfollow);
			btn.setTextAppearance(btn.getContext(), R.style.btn_action_negative);
			ThemeUtil.setBtnActionNegative(btn);
			btn.setEnabled(true);
			btnClickListener = new ProfileFollowClickListener(user);
			if (ivFriendship != null && relationship.isSourceFollowedByTarget()) {
				ivFriendship.setVisibility(View.VISIBLE);
			}
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
