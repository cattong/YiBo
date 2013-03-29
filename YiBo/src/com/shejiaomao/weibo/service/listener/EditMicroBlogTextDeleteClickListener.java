package com.shejiaomao.weibo.service.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.shejiaomao.maobo.R;

public class EditMicroBlogTextDeleteClickListener implements OnClickListener {
    private Context context;
    
	public EditMicroBlogTextDeleteClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onClick(View v) {
		final EditText etText = (EditText)((Activity)context).findViewById(R.id.etText);
        if (etText == null) {
        	return;
        }
       
		new AlertDialog.Builder(context)
			.setTitle(R.string.title_dialog_alert)
			.setMessage(R.string.msg_delete_text)
			.setPositiveButton(R.string.btn_confirm,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etText.setText("");
					}
			})
			.setNegativeButton(R.string.btn_cancel,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
			})
			.show();
	}

}
