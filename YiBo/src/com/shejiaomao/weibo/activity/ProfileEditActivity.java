package com.shejiaomao.weibo.activity;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import com.shejiaomao.maobo.R;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cattong.commons.Logger;
import com.cattong.commons.util.DateTimeUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.User;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.FileModifiedTimeComparator;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.Theme;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.listener.EditMicroBlogCameraClickListener;
import com.shejiaomao.weibo.service.listener.ProfileTextWatcher;
import com.shejiaomao.weibo.service.task.ImageLoad4HeadTask;
import com.shejiaomao.weibo.service.task.UpdateProfilePhotoTask;
import com.shejiaomao.weibo.service.task.UpdateProfileTask;

public class ProfileEditActivity extends BaseActivity {
	private static final String TAG = ProfileEditActivity.class.getSimpleName();

	private User user;
	private boolean updateSuccess;

	private TextView tvScreenName;
	private ImageView ivVerify;
	private TextView tvImpress;

	private EditText etScreenName;
	private EditText etDescription;

	private ProfileTextWatcher profileTextWatcher;

	private String imagePath;

	private SheJiaoMaoApplication sheJiaoMao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile_edit);
		if (getIntent().hasExtra("USER")) {
			this.user = (User)getIntent().getSerializableExtra("USER");
		} else {
			this.finish();
		}

		this.sheJiaoMao = (SheJiaoMaoApplication) getApplication();
		restoreFromInstanceState(savedInstanceState);

		initComponents();
		bindEvent();
		updateViewContent();
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
    	ThemeUtil.setSecondaryHeader(llHeaderBase);
    	
    	//个人资料头部
    	LinearLayout llProfileHeader = (LinearLayout)findViewById(R.id.llProfileHeader);
		tvScreenName = (TextView)findViewById(R.id.tvScreenName);
		ivVerify = (ImageView)findViewById(R.id.ivVerify);
		tvImpress = (TextView)findViewById(R.id.tvImpress);
		ThemeUtil.setHeaderProfile(llProfileHeader);
		Theme theme = ThemeUtil.createTheme(this);
		tvScreenName.setTextColor(theme.getColor("highlight"));
		ivVerify.setImageDrawable(GlobalResource.getIconVerification(this));
		tvImpress.setTextColor(theme.getColor("content"));
		
		//内容编辑
		ScrollView llContentPanel = (ScrollView)findViewById(R.id.llContentPanel);
		LinearLayout llChangeProfilePhoto = (LinearLayout)findViewById(R.id.llChangeProfilePhoto);
		ImageView ivProfileEdit = (ImageView)findViewById(R.id.ivProfileEdit);
		TextView tvProfileEdit = (TextView)findViewById(R.id.tvProfileEdit);
		etScreenName = (EditText) this.findViewById(R.id.etProfileScreenName);
		etDescription = (EditText) this.findViewById(R.id.etProfileDescription);
		llContentPanel.setBackgroundColor(theme.getColor("background_content"));
		llChangeProfilePhoto.setBackgroundDrawable(theme.getDrawable("selector_btn_action_negative"));
		ivProfileEdit.setImageDrawable(theme.getDrawable("icon_profile_edit"));
		tvProfileEdit.setTextColor(theme.getColorStateList("selector_btn_action_negative"));
		etScreenName.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
		int content = theme.getColor("content");
		etScreenName.setTextColor(content);
		etDescription.setBackgroundDrawable(theme.getDrawable("selector_input_frame"));
		etDescription.setTextColor(content);
		
		//工具条
        LinearLayout llToolbar = (LinearLayout)findViewById(R.id.llToolbar);
        Button btnProfileUpdate = (Button)findViewById(R.id.btnProfileUpdate);
        Button btnProfileReset = (Button)findViewById(R.id.btnProfileReset);
        llToolbar.setBackgroundDrawable(theme.getDrawable("bg_toolbar"));
        llToolbar.setGravity(Gravity.CENTER);
        int padding4 = theme.dip2px(4);
        llToolbar.setPadding(padding4, padding4, padding4, padding4);
        ThemeUtil.setBtnActionPositive(btnProfileUpdate);
        ThemeUtil.setBtnActionNegative(btnProfileReset);        
		
		profileTextWatcher = new ProfileTextWatcher(this);
		Button btnFollow = (Button) this.findViewById(R.id.btnFollow);
		btnFollow.setVisibility(View.INVISIBLE);
	}

	private void updateViewContent() {
		if (user == null) {
			return;
		}

		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(user.getScreenName());
		tvScreenName.setText(user.getScreenName());
		if (user.isVerified()) {
        	ivVerify.setVisibility(View.VISIBLE);
        } else {
        	ivVerify.setVisibility(View.INVISIBLE);
        }
		if (StringUtil.isEmpty(user.getLocation()) || ",".equals(user.getLocation())) {
			String gender = ResourceBook.getGenderValue(user.getGender(), this);
			tvImpress.setText(gender);
		} else {
			String gender = ResourceBook.getGenderValue(user.getGender(), this);
			tvImpress.setText(gender + "," + user.getLocation());
		}
		etScreenName.setText(user.getScreenName());
		etDescription.setText(user.getDescription());

		updateProfileImage(user.getProfileImageUrl());
	}

	private void bindEvent() {
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (updateSuccess) {
					ProfileEditActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
				}
				ProfileEditActivity.this.finish();
			}
		});

		final Button btnSubmit = (Button) this.findViewById(R.id.btnProfileUpdate);
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new UpdateProfileTask(
						ProfileEditActivity.this,
						etScreenName.getText().toString(),
						etDescription.getText().toString()).execute();
			}
		});

		Button btnReset = (Button) this.findViewById(R.id.btnProfileReset);
		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etScreenName.setText(user.getScreenName());
				etDescription.setText(user.getDescription());
			}
		});


		etScreenName.addTextChangedListener(profileTextWatcher);
		etDescription.addTextChangedListener(profileTextWatcher);

		LinearLayout llChangeProfilePhoto = (LinearLayout) findViewById(R.id.llChangeProfilePhoto);
		llChangeProfilePhoto.setOnClickListener(new EditMicroBlogCameraClickListener(this));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.d(TAG, "onActivityResult……");
		super.onActivityResult(requestCode, resultCode, intent);

		switch(requestCode) {
		case Constants.REQUEST_CODE_IMG_SELECTOR:
			if (resultCode == RESULT_OK) {
				startImageCropActivity(intent.getData());
			}
			break;
		case Constants.REQUEST_CODE_CAMERA:
			if (resultCode != RESULT_OK) {
				break;
			}

			Uri imageUri = null;
			Bitmap bitmap = null;
    		if (CompatibilityUtil.hasImageCaptureBug()) {
    			Bundle extras = intent.getExtras();
    			bitmap = (Bitmap) extras.get("data"); //如果以这种方式传递，宽高肯定不大，否则占内存

        		imagePath = getImageFilePath_CameraBug();
        		if (StringUtil.isEmpty(imagePath)) {
        			imageUri = saveCameraImage(bitmap);
        		} else {
        			imageUri = Uri.parse(imagePath);
        		}

            } else {
                if (intent == null) {
                	Log.v(TAG, "data intent is null!");
                }
                if (StringUtil.isNotEmpty(imagePath)) {
                	imageUri = Uri.fromFile(new File(imagePath));
                }
            }

    		if (imageUri != null) {
                startImageCropActivity(imageUri);
            }

			break;
		case Constants.REQUEST_CODE_IMAGE_CROP:
			if (resultCode != RESULT_OK) {
				break;
			}
			String filePath = null;
			if (StringUtil.isNotEmpty(intent.getAction())) {
				filePath = getImagePathFromUri(Uri.parse(intent.getAction()));
			}

			if (StringUtil.isNotEmpty(filePath)) {
				new UpdateProfilePhotoTask(ProfileEditActivity.this, new File(filePath)).execute();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (updateSuccess) {
				this.setResult(Constants.RESULT_CODE_SUCCESS);
			}
			this.finish();
		}

		return super.onKeyUp(keyCode, event);
	}

	public void updateUser(User user) {
		if (user == null) {
			return;
		}
		this.updateSuccess = true;
		this.user = user;
		updateViewContent();
		sheJiaoMao.getCurrentAccount().setUser(user);
		profileTextWatcher.setUser(user);
	}

	public void updateProfileImage(String imageUrl) {
		ImageView ivProfilePicture = (ImageView) this.findViewById(R.id.ivProfilePicture);
		if (StringUtil.isNotEmpty(imageUrl)) {
			ImageLoad4HeadTask loadTask = new ImageLoad4HeadTask(ivProfilePicture, imageUrl, false);
			loadTask.execute();
		}
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	private void startImageCropActivity(Uri uri) {
		if (uri != null) {
			Intent intent = new Intent();
			intent.setData(uri);
			intent.setClass(this, CropImageActivity.class);
			startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE_CROP);
		}
	}

	private String getImagePathFromUri(Uri uri) {
		if (uri == null) {
			return null;
		}

		String path = null;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.moveToFirst()) {
				path = cursor.getString(columnIndex); //图片文件路径
			}
		    cursor.close();
		} else {
			path = uri.toString();
		}

		return path;
	}

	/*
	 * 存在照相bug的版本，采用获取最新相机文件夹中最新图片的方法
	 */
	private String getImageFilePath_CameraBug() {
		String imgPath = "";

		File dcimFolder = new File(Constants.DCIM_PATH);
		if (!dcimFolder.exists()) {
			return imgPath;
		}

		File[] cameraFolders = dcimFolder.listFiles();
		if (cameraFolders == null || cameraFolders.length < 1) {
			return imgPath;
		}
		FileModifiedTimeComparator comparator = new FileModifiedTimeComparator();
		Arrays.sort(cameraFolders, comparator);

		File cameraFolder = cameraFolders[0];
		if (!cameraFolder.isDirectory()) {
			return imgPath;
		}
        File[] imgFiles = cameraFolder.listFiles();
        if (imgFiles == null || imgFiles.length < 1) {
        	return imgPath;
        }
		Arrays.sort(imgFiles, comparator);

		//获取最新拍照的图片文件;
		File imgFile = imgFiles[0];
		imgPath = imgFile.getAbsolutePath();

		return imgPath;
	}

	private Uri saveCameraImage(Bitmap bitmap) {
		String name = Constants.PICTURE_NAME_PREFIX + DateTimeUtil.getShortFormat(new Date()) + ".jpg";

		ContentResolver resolver = this.getContentResolver();
		String uriStr = MediaStore.Images.Media.insertImage(resolver, bitmap, name, null);
		return Uri.parse(uriStr);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState == null) {
			return;
		}

		if (sheJiaoMao.getCurrentAccount() != null) {
			outState.putLong(Constants.PREFS_KEY_CURRENT_ACCOUNT, sheJiaoMao.getCurrentAccount().getAccountId());
		}
		if (StringUtil.isNotEmpty(imagePath)) {
			outState.putString(Constants.PREFS_KEY_IMAGE_PATH, imagePath);
		}

		if (Logger.isDebug()) {
			Log.v(TAG, "onSaveInstanceState……");
		}
	}

	private void restoreFromInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		if (Logger.isDebug()) {
			Log.v(TAG, "restoreFromInstanceState……");
		}

		if (savedInstanceState.containsKey(Constants.PREFS_KEY_CURRENT_ACCOUNT)) {
			long accountId = savedInstanceState.getLong(Constants.PREFS_KEY_CURRENT_ACCOUNT);
			if (Logger.isDebug()) {
				Log.v(TAG, "Restore AccountId : " + accountId);
			}
			LocalAccount currentAccount = GlobalVars.getAccount(accountId);
			if (currentAccount != null) {
				sheJiaoMao.setCurrentAccount(currentAccount);
			}
		}

		if (savedInstanceState.containsKey(Constants.PREFS_KEY_IMAGE_PATH)) {
			this.imagePath = savedInstanceState.getString(Constants.PREFS_KEY_IMAGE_PATH);
		}


	}

	public User getUser() {
		return user;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop……");
	}

}
