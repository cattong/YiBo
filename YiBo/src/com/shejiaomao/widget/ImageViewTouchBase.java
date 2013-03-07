package com.shejiaomao.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

public abstract class ImageViewTouchBase extends ImageView {

    @SuppressWarnings("unused")
    private static final String TAG = ImageViewTouchBase.class.getSimpleName();

    // This is the base transformation which is used to show the image
    // initially.  The current computation for this shows the image in
    // it's entirety, letterboxing as needed.  One could choose to
    // show the image as cropped instead.
    //
    // This matrix is recomputed when we go from the thumbnail image to
    // the full size image.
    // 这是用于显示初始图片的基本变换矩阵，此矩阵的当前计算值将按需以完整模式或宽屏模式显示图片。
    // 可以选择裁剪图片作为替代。
    // 此矩阵在从缩略图变为全尺寸图时会被重新计算
    // Letterboxing is a technique used to display widescreen content on
    // a traditional 4x3 screen such as those used by old-style television sets.
    // The technique is very simple — the picture frame is reduced in size until
    // it fits the screen, leaving black bars above and below the picture.
    // The effect is like looking through the slot of a letterbox, hence the name.
    protected Matrix mBaseMatrix = new Matrix();

    // This is the supplementary transformation which reflects what
    // the user has done in terms of zooming and panning.
    // This matrix remains the same when we go from the thumbnail image
    // to the full size image.
    // 这是增补变换矩阵，反映用户的缩放和平移变化；在从缩略图变为全尺寸图时保持不变
    protected Matrix mSuppMatrix = new Matrix();

    // This is the final matrix which is computed as the concatenation
    // of the base matrix and the supplementary matrix.
    // 这是最终矩阵，由基本矩阵和增补矩阵进行连接运算的结果。
    private final Matrix mDisplayMatrix = new Matrix();

    // Temporary buffer used for getting the values out of a matrix.
    // 获取矩阵值的临时缓存
    private final float[] mMatrixValues = new float[9];

    // The current bitmap being displayed.
    protected Bitmap mBitmapDisplayed = null;

	protected int mRotation;

	protected int mThisWidth = -1, mThisHeight = -1;

    protected float mMaxZoom;

    // ImageViewTouchBase will pass a Bitmap to the Recycler if it has finished
    // its use of that Bitmap.
    public interface Recycler {
        public void recycle(Bitmap b);
    }

    public void setRecycler(Recycler r) {
        mRecycler = r;
    }

    private Recycler mRecycler;

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mThisWidth = right - left;
        mThisHeight = bottom - top;
        Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        }
        if (mBitmapDisplayed != null) {
        	getProperBaseMatrix(mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
            mMaxZoom = maxZoom();
            center(true, true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getScale() > 1.0F) {
            // If we're zoomed in, pressing Back jumps out to show the entire
            // image, otherwise Back returns the user to the gallery.
            zoomTo(1.0F);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected Handler mHandler = new Handler();

    protected int mLastXTouchPos;
    protected int mLastYTouchPos;

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, mRotation);
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = getDrawable();
        if (d != null) {
            d.setDither(true);
        }

        Bitmap old = mBitmapDisplayed;
        mBitmapDisplayed = bitmap;
        mRotation = rotation % 360;

        if (old != null && old != bitmap && mRecycler != null) {
            mRecycler.recycle(old);
        }
    }

    public void clear() {
        setImageBitmapResetBase(null, 0, true);
    }

    private Runnable mOnLayoutRunnable = null;

    // This function changes bitmap, reset base matrix according to the size
    // of the bitmap, and optionally reset the supplementary matrix.
    public void setImageBitmapResetBase(final Bitmap bitmap, final int rotation,
            final boolean resetSupp) {
        final int viewWidth = getWidth();

        if (viewWidth <= 0)  {
            mOnLayoutRunnable = new Runnable() {
                public void run() {
                    setImageBitmapResetBase(bitmap, mRotation, resetSupp);
                }
            };
            return;
        }

        if (bitmap != null) {
        	setImageBitmap(bitmap, rotation);
            getProperBaseMatrix(mBaseMatrix);
        } else {
            mBaseMatrix.reset();
            setImageBitmap(null, 0);
        }

        if (resetSupp) {
            mSuppMatrix.reset();
        }
        setImageMatrix(getImageViewMatrix());
        mMaxZoom = maxZoom();
    }

    // Center as much as possible in one or both axis.  Centering is
    // defined as follows:  if the image is scaled down below the
    // view's dimensions then center it (literally).  If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    public void center(boolean horizontal, boolean vertical) {
        if (mBitmapDisplayed == null) {
            return;
        }

        Matrix m = getImageViewMatrix();

        RectF rect = new RectF(0, 0,
                mBitmapDisplayed.getWidth(),
                mBitmapDisplayed.getHeight());

        m.mapRect(rect);

        float height = rect.height();
        float width  = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }

        postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }

    public ImageViewTouchBase(Context context) {
        super(context);
        init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    // Get the scale factor out of the matrix.
    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    public float getScale() {
        return getScale(mSuppMatrix);
    }

    // Setup the base matrix so that the image is centered and scaled properly.
    // 现修改为缩放以宽缩放比例为基准
    private void getProperBaseMatrix(Matrix matrix) {
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float w = getBitmapDisplayedWidth();
        float h = getBitmapDisplayedHeight();
        matrix.reset();

        float widthScale = Math.min(viewWidth / w, 1.0f);
        // float heightScale = Math.min(viewHeight / h, 1.0f);
        // float scale = Math.min(widthScale, heightScale);
        float scale = widthScale;

        matrix.postConcat(getRotateMatrix());
        matrix.postScale(scale, scale);

        matrix.postTranslate(
                (viewWidth  - w * scale) / 2F,
                // (viewHeight - h * scale) / 2F);
                Math.max((viewHeight - h * scale), 0) / 2F);
    }

    private Matrix getRotateMatrix() {
        // By default this is an identity matrix.
        Matrix matrix = new Matrix();
        if (mRotation != 0 && mBitmapDisplayed != null) {
            // We want to do the rotation at origin, but since the bounding
            // rectangle will be changed after rotation, so the delta values
            // are based on old & new width/height respectively.
            int cx = mBitmapDisplayed.getWidth() / 2;
            int cy = mBitmapDisplayed.getHeight() / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);
            matrix.postTranslate(getBitmapDisplayedWidth() / 2, getBitmapDisplayedHeight() / 2);
        }
        return matrix;
    }

    // Combine the base matrix and the supp matrix to make the final matrix.
    // 基本矩阵和增补矩阵连接运算得到最终矩阵
    protected Matrix getImageViewMatrix() {
        // The final matrix is computed as the concatenation of the base matrix
        // and the supplementary matrix.
    	// 最终矩阵由基矩阵和增补矩阵连接运算而成
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    static final float SCALE_RATE = 1.25F;

    // Sets the maximum zoom, which is a scale relative to the base matrix. It
    // is calculated to show the image at 400% zoom regardless of screen or
    // image orientation. If in the future we decode the full 3 megapixel image,
    // rather than the current 1024x768, this should be changed down to 200%.
    protected float maxZoom() {
        if (mBitmapDisplayed == null) {
            return 1F;
        }

        float fw = (float) getBitmapDisplayedWidth()  / (float) mThisWidth;
        float fh = (float) getBitmapDisplayedHeight() / (float) mThisHeight;
        float max = Math.max(Math.max(fw, fh), 1.0F) * 4;
        return max;
    }

    private boolean isOrientationChanged() {
        return (mRotation / 90) % 2 != 0;
    }

    private int getBitmapDisplayedHeight() {
        if (isOrientationChanged()) {
            return mBitmapDisplayed.getWidth();
        } else {
            return mBitmapDisplayed.getHeight();
        }
    }

    private int getBitmapDisplayedWidth() {
        if (isOrientationChanged()) {
            return mBitmapDisplayed.getHeight();
        } else {
            return mBitmapDisplayed.getWidth();
        }
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }

        float oldScale = getScale();
        float deltaScale = scale / oldScale;

        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    protected void zoomTo(final float scale, final float centerX,
                          final float centerY, final float durationMs) {
        final float incrementPerMs = (scale - getScale()) / durationMs;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();

        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float target = oldScale + (incrementPerMs * currentMs);
                zoomTo(target, centerX, centerY);

                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }

    protected void zoomTo(float scale) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy);
    }

    public void zoomIn() {
        zoomIn(SCALE_RATE);
    }

    public void zoomOut() {
        zoomOut(SCALE_RATE);
    }

    protected void zoomIn(float rate) {
        if (getScale() >= mMaxZoom) {
            return;     // Don't let the user zoom into the molecular level.
        }
        if (mBitmapDisplayed == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        mSuppMatrix.postScale(rate, rate, cx, cy);
        setImageMatrix(getImageViewMatrix());
    }

    protected void zoomOut(float rate) {
        if (mBitmapDisplayed == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        // Zoom out to at most 1x.
        Matrix tmp = new Matrix(mSuppMatrix);
        tmp.postScale(1F / rate, 1F / rate, cx, cy);

        if (getScale(tmp) < 1F) {
            mSuppMatrix.setScale(1F, 1F, cx, cy);
        } else {
            mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
        }
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    protected void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
    }

    protected void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    public void rotate(int rotation) {
    	rotateTo(mRotation + rotation);
    }

    public void rotateTo(int rotation) {
    	mRotation = rotation % 360;
    	if (mBitmapDisplayed == null) {
            return;
        }
    	getProperBaseMatrix(mBaseMatrix);
        setImageMatrix(getImageViewMatrix());
        mMaxZoom = maxZoom();
        center(true, true);
    }

	public int getRotation() {
		return mRotation;
	}

}
