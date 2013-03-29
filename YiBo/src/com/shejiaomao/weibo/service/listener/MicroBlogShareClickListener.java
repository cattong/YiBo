package com.shejiaomao.weibo.service.listener;

import java.io.File;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Status;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.EntityUtil;

public class MicroBlogShareClickListener implements OnClickListener {
	private Context context;
	private Status status;
	public MicroBlogShareClickListener(Context context) {
		this.context = context;
	}

	public MicroBlogShareClickListener(Context context, Status status) {
		this.context = context;
		this.status = status;
	}

	@Override
	public void onClick(View v) {
		if (status == null || status.getStatusId() == null) {
			return;
		}

		Intent intent = new Intent(Intent.ACTION_SEND);
		
		if (EntityUtil.hasPicture(status)) {
			intent.setType("image/*");
			
			String imagePath = EntityUtil.getMaxLocalCachedPicture(status);
			if (StringUtil.isNotEmpty(imagePath)) {
				Uri uri = Uri.fromFile(new File(imagePath));
			    intent.putExtra(Intent.EXTRA_STREAM, uri);
			} else {
				intent.setType("text/plain");
				Toast.makeText(context, context.getString(R.string.msg_blog_share_picture), Toast.LENGTH_LONG).show();
			}
		} else {
			intent.setType("text/plain");
		}
		
		ClipboardManager clip = (ClipboardManager)context
            .getSystemService(Context.CLIPBOARD_SERVICE);
		String statusText = extraStatus(context, status);
		clip.setText(statusText);
		
		intent.putExtra(Intent.EXTRA_TEXT, statusText);				
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.msg_extra_subject));
		context.startActivity(intent);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

    public String extraStatus(Context context, Status status) {
		String statusText = "@" + status.getUser().getScreenName() +
	        ": " + status.getText();
		String middleUrl = status.getMiddlePictureUrl();
	    Status retweet = status.getRetweetedStatus();
	    if (retweet != null) {
		    String retweetText = "@" + retweet.getUser().getScreenName() +
		        ": " + retweet.getText();
		    statusText = context.getString(R.string.msg_extra_rich_text, statusText, retweetText);
		    middleUrl = retweet.getMiddlePictureUrl();
	    }
	    if (middleUrl != null) {
	    	statusText += context.getString(R.string.msg_extra_image, middleUrl);
	    }
	    
	    return statusText;
    }
}
