package com.shejiaomao.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.FeaturePatternUtils;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.adapter.UserSuggestAdapter;
import com.shejiaomao.weibo.service.listener.EditMicroBlogMentionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTextDeleteClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTokenizer;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTopicClickListener;
import com.shejiaomao.weibo.service.listener.EditRetweetEmotionClickListener;
import com.shejiaomao.weibo.service.listener.EditRetweetSendClickListener;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogTextWatcher;
import com.shejiaomao.weibo.widget.EmotionViewController;

public class EditRetweetActivity extends BaseActivity {

	private Integer type;
	private Status status;
    private Status retweetedStatus;

    private boolean isComment = false;
    private boolean isCommentToOrigin = false;

    private CheckBox cbComment = null;
    private CheckBox cbCommentToOrigin = null;
    private TextView tvText = null;
    
    private EmotionViewController emotionViewController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_retweet);

		if (!getIntent().hasExtra("STATUS")) {
			finish();
		}

		//默认不弹出输入法
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initParams();
		initComponents();
		bindEvent();
	}

	private void initParams() {
		Intent intent = getIntent();

		type = intent.getIntExtra("TYPE", Constants.EDIT_TYPE_RETWEET);
	    status = (Status)intent.getSerializableExtra("STATUS");
	    emotionViewController = new EmotionViewController(this);
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		LinearLayout llContentPanel = (LinearLayout)findViewById(R.id.llContentPanel);
		LinearLayout llEditText = (LinearLayout)findViewById(R.id.llEditText);
		MultiAutoCompleteTextView etText  = (MultiAutoCompleteTextView)findViewById(R.id.etText);
		Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
		Button btnMention = (Button)this.findViewById(R.id.btnMention);
		Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
		Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
		cbComment = (CheckBox) this.findViewById(R.id.cbComment);
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
		cbComment.setButtonDrawable(theme.getDrawable("selector_checkbox"));
		cbComment.setTextColor(theme.getColor("content"));
		cbCommentToOrigin.setButtonDrawable(theme.getDrawable("selector_checkbox"));
		cbCommentToOrigin.setTextColor(theme.getColor("content"));
		tvText.setTextColor(theme.getColor("quote"));
		
		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_retweet);
         
		MicroBlogTextWatcher textWatcher = new MicroBlogTextWatcher(this); 
		etText.addTextChangedListener(textWatcher);
		etText.setHint(R.string.hint_retweet);
		etText.requestFocus();
		etText.setAdapter(new UserSuggestAdapter(this));
		etText.setTokenizer(new EditMicroBlogTokenizer());
		
		retweetedStatus = status;
		if (status.getServiceProvider() != ServiceProvider.Sohu) {
			if (status.getServiceProvider() == ServiceProvider.Fanfou
				|| status.getRetweetedStatus() != null) {
				etText.setText(
					String.format(
						FeaturePatternUtils.getRetweetFormat(status.getServiceProvider()),
						FeaturePatternUtils.getRetweetSeparator(status.getServiceProvider()),
						status.getUser().getMentionName(),
						status.getText()
					)
				);
			}
			if (!(status.getServiceProvider() == ServiceProvider.Fanfou
				|| status.getRetweetedStatus() == null)) {
				retweetedStatus = status.getRetweetedStatus();
			}
			etText.setSelection(0);
		}

		int length = StringUtil.getLengthByByte(etText.getText().toString());
        int leavings = (int) Math.floor((double) (Constants.STATUS_TEXT_MAX_LENGTH * 2 - length) / 2);
        btnTextCount.setText((leavings < 0 ? "-" : "") + Math.abs(leavings));

		String lableComment = this.getString(R.string.label_retweet_with_comment, 
			status.getUser().getScreenName());
		cbComment.setText(lableComment);

		if (isComment2OriginVisible()) {
			String lableCommentToOrigin = this.getString(
				R.string.label_retweet_with_comment_to_origin,
				retweetedStatus.getUser().getScreenName());
			cbCommentToOrigin.setText(lableCommentToOrigin);
			cbCommentToOrigin.setVisibility(View.VISIBLE);
		}

		String promptText = retweetedStatus.getUser().getMentionTitleName()
			+ ":" + retweetedStatus.getText();
	    tvText.setText(promptText);
	}

	public boolean isComment2OriginVisible() {
    	return status != null && status.getRetweetedStatus() != null;
    }

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		EditText etText = (EditText)this.findViewById(R.id.etText);
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
		btnSend.setOnClickListener(new EditRetweetSendClickListener(this));

		Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
		btnTopic.setOnClickListener(new EditMicroBlogTopicClickListener(this));

		Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
		btnEmotion.setOnClickListener(new EditRetweetEmotionClickListener(this));

	    Button btnMention = (Button)this.findViewById(R.id.btnMention);
	    btnMention.setOnClickListener(new EditMicroBlogMentionClickListener());

	    Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
	    btnTextCount.setOnClickListener(new EditMicroBlogTextDeleteClickListener(this));

        CheckBox cbComment = (CheckBox)this.findViewById(R.id.cbComment);
        cbComment.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isComment = isChecked;
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case Constants.REQUEST_CODE_IMG_SELECTOR:
		case Constants.REQUEST_CODE_CAMERA:
			break;
		case Constants.REQUEST_CODE_USER_SELECTOR:
			if (resultCode == Constants.RESULT_CODE_SUCCESS) {
				List<User> userList = (List<User>)data.getSerializableExtra("LIST_USER");
                if (ListUtil.isEmpty(userList)) {
                	userList = new ArrayList<User>();
                }
				MultiAutoCompleteTextView etText = (MultiAutoCompleteTextView)this.findViewById(R.id.etText);
				StringBuilder mentions = new StringBuilder();
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
    
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public Status getRetweetedStatus() {
		return retweetedStatus;
	}

	public boolean isComment() {
		return isComment;
	}

	public boolean isCommentToOrigin() {
		return isCommentToOrigin;
	}

	public EmotionViewController getEmotionViewController() {
		return emotionViewController;
	}
}
