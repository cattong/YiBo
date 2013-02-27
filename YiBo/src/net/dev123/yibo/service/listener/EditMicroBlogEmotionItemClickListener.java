package net.dev123.yibo.service.listener;

import net.dev123.commons.util.StringUtil;
import net.dev123.yibo.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class EditMicroBlogEmotionItemClickListener implements
		OnItemClickListener {
    private Context context;
	public EditMicroBlogEmotionItemClickListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
		String emotion = (String)adapter.getItem(position);
        if (StringUtil.isEmpty(emotion)) {
        	return;
        }
        
		EditText etText = (EditText)((Activity)context).findViewById(R.id.etText);
		int currentPos = 0;
		if (etText != null) {
			currentPos = etText.getSelectionStart();
			etText.getText().insert(currentPos, emotion);
		}

	}

}
