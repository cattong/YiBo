package com.shejiaomao.weibo.service.listener;

import java.io.File;

import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Status;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.activity.EditMicroBlogActivity;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.activity.ProfileActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.EntityUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.db.LocalStatus;
import com.shejiaomao.weibo.service.adapter.AdapterUtil;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;
import com.shejiaomao.weibo.service.task.DestroyStatusTask;
import com.shejiaomao.weibo.service.task.ToggleFavoriteTask;

public class MicroBlogContextMenuListener implements
		OnCreateContextMenuListener {
	private ListView lvMicroBlog;
    private LocalAccount account;
    private View targetView;
    private int position;

	public MicroBlogContextMenuListener(ListView lvMicroBlog) {
		this.lvMicroBlog = lvMicroBlog;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Adapter adapter = lvMicroBlog.getAdapter();
		this.position = info.position;
		targetView = info.targetView;
		Status status= (Status)adapter.getItem(position);
		if (status == null 
			|| (status instanceof LocalStatus
				&& ((LocalStatus)status).isDivider())) {
			return;
		}

		Context context = v.getContext();
		analyzeStatusMenu(adapter, status, menu, context);
	}

	private void analyzeStatusMenu(final Adapter adapter, final Status status, ContextMenu menu, final Context context) {
		//menu.addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems)
		menu.setHeaderTitle(R.string.menu_title_blog);
	    int order = 0;

	    MenuItem commentMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_COMMENT, order++, R.string.menu_blog_comment);
	    Intent commentIntent = new Intent(context, EditCommentActivity.class);
	    commentIntent.putExtra("TYPE", Constants.EDIT_TYPE_COMMENT);
	    commentIntent.putExtra("STATUS", status);
	    commentMenu.setIntent(commentIntent);

	    MenuItem retweetMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_RETWEET, order++, R.string.menu_blog_retweet);
	    retweetMenu.setOnMenuItemClickListener(new MicroBlogRetweetClickListener(context, status));

	    MenuItem favoriteMenu = null;
	    if (status.isFavorited()) {
	    	favoriteMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_FAVORITE, order++, R.string.menu_blog_favorite_cancel);
	    } else {
	    	favoriteMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_FAVORITE, order++, R.string.menu_blog_favorite);
	    }
	    favoriteMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ToggleFavoriteTask task = new ToggleFavoriteTask(context, status, targetView);
				task.execute();
				return false;
			}
	    });

	    account = getAccount(adapter);
	    if (account != null 
	    	&& account.getUser() != null
	    	&& account.getUser().equals(status.getUser())) {
	    	MenuItem deleteMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_DELETE, order++, R.string.menu_blog_delete);
	    	deleteMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					CacheAdapter<Status> cacheAdapter = (CacheAdapter<Status>)AdapterUtil.getCacheAdapter(adapter);
					new DestroyStatusTask(cacheAdapter, status).execute();
					return false;
				}
			});
	    }

	    MenuItem userMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_PERSONAL, order++, R.string.menu_blog_personal);
	    Intent userIntent = new Intent(context, ProfileActivity.class);
	    userIntent.putExtra("USER", status.getUser());
	    userMenu.setIntent(userIntent);

	    final Status retweet = status.getRetweetedStatus();
	    if (retweet != null) {
	    	MenuItem retweetOriginMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_RETWEET_ORIGIN, 
	    		order++, R.string.menu_blog_retweet_origin);
	    	retweetOriginMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					StatusUtil.retweet(context, retweet);
					return false;
				}
		    });
	    }
	    if (retweet != null) {
	        MenuItem commentOriginMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_COMMENT_ORIGIN, 
	        	order++, R.string.menu_blog_comment_origin);
	        Intent commentOriginIntent = new Intent(context, EditCommentActivity.class);
	        commentOriginIntent.putExtra("TYPE", Constants.EDIT_TYPE_COMMENT);
	        commentOriginIntent.putExtra("STATUS", retweet);
	        commentOriginMenu.setIntent(commentOriginIntent);
	    }
	    if (retweet != null) {
	        MenuItem showOriginMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_SHOW_ORIGIN, 
	        	order++, R.string.menu_blog_show_origin);
	        Intent showOriginIntent = new Intent(context, MicroBlogActivity.class);
	        showOriginIntent.putExtra("STATUS", retweet);
	        showOriginMenu.setIntent(showOriginIntent);
	    }
	    
	    MenuItem shareToAccountsMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_SHARE_TO_ACCOUNTS, order++, R.string.menu_blog_share_to_accounts);
	    shareToAccountsMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
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
				
				ClipboardManager clip = (ClipboardManager)context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
				String statusText = StatusUtil.extraSimpleStatus(context, status);
				clip.setText(statusText);

				intent.putExtra(Intent.EXTRA_TEXT, statusText);
				intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.msg_extra_subject));
				context.startActivity(intent);
				return false;
			}
	    });

	    MenuItem copyMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_COPY, order++, R.string.menu_blog_copy);
	    copyMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ClipboardManager clip = (ClipboardManager)context
                   .getSystemService(Context.CLIPBOARD_SERVICE);
				String statusText = StatusUtil.extraSimpleStatus(context, status);
				clip.setText(statusText);
				Toast.makeText(context, R.string.msg_blog_copy, Toast.LENGTH_SHORT).show();
				return false;
			}
	    });

	    MenuItem shareMenu = menu.add(0, Constants.CONTEXT_MENU_BLOG_SHARE, order++, R.string.menu_blog_share);
	    shareMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(Intent.ACTION_SEND);

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
				
				ClipboardManager clip = (ClipboardManager)context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
				String statusText = StatusUtil.extraRichStatus(context, status);
				clip.setText(statusText);

				intent.putExtra(Intent.EXTRA_TEXT, statusText);
				intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.msg_extra_subject));
				context.startActivity(intent);
				return false;
			}
	    });
	}
    
    private LocalAccount getAccount(Adapter adapter) {
    	LocalAccount account = null;
	    CacheAdapter<?> cacheAdapter = AdapterUtil.getCacheAdapter(adapter);
	    if (cacheAdapter != null) {
	    	account = cacheAdapter.getAccount();
	    }
	    return account;
    }
}
