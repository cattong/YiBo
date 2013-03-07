package com.shejiaomao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TouchImageViewV4 extends ImageViewTouchBase {

    private GestureDetector mGestureDetector;
    private float mScaleFactor = 1.0F;

    public TouchImageViewV4(Context context) {
        super(context);
        init(context);
    }

    public TouchImageViewV4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
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
        mGestureDetector.onTouchEvent(event);
        return true;
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

}
