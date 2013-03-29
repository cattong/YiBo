package com.shejiaomao.weibo.service.listener;

import java.io.File;
import java.util.Date;

import com.cattong.commons.util.DateTimeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.activity.ProfileEditActivity;
import com.shejiaomao.weibo.common.Constants;

public class EditMicroBlogCameraClickListener implements OnClickListener {

    public static final String IMG_TYPE = "image/*";
	public static final String FILE_PREX    = "sheJiaoMao_";
	public static final String FILE_SUBFIX_FORMAT = "yyyy-MM-dd-HHmmss";
	public static final String DEFAULT_IMAGE_STORE_DIR = "/sdcard/DCIM/100MEDIA/";
	static {
		File temp = new File(DEFAULT_IMAGE_STORE_DIR);
		if (!temp.exists()) {
			temp.mkdirs();
		}
	}

	private Context context;
	public EditMicroBlogCameraClickListener(Context context) {
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		String[] selectItems = {
			v.getContext().getString(R.string.label_edit_status_take_photo),
			v.getContext().getString(R.string.label_edit_status_select_photo)
		};
		new AlertDialog.Builder(context)
        .setTitle(R.string.title_dialog_photo)
        .setItems(selectItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                	jumpToTakePicture(context);
                } else if (which == 1) {
                	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            		intent.setType(IMG_TYPE);
            		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_IMG_SELECTOR);
                }
            }
        })
        .create()
        .show();
	}

	public static void jumpToTakePicture(Context context) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String name = DEFAULT_IMAGE_STORE_DIR + FILE_PREX +
		    DateTimeUtil.getFormatString(new Date(), FILE_SUBFIX_FORMAT) + ".jpg";
		File out = new File(name);
		Uri uri  = Uri.fromFile(out);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

		if (context instanceof EditMicroBlogActivity) {
			EditMicroBlogActivity editMicroBlogActivity = (EditMicroBlogActivity)context;
			editMicroBlogActivity.setImagePath(out.getAbsolutePath());
		} else if (context instanceof ProfileEditActivity) {
			ProfileEditActivity profileEditActivity = (ProfileEditActivity)context;
			profileEditActivity.setImagePath(out.getAbsolutePath());
		}

		((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_CAMERA);
	}
}
