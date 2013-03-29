package com.shejiaomao.weibo.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.shejiaomao.maobo.R;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.activity.ImageViewerActivity.Mode;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.service.listener.ImageViewerSaveClickListener;
import com.shejiaomao.weibo.service.listener.SlideFinishOnGestureListener.SlideDirection;
import com.shejiaomao.weibo.widget.GifView;
import com.shejiaomao.weibo.widget.GifView.GifImageType;
import com.shejiaomao.widget.ImageViewTouchBase;

public class ImageViewer4GifActivity extends BaseActivity {
	private static final String TAG = ImageViewer4GifActivity.class.getSimpleName();

	private static final int RETRY_COUNT = 3;

    private boolean isGif;
    private boolean isFullScreen;
    private String imagePath;
	private GifView gifViewer;
	private boolean isInitialized;

	private ImageViewTouchBase ivImageViewer;

	private ImageView ivRotateLeft;
	private ImageView ivRotateRight;
	private ImageView ivZoomIn;
	private ImageView ivZoomOut;

	private Button btnOperate;
	private Mode mode = Mode.View;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.image_viewer);
		initComponent();
		bindEvent();
		if(Logger.isDebug()) {
			Log.d(TAG, "onCreate……");
		}
	}

	private void initComponent() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		LinearLayout llToolbar = (LinearLayout)findViewById(R.id.llToolbar);
		ivRotateLeft = (ImageView) findViewById(R.id.ivRotateLeft);
		ivRotateRight = (ImageView) findViewById(R.id.ivRotateRight);
		ivZoomIn = (ImageView) findViewById(R.id.ivZoomIn);
		ivZoomOut = (ImageView) findViewById(R.id.ivZoomOut);
		
		ThemeUtil.setSecondaryImageHeader(llHeaderBase);
		llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
		int padding8 = theme.dip2px(8);
		llToolbar.setPadding(padding8, padding8, padding8, padding8);
		ivRotateLeft.setBackgroundDrawable(theme.getDrawable("selector_btn_image_rotate_left"));
		ivRotateRight.setBackgroundDrawable(theme.getDrawable("selector_btn_image_rotate_right"));
		ivZoomIn.setBackgroundDrawable(theme.getDrawable("selector_btn_image_zoom_in"));
		ivZoomOut.setBackgroundDrawable(theme.getDrawable("selector_btn_image_zoom_out"));
		
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.title_image_viewer);

		btnOperate = (Button) findViewById(R.id.btnOperate);

		ivImageViewer = (ImageViewTouchBase) findViewById(R.id.ivImageViewer);
		ivImageViewer.setRecycler(new ImageViewTouchBase.Recycler() {
			@Override
			public void recycle(Bitmap b) {
				if (!(b == null || b.isRecycled())) {
					if (Logger.isDebug()) {
						Log.d(TAG, "Recycle Bitmap : " + b);
					}
					b.recycle();
				}
			}
		});

		ivImageViewer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mode == Mode.View) {
					updateView();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Logger.isDebug()) {
			Log.d(TAG, "onResume……");
		}
		if (isInitialized) {
			if (isGif) {
				gifViewer.showAnimation();
			}
		} else {
			initImageData();
		}
	}

	private void initImageData() {
		if(Logger.isDebug()) {
			Log.d(TAG, "initImageData……");
		}
		Uri uri = getIntent().getData();
		if (uri != null) {
			if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
				imagePath = uri.getPath();
			} else {
				imagePath = uri.toString();
			}

			mode = Mode.View;
		} else {
			imagePath = getIntent().getStringExtra("image-path");
			try {
				mode = Mode.valueOf(getIntent().getStringExtra("mode"));
			} catch (Exception e) {
				if (Logger.isDebug()) {
					Log.d(TAG, e.getMessage(), e);
				}
			}
		}

		if (Logger.isDebug()) {
			Log.d(TAG, "Image Path : " + imagePath);
		}

		if (StringUtil.isEmpty(imagePath)) {
			onBackPressed();
			return;
		}

		isGif = FileUtil.isGif(imagePath);
		if (isGif) {
			InputStream inputStream = getInputStreamFromFile(imagePath);
			if (inputStream == null) {
				onBackPressed();
				return;
			}

			gifViewer = new GifView(this, ivImageViewer);
			gifViewer.setGifImageType(GifImageType.SYNC_DECODER);
			gifViewer.setGifImage(inputStream);
			gifViewer.showAnimation();
		} else {
			uri = Uri.fromFile(new File(imagePath));
			Bitmap bitmap = getBitmapFromUri(uri);
			if (bitmap == null) {
				onBackPressed();
				return;
			}

			ivImageViewer.setImageBitmap(bitmap);
		}

		View.OnClickListener onClickListener = null;
		btnOperate.setVisibility(View.VISIBLE);
		if (mode == Mode.Edit) {
			btnOperate.setText(R.string.btn_delete);
			onClickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(Constants.RESULT_CODE_IMAGE_DELETED);
					finish();
				}
			};
		}else {
			btnOperate.setText(R.string.btn_save);
			onClickListener = new ImageViewerSaveClickListener(imagePath);
		}
		btnOperate.setOnClickListener(onClickListener);
		updateView();

		isInitialized = true;
	}

	private InputStream getInputStreamFromFile(String path) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(imagePath));
			inputStream = new BufferedInputStream(inputStream, 24576); // 24 * 1024
		} catch (FileNotFoundException e) {
			try {
				int retryCount = 0;
				while (inputStream == null && retryCount < RETRY_COUNT) {
					if (Logger.isDebug()) {
						Log.d(TAG, "Reload Image: " + retryCount + " : " + path);
					}

					Thread.sleep(500);
					inputStream = new FileInputStream(new File(imagePath));
					inputStream = new BufferedInputStream(inputStream, 24576); // 24 * 1024
					retryCount ++;
				}
			} catch (Exception ee) {
				if (Logger.isDebug()) {
					Log.d(TAG, ee.getMessage(), ee);
				}
			}
		} catch (Exception e) {
			if (Logger.isDebug()) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		
		return inputStream;
	}

	private Bitmap getBitmapFromUri(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = ImageUtil.createBitmapFromUri(this, uri.toString(), 1600, 1600);
		} catch (FileNotFoundException e){
			try {
				int retryCount = 0;
				while (bitmap == null && retryCount < RETRY_COUNT) {
					if (Logger.isDebug()) {
						Log.d(TAG, "Reload Image: " + retryCount + " : " + uri.toString());
					}

					Thread.sleep(500);
					bitmap = ImageUtil.createBitmapFromUri(this, uri.toString(), 1600, 1600);
					retryCount ++;
				}
			} catch (Exception ee) {
				if (Logger.isDebug()) {
					Log.d(TAG, ee.getMessage(), ee);
				}
			}
		} catch (Exception e) {
			if (Logger.isDebug()) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return bitmap;
	}

	private void updateView() {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT
			&& mode == Mode.View) {
			isFullScreen = !isFullScreen;
		} else {
			isFullScreen = false;
		}

		View llToolbar = findViewById(R.id.llToolbar);
		if (isFullScreen) {
			llToolbar.setVisibility(View.GONE);
		} else {
			llToolbar.setVisibility(View.VISIBLE);
		}
	}

	private void bindEvent() {
		Button back = (Button) this.findViewById(R.id.btnBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ivRotateLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivImageViewer == null) {
					return;
				}
				ivImageViewer.rotate(-90);
			}
		});

		ivRotateRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivImageViewer == null) {
					return;
				}
				ivImageViewer.rotate(+90);
			}
		});

		ivZoomIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivImageViewer == null) {
					return;
				}
				ivImageViewer.zoomIn();
			}
		});

		ivZoomOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivImageViewer == null) {
					return;
				}
				ivImageViewer.zoomOut();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gifViewer != null) {
			gifViewer.showCover();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if(Logger.isDebug()) {
			Log.d(TAG, "onNewIntent……");
		}
		setIntent(intent);
		isInitialized = false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ivImageViewer != null
			&& Math.abs(1 - ivImageViewer.getScale()) < 0.01F) {
			setSlideDirection(SlideDirection.RIGHT);
		} else {
			setSlideDirection(SlideDirection.NONE);
		}

		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (ivImageViewer != null) {
			ivImageViewer.onTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recycle();
	}

	private void recycle() {
		isFullScreen = false;
		isGif = false;
		mode = Mode.View;
		imagePath = null;
		if (ivImageViewer != null) {
			ivImageViewer.clear();
		}
		if (gifViewer != null) {
			gifViewer.destroy();
			gifViewer = null;
		}
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
		recycle();
		//this.finish();
	}
}
