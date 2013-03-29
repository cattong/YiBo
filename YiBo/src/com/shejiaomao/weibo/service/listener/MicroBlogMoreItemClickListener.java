package com.shejiaomao.weibo.service.listener;

import java.io.File;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Status;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.common.EntityUtil;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.MicroBlogMoreListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;
import com.shejiaomao.weibo.service.task.DestroyStatusTask;
import com.shejiaomao.weibo.widget.ListChooseDialog;

public class MicroBlogMoreItemClickListener implements OnItemClickListener {
    private ListChooseDialog chooseDialog;
	public MicroBlogMoreItemClickListener(ListChooseDialog chooseDialog) {
		this.chooseDialog = chooseDialog;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        Adapter adapter = parent.getAdapter();
        BaseAdapter baseAdapter = AdapterUtil.getAdapter(adapter);
        if (!(baseAdapter instanceof MicroBlogMoreListAdapter)) {
        	return;
        }
        MicroBlogMoreListAdapter listAdapter = (MicroBlogMoreListAdapter)baseAdapter;
        chooseDialog.dismiss();
        
        final Context context = view.getContext();
        final Status status = listAdapter.getStatus();
        int itemId = (int)listAdapter.getItemId(position);
    	ClipboardManager clip = (ClipboardManager)context
            .getSystemService(Context.CLIPBOARD_SERVICE);
        switch (itemId) {
        case MicroBlogMoreListAdapter.ITEM_DELETE:
        	new AlertDialog.Builder(context)
		    .setTitle(R.string.title_dialog_alert)
		    .setMessage(R.string.msg_blog_delete)
		    .setNegativeButton(R.string.btn_cancel,
			    new AlertDialog.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    }
			    })
		    .setPositiveButton(R.string.btn_confirm, 
			    new AlertDialog.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        	DestroyStatusTask task = new DestroyStatusTask(context, status);
						task.execute();
			    }
		    }).show();
        	break;
        case MicroBlogMoreListAdapter.ITEM_COPY:
			String copyStatusText = StatusUtil.extraSimpleStatus(context, status);
			clip.setText(copyStatusText);
			Toast.makeText(context, R.string.msg_blog_copy, Toast.LENGTH_SHORT).show();
        	break;
        case MicroBlogMoreListAdapter.ITEM_SHARE_TO_ACCOUNTS:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setClass(context, EditMicroBlogActivity.class);

			if (EntityUtil.hasPicture(status)) {
				intent.setType("image/*");
				
				CachedImageKey info = EntityUtil.getMaxLocalCachedImageInfo(status);
				String imagePath = ImageCache.getRealPath(info);
				if (StringUtil.isNotEmpty(imagePath)) {
					if (info.getCacheType() == CachedImageKey.IMAGE_THUMBNAIL) {
						Toast.makeText(
							context, 
							context.getString(R.string.msg_blog_share_picture_thumbnail),
						    Toast.LENGTH_LONG
						).show();
					}
					Uri uri = Uri.fromFile(new File(imagePath));
				    intent.putExtra(Intent.EXTRA_STREAM, uri);
				} else {
					intent.setType("text/plain");
					Toast.makeText(context, context.getString(R.string.msg_blog_share_picture), Toast.LENGTH_LONG).show();
				}
			} else {
				intent.setType("text/plain");
			}

			String statusText = StatusUtil.extraSimpleStatus(context, status);
			clip.setText(statusText);

			intent.putExtra(Intent.EXTRA_TEXT, statusText);
			intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.msg_extra_subject));
			context.startActivity(intent);
        	break;
        }
	}

}
