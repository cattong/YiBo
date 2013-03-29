package com.shejiaomao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.common.theme.Theme;


public class PullToRefreshListView extends ListView implements OnScrollListener {
	private static final String TAG = "PullToRefreshListView";
    private static final boolean DEBUG = false;
    
    private static final int TAP_TO_REFRESH      = 1;
    private static final int PULL_TO_REFRESH     = 2;
    private static final int RELEASE_TO_REFRESH  = 3;
    private static final int REFRESHING          = 4;
    
    private OnRefreshListener mOnRefreshListener;

    /**
     * Listener that will receive notifications every time the list scrolls.
     */
    private OnScrollListener mOnScrollListener;

    private LinearLayout mRefreshView;
    private TextView mRefreshViewText;
    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress;
    private TextView mRefreshViewLastUpdated;

    private int mCurrentScrollState;
    private int mRefreshState;

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    private int mRefreshViewHeight;
    private int mRefreshOriginalTopPadding;
//    private int mLastMotionY;

    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(180, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mRefreshView = (LinearLayout) mInflater.inflate(
                R.layout.widget_pull_to_refresh_header, null);

        mRefreshViewText =
            (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
        mRefreshViewImage =
            (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_image);
        mRefreshViewProgress =
            (ProgressBar) mRefreshView.findViewById(R.id.pull_to_refresh_progress);
        mRefreshViewLastUpdated =
            (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_updated_at);

        Theme theme = new Theme(context);
        mRefreshViewText.setTextColor(theme.getColor("content"));
        
        mRefreshViewImage.setMinimumHeight(50);
        mRefreshView.setOnClickListener(new OnClickRefreshListener());
        mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();

        mRefreshState = TAP_TO_REFRESH;

        addHeaderView(mRefreshView);

        super.setOnScrollListener(this);

        measureView(mRefreshView);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //setSelection(1);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l The scroll listener.
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     *
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated.
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText(lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        final int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }

                if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
                    if (mRefreshView.getBottom() > mRefreshViewHeight
                        || mRefreshView.getTop() >= 0
                        && mRefreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        mRefreshState = REFRESHING;
                        prepareForRefresh();
                        onRefresh();
                    } else if (mRefreshView.getBottom() < mRefreshViewHeight) {
                        // Abort refresh and scroll down below the refresh view
                        resetHeader();
                        setSelection(1);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
//                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
//            	if (mRefreshState == PULL_TO_REFRESH || mRefreshState == RELEASE_TO_REFRESH) {
//                    applyHeaderPadding(event);
//            	}
                break;
        }
        return super.onTouchEvent(event);
    }

//    private void applyHeaderPadding(MotionEvent ev) {
//    	if (mRefreshState != PULL_TO_REFRESH && mRefreshState != RELEASE_TO_REFRESH) {
//    		return;
//    	}
//    	
//    	if (mRefreshView.getTop() < 0) {
//    		return;
//    	}
//    	
//        int historySize = ev.getHistorySize();
//
//        int historicalY = 0;
//        int topPadding = 0;
//        for (int h = 0; h < historySize; h++) {
//            historicalY = 0;
//            try {
//            	historicalY = (int)ev.getHistoricalY(h);
//            } catch (IllegalArgumentException e) {
//            	Log.e(TAG, e.getMessage(), e);
//            }
//
//            int distance = historicalY - mLastMotionY;
//            if (mRefreshView.getTop() >= 0) {
//            	topPadding = mRefreshView.getPaddingTop() + distance;
//            	mRefreshView.setPadding(
//                    mRefreshView.getPaddingLeft(), topPadding,
//                    mRefreshView.getPaddingRight(), mRefreshView.getPaddingBottom());
//            }
//            
//            mLastMotionY = historicalY;            
//        }
//    }

    /**
     * Sets the header padding back to original size.
     */
    private void resetHeaderPadding(int topPadding) {
        mRefreshView.setPadding(
            mRefreshView.getPaddingLeft(), mRefreshOriginalTopPadding + topPadding,
            mRefreshView.getPaddingRight(), mRefreshView.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.
     */
    private void resetHeader() {
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH;

            resetHeaderPadding(0);

            // Set refresh view text to the pull label
            mRefreshViewText.setText(R.string.label_pull_to_refresh_tap);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.widget_pull_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (firstVisibleItem == 0
        	&& mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
            && mRefreshState != REFRESHING) {          
        	if (mRefreshViewImage.getVisibility() != View.VISIBLE) {
        		resetHeaderPadding(180);
        		setSelection(1);
                mRefreshViewImage.setVisibility(View.VISIBLE);
        	}
            if (mRefreshState != PULL_TO_REFRESH
            	&& mRefreshView.getBottom() < mRefreshViewHeight + 5) {
                mRefreshViewText.setText(R.string.label_pull_to_refresh);
                if (mRefreshState != TAP_TO_REFRESH) {
                    mRefreshViewImage.clearAnimation();
                    mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                }
                mRefreshState = PULL_TO_REFRESH;
            } else if (
            	mRefreshState != RELEASE_TO_REFRESH
            	&& (mRefreshView.getBottom() > mRefreshViewHeight + 5
                     || mRefreshView.getTop() >= 0)) {
                    mRefreshViewText.setText(R.string.label_release_to_refresh);
                    mRefreshViewImage.clearAnimation();
                    mRefreshViewImage.startAnimation(mFlipAnimation);
                    mRefreshState = RELEASE_TO_REFRESH;
            }
        } else if (
        	firstVisibleItem == 0
        	&& mCurrentScrollState == SCROLL_STATE_FLING
            && mRefreshState != REFRESHING) {
            setSelection(1);
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, 
            	visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void prepareForRefresh() {
        resetHeaderPadding(0);
        setSelectionFromTop(0, 0);
        mRefreshViewImage.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
        mRefreshViewImage.setImageDrawable(null);
        mRefreshViewProgress.setVisibility(View.VISIBLE);

        // Set refresh view text to the refreshing label
        mRefreshViewText.setText(R.string.label_refreshing);

        mRefreshState = REFRESHING;
    }

    public void onRefresh() {
        if (DEBUG) Log.d(TAG, "onRefresh");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    /**
     * Resets the list to a normal state after a refresh.
     * @param lastUpdated Last updated at.
     */
    public void onRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onRefreshComplete();
    }

    /**
     * Resets the list to a normal state after a refresh.
     */
    public void onRefreshComplete() {
        if (DEBUG) Log.d(TAG, "onRefreshComplete");

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (this.getFirstVisiblePosition() < 1) {
            invalidateViews();
            setSelection(1);
        }
    }

    protected float getTopFadingEdgeStrength() {
        return 0.2f;
    }
    
    protected float getBottomFadingEdgeStrength() {
        return 0.2f;
    }
    
    /**
     * Invoked when the refresh view is clicked on. This is mainly used when
     * there's only a few items in the list and it's not possible to drag the
     * list.
     */
    private class OnClickRefreshListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mRefreshState != REFRESHING) {
                prepareForRefresh();
                onRefresh();
            }
        }

    }

    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     */
    public interface OnRefreshListener {
        /**
         * Called when the list should be refreshed.
         * <p>
         * A call to {@link PullToRefreshListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh(PullToRefreshListView list);
    }
}
