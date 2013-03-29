package com.shejiaomao.weibo.service.listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;

import com.cattong.commons.util.ObjectUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.weibo.FeaturePatternUtils;
import com.shejiaomao.weibo.activity.EditCommentActivity;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.task.DestroyCommentTask;

public class CommentsOfStatusContextMenuListener implements
		OnCreateContextMenuListener {
	private CacheAdapter<Comment> adapter;
    private LocalAccount currentAccount;
	public CommentsOfStatusContextMenuListener(Adapter adapter) {
		this.adapter = getCacheAdapter(adapter);
		this.currentAccount = this.adapter.getAccount();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int position = info.position - 1;
		if (position < 0 || position >= adapter.getCount()) {
			return;
		}
		Comment comment = (Comment)adapter.getItem(position);
		
		Context context = v.getContext();
		analyzeCommentMenu(comment, menu, context);
	}

	private void analyzeCommentMenu(final Comment comment, ContextMenu menu, final Context context) {
		menu.setHeaderTitle(R.string.title_dialog_comment);
		int order = 0;
		
		MenuItem replyMenu = menu.add(0, order, order++, R.string.menu_comment_reply);
		replyMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
	        	intent.setClass(context, EditCommentActivity.class);
	        	intent.putExtra("TYPE", Constants.EDIT_TYPE_RECOMMENT);
	        	intent.putExtra("COMMENT", comment);
	        	((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_COMMENT_OF_STATUS);
	        	return true;
			}
		});
		
        MenuItem profileMenu = menu.add(0, order, order++, R.string.menu_comment_personal_info);
        Intent personalIntent = new Intent();
        personalIntent.setAction(Intent.ACTION_VIEW);
        String persionalUrl = Constants.URI_PERSONAL_INFO.toString() + "@" +
    	    comment.getUser().getDisplayName();
        personalIntent.setData(Uri.parse(persionalUrl));
        profileMenu.setIntent(personalIntent);
        
//        profileMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				Intent intent = new Intent();
//	        	Uri personalUri = Uri.parse(
//	            	Constants.URI_PERSONAL_INFO.toString() + "@" +
//	            	comment.getUser().getDisplayName()
//	            );
//	            intent.setData(personalUri);
//	            context.startActivity(intent);
//	        	return true;
//			}
//		});
		
        //是否添加删除按钮,在评论是自己发的，或微博是自己发的情况下
        Status inReplyToStatus = comment.getReplyToStatus();
		if ((currentAccount != null
			    && ObjectUtil.isEquals(comment.getUser(), currentAccount.getUser()))
			|| (inReplyToStatus != null
			    && ObjectUtil.isEquals(inReplyToStatus.getUser(), 
			    	   currentAccount.getUser()))) {
			MenuItem destroyMenu = menu.add(0, order, order++, R.string.menu_comment_destroy);
			destroyMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
	        		DestroyCommentTask destroyTask = new DestroyCommentTask(adapter, comment);
	        		destroyTask.execute();
		        	return true;
				}
			});
		}
		
        Pattern mentionPattern = FeaturePatternUtils.getMentionPattern(comment.getServiceProvider()) ;
        Matcher mentionMatcher = mentionPattern.matcher(comment.getText());
		while (mentionMatcher.find()) {
			String mention = Constants.URI_PERSONAL_INFO.toString() + mentionMatcher.group();
			MenuItem mentionMenu = menu.add(0, order, order++, mentionMatcher.group());
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(mention));
			mentionMenu.setIntent(intent);
		}
		
        Matcher urlMatcher = Constants.URL_PATTERN.matcher(comment.getText());
		while (urlMatcher.find()) {
			String url = urlMatcher.group();
			MenuItem urlMenu = menu.add(0, order, order++, url);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			urlMenu.setIntent(intent);
		}
	}
	
    @SuppressWarnings("unchecked")
	private CacheAdapter<Comment> getCacheAdapter(Adapter adapter) {
    	CacheAdapter<Comment> cacheAdapter = null;
	    Adapter tempAdapter = adapter;
	    if (tempAdapter instanceof HeaderViewListAdapter) {
	    	tempAdapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
	    }
	    cacheAdapter = (CacheAdapter<Comment>)tempAdapter;
	    return cacheAdapter;
    }
    
}
