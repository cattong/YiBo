package com.shejiaomao.weibo.service.listener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.shejiaomao.maobo.R;
import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.weibo.entity.DirectMessage;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.AutoUpdateService;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.CacheAdapter;
import com.shejiaomao.weibo.service.adapter.CommentsListAdapter;
import com.shejiaomao.weibo.service.adapter.DirectMessagesListAdapter;
import com.shejiaomao.weibo.service.adapter.MentionsListAdapter;
import com.shejiaomao.weibo.service.cache.AdapterCollectionCache;
import com.shejiaomao.weibo.service.cache.Cache;
import com.shejiaomao.weibo.widget.Skeleton;
import com.shejiaomao.weibo.widget.ValueSetEvent;
import com.shejiaomao.weibo.widget.ViewChangeEvent;
import com.shejiaomao.widget.PullToRefreshListView;

public class MessagesChangeListener implements PropertyChangeListener {
	private Activity context;
	private SheJiaoMaoApplication sheJiaoMao;
	private View contentView; //mention and comment
	private View directMessageView;
	
	private HomePageRefreshListener refreshListener;
	private MicroBlogItemClickListener statusItemClickListener;
	private StatusRecyclerListener statusRecyclerListener;
	
	private MicroBlogContextMenuListener contextMenuListener;
    private CommentsItemClickListener commentItemClickListener;
	private CommentRecyclerListener commentRecyclerListener;
	
	private DirectMessagesItemClickListener directMessageItemClickListener;
	private DirectMessageRecyclerListener directMessageRecyclerListener;
	
	private int currentType;
	private LocalAccount currentAccount;
	private ViewGroup currentContainer;
	
    private Button btnMention;
    private Button btnComment;
    private Button btnDirectMessage;
	public MessagesChangeListener(Context context) {
		this.context = (Activity)context;
		this.sheJiaoMao = (SheJiaoMaoApplication)context.getApplicationContext();
        
		refreshListener = new HomePageRefreshListener();
		statusItemClickListener = new MicroBlogItemClickListener(context);
		statusRecyclerListener = new StatusRecyclerListener();
        
        commentItemClickListener = new CommentsItemClickListener(context);
        commentRecyclerListener = new CommentRecyclerListener();
        
        directMessageItemClickListener = new DirectMessagesItemClickListener(context);
        directMessageRecyclerListener = new DirectMessageRecyclerListener();
        
        currentType = Skeleton.TYPE_MENTION;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
        if (event instanceof ViewChangeEvent) {
        	int type = (Integer)event.getNewValue();
        	if (type != Skeleton.TYPE_MESSAGE 
        		&& type != Skeleton.TYPE_MENTION 
        		&& type != Skeleton.TYPE_COMMENT 
        		&& type != Skeleton.TYPE_DIRECT_MESSAGE) {
        		return;
        	}
    		ViewChangeEvent changeEvent = (ViewChangeEvent)event;
    		LocalAccount account = changeEvent.getAccount();
    		ViewGroup container = (ViewGroup)changeEvent.getView();
        	viewChange(account, type, container);
        } else if (event instanceof ValueSetEvent) {
        	valueSet(event);
        }
	}

	private void viewChange(LocalAccount account, int type, ViewGroup container) {
		if (account == null || container == null) {
			return;
		}
		if (type != Skeleton.TYPE_MESSAGE) {
			currentType = type;
		}
		currentAccount = account;
		currentContainer = container;
		
		View view = null;
		switch (currentType) {
		case Skeleton.TYPE_MENTION:
			view = getMentionContentView(account);
			break;
		case Skeleton.TYPE_COMMENT:
			view = getCommentContentView(account);
			break;
		case Skeleton.TYPE_DIRECT_MESSAGE:
			view = getDirectMessageContentView(account);
			break;
		}
					
		container.removeAllViews();				
		container.addView(view);
	    
	    View llHeaderBase = context.findViewById(R.id.llHeaderBase);
	    llHeaderBase.setVisibility(View.GONE);
	    View llHeaderMessage = context.findViewById(R.id.llHeaderMessage);
	    llHeaderMessage.setVisibility(View.VISIBLE);
	    
	    btnMention = (Button)context.findViewById(R.id.btnMention);
	    btnComment = (Button)context.findViewById(R.id.btnComment);
	    btnDirectMessage = (Button)context.findViewById(R.id.btnDirectMessage);
        btnMention.setOnClickListener(tabClickListener);
        btnComment.setOnClickListener(tabClickListener);
        btnDirectMessage.setOnClickListener(tabClickListener);
	    btnMention.setEnabled(true);
	    btnComment.setEnabled(true);
	    btnDirectMessage.setEnabled(true);
	    switch (currentType) {
		case Skeleton.TYPE_MENTION:
			btnMention.setEnabled(false);
			break;
		case Skeleton.TYPE_COMMENT:
			btnComment.setEnabled(false);
			break;
		case Skeleton.TYPE_DIRECT_MESSAGE:
			btnDirectMessage.setEnabled(false);
			break;
	    }
	}
	
	private void valueSet(PropertyChangeEvent event) {
		ValueSetEvent setEvent = (ValueSetEvent)event;
		LocalAccount account = setEvent.getAccount();
		
		switch (setEvent.getAction()) {
		case ACTION_INIT_ADAPTER:
			initMentionAdapter(account);
			initCommentAdapter(account);
			initDirectMessageAdapter(account);
			break;
		case ACTION_RECLAIM_MEMORY:
			contentView = null;
			directMessageView = null;
			break;
		default:
			break;
		}
	}
	
	private View getMentionContentView(LocalAccount account) {
		ListView lvMicroBlog = null;
		if (account == null) {
			return lvMicroBlog;
		}
		
		if (contentView != null) {
		    lvMicroBlog = (ListView)contentView.findViewById(R.id.lvMicroBlog);
		} else {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        contentView = inflater.inflate(R.layout.home_page_content_message, null);
	        lvMicroBlog = (ListView)contentView.findViewById(R.id.lvMicroBlog);
	        if (lvMicroBlog instanceof PullToRefreshListView) {
	        	((PullToRefreshListView)lvMicroBlog).setOnRefreshListener(refreshListener);
	        }
	        lvMicroBlog.setOnScrollListener(new StatusScrollListener());
	        
	        View emptyView = contentView.findViewById(R.id.llLoadingView);
	        lvMicroBlog.setEmptyView(emptyView);
	        
	        ThemeUtil.setContentBackground(contentView);
	        ThemeUtil.setListViewStyle(lvMicroBlog);
	        ThemeUtil.setListViewLoading(emptyView);
		}
		
		lvMicroBlog.setOnItemClickListener(statusItemClickListener);
        
		CacheAdapter<Status> adapter = initMentionAdapter(account);			
		lvMicroBlog.setAdapter(adapter);
		if (contextMenuListener == null) {
		    contextMenuListener = new MicroBlogContextMenuListener(lvMicroBlog);
		}
		lvMicroBlog.setOnCreateContextMenuListener(contextMenuListener);
		lvMicroBlog.setRecyclerListener(statusRecyclerListener);
		
		lvMicroBlog.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		View llDirectMessageHeader = contentView.findViewById(R.id.llDirectMessageHeader);
		llDirectMessageHeader.setVisibility(View.GONE);

		return contentView; 
	}
	
	private CacheAdapter<Status> initMentionAdapter(LocalAccount account) {
		Cache cache = CacheManager.getInstance().getCache(account);
		AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
		if (adapterCache == null) {
			adapterCache = new AdapterCollectionCache(account);
			CacheManager.getInstance().putCache(account, adapterCache);
		}
		MentionsListAdapter adapter = adapterCache.getMentionsListAdapter();	
		if (adapter == null) {
			adapter = new MentionsListAdapter(context, account);
			adapterCache.setMentionsListAdapter(adapter);
			AutoUpdateService.registerUpdateAccount(account);
		}

		return adapter;
	}
	
	private View getCommentContentView(LocalAccount account) {
		ListView lvComment = null;
		if (account == null) {
			return lvComment;
		}

		if (contentView != null) {
		    lvComment = (ListView)contentView.findViewById(R.id.lvMicroBlog);
		} else {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        contentView = inflater.inflate(R.layout.home_page_content_message, null);
	        lvComment = (ListView)contentView.findViewById(R.id.lvMicroBlog);
	        if (lvComment instanceof PullToRefreshListView) {
	        	((PullToRefreshListView)lvComment).setOnRefreshListener(refreshListener);
	        }
	        lvComment.setOnScrollListener(new AutoLoadMoreListener());
	        
	        View emptyView = contentView.findViewById(R.id.llLoadingView);
	        lvComment.setEmptyView(emptyView);
	        
	        ThemeUtil.setContentBackground(contentView);
	        ThemeUtil.setListViewStyle(lvComment);
	        ThemeUtil.setListViewLoading(emptyView);
		}

		lvComment.setOnItemClickListener(commentItemClickListener);
        lvComment.setOnCreateContextMenuListener(null);
        
		CacheAdapter<Comment> adapter = initCommentAdapter(account);
		lvComment.setAdapter(adapter);
		lvComment.setRecyclerListener(commentRecyclerListener);
		
		lvComment.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		View llDirectMessageHeader = contentView.findViewById(R.id.llDirectMessageHeader);
		llDirectMessageHeader.setVisibility(View.GONE);

		return contentView;
	}

	private CacheAdapter<Comment> initCommentAdapter(LocalAccount account) {
		Cache cache = CacheManager.getInstance().getCache(account);
		AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
		if (adapterCache == null) {
			adapterCache = new AdapterCollectionCache(account);
			CacheManager.getInstance().putCache(account, adapterCache);
		}
		CommentsListAdapter adapter = adapterCache.getCommentsListAdapter();
		if (adapter == null) {
			adapter = new CommentsListAdapter(context, account);
			adapterCache.setCommentsListAdapter(adapter);
			AutoUpdateService.registerUpdateAccount(account);
		}

		return adapter;
	}

	public View getDirectMessageContentView(LocalAccount account) {
		CacheAdapter<DirectMessage> adapter = initDirectMessageAdapter(account);
		ListView lvDirectMessage;
		if (directMessageView != null) {
			lvDirectMessage = (ListView)directMessageView.findViewById(R.id.lvMicroBlog);
		} else {			
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            directMessageView = inflater.inflate(R.layout.home_page_content_message, null);  
            ThemeUtil.setContentBackground(directMessageView);
            
            lvDirectMessage = (ListView)directMessageView.findViewById(R.id.lvMicroBlog);
	        if (lvDirectMessage instanceof PullToRefreshListView) {
	        	((PullToRefreshListView)lvDirectMessage).setOnRefreshListener(refreshListener);
	        }
	        ThemeUtil.setListViewStyle(lvDirectMessage);
	        lvDirectMessage.setOnScrollListener(new AutoLoadMoreListener());
            View emptyView = directMessageView.findViewById(R.id.llLoadingView);
            ThemeUtil.setListViewLoading(emptyView);
            lvDirectMessage.setOnItemClickListener(directMessageItemClickListener);            
            lvDirectMessage.setEmptyView(emptyView);
            
            View llDirectMessageHeader = directMessageView.findViewById(R.id.llDirectMessageHeader);
            ThemeUtil.setHeaderCornerTab(llDirectMessageHeader);
			//edit direct message
			HomePageEditMessageClickListener editMessageClickListener = new HomePageEditMessageClickListener(context); 
			EditText etDirectMessage = (EditText)directMessageView.findViewById(R.id.etDirectMessage);
			etDirectMessage.setOnClickListener(editMessageClickListener);
			etDirectMessage.setInputType(InputType.TYPE_NULL);
			Button btnDirectMessage = (Button)directMessageView.findViewById(R.id.btnDirectMessage);
			btnDirectMessage.setOnClickListener(editMessageClickListener);
			
			Theme theme = ThemeUtil.createTheme(context);
			btnDirectMessage.setBackgroundDrawable(theme.getDrawable("selector_btn_message_edit"));
			etDirectMessage.setBackgroundDrawable(theme.getDrawable("bg_input_frame_left_half"));
		}
		lvDirectMessage.setAdapter(adapter);
		lvDirectMessage.setFastScrollEnabled(sheJiaoMao.isSliderEnabled());
		lvDirectMessage.setRecyclerListener(directMessageRecyclerListener);
		
		View llDirectMessageHeader = directMessageView.findViewById(R.id.llDirectMessageHeader);
		llDirectMessageHeader.setVisibility(View.VISIBLE);
		EditText etDirectMessage = (EditText)directMessageView.findViewById(R.id.etDirectMessage);
		etDirectMessage.clearFocus();
		
		return directMessageView;
	}
	
	private DirectMessagesListAdapter initDirectMessageAdapter(LocalAccount account) {
		Cache cache = CacheManager.getInstance().getCache(account);
		AdapterCollectionCache adapterCache = (AdapterCollectionCache)cache;
		if (adapterCache == null) {
			adapterCache = new AdapterCollectionCache(account);
			CacheManager.getInstance().putCache(account, adapterCache);
		}
		DirectMessagesListAdapter adapter = adapterCache.getDirectMessagesListAdapter();
		if (adapter == null) {
			adapter = new DirectMessagesListAdapter(context, account);
			adapterCache.setDirectMessagesListAdapter(adapter);
			AutoUpdateService.registerUpdateAccount(account);
		}

		return adapter;
	}
	
	private View.OnClickListener tabClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnMention) {
				viewChange(currentAccount, Skeleton.TYPE_MENTION, currentContainer);
			} else if (v.getId() == R.id.btnComment) {
				viewChange(currentAccount, Skeleton.TYPE_COMMENT, currentContainer);
			} else if (v.getId() == R.id.btnDirectMessage) {
				viewChange(currentAccount, Skeleton.TYPE_DIRECT_MESSAGE, currentContainer);
			}
		}
	};
}
