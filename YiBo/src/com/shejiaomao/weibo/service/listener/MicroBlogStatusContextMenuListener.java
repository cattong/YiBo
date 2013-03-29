package com.shejiaomao.weibo.service.listener;

import com.cattong.entity.Status;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Toast;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.MicroBlogActivity;

public class MicroBlogStatusContextMenuListener implements
		OnCreateContextMenuListener {
	public static final int CONTEXT_MENU_MENU_COPY_TWEET = 0;
	public static final int CONTEXT_MENU_MENU_COPY_RETWEET = 1;
	public static final int CONTEXT_MENU_MENU_VIEW_RETWEET = 2;
	
    private Status status;
	public MicroBlogStatusContextMenuListener(Status status) {
		this.status = status;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
        if (status == null) {
        	return;
        }
        
        final Context context = v.getContext();
        int order = 0;
        
        menu.setHeaderTitle(R.string.menu_title_blog);
        MenuItem copytweetMenu = menu.add(
        	0, CONTEXT_MENU_MENU_COPY_TWEET, order++, R.string.menu_blog_copy_tweet
        );
        copytweetMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ClipboardManager clip = (ClipboardManager)context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
				String statusText = status.getText();
				clip.setText(statusText);
				Toast.makeText(context, R.string.msg_blog_copy, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
        
        final Status retweet = status.getRetweetedStatus();
        if (retweet != null) {
            MenuItem copyRetweetMenu = menu.add(
                0, CONTEXT_MENU_MENU_COPY_RETWEET, order++, R.string.menu_blog_copy_retweet
            );
            copyRetweetMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    			@Override
    			public boolean onMenuItemClick(MenuItem item) {
    				ClipboardManager clip = (ClipboardManager)context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
    				String statusText = retweet.getText();
    				clip.setText(statusText);
    				Toast.makeText(context, R.string.msg_blog_copy, Toast.LENGTH_SHORT).show();
    				return false;
    			}
    		});
            
            MenuItem showRetweetMenu = menu.add(
            	0, CONTEXT_MENU_MENU_VIEW_RETWEET, order++, R.string.menu_blog_show_origin
            );
            Intent showRetweetIntent = new Intent(context, MicroBlogActivity.class);
	        showRetweetIntent.putExtra("STATUS", retweet);
	        showRetweetMenu.setIntent(showRetweetIntent);
        }
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
