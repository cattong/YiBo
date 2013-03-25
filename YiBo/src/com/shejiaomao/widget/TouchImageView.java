package com.shejiaomao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.cattong.commons.Logger;

public class TouchImageView extends ImageViewTouchBase {
    private static final String TAG = TouchImageView.class.getSimpleName();

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private boolean mScaleGesture;
    private float mScaleFactor = 1.0F;

    public TouchImageView(Context context) {
        super(context);
        init(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

	@Override
	public void zoomIn() {
		super.zoomIn();
		mScaleFactor = getScale();
	}

	@Override
	public void zoomOut() {
		super.zoomOut();
		mScaleFactor = getScale();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (Logger.isDebug()) {
            Log.d(TAG, "onTouchEvent……");
            dumpEvent(event);
        }

    	int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (event.getPointerCount() == 2
            && (action == MotionEvent.ACTION_POINTER_1_DOWN
                || action == MotionEvent.ACTION_POINTER_2_DOWN )) {
        	mScaleGesture = true;
        } else if (event.getPointerCount() == 1
        		   && action == MotionEvent.ACTION_DOWN) {
        	mScaleGesture = false;
        }

        if (mScaleGesture) {
            mScaleGestureDetector.onTouchEvent(event);
        } else {
        	mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    private void dumpEvent(MotionEvent event) {
		String[] names = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION.").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(", ").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount()) {
				sb.append(":");
			}
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    	@Override
        public boolean onDoubleTap(MotionEvent e) {
    		if (mScaleFactor == 1.0F) {
    			mScaleFactor = 3.0F;
    		} else {
    			mScaleFactor = 1.0F;
    		}
    		float posX = e.getX();
            float posY = e.getY();
            posX -= (getWidth() / 2);
            posY -= (getHeight() / 2);
            postTranslate(-posX, -posY);
    		zoomTo(mScaleFactor);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            postTranslate(-distanceX, -distanceY);
            center(true, true);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
        	performClick();
        	return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        	performLongClick();
        }
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    	@Override
        public boolean onScale(ScaleGestureDetector detector) {
             mScaleFactor *= detector.getScaleFactor();
             // Don't let the object get too small or too large.
             mScaleFactor = Math.max(1.0F, Math.min(mScaleFactor, maxZoom()));
             if (Logger.isDebug()) {
            	 Log.d(TAG, "Zoom To : " + mScaleFactor);
             }
             zoomTo(mScaleFactor);
             return true;
        }

    }

}
