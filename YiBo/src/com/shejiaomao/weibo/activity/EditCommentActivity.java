package com.shejiaomao.weibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.shejiaomao.maobo.R;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.Comment;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.FeaturePatternUtils;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.CommentsOfStatusListAdapter;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.adapter.UserSuggestAdapter;
import com.shejiaomao.weibo.service.listener.EditCommentEmotionClickListener;
import com.shejiaomao.weibo.service.listener.EditCommentSendClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogMentionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTextDeleteClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTokenizer;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTopicClickListener;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogTextWatcher;
import com.shejiaomao.weibo.service.task.DestroyCommentTask;
import com.shejiaomao.weibo.widget.EmotionViewController;

public class EditCommentActivity extends BaseActivity {
    private CommentsOfStatusListAdapter commentsAdapter;
    private LocalAccount account;
    private Status status;
    private Status retweetedStatus;
    private Comment recomment;

    private int type;
    private boolean isRetweet = false;
    private boolean isCommentToOrigin = false;

    private CheckBox cbRetweet = null;
    private CheckBox cbCommentToOrigin = null;
    private TextView tvText = null;

    private EmotionViewController emotionViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_comment);

        SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication)this.getApplication();
        account = sheJiaoMao.getCurrentAccount();

        //默认不弹出输入法
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initParams(savedInstanceState);
        initCompoments();

        bindEvent();
    }

    private void initParams(Bundle savedInstanceState) {
        Bundle bundle = this.getIntent().getExtras();
        if (savedInstanceState != null) {
        	bundle = savedInstanceState;
        }
        
        type = bundle.getInt("TYPE");
        if (type == Constants.EDIT_TYPE_COMMENT) {
            Object temp = bundle.getSerializable("STATUS");
            if (temp != null) {
                status = (Status)temp;
            }
        } else if (type == Constants.EDIT_TYPE_RECOMMENT) {
            Object temp = bundle.getSerializable("COMMENT");
            if (temp != null) {
                recomment = (Comment)temp;
                status = recomment.getReplyToStatus();
            }
        } else {
            this.finish();
        }
    }

    private void initCompoments() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		LinearLayout llContentPanel = (LinearLayout)findViewById(R.id.llContentPanel);
		LinearLayout llEditText = (LinearLayout)findViewById(R.id.llEditText);
		MultiAutoCompleteTextView etText  = (MultiAutoCompleteTextView)findViewById(R.id.etText);
		Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
		Button btnMention = (Button)this.findViewById(R.id.btnMention);
		Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
		Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
		cbRetweet = (CheckBox)this.findViewById(R.id.cbRetweet);
		cbCommentToOrigin = (CheckBox)this.findViewById(R.id.cbCommentToOrigin);
		tvText = (TextView)this.findViewById(R.id.tvText);

		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(llContentPanel);
		int padding6 = theme.dip2px(6);
		int padding8 = theme.dip2px(8);
		llContentPanel.setPadding(padding6, padding8, padding6, 0);
		llEditText.setBackgroundDrawable(theme.getDrawable("bg_input_frame_normal"));
		etText.setTextColor(theme.getColor("content"));
		btnEmotion.setBackgroundDrawable(theme.getDrawable("selector_btn_emotion"));
		btnMention.setBackgroundDrawable(theme.getDrawable("selector_btn_mention"));
		btnTopic.setBackgroundDrawable(theme.getDrawable("selector_btn_topic"));
		btnTextCount.setBackgroundDrawable(theme.getDrawable("selector_btn_text_count"));
		btnTextCount.setPadding(padding6, 0, theme.dip2px(20), 0);
		btnTextCount.setTextColor(theme.getColor("status_capability"));
		cbRetweet.setButtonDrawable(theme.getDrawable("selector_checkbox"));
		cbRetweet.setTextColor(theme.getColor("content"));
		cbCommentToOrigin.setButtonDrawable(theme.getDrawable("selector_checkbox"));
		cbCommentToOrigin.setTextColor(theme.getColor("content"));
		tvText.setTextColor(theme.getColor("quote"));

        TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);

		MicroBlogTextWatcher textWatcher = new MicroBlogTextWatcher(this);
		etText.addTextChangedListener(textWatcher);
		etText.requestFocus();
		etText.setAdapter(new UserSuggestAdapter(this));
		etText.setTokenizer(new EditMicroBlogTokenizer());
        if (status.getServiceProvider() == ServiceProvider.Twitter
        	|| status.getServiceProvider() == ServiceProvider.Fanfou) {
            etText.setText(status.getUser().getMentionName() + " ");
            Set<String> mentions = StatusUtil.extraStatusMentions(status, true);
            if (mentions != null && mentions.size() > 0) {
                String mentionsStr = StringUtil.join(mentions.toArray(), " ") + " ";
                int startIndex = etText.length();
                etText.append(mentionsStr);
                etText.setSelection(startIndex, etText.length());
            } else {
                etText.setSelection(etText.length());
            }
        }

        tvTitle.setText(R.string.title_comment);

        int length = StringUtil.getLengthByByte(etText.getText().toString());
        int leavings = (int)Math.floor((double)(Constants.STATUS_TEXT_MAX_LENGTH * 2 - length) / 2);
        btnTextCount.setText((leavings < 0 ? "-" : "") + Math.abs(leavings));

        if (recomment != null) {
            tvText.setText(recomment.getUser().getScreenName() + ":" + recomment.getText());
        } else if (status != null) {
            tvText.setText(status.getUser().getMentionName() + ":" + status.getText());
        }

        if (isComment2OriginVisible()) {
            retweetedStatus = status.getRetweetedStatus();
            cbCommentToOrigin = (CheckBox)this.findViewById(R.id.cbCommentToOrigin);
            String lableCommentToOrigin = this.getString(
                R.string.label_retweet_with_comment_to_origin,
                retweetedStatus.getUser().getScreenName()
            );
            cbCommentToOrigin.setText(lableCommentToOrigin);
            cbCommentToOrigin.setVisibility(View.VISIBLE);
        }

        emotionViewController = new EmotionViewController(this);
    }

    public boolean isComment2OriginVisible() {
    	return recomment == null
                && status != null
                && status.getRetweetedStatus() != null;
    }

    private void bindEvent() {
        Button btnBack = (Button)this.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new GoBackClickListener());

        EditText etText = (EditText)this.findViewById(R.id.etText);
        etText.addTextChangedListener(new MicroBlogTextWatcher(this));
        etText.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emotionViewController.hideEmotionView();
				displayOptions(true);
				return false;
			}
		});

        Button btnSend = (Button)this.findViewById(R.id.btnOperate);
        btnSend.setText(R.string.label_send);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new EditCommentSendClickListener(this));

        Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
        btnTopic.setOnClickListener(new EditMicroBlogTopicClickListener(this));

        Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
        btnEmotion.setOnClickListener(new EditCommentEmotionClickListener(this));

        Button btnMention = (Button)this.findViewById(R.id.btnMention);
        btnMention.setOnClickListener(new EditMicroBlogMentionClickListener());

        Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
        btnTextCount.setOnClickListener(new EditMicroBlogTextDeleteClickListener(this));

        cbRetweet.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                isRetweet = isChecked;
            }
        });
        CheckBox cbCommentToOrigin = (CheckBox)this.findViewById(R.id.cbCommentToOrigin);
        cbCommentToOrigin.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                isCommentToOrigin = isChecked;
            }
        });

        if (type == Constants.EDIT_TYPE_RECOMMENT && recomment != null) {
            String recommentText = null;
            if (recomment.getServiceProvider() == ServiceProvider.Tencent) {
            	recommentText =
            		String.format(
            			FeaturePatternUtils.getRetweetFormat(recomment.getServiceProvider()),
	        			FeaturePatternUtils.getRetweetSeparator(recomment.getServiceProvider()),
	        			recomment.getUser().getMentionName(),
	        			recomment.getText()
	            	);
            	etText.setText(recommentText);
                etText.setSelection(0);
            } else {
            	recommentText = this.getString(R.string.hint_recomment,
            		recomment.getUser().getScreenName());
            	etText.setText(recommentText);
                etText.setSelection(recommentText.length());
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
        case Constants.REQUEST_CODE_USER_SELECTOR:
            if (resultCode == Constants.RESULT_CODE_SUCCESS) {
                List<User> userList = (List<User>)data.getSerializableExtra("LIST_USER");
                if (ListUtil.isEmpty(userList)) {
                	userList = new ArrayList<User>();
                }
                MultiAutoCompleteTextView etText  =
                	(MultiAutoCompleteTextView)this.findViewById(R.id.etText);
                StringBuilder mentions = new StringBuilder("");
                for (User user : userList) {
                    mentions.append(user.getMentionName()).append(" ");
                }
                int currentPos = etText.getSelectionStart();
                etText.getText().insert(currentPos, mentions);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_context_comment, menu);
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        Comment comment = (Comment)commentsAdapter.getItem(position);

        menu.setHeaderTitle(R.string.title_dialog_comment);
        int order = 0;
        menu.add(0, order, order++, R.string.menu_comment_reply);
        menu.add(0, order, order++, R.string.menu_comment_personal_info);

        //是否添加删除按钮,在评论是自己发的，或微博是自己发的情况下
        Status inReplyToStatus = comment.getReplyToStatus();
        if (comment.getUser().equals(account.getUser())
            ||((inReplyToStatus != null)
                && (inReplyToStatus.getUser() != null)
                && inReplyToStatus.getUser().equals(account.getUser()))) {
            menu.add(0, order, order++, R.string.menu_comment_destroy);
        }

        Matcher m = Constants.URL_PATTERN.matcher(comment.getText());
        while (m.find()) {
            String url = m.group();
            menu.add(0, order, order++, url);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        Comment comment = (Comment) commentsAdapter.getItem(position);
        Intent intent = new Intent();

        switch (item.getItemId()) {
        case 0:
            recomment = comment;
            EditText etComment = (EditText) this.findViewById(R.id.etText);
            String recommentText = this.getString(
                R.string.hint_recomment, comment.getUser().getScreenName()
            );
            etComment.setText(recommentText);
            etComment.setSelection(recommentText.length());
            break;
        case 1:
            Uri personalUri = Uri.parse(
                Constants.URI_PERSONAL_INFO.toString() + "@"
                    + comment.getUser().getScreenName()
            );
            intent.setData(personalUri);

            startActivity(intent);
            break;
        case 2:
            Matcher m = Constants.URL_PATTERN.matcher(item.getTitle().toString());
            if (m.matches()) {
                gotoUrl(item.getTitle().toString());
            } else {
                DestroyCommentTask destroyTask = new DestroyCommentTask(commentsAdapter, comment);
                destroyTask.execute();
            }
            break;
        default:
            gotoUrl(item.getTitle().toString());
            break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
	public void onBackPressed() {
    	if (emotionViewController.getEmotionViewVisibility() == View.VISIBLE) {
			emotionViewController.hideEmotionView();
			displayOptions(true);
		} else {
			super.onBackPressed();
		}
	}

    @Override
	protected void onResume() {
		super.onResume();
		emotionViewController.hideEmotionView();
		displayOptions(true);
	}

    public void displayOptions(boolean isDisplay) {
    	LinearLayout llOptions = (LinearLayout) findViewById(R.id.llOptions);
		if (isDisplay) {
			llOptions.setVisibility(View.VISIBLE);
		} else {
			llOptions.setVisibility(View.GONE);
		}
    }

    private void gotoUrl(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public void setRetweet(boolean isRetweet) {
        this.isRetweet = isRetweet;
    }

    public Comment getRecomment() {
        return recomment;
    }

    public void setRecomment(Comment recomment) {
        this.recomment = recomment;
    }

    public boolean isCommentToOrigin() {
        return isCommentToOrigin;
    }

    public void setCommentToOrigin(boolean isCommentToOrigin) {
        this.isCommentToOrigin = isCommentToOrigin;
    }

	public EmotionViewController getEmotionViewController() {
		return emotionViewController;
	}
}
