package net.dev123.yibo;

import net.dev123.mblog.entity.User;
import net.dev123.yibo.common.theme.ThemeUtil;
import net.dev123.yibo.db.LocalAccount;
import net.dev123.yibo.service.adapter.ConversationListAdapter;
import net.dev123.yibo.service.listener.ConversationItemClickListener;
import net.dev123.yibo.service.listener.ConversationSendClickListener;
import net.dev123.yibo.service.listener.DirectMessageRecyclerListener;
import net.dev123.yibo.service.listener.GoBackClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ConversationActivity extends BaseActivity {
	private YiBoApplication yibo;
    private ConversationListAdapter adapter = null;
    
	private User user;
    private LocalAccount account;

	private ListView lvDirectMessage;

	private DirectMessageRecyclerListener directMessageRecyclerListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);

		yibo = (YiBoApplication) getApplication();
		initComponents();
		bindEvent();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		lvDirectMessage = (ListView)this.findViewById(R.id.lvDirectMessage);
		LinearLayout llFooterAction = (LinearLayout)findViewById(R.id.llFooterAction);
		EditText etText = (EditText)findViewById(R.id.etText);
		Button btnSend = (Button)this.findViewById(R.id.btnSend);
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ThemeUtil.setContentBackground(lvDirectMessage);
		ThemeUtil.setListViewStyle(lvDirectMessage);
		ThemeUtil.setFooterAction(llFooterAction);
		ThemeUtil.setBtnActionPositive(btnSend);
		ThemeUtil.setEditText(etText);
		
		setBack2Top(lvDirectMessage);
		
		Intent intent = this.getIntent();
		user = (User)intent.getSerializableExtra("USER");
        if (user == null) {
        	return;
        }
        if (account == null) {
        	account = yibo.getCurrentAccount();
        }

		adapter = new ConversationListAdapter(this, account, user);

		lvDirectMessage.setFastScrollEnabled(yibo.isSliderEnabled());
		lvDirectMessage.setAdapter(adapter);
        lvDirectMessage.setOnItemClickListener(new ConversationItemClickListener(this));
        
		directMessageRecyclerListener = new DirectMessageRecyclerListener();
		lvDirectMessage.setRecyclerListener(directMessageRecyclerListener);

		TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
		tvTitle.setText(user.getScreenName());
	}

	private void bindEvent() {
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new GoBackClickListener());

		Button btnSend = (Button)this.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new ConversationSendClickListener(this));
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
