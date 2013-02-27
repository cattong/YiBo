package net.dev123.yibo.service.adapter;

import net.dev123.yibo.R;
import net.dev123.yibo.common.Constants;
import net.dev123.yibo.common.GlobalResource;
import net.dev123.yibo.common.theme.Theme;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.service.listener.ThemeOperateClickListener;
import net.dev123.yibo.service.task.ImageLoad4ThumbnailTask;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ThemeHolder {
	private static final String TAG = "ThemeHolder";
	private Context context;
	ImageView ivThemePicture;
	TextView tvThemeName;
	TextView tvFileSize;
	Button btnThemeOperate;

	ThemeOperateClickListener operateClickListener;
	
	ImageLoad4ThumbnailTask themePictureTask;
	public ThemeHolder(View convertView) {
		if (convertView == null) {
			throw new IllegalArgumentException("convertView is null!");
		}
		context = convertView.getContext();
		ivThemePicture = (ImageView) convertView.findViewById(R.id.ivThemePicture);
		tvThemeName = (TextView) convertView.findViewById(R.id.tvThemeName);
		tvFileSize = (TextView) convertView.findViewById(R.id.tvFileSize);
		btnThemeOperate = (Button) convertView.findViewById(R.id.btnThemeOperate);
		
		Theme theme = ThemeUtil.createTheme(context);
		tvThemeName.setTextColor(theme.getColor("content"));
		tvFileSize.setTextColor(theme.getColor("remark"));
		ThemeUtil.setBtnActionNegative(btnThemeOperate);
		
		operateClickListener = new ThemeOperateClickListener();
		btnThemeOperate.setOnClickListener(operateClickListener);
	}

	public void reset() {
		if (ivThemePicture != null) {
			ivThemePicture.setImageDrawable(GlobalResource.getDefaultThumbnail(context));
		}

		if (tvFileSize != null) {
			tvFileSize.setText("");
		}

		if (btnThemeOperate != null) {
			btnThemeOperate.setVisibility(View.VISIBLE);
			btnThemeOperate.setText(R.string.btn_loading);
			btnThemeOperate.setTextAppearance(btnThemeOperate.getContext(), R.style.btn_action_negative);
			ThemeUtil.setBtnActionNegative(btnThemeOperate);
			btnThemeOperate.setEnabled(false);
		}

		themePictureTask = null;
	}

	public void recycle() {
		if (themePictureTask != null) {
			themePictureTask.cancel(true);
		}
		if (Constants.DEBUG) Log.d(TAG, "user convertView recycle");
	}
}
