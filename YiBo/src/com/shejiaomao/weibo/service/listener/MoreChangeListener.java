package com.shejiaomao.weibo.service.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.ServiceProvider;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.activity.GroupActivity;
import com.shejiaomao.weibo.activity.HomePageActivity;
import com.shejiaomao.weibo.activity.SettingActivity;
import com.shejiaomao.weibo.activity.ThemeActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.task.SocialGraphTask;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.weibo.widget.ValueSetEvent;
import com.shejiaomao.weibo.widget.ViewChangeEvent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MoreChangeListener implements PropertyChangeListener {
	private Activity context;
	private SheJiaoMaoApplication sheJiaoMao;

	//private WeakReference<View> refView;
	public MoreChangeListener(Context context) {
		this.context = (Activity)context;
		sheJiaoMao = (SheJiaoMaoApplication) context.getApplicationContext();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
        if (event instanceof ViewChangeEvent) {
        	viewChange(event);
        } else if (event instanceof ValueSetEvent) {
        	valueSet(event);
        }
	}

	private void viewChange(PropertyChangeEvent event) {
		if (!(event instanceof ViewChangeEvent 
			&& event.getNewValue().equals(Skeleton.TYPE_MORE))) {
			return;
		}

		ViewChangeEvent changeEvent = (ViewChangeEvent)event;
		ViewGroup viewGroup = (ViewGroup)changeEvent.getView();

		viewGroup.removeAllViews();
		View view = getContentView();
		viewGroup.addView(view);

        updateHeader(changeEvent);
	}

	private void valueSet(PropertyChangeEvent event) {
		ValueSetEvent setEvent = (ValueSetEvent)event;

		switch (setEvent.getAction()) {
		case ACTION_INIT_ADAPTER:
			break;
		case ACTION_RECLAIM_MEMORY:
			//refView = null;
			break;
		default:
			break;
		}
	}

	private View getContentView() {
		View contentView = null;
//		if (refView != null) {
//			contentView = refView.get();
//			if (Constants.DEBUG && contentView == null) {
//				Log.v("AppChangeListener", "View recycle");
//			}
//		}
//		if (contentView != null) {
//			return contentView;
//		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.home_page_content_more, null);
		initTheme(contentView);
		
		LinearLayout llSetting  = (LinearLayout)contentView.findViewById(R.id.llSetting);
		llSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Activity activity = (Activity)v.getContext();
				intent.setClass(activity, SettingActivity.class);
				activity.startActivityForResult(intent, Constants.REQUEST_CODE_SETTINGS);
			}
		});

		LinearLayout llAccounts  = (LinearLayout)contentView.findViewById(R.id.llAccounts);
		llAccounts.setOnClickListener(new AccountManageClickListener());

		LinearLayout llGroups  = (LinearLayout)contentView.findViewById(R.id.llGroups);
		llGroups.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("SOCIAL_GRAPH_TYPE", SocialGraphTask.TYPE_FRIENDS);
				intent.putExtra("USER", sheJiaoMao.getCurrentAccount().getUser());
				intent.putExtra("TAB_TYPE", GroupActivity.TAB_TYPE_GROUP);
				intent.setClass(v.getContext(), GroupActivity.class);
				v.getContext().startActivity(intent);
			}
		});

		LinearLayout llThemes  = (LinearLayout)contentView.findViewById(R.id.llThemes);
		llThemes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), ThemeActivity.class);
				v.getContext().startActivity(intent);
			}
		});
		
		LinearLayout llOffical  = (LinearLayout)contentView.findViewById(R.id.llOffical);
		llOffical.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalAccount account = sheJiaoMao.getCurrentAccount();
				String uri = null;
				if (account.getServiceProvider() == ServiceProvider.Fanfou) {
					//饭否不支持通过昵称获取用户信息，这边直接设置为官方微博的id（~0jFVfHMEtG4）
					uri = Constants.URI_PERSONAL_INFO.toString() + "@"
						+ com.cattong.commons.Constants.FANFOU_OFFICAL_USER_ID;
				} else {
					String officalName = account.getServiceProvider().getOfficalName();
					uri = Constants.URI_PERSONAL_INFO.toString() + "@" + officalName;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				v.getContext().startActivity(intent);
			}
		});

		LinearLayout llFeedback  = (LinearLayout)contentView.findViewById(R.id.llFeedback);
		llFeedback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalAccount account = sheJiaoMao.getCurrentAccount();
				Context context = v.getContext();

				String feedbackFormat = context.getString(R.string.hint_feedback_identify);
				String officeName =  account.getServiceProvider().getOfficalName();
				String model = CompatibilityUtil.getModel();
				String release = CompatibilityUtil.getRelease();
				String netOperator = GlobalVars.NET_OPERATOR == null ? "" : GlobalVars.NET_OPERATOR.toString();
				String net = GlobalVars.NET_TYPE == null ? "" : GlobalVars.NET_TYPE.toString();
				String handset = model + " " + release + " " + netOperator + " " + net;

				String versionName = "";
				try {
					String packageName = context.getPackageName();
					PackageInfo packageInfo = context.getPackageManager().
					    getPackageInfo(packageName, 0);
					versionName = packageInfo.versionName + "_" + packageInfo.versionCode;
				} catch (NameNotFoundException e) {
					versionName = context.getString(R.string.defaultVersion);
				}

				String feedbackIdentify = String.format(
					feedbackFormat, versionName, handset, officeName);

				Intent intent = new Intent();
				intent.putExtra("TYPE", Constants.EDIT_TYPE_FEEDBACK);
				intent.putExtra("APPEND_TEXT", feedbackIdentify);
				intent.setClass(context, EditMicroBlogActivity.class);
				context.startActivity(intent);
			}
		});

		LinearLayout llCheckUpdate  = (LinearLayout)contentView.findViewById(R.id.llCheckUpdate);
		llCheckUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context vContext = v.getContext();
				final ProgressDialog progressDialog = ProgressDialog.show(
					vContext, null, vContext.getString(R.string.msg_dialog_check_update)
				);
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			    	public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			    		progressDialog.dismiss();
			    		switch(updateStatus) {
			    		case 0: break;
			    		case 1: Toast.makeText(vContext, R.string.msg_is_lastest_version, Toast.LENGTH_SHORT).show(); break;
			    		case 2: Toast.makeText(vContext, R.string.msg_check_update_time_out, Toast.LENGTH_SHORT).show(); break;
			    		case 3: break;
			    		}
			    	}
			    });
				UmengUpdateAgent.update(vContext);
			}
		});

		LinearLayout llQuit  = (LinearLayout)contentView.findViewById(R.id.llQuit);
		llQuit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context context = v.getContext();
				new AlertDialog.Builder(v.getContext())
					.setTitle(R.string.title_dialog_alert)
					.setMessage(R.string.msg_quit)
					.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HomePageActivity homePageActivity = (HomePageActivity)context;
							homePageActivity.exitApp();
						}
					})
					.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					})
					.create()
					.show();
			}
		});

		//refView = new WeakReference<View>(contentView);
		return contentView;
	}

	private void updateHeader(ViewChangeEvent changeEvent) {
	    View llHeaderBase = ((Activity)context).findViewById(R.id.llHeaderBase);
	    llHeaderBase.setVisibility(View.VISIBLE);
	    View llHeaderMessage = ((Activity)context).findViewById(R.id.llHeaderMessage);
	    llHeaderMessage.setVisibility(View.GONE);

		TextView tvTitle = (TextView) context.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_tab_more);
		ImageButton ibProfileImage = (ImageButton) context.findViewById(R.id.ibProfileImage);
		ibProfileImage.setVisibility(View.VISIBLE);
		ImageButton ibGroup = (ImageButton) context.findViewById(R.id.ibGroup);
		ibGroup.setVisibility(View.GONE);
		ImageButton ibEdit = (ImageButton) context.findViewById(R.id.ibEdit);
		ibEdit.setVisibility(View.VISIBLE);
		ibEdit.setOnClickListener(new HomePageEditStatusClickListener(context));
	}

	private void initTheme(View contentView) {
		if (contentView == null) {
			return;
		}
        ThemeUtil.setContentBackground(contentView);
        Theme theme = ThemeUtil.createTheme(context);
        
        LinearLayout llMoreManage  = (LinearLayout)contentView.findViewById(R.id.llMoreManage);
        LinearLayout llSetting  = (LinearLayout)contentView.findViewById(R.id.llSetting);
        ImageView ivSetting = (ImageView)contentView.findViewById(R.id.ivSetting);
        TextView tvSetting = (TextView)contentView.findViewById(R.id.tvSetting);
        ImageView ivSettingMore = (ImageView)contentView.findViewById(R.id.ivSettingMore);
        LinearLayout llAccounts  = (LinearLayout)contentView.findViewById(R.id.llAccounts);
        ImageView ivAccounts = (ImageView)contentView.findViewById(R.id.ivAccounts);
        TextView tvAccounts = (TextView)contentView.findViewById(R.id.tvAccounts);
        ImageView ivAccountsMore = (ImageView)contentView.findViewById(R.id.ivAccountsMore);
        LinearLayout llGroups  = (LinearLayout)contentView.findViewById(R.id.llGroups);
        ImageView ivGroups = (ImageView)contentView.findViewById(R.id.ivGroups);
        TextView tvGroups = (TextView)contentView.findViewById(R.id.tvGroups);
        ImageView ivGroupsMore = (ImageView)contentView.findViewById(R.id.ivGroupsMore);
        LinearLayout llThemes  = (LinearLayout)contentView.findViewById(R.id.llThemes);
        ImageView ivThemes = (ImageView)contentView.findViewById(R.id.ivThemes);
        TextView tvThemes = (TextView)contentView.findViewById(R.id.tvThemes);
        ImageView ivThemesMore = (ImageView)contentView.findViewById(R.id.ivThemesMore);
        
        llMoreManage.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
        int padding1 = theme.dip2px(1);
        llMoreManage.setPadding(padding1, padding1, padding1, padding1);
        llSetting.setBackgroundDrawable(theme.getDrawable("selector_frame_item_top_corner"));
        int padding8 = theme.dip2px(8);
        llSetting.setPadding(padding8, padding8, padding8, padding8);
        ivSetting.setImageDrawable(theme.getDrawable("icon_more_setting"));
        int content = theme.getColor("content");
        tvSetting.setTextColor(content);
        ivSettingMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llAccounts.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
        llAccounts.setPadding(padding8, padding8, padding8, padding8);
        ivAccounts.setImageDrawable(theme.getDrawable("icon_more_accounts"));
        tvAccounts.setTextColor(content);
        ivAccountsMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llGroups.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
        llGroups.setPadding(padding8, padding8, padding8, padding8);
        ivGroups.setImageDrawable(theme.getDrawable("icon_more_groups"));
        tvGroups.setTextColor(content);
        ivGroupsMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llThemes.setBackgroundDrawable(theme.getDrawable("selector_frame_item_bottom_corner"));
        llThemes.setPadding(padding8, padding8, padding8, padding8);
        ivThemes.setImageDrawable(theme.getDrawable("icon_more_themes"));
        tvThemes.setTextColor(content);
        ivThemesMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        
        LinearLayout llMoreInfo  = (LinearLayout)contentView.findViewById(R.id.llMoreInfo);
		LinearLayout llOffical  = (LinearLayout)contentView.findViewById(R.id.llOffical);
        ImageView ivOffical = (ImageView)contentView.findViewById(R.id.ivOffical);
        TextView tvOffical = (TextView)contentView.findViewById(R.id.tvOffical);
        ImageView ivOfficalMore = (ImageView)contentView.findViewById(R.id.ivOfficalMore);
		LinearLayout llFeedback  = (LinearLayout)contentView.findViewById(R.id.llFeedback);
        ImageView ivFeedback = (ImageView)contentView.findViewById(R.id.ivFeedback);
        TextView tvFeedback = (TextView)contentView.findViewById(R.id.tvFeedback);
        ImageView ivFeedbackMore = (ImageView)contentView.findViewById(R.id.ivFeedbackMore);
		LinearLayout llCheckUpdate  = (LinearLayout)contentView.findViewById(R.id.llCheckUpdate);
        ImageView ivCheckUpdate = (ImageView)contentView.findViewById(R.id.ivCheckUpdate);
        TextView tvCheckUpdate = (TextView)contentView.findViewById(R.id.tvCheckUpdate);
        ImageView ivCheckUpdateMore = (ImageView)contentView.findViewById(R.id.ivCheckUpdateMore);
		LinearLayout llQuit  = (LinearLayout)contentView.findViewById(R.id.llQuit);
        ImageView ivQuit = (ImageView)contentView.findViewById(R.id.ivQuit);
        TextView tvQuit = (TextView)contentView.findViewById(R.id.tvQuit);
        ImageView ivQuitMore = (ImageView)contentView.findViewById(R.id.ivQuitMore);
        
        llMoreInfo.setBackgroundDrawable(theme.getDrawable("bg_frame_normal"));
        llMoreInfo.setPadding(padding1, padding1, padding1, padding1);
        llOffical.setBackgroundDrawable(theme.getDrawable("selector_frame_item_top_corner"));
        llOffical.setPadding(padding8, padding8, padding8, padding8);
        ivOffical.setImageDrawable(theme.getDrawable("icon_more_offical"));
        tvOffical.setTextColor(content);
        ivOfficalMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llFeedback.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
        llFeedback.setPadding(padding8, padding8, padding8, padding8);
        ivFeedback.setImageDrawable(theme.getDrawable("icon_more_feedback"));
        tvFeedback.setTextColor(content);
        ivFeedbackMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llCheckUpdate.setBackgroundDrawable(theme.getDrawable("selector_frame_item_no_corner"));
        llCheckUpdate.setPadding(padding8, padding8, padding8, padding8);
        ivCheckUpdate.setImageDrawable(theme.getDrawable("icon_more_update"));
        tvCheckUpdate.setTextColor(content);
        ivCheckUpdateMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        llQuit.setBackgroundDrawable(theme.getDrawable("selector_frame_item_bottom_corner"));
        llQuit.setPadding(padding8, padding8, padding8, padding8);
        ivQuit.setImageDrawable(theme.getDrawable("icon_more_quit"));
        tvQuit.setTextColor(content);
        ivQuitMore.setBackgroundDrawable(theme.getDrawable("icon_more_detail"));
        
        ImageView ivLineSeperator_1 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_1);
        ImageView ivLineSeperator_2 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_2);
        ImageView ivLineSeperator_3 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_3);
        ImageView ivLineSeperator_4 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_4);
        ImageView ivLineSeperator_5 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_5);
        ImageView ivLineSeperator_6 = (ImageView)contentView.findViewById(R.id.ivLineSeperator_6);
        ivLineSeperator_1.setBackgroundDrawable(theme.getDrawable("line_seperator"));
        ivLineSeperator_2.setBackgroundDrawable(theme.getDrawable("line_seperator"));
        ivLineSeperator_3.setBackgroundDrawable(theme.getDrawable("line_seperator"));
        ivLineSeperator_4.setBackgroundDrawable(theme.getDrawable("line_seperator"));
        ivLineSeperator_5.setBackgroundDrawable(theme.getDrawable("line_seperator"));
        ivLineSeperator_6.setBackgroundDrawable(theme.getDrawable("line_seperator"));
	}
}
