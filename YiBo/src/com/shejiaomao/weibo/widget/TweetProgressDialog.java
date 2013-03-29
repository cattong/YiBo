package com.shejiaomao.weibo.widget;

import java.util.List;

import com.cattong.commons.util.ListUtil;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.TweetProgressListAdapter;

public class TweetProgressDialog {
    private Context context;
	private View parent;
	private PopupWindow progressWindow;
	private ListView lvAccount;
	private TweetProgressListAdapter listAdapter;

	private TextView tvDialogTitle;
	private Button btnPositive;
	private Button btnNegative;

    public enum State {
		Waiting(0),
		Loading(1),
		Success(2),
		Failed(3);

		private int state;
		private State(int state) {
			this.state = state;
		}
		public int getState() {
			return state;
		}
	}
	public TweetProgressDialog(Context context, View parent) {
		this.context = context;
		this.parent = parent;
		initComponents();
	}

	private void initComponents() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.widget_dialog_tweet_progress, null);
        tvDialogTitle = (TextView)contentView.findViewById(R.id.tvDialogTitle);
        lvAccount = (ListView)contentView.findViewById(R.id.lvAccount);
	    btnPositive = (Button)contentView.findViewById(R.id.btnPositive);
	    btnNegative = (Button)contentView.findViewById(R.id.btnNegative);
	    btnPositive.setEnabled(false);

	    listAdapter = new TweetProgressListAdapter(context);
	    lvAccount.setAdapter(listAdapter);

	    progressWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    progressWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(100, 158, 158, 158)));
	    progressWindow.setFocusable(true);
	    progressWindow.setOutsideTouchable(true);
	    progressWindow.setAnimationStyle(android.R.anim.fade_in);
	}

	public void show() {
		try {
		     progressWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		} catch(Exception e) {}
	}

	public void dismiss() {
		try {
		    progressWindow.dismiss();
		} catch(Exception e) {}
	}

	public boolean isShowing() {
		boolean isShowing = false;
		try {
			isShowing = progressWindow.isShowing();
		} catch(Exception e) {}

		return isShowing;
	}

	public void setListUpdateAccount(List<LocalAccount> listUpdateAccount) {
		if (ListUtil.isEmpty(listUpdateAccount)) {
			return;
		}
		listAdapter.setListUpdateAccount(listUpdateAccount);
	}

	public boolean updateState(LocalAccount account, State state) {
		if (account == null || state == null) {
			return false;
		}
		return listAdapter.updateState(account, state);
	}

	public void setPositiveClickListener(OnClickListener positiveClickListener) {
		if (positiveClickListener == null) {
			btnPositive.setEnabled(false);
		} else {
		    btnPositive.setEnabled(true);
		}
		btnPositive.setOnClickListener(positiveClickListener);
	}

	public void setPositiveBtnText(int resId) {
		btnPositive.setText(resId);
	}

	public void setNegativeClickListener(OnClickListener negativeClickListener) {
		btnNegative.setOnClickListener(negativeClickListener);
	}
	
	public void setDialogTitle(int resId) {
		tvDialogTitle.setText(resId);
	}
}
