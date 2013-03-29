package com.shejiaomao.weibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.SelectMode;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.adapter.UserSuggestAdapter;
import com.shejiaomao.weibo.service.listener.EditDirectMessageSendClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogEmotionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogMentionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTextDeleteClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTopicClickListener;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogTextWatcher;
import com.shejiaomao.weibo.widget.EmotionViewController;

public class EditDirectMessageActivity extends BaseActivity {
    private String displayName = null;

    private EmotionViewController emotionViewController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_direct_message);

		Bundle bundle = this.getIntent().getExtras();
		displayName = bundle.getString("DISPLAY_NAME");

		initComponents();
		bindEvent();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		LinearLayout llContentPanel = (LinearLayout)findViewById(R.id.llContentPanel);
		AutoCompleteTextView etDisplayName = (AutoCompleteTextView)this.findViewById(R.id.etDisplayName);
		Button btnUserSelector = (Button)this.findViewById(R.id.btnUserSelector);
		LinearLayout llEditText = (LinearLayout)findViewById(R.id.llEditText);
		MultiAutoCompleteTextView etText  = (MultiAutoCompleteTextView)findViewById(R.id.etText);
		Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
		Button btnMention = (Button)this.findViewById(R.id.btnMention);
		Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
		Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
		
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(llContentPanel);
		int padding6 = theme.dip2px(6);
		int padding8 = theme.dip2px(8);
		llContentPanel.setPadding(padding6, padding8, padding6, 0);
		etDisplayName.setBackgroundDrawable(theme.getDrawable("bg_input_frame_left_half"));
		etDisplayName.setTextColor(theme.getColor("content"));
		btnUserSelector.setBackgroundDrawable(theme.getDrawable("selector_btn_message_user"));
		llEditText.setBackgroundDrawable(theme.getDrawable("bg_input_frame_normal"));
		etText.setTextColor(theme.getColor("content"));
		btnEmotion.setBackgroundDrawable(theme.getDrawable("selector_btn_emotion"));
		btnMention.setBackgroundDrawable(theme.getDrawable("selector_btn_mention"));
		btnTopic.setBackgroundDrawable(theme.getDrawable("selector_btn_topic"));
		btnTextCount.setBackgroundDrawable(theme.getDrawable("selector_btn_text_count"));
		btnTextCount.setPadding(padding6, 0, theme.dip2px(20), 0);
		btnTextCount.setTextColor(theme.getColor("status_capability"));
		
		
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_edit_direct_message);
		if (StringUtil.isNotEmpty(displayName)) {
			etDisplayName.setText(displayName);
			etText.requestFocus();
		}
		etDisplayName.setAdapter(new UserSuggestAdapter(this));
		etDisplayName.setOnTouchListener(hideEmotionGridListener);

		int length = StringUtil.getLengthByByte(etText.getText().toString());
        int leavings = (int)Math.floor((double)(Constants.STATUS_TEXT_MAX_LENGTH * 2 - length) / 2);
        btnTextCount.setText((leavings < 0 ? "-" : "") + Math.abs(leavings));
        
        emotionViewController = new EmotionViewController(this);
	}

	private View.OnTouchListener hideEmotionGridListener = new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			emotionViewController.hideEmotionView();
			return false;
		}
		
	};
	
	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnSend = (Button) this.findViewById(R.id.btnOperate);
		btnSend.setText(R.string.label_send);
		btnSend.setVisibility(View.VISIBLE);
		btnSend.setOnClickListener(new EditDirectMessageSendClickListener(this));

		Button btnUserSelector = (Button) this.findViewById(R.id.btnUserSelector);
		//btnUserSelector.setOnClickListener(new EditDirectMessageUserSelectorClickListener(this));
		EditMicroBlogMentionClickListener userSelectorListener = new EditMicroBlogMentionClickListener();
		userSelectorListener.setRequestCode(Constants.REQUEST_CODE_USER_SELECTOR_MESSAGE);
		userSelectorListener.setSelectMode(SelectMode.Single);
		userSelectorListener.setTitleId(R.string.title_select_recipient);
		btnUserSelector.setOnClickListener(userSelectorListener);

		EditText etText = (EditText) this.findViewById(R.id.etText);
		etText.addTextChangedListener(new MicroBlogTextWatcher(this));
		etText.setOnTouchListener(hideEmotionGridListener);

		Button btnTopic = (Button) this.findViewById(R.id.btnTopic);
		btnTopic.setOnClickListener(new EditMicroBlogTopicClickListener(this));

		Button btnEmotion = (Button) this.findViewById(R.id.btnEmotion);
		btnEmotion.setOnClickListener(new EditMicroBlogEmotionClickListener(this));

	    Button btnMention = (Button) this.findViewById(R.id.btnMention);
	    btnMention.setOnClickListener(new EditMicroBlogMentionClickListener());

	    Button btnTextCount = (Button) this.findViewById(R.id.btnTextCount);
	    btnTextCount.setOnClickListener(new EditMicroBlogTextDeleteClickListener(this));
	}

    @SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		List<User> userList = null;
		switch (requestCode) {
		case Constants.REQUEST_CODE_USER_SELECTOR_MESSAGE:
			if (resultCode != Constants.RESULT_CODE_SUCCESS) {
				break;
			}
			userList = (List<User>)data.getSerializableExtra("LIST_USER");
            if (ListUtil.isEmpty(userList)) {
            	userList = new ArrayList<User>();
            }
			AutoCompleteTextView etDisplayName = 
				(AutoCompleteTextView)this.findViewById(R.id.etDisplayName);
			//String recipientName = etDisplayName.getText().toString().trim();
			StringBuilder newRecipientName = new StringBuilder();

			for (User user : userList) {
				if (newRecipientName.length() == 0) {
					newRecipientName.append(user.getDisplayName());
				} else {
					newRecipientName.append(Constants.SEPARATOR_RECEIVER)
					    .append(user.getDisplayName());
				}
			}

			etDisplayName.setText(newRecipientName.toString());
			etDisplayName.setSelection(newRecipientName.length());
			break;
		case Constants.REQUEST_CODE_USER_SELECTOR:
			if (resultCode != Constants.RESULT_CODE_SUCCESS) {
				break;
			}
			userList = (List<User>)data.getSerializableExtra("LIST_USER");
            if (ListUtil.isEmpty(userList)) {
            	userList = new ArrayList<User>();
            }
			MultiAutoCompleteTextView etText  = (MultiAutoCompleteTextView)this.findViewById(R.id.etText);
			StringBuilder mentions = new StringBuilder("");
			for (User user : userList) {
				mentions.append(user.getMentionName()).append(" ");
			}
			int currentPos = etText.getSelectionStart();
			etText.getText().insert(currentPos, mentions);
			break;
		default:
			break;
		}
    }

    @Override
	public void onBackPressed() {
    	if (emotionViewController.getEmotionViewVisibility() == View.VISIBLE) {
			emotionViewController.hideEmotionView();
		} else {
			super.onBackPressed();
		}
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		emotionViewController.hideEmotionView();
	}

	public EmotionViewController getEmotionViewController() {
		return emotionViewController;
	}

}
