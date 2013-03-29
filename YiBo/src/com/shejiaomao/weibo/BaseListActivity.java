package com.shejiaomao.weibo;

import com.shejiaomao.maobo.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class BaseListActivity extends BaseActivity {
    public static final int STATE_LOADING = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_NO_MORE = 3;
    
    private View listItemFooter;
    
    protected abstract ListView getListView();
    
	protected abstract OnClickListener createLoadMoreClickListener();
	
	public void showListFooter(int footerState) {
		ListView listView = getListView();
		if (listView == null) {
			return;
		}
		
		if (listItemFooter == null) {
			listItemFooter = getLayoutInflater().inflate(R.layout.list_item_footer, null);			
			listView.addFooterView(listItemFooter);
			
			LinearLayout llFooterLoadMore = (LinearLayout)listItemFooter.findViewById(R.id.llFooterLoadMore);
			llFooterLoadMore.setOnClickListener(createLoadMoreClickListener());
		}
		
		LinearLayout llFooterLoading = (LinearLayout)listItemFooter.findViewById(R.id.llFooterLoading);
		LinearLayout llFooterLoadMore = (LinearLayout)listItemFooter.findViewById(R.id.llFooterLoadMore);
		LinearLayout llFooterNoMore = (LinearLayout)listItemFooter.findViewById(R.id.llFooterNoMore);
		
		switch (footerState) {
		case STATE_LOADING:
			llFooterLoading.setVisibility(View.VISIBLE);
			llFooterLoadMore.setVisibility(View.GONE);
			llFooterNoMore.setVisibility(View.GONE);
			break;
		case STATE_LOAD_MORE:
			llFooterLoading.setVisibility(View.GONE);
			llFooterLoadMore.setVisibility(View.VISIBLE);
			llFooterNoMore.setVisibility(View.GONE);
			break;
		case STATE_NO_MORE:
			llFooterLoading.setVisibility(View.GONE);
			llFooterLoadMore.setVisibility(View.GONE);
			llFooterNoMore.setVisibility(View.VISIBLE);
			break;
		}
	}
}
