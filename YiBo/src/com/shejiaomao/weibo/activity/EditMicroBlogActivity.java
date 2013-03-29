package com.shejiaomao.weibo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.cattong.commons.ServiceProvider;
import com.cattong.commons.util.DateTimeUtil;
import com.cattong.commons.util.FileUtil;
import com.cattong.commons.util.ListUtil;
import com.cattong.commons.util.StringUtil;
import com.cattong.entity.GeoLocation;
import com.cattong.entity.Status;
import com.cattong.entity.User;
import com.cattong.weibo.FeaturePatternUtils;
import com.shejiaomao.common.CompatibilityUtil;
import com.shejiaomao.common.ExifUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.weibo.BaseActivity;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.FileModifiedTimeComparator;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.common.theme.ThemeUtil;
import com.shejiaomao.weibo.db.LocalAccount;
import com.shejiaomao.weibo.service.adapter.UserSuggestAdapter;
import com.shejiaomao.weibo.service.listener.EditMicroBlogAccountSelectorClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogCameraClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogEmotionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogLocationClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogMentionClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogSendClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTextDeleteClickListener;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTokenizer;
import com.shejiaomao.weibo.service.listener.EditMicroBlogTopicClickListener;
import com.shejiaomao.weibo.service.listener.GoBackClickListener;
import com.shejiaomao.weibo.service.listener.MicroBlogTextWatcher;
import com.shejiaomao.weibo.widget.EmotionViewController;

public class EditMicroBlogActivity extends BaseActivity {
	public static final String TAG = EditMicroBlogActivity.class.getSimpleName();
    public static final int DIALOG_AGREEMENT = 1;
	
    /** 是否由于新浪协议限制，只更新新浪账号，不更新其他平台账号 */
	private boolean isUpdateSinaAndPauseOthers = false;
	
	public boolean isUpdateSinaAndPauseOthers() {
		return isUpdateSinaAndPauseOthers;
	}
	
	public void setUpdateSinaAndPauseOthers(boolean isUpdateSinaAndPauseOthers) {
		this.isUpdateSinaAndPauseOthers = isUpdateSinaAndPauseOthers;
	}

	private SharedPreferences prefs;

	private SheJiaoMaoApplication sheJiaoMao;

	//默认更新的帐户，已经登录时，为当前帐户;未登录时，为默认登录帐户
	private LocalAccount defaultUpdateAccount;
	private List<LocalAccount> listUpdateAccount;

	private Integer type = null;
	private Integer sourceType = null;
	private Status status = null;
	private String appendText = null; //转发等传递过来的附加文本;

	private GeoLocation geoLocation = null;
    private boolean isComment = false;

    private boolean hasImageFile;
    private String imagePath;
    private int rotateDegrees;
    private Bitmap thumbnail;
    
    private EmotionViewController emotionViewController;

    private EditMicroBlogLocationClickListener locationListener;

    public void removeAllSinaAccount(List<LocalAccount> sinaAccountList) {
    	if (sinaAccountList == null) {
    		return;
    	}
    	listUpdateAccount.removeAll(sinaAccountList);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_micro_blog);
		sheJiaoMao = (SheJiaoMaoApplication)getApplication();
		restoreFromInstanceState(savedInstanceState);

		//默认不弹出输入法
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initParams();
		initComponents();
		bindEvent();
	}

	private void initParams() {
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		type = Constants.EDIT_TYPE_TWEET;
		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			try {
				appendText = bundle.getString(Intent.EXTRA_TEXT);
			} catch (Exception e) {
				appendText = " ";
			}

			Uri uri = bundle.getParcelable(Intent.EXTRA_STREAM);
			if (uri != null
				&& intent.getType() != null
				&& intent.getType().startsWith("image/")) {
				updateAttachedImage(uri);
			}
		} else {
			try {
			    type = bundle.getInt("TYPE");
			    sourceType = bundle.getInt("SOURCE");

			    Object temp = bundle.getSerializable("STATUS");
			    if (temp != null) {
			        status = (Status)temp;
			    }
			} catch (Exception e) { }

			try {
				appendText = bundle.getString("APPEND_TEXT");
			} catch (Exception e) { }
		}

		if (sourceType != null
			&& sourceType == Constants.SOURCE_WIDGET_CAMERA) {
			EditMicroBlogCameraClickListener.jumpToTakePicture(this);
		}

		//初始化默认更新的帐户
		listUpdateAccount = new ArrayList<LocalAccount>();
		SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication) this.getApplication();
		if (sheJiaoMao.isSyncToAllAsDefault()
			&& type != Constants.EDIT_TYPE_FEEDBACK
			&& type != Constants.EDIT_TYPE_MENTION
			&& type != Constants.EDIT_TYPE_RETWEET) {
			listUpdateAccount.addAll(GlobalVars.getAccountList(this, false));
		} else if (sheJiaoMao.getCurrentAccount() != null) {
			defaultUpdateAccount = sheJiaoMao.getCurrentAccount();
			listUpdateAccount.add(defaultUpdateAccount);
		}

		//用于缓存文本
		prefs = getSharedPreferences(Constants.PREFS_NAME_APP_TEMP, MODE_PRIVATE);
		emotionViewController = new EmotionViewController(this);
	}

	private void initComponents() {
		LinearLayout llHeaderBase = (LinearLayout)findViewById(R.id.llHeaderBase);
		ImageView ivDropDown = (ImageView)findViewById(R.id.ivDropDown);		
		LinearLayout llContentPanel = (LinearLayout)findViewById(R.id.llContentPanel);
		LinearLayout llEditText = (LinearLayout)findViewById(R.id.llEditText);
		MultiAutoCompleteTextView etText  = (MultiAutoCompleteTextView)findViewById(R.id.etText);
		ImageView ivAttachment = (ImageView)findViewById(R.id.ivAttachment);
		Button btnCamera = (Button)this.findViewById(R.id.btnCamera);
		Button btnLocation = (Button)this.findViewById(R.id.btnLocation);
		Button btnMention = (Button)this.findViewById(R.id.btnMention);
		Button btnEmotion = (Button)this.findViewById(R.id.btnEmotion);
		Button btnTopic = (Button)this.findViewById(R.id.btnTopic);
		Button btnTextCount = (Button)this.findViewById(R.id.btnTextCount);
		
		ThemeUtil.setSecondaryHeader(llHeaderBase);
		ivDropDown.setImageDrawable(theme.getDrawable("icon_dropdown_normal"));
		ThemeUtil.setContentBackground(llContentPanel);
		int padding6 = theme.dip2px(6);
		int padding8 = theme.dip2px(8);
		llContentPanel.setPadding(padding6, padding8, padding6, 0);
		llEditText.setBackgroundDrawable(theme.getDrawable("bg_input_frame_normal"));
		etText.setTextColor(theme.getColor("content"));
		ivAttachment.setBackgroundDrawable(theme.getDrawable("shape_attachment"));
		btnCamera.setBackgroundDrawable(theme.getDrawable("selector_btn_camera"));
		btnLocation.setBackgroundDrawable(theme.getDrawable("selector_btn_location"));
		btnEmotion.setBackgroundDrawable(theme.getDrawable("selector_btn_emotion"));
		btnMention.setBackgroundDrawable(theme.getDrawable("selector_btn_mention"));
		btnTopic.setBackgroundDrawable(theme.getDrawable("selector_btn_topic"));
		btnTextCount.setBackgroundDrawable(theme.getDrawable("selector_btn_text_count"));
		btnTextCount.setPadding(padding6, 0, theme.dip2px(20), 0);
		btnTextCount.setTextColor(theme.getColor("status_capability"));
		
		
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		etText.addTextChangedListener(new MicroBlogTextWatcher(this));

		ServiceProvider sp = (defaultUpdateAccount == null) ? ServiceProvider.Sina : defaultUpdateAccount.getServiceProvider();
		String retweetSeparator = FeaturePatternUtils.getRetweetSeparator(sp);

		switch(type) {
		case Constants.EDIT_TYPE_MENTION:
		case Constants.EDIT_TYPE_FEEDBACK:
		case Constants.EDIT_TYPE_RETWEET:
			tvTitle.setText(R.string.title_tweet);

			if (!StringUtil.isEmpty(appendText)) {
			    etText.setText(appendText + " ");
			    etText.setSelection(etText.getEditableText().length());
			}

			break;
		case Constants.EDIT_TYPE_TWEET:
			tvTitle.setText(R.string.title_tweet);
			boolean isQuote = false;
			if (!StringUtil.isEmpty(appendText)) {
				isQuote = appendText.trim().startsWith(retweetSeparator.trim());
			    etText.setText(appendText + " ");
			    etText.setSelection(etText.getEditableText().length());
			} else {
				String tempBlog = prefs.getString(Constants.PREFS_KEY_TEMP_EDIT_BLOG, "");
				isQuote = tempBlog.trim().startsWith(retweetSeparator.trim());
				etText.setText(tempBlog);
			    etText.setSelection(etText.getEditableText().length());
			}

			if (isQuote) {
				etText.setSelection(0);
			}

			break;
		default:
			tvTitle.setText(R.string.title_tweet);
		}
		updateSelectorText();

		int length = StringUtil.getLengthByByte(etText.getText().toString());
        int leavings = (int)Math.floor((double)(Constants.STATUS_TEXT_MAX_LENGTH * 2 - length) / 2);
        btnTextCount.setText((leavings < 0 ? "-" : "") + Math.abs(leavings));

		etText.setAdapter(new UserSuggestAdapter(this));
		etText.setTokenizer(new EditMicroBlogTokenizer());

		updateAttachedImage(imagePath, rotateDegrees);
	}

	private void bindEvent() {
		Button btnClose = (Button) this.findViewById(R.id.btnBack);
		btnClose.setOnClickListener(new GoBackClickListener());

		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		EditMicroBlogAccountSelectorClickListener accountsSelectorListener =
			new EditMicroBlogAccountSelectorClickListener(this);
		tvTitle.setOnClickListener(accountsSelectorListener);

		EditText etText = (EditText) this.findViewById(R.id.etText);
		etText.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				emotionViewController.hideEmotionView();
				return false;
			}

		});

		Button btnSend = (Button) this.findViewById(R.id.btnOperate);
		btnSend.setText(R.string.label_send);
		btnSend.setVisibility(View.VISIBLE);
		btnSend.setOnClickListener(new EditMicroBlogSendClickListener(this));

		Button btnCamera = (Button) this.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(new EditMicroBlogCameraClickListener(this));

		Button btnTopic = (Button) this.findViewById(R.id.btnTopic);
		btnTopic.setOnClickListener(new EditMicroBlogTopicClickListener(this));

		Button btnLocation = (Button) this.findViewById(R.id.btnLocation);
		locationListener = new EditMicroBlogLocationClickListener(this);
		btnLocation.setOnClickListener(locationListener);

		Button btnEmotion = (Button) this.findViewById(R.id.btnEmotion);
		btnEmotion.setOnClickListener(new EditMicroBlogEmotionClickListener(this));

	    Button btnMention = (Button) this.findViewById(R.id.btnMention);
	    btnMention.setOnClickListener(new EditMicroBlogMentionClickListener());

	    Button btnTextCount = (Button) this.findViewById(R.id.btnTextCount);
	    btnTextCount.setOnClickListener(new EditMicroBlogTextDeleteClickListener(this));

		ImageView ivAttachment = (ImageView) this.findViewById(R.id.ivAttachment);
		ivAttachment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;
				if (FileUtil.isGif(imagePath)) {
					intent = new Intent(EditMicroBlogActivity.this, ImageViewer4GifActivity.class);
				} else {
					intent = new Intent(EditMicroBlogActivity.this, ImageViewerActivity.class);
				}
				intent.putExtra("image-path", imagePath);
				intent.putExtra("rotation", rotateDegrees);
				intent.putExtra("mode", ImageViewerActivity.Mode.Edit.toString());
				EditMicroBlogActivity.this.startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE_EDIT);
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (type != Constants.EDIT_TYPE_RETWEET
			&& type != Constants.EDIT_TYPE_FEEDBACK) {
			SharedPreferences.Editor editor = prefs.edit();
			EditText etText  = (EditText)this.findViewById(R.id.etText);
			String blog = etText.getEditableText().toString();
			editor.putString(Constants.PREFS_KEY_TEMP_EDIT_BLOG, blog);
			editor.commit();
		}
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
		if (rotateDegrees > 0) {
			outState.putInt(Constants.PREFS_KEY_IMAGE_ROTATION, rotateDegrees);
		}
	}

	private void restoreFromInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		if (savedInstanceState.containsKey(Constants.PREFS_KEY_CURRENT_ACCOUNT)) {
			long accountId = savedInstanceState.getLong(Constants.PREFS_KEY_CURRENT_ACCOUNT);
			LocalAccount currentAccount = GlobalVars.getAccount(accountId);
			if (currentAccount != null) {
				sheJiaoMao.setCurrentAccount(currentAccount);
			}
		}
		if (savedInstanceState.containsKey(Constants.PREFS_KEY_IMAGE_PATH)) {
			this.imagePath = savedInstanceState.getString(Constants.PREFS_KEY_IMAGE_PATH);
		}
		if (savedInstanceState.containsKey(Constants.PREFS_KEY_IMAGE_ROTATION)) {
			this.rotateDegrees = savedInstanceState.getInt(Constants.PREFS_KEY_IMAGE_ROTATION);
		}
	}

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case Constants.REQUEST_CODE_IMG_SELECTOR:
			if (resultCode == RESULT_OK) {
				updateAttachedImage(data.getData());
			}
			break;
		case Constants.REQUEST_CODE_CAMERA:
			if (resultCode != RESULT_OK) {
				break;
			}

			Uri uri = null;
			Bitmap b = null;
			String imgPath = null;
    		if (CompatibilityUtil.hasImageCaptureBug()) {
    			Bundle extras = data.getExtras();
        		b = (Bitmap) extras.get("data"); //如果以这种方式传递，宽高肯定不大，否则占内存

        		imgPath = getImageFilePath_CameraBug();
        		if (imgPath.equals("")) {
                    uri = saveCameraImage(b);
        		}
        		updateAttachedImage(uri);
            } else {
                if (data == null) {
                	Log.v(TAG, "data intent is null!");
                }

                updateAttachedImage(imagePath);
            }

			break;
		case Constants.REQUEST_CODE_USER_SELECTOR:
			if (resultCode == Constants.RESULT_CODE_SUCCESS) {
				List<User> userList = (List<User>)data.getSerializableExtra("LIST_USER");
                if (ListUtil.isEmpty(userList)) {
                	userList = new ArrayList<User>();
                }
				MultiAutoCompleteTextView etText  =
				    (MultiAutoCompleteTextView)this.findViewById(R.id.etText);
				StringBuilder mentions = new StringBuilder("");
				for (User user : userList) {
					mentions.append(user.getMentionName()).append(" ");
				}
				int currentPos = etText.getSelectionStart();
				etText.getText().insert(currentPos, mentions);
			}
			break;
		case Constants.REQUEST_CODE_IMAGE_EDIT:
			if (resultCode == Constants.RESULT_CODE_IMAGE_DELETED) {
				updateAttachedImage(null, 0);
			} else if (resultCode == Constants.RESULT_CODE_IMAGE_ROTATED) {
				int rotation = data.getIntExtra("rotation", Integer.MIN_VALUE);
				if (rotation > Integer.MIN_VALUE) {
					updateAttachedImage(imagePath, rotation);
				}
			}
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (thumbnail != null) {
			thumbnail.recycle();
			thumbnail = null;
		}
		if (locationListener != null) {
			locationListener.removeListener();
		}
	}

	private void updateAttachedImage(Uri uri) {
		if (uri == null) {
			updateAttachedImage(null, 0);
			return;
		}

		String path = null;
		int rotate = 0;
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			int rotateIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
			if (cursor.moveToFirst()) {
				path = cursor.getString(columnIndex); //图片文件路径
				if (rotateIndex > 0) {
					rotate = cursor.getInt(rotateIndex);
				}
			}
			cursor.deactivate();
		    cursor.close();
		    updateAttachedImage(path, rotate);
		} else { //第三方文件管理器，不规范，直接返回原始路径
			path = getFilePath(uri);
			updateAttachedImage(path);
		}
	}

	private void updateAttachedImage(String imagePath) {
		if (StringUtil.isEmpty(imagePath)) {
			updateAttachedImage(null, 0);
			return;
		}

		int rotation = 0;
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
			rotation = ExifUtil.getExifRotation(imagePath);
		}
		updateAttachedImage(imagePath, rotation);
	}

	private void updateAttachedImage(String path, int rotation) {
		ImageView ivAttachment = (ImageView)this.findViewById(R.id.ivAttachment);
		if (StringUtil.isEmpty(path)) {
			hasImageFile = false;
			imagePath = null;
			rotateDegrees = 0;
			if (thumbnail != null) {
				thumbnail.recycle();
				thumbnail = null;
			}

			ivAttachment.setVisibility(View.GONE);
		} else {
			File imageFile = new File(path);
			imagePath = path;
			rotateDegrees = rotation;
			hasImageFile = imageFile.exists();

			Bitmap bitmap = ImageUtil.scaleImageFile(imageFile, Constants.IMAGE_THUMBNAIL_WIDTH);
			Bitmap rotated = ImageUtil.rotate(bitmap, rotateDegrees);
			thumbnail = ImageUtil.extractThumbnail(rotated, 60, 48, ImageUtil.OPTIONS_RECYCLE_INPUT);
			bitmap = null;
			rotated = null;

			ivAttachment.setVisibility(View.VISIBLE);
		}

		ivAttachment.setImageBitmap(thumbnail);
	}

	private String getFilePath(Uri uri) {
		if (uri == null) {
			return null;
		}
	    return uri.getPath();
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
		String uriStr = MediaStore.Images.Media.insertImage(resolver, bitmap, name, null);  //生成一张小图(camera)和缩略图(在.thumnail);
		return Uri.parse(uriStr);
	}

	public void updateSelectorText() {
		TextView tvAccountsSelector = (TextView)findViewById(R.id.tvTitle);
		if (tvAccountsSelector == null) {
			return;
		}

		if (listUpdateAccount.size() == 0) {
			tvAccountsSelector.setText(R.string.title_accounts_selector);
		} else if (listUpdateAccount.size() == 1) {
			LocalAccount account = listUpdateAccount.get(0);
			tvAccountsSelector.setText(account.getUser().getScreenName());
		} else if (listUpdateAccount.size() < GlobalVars.getAccountList(this, false).size()) {
			String updateText = getString(
				R.string.title_accounts_selector_multiple, listUpdateAccount.size()
			);
			tvAccountsSelector.setText(updateText);
		} else {
			tvAccountsSelector.setText(R.string.title_accounts_selector_all);
		}
	}

	private static final String MAP = "http://maps.google.com/maps\\?q=";
	private static final Pattern pattern = Pattern.compile(MAP + "[-+]?\\d+\\.\\d+,[-+]?\\d+\\.\\d+");
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
		if (geoLocation == null) {
			return;
		}
		//用最新的坐标替换
		EditText etText  = (EditText)this.findViewById(R.id.etText);
		String status = etText.getEditableText().toString();
		Matcher matcher = pattern.matcher(status);
		if (!matcher.find() && locationListener != null && !locationListener.isAutoLocate()) {
			String locationText = getString(R.string.hint_my_location, geoLocation.getLatitude(), geoLocation.getLongitude());
			etText.setText(status + " " + locationText + " ");
		} else {
			status = matcher.replaceAll(MAP + geoLocation.getLatitude() + "," + geoLocation.getLongitude());
			etText.setText(status);
		}
		etText.setSelection(etText.getEditableText().length());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
    	switch(id) {
    	case DIALOG_AGREEMENT:
    		dialog =
    			new AlertDialog.Builder(this)
    			    .setTitle(R.string.title_dialog_alert)
    				.setMessage(R.string.msg_agreement_edit)
    				.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog,	int which) {
    						dialog.dismiss();
    					}
    				})
    				.create();
    		break;
    	default:
    		break;
    	}
    	return dialog;
	}

	@Override
	public void onBackPressed() {
		if (emotionViewController.getEmotionViewVisibility() == View.VISIBLE) {
			emotionViewController.hideEmotionView();
		} else {
			super.onBackPressed();
		}
	}

	public List<LocalAccount> getListUpdateAccount() {
		return listUpdateAccount;
	}

	public void setListUpdateAccount(List<LocalAccount> listUpdateAccount) {
		this.listUpdateAccount = listUpdateAccount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public boolean isHasImageFile() {
		return hasImageFile;
	}

	public void setHasImageFile(boolean hasImageFile) {
		this.hasImageFile = hasImageFile;
	}

	public int getRotateDegrees() {
		return rotateDegrees;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Status getStatus() {
		return status;
	}

	public boolean isComment() {
		return isComment;
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
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
