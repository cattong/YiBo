package com.shejiaomao.weibo.common.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.BaseActivity;

public class ThemeUtil {

	public static Theme createTheme(Context context) {
		Theme theme = null;
		if (context instanceof BaseActivity) {
			theme = ((BaseActivity)context).getSkinTheme();
		} else {
			theme = new Theme(context);
		}
		return theme;
	}
	
	public static void setRootBackground(View root) {
		if (root == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(root.getContext());
		View panel = root.findViewById(R.id.llRoot);
		panel.setBackgroundDrawable(theme.getDrawable("shape_panel_background"));
	}
	
	public static void setSecondaryHeader(View headerView) {
		if (headerView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerView.getContext());
		LinearLayout llHeaderBase = (LinearLayout)headerView.findViewById(R.id.llHeaderBase);
		Button btnBack = (Button)headerView.findViewById(R.id.btnBack);
		Button btnOperate = (Button) headerView.findViewById(R.id.btnOperate);
		TextView tvTitle = (TextView) headerView.findViewById(R.id.tvTitle);
		
		llHeaderBase.setBackgroundDrawable(theme.getDrawable("bg_header"));
		llHeaderBase.setGravity(Gravity.CENTER);
		int padding8 = theme.dip2px(8);
		llHeaderBase.setPadding(padding8, 0, padding8, 0);
		btnBack.setBackgroundDrawable(theme.getDrawable("selector_btn_action_back"));
		btnBack.setTextColor(theme.getColorStateList("selector_btn_header_action"));
		tvTitle.setTextColor(theme.getColorStateList("selector_header_title"));
		btnOperate.setBackgroundDrawable(theme.getDrawable("selector_btn_action_additional"));
		int padding12 = theme.dip2px(12);
		btnOperate.setPadding(padding12, 0, padding12, 0);
		btnOperate.setTextColor(theme.getColor("selector_btn_header_action"));
	}
	
	public static void setSecondaryImageHeader(View headerView) {
		if (headerView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerView.getContext());
		LinearLayout llHeaderBase = (LinearLayout)headerView.findViewById(R.id.llHeaderBase);
		Button btnBack = (Button)headerView.findViewById(R.id.btnBack);
		Button btnOperate = (Button) headerView.findViewById(R.id.btnOperate);
		TextView tvTitle = (TextView) headerView.findViewById(R.id.tvTitle);
		
		llHeaderBase.setBackgroundDrawable(theme.getDrawable("bg_header_image"));
		llHeaderBase.setGravity(Gravity.CENTER);
		int padding8 = theme.dip2px(8);
		llHeaderBase.setPadding(padding8, 0, padding8, 0);
		btnBack.setBackgroundDrawable(theme.getDrawable("selector_btn_action_back"));
		btnBack.setTextColor(theme.getColorStateList("selector_btn_header_action"));
		tvTitle.setTextColor(theme.getColorStateList("selector_header_title"));
		btnOperate.setBackgroundDrawable(theme.getDrawable("selector_btn_action_additional"));
		int padding12 = theme.dip2px(12); 
		btnOperate.setPadding(padding12, 0, padding12, 0);
		btnOperate.setTextColor(theme.getColor("selector_btn_header_action"));
	}
	
	public static void setSecondaryMicroBlogHeader(View headerView) {
		if (headerView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerView.getContext());
		LinearLayout llHeaderBase = (LinearLayout)headerView.findViewById(R.id.llHeaderBase);
		Button btnBack = (Button)headerView.findViewById(R.id.btnBack);
		Button btnPrevious = (Button)headerView.findViewById(R.id.btnPrevious);
		Button btnNext = (Button)headerView.findViewById(R.id.btnNext);
		Button btnOperate = (Button) headerView.findViewById(R.id.btnOperate);
		TextView tvTitle = (TextView) headerView.findViewById(R.id.tvTitle);
		
		llHeaderBase.setBackgroundDrawable(theme.getDrawable("bg_header"));
		llHeaderBase.setGravity(Gravity.CENTER);
		int padding8 = theme.dip2px(8);
		llHeaderBase.setPadding(padding8, 0, padding8, 0);
		btnBack.setBackgroundDrawable(theme.getDrawable("selector_btn_action_back"));
		btnBack.setTextColor(theme.getColorStateList("selector_btn_header_action"));
		tvTitle.setTextColor(theme.getColorStateList("selector_header_title"));
		btnPrevious.setBackgroundDrawable(theme.getDrawable("selector_btn_action_previous"));
		btnNext.setBackgroundDrawable(theme.getDrawable("selector_btn_action_next"));
		btnOperate.setBackgroundDrawable(theme.getDrawable("selector_btn_action_additional"));
		int padding12 = theme.dip2px(12);
		btnOperate.setPadding(padding12, 0, padding12, 0);
		btnOperate.setTextColor(theme.getColor("selector_btn_header_action"));
	}
	
	public static void setHeaderCornerTab(View headerCornerTab) {
		if (headerCornerTab == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerCornerTab.getContext());
		headerCornerTab.setBackgroundDrawable(theme.getDrawable("bg_header_corner_tab"));
		int padding8 = theme.dip2px(8);
		headerCornerTab.setPadding(padding8, padding8, padding8, padding8);
	}
	
	public static void setHeaderToggleTab(View headerCornerTab) {
		if (headerCornerTab == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerCornerTab.getContext());
		Button btnTabLeft = (Button)headerCornerTab.findViewById(R.id.btnTabLeft);
		Button btnTabRight = (Button)headerCornerTab.findViewById(R.id.btnTabRight);
		headerCornerTab.setBackgroundDrawable(theme.getDrawable("bg_header_corner_tab"));
		int padding8 = theme.dip2px(8);
		headerCornerTab.setPadding(padding8, padding8, padding8, padding8);
		btnTabLeft.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_left"));
		int padding4 = theme.dip2px(4);
		btnTabLeft.setPadding(padding4, padding4, padding4, padding4);
		ColorStateList selectorBtnTab = theme.getColorStateList("selector_btn_tab");
		btnTabLeft.setTextColor(selectorBtnTab);
		btnTabRight.setBackgroundDrawable(theme.getDrawable("selector_tab_toggle_right"));
		btnTabRight.setPadding(padding4, padding4, padding4, padding4);
		btnTabRight.setTextColor(selectorBtnTab);
	}
	
	public static void setHeaderUserSelector(View headerCornerTab) {
		if (headerCornerTab == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(headerCornerTab.getContext());
		EditText etFilterName = (EditText)headerCornerTab.findViewById(R.id.etFilterName);
		Button btnSearch = (Button)headerCornerTab.findViewById(R.id.btnSearch);
		headerCornerTab.setBackgroundDrawable(theme.getDrawable("bg_header_corner_tab"));
		int padding8 = theme.dip2px(8);
		headerCornerTab.setPadding(padding8, padding8, padding8, padding8);
		etFilterName.setBackgroundDrawable(theme.getDrawable("bg_input_frame_left_half"));
		btnSearch.setBackgroundDrawable(theme.getDrawable("selector_btn_search"));
	}
	
	public static void setContentBackground(View view) {
		if (view == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(view.getContext());
		view.setBackgroundDrawable(theme.getDrawable("bg_header_corner_base"));
	}
	
	public static void setListViewStyle(ListView listView) {
		if (listView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(listView.getContext());
		listView.setDivider(theme.getDrawable("line_seperator"));
		listView.setSelector(theme.getDrawableByColor("selector_list_item"));
	}
	
	public static void setListViewGap(View gapView) {
		if (gapView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(gapView.getContext());
		gapView.setBackgroundDrawable(theme.getDrawable("selector_bg_gap"));
		TextView tvLoading = (TextView)gapView.findViewById(R.id.tvLoading);
		tvLoading.setTextColor(theme.getColor("content"));
	}
	
	public static void setListViewMore(View footerView) {
		if (footerView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(footerView.getContext());
		footerView.setBackgroundColor(theme.getColor("list_item_more"));
		TextView tvFooter = (TextView)footerView.findViewById(R.id.tvFooter);
		TextView tvLoading = (TextView)footerView.findViewById(R.id.tvLoading);
		int contentColor = theme.getColor("content");
		tvFooter.setTextColor(contentColor);
		tvLoading.setTextColor(contentColor);
	}
	
	public static void setListViewLoading(View footerView) {
		if (footerView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(footerView.getContext());
		footerView.setBackgroundColor(theme.getColor("list_item_more"));
		TextView tvLoading = (TextView)footerView.findViewById(R.id.tvLoading);
		int contentColor = theme.getColor("content");
		tvLoading.setTextColor(contentColor);
	}
	
	public static void setBtnActionPositive(Button button) {
		if (button == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(button.getContext());
		button.setTextColor(theme.getColorStateList("selector_btn_action_positive"));
		button.setBackgroundDrawable(theme.getDrawable("selector_btn_action_positive"));
	}
	
	public static void setBtnActionNegative(Button button) {
		if (button == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(button.getContext());
		button.setTextColor(theme.getColorStateList("selector_btn_action_negative"));
		button.setBackgroundDrawable(theme.getDrawable("selector_btn_action_negative"));
	}
	
	public static void setEditText(EditText etText) {
		if (etText == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(etText.getContext());
		etText.setBackgroundDrawable(theme.getDrawable("bg_input_frame_normal"));
		etText.setTextColor(theme.getColor("content"));
	}
	
	public static void setFooterAction(View footerActionView) {
		if (footerActionView == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(footerActionView.getContext());
		footerActionView.setBackgroundDrawable(theme.getDrawable("bg_footer_action"));
		int padding8 = theme.dip2px(8);
		footerActionView.setPadding(padding8, padding8, padding8, padding8);
	}
	
	public static void setHeaderProfile(LinearLayout llProfileHeader) {
		if (llProfileHeader == null) {
			return;
		}
		Theme theme = ThemeUtil.createTheme(llProfileHeader.getContext());
		llProfileHeader.setBackgroundDrawable(theme.getDrawable("selector_bg_profile_header"));
		int padding8 = theme.dip2px(8);
		llProfileHeader.setPadding(padding8, padding8, padding8, padding8);
		llProfileHeader.setGravity(Gravity.CENTER);
	}
}
