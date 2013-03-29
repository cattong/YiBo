package com.shejiaomao.weibo.activity;

import java.io.File;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.EditText;

import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageQuality;
import com.shejiaomao.common.NetUtil;
import com.shejiaomao.maobo.R;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalResource;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.task.CacheCleanTask;
import com.shejiaomao.weibo.service.task.ImageCacheCleanTask;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends PreferenceActivity {

	private PreferenceCategory updateCatalog;
	private PreferenceCategory ringtoneCatalog;

	private CheckBoxPreference showHead;
	private CheckBoxPreference detectImageInfo;
	private ListPreference autoLoadComments;
	private ListPreference showThumbnail;
	private CheckBoxPreference enableUpdates;
	private ListPreference updateInterval;
	private CheckBoxPreference autoScreenOrientation;
	private Preference clearCache;
	private Preference clearImageCache;

	private Preference checkFor;
	private Preference notifications;

	private Preference imageFolder;
	private Preference about;


	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(Constants.PREFS_NAME_APP_SETTING, MODE_PRIVATE);

		getPreferenceManager().setSharedPreferencesName(Constants.PREFS_NAME_APP_SETTING);
		getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);

		addPreferencesFromResource(R.xml.preferences);

		updateCatalog = (PreferenceCategory)findPreference("UPDATE_SETTING");
		ringtoneCatalog = (PreferenceCategory)findPreference("RINGTONE_SETTING");

		ListPreference locale = (ListPreference)findPreference("LOCALE");
		locale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String value = newValue.toString();
				Locale locale = Locale.getDefault();
				if (StringUtil.isNotEmpty(value) && !value.equals("auto")) {
					String[] values = value.split("_");
					if (values.length == 2) {
						locale = new Locale(values[0], values[1]);
					} else {
						locale = new Locale(values[0]);
					}
				}
				GlobalVars.LOCALE = locale;
				SettingActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
				return true;
			}
		});
		ListPreference fontSize = (ListPreference)findPreference("FONT_SIZE");
		fontSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				GlobalVars.FONT_SIZE_HOME_BLOG = Integer.parseInt(newValue.toString());
				GlobalVars.FONT_SIZE_HOME_RETWEET = GlobalVars.FONT_SIZE_HOME_BLOG;
				SettingActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
				return true;
			}
		});

		showHead = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_SHOW_HEAD);
		showHead.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (showHead.isChecked()) {
					GlobalVars.IS_SHOW_HEAD = true;
				} else {
					GlobalVars.IS_SHOW_HEAD = false;
				}
				return true;
			}
		});

		showThumbnail = (ListPreference)findPreference(Constants.PREFS_KEY_SHOW_THUMBNAIL);
		showThumbnail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int policy = Integer.parseInt(newValue.toString());
				GlobalVars.IS_SHOW_THUMBNAIL = NetUtil.isPolicyPositive(policy);
				return true;
			}
		});

		ListPreference updateCount = (ListPreference)findPreference(Constants.PREFS_KEY_UPDATE_COUNT);
		updateCount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int updateCount = Integer.parseInt(newValue.toString());
				GlobalVars.UPDATE_COUNT = updateCount;
				return true;
			}
		});

		CheckBoxPreference useSlider = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_USE_SLIDER);
		useSlider.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SettingActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
				return true;
			}
		});

		final CheckBoxPreference gestureSupport = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_ENABLE_GESTURE);
		gestureSupport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (gestureSupport.isChecked()) {
					GlobalVars.IS_ENABLE_GESTURE = true;
				} else {
					GlobalVars.IS_ENABLE_GESTURE = false;
				}
				return true;
			}
		});

		ListPreference imageDownloadQuality = (ListPreference)findPreference(Constants.PREFS_KEY_IMAGE_DOWNLOAD_QUALITY);
		imageDownloadQuality.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				ImageQuality quality = ImageQuality.valueOf(newValue.toString());
				GlobalVars.IMAGE_DOWNLOAD_QUALITY = quality;
				return true;
			}
		});

		autoScreenOrientation = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_AUTO_SCREEN_ORIENTATION);
		autoScreenOrientation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SettingActivity.this.setResult(Constants.RESULT_CODE_SUCCESS);
				return true;
			}
		});

		clearCache = (Preference) findPreference(Constants.PREFS_KEY_CLEAR_CACHE);
		clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(SettingActivity.this)
					.setTitle(R.string.title_dialog_alert)
					.setMessage(R.string.msg_setting_clear_confirm)
					.setNegativeButton(R.string.btn_cancel, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setPositiveButton(R.string.btn_confirm, new AlertDialog.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
						    CacheCleanTask task = new CacheCleanTask(SettingActivity.this);
						    task.execute();
					    }
				    })
				   .show();

				return false;
			}
		});

		clearImageCache = (Preference) findPreference(Constants.PREFS_KEY_CLEAR_IMAGE_CACHE);
		clearImageCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(SettingActivity.this)
					.setTitle(R.string.title_dialog_alert)
					.setMessage(R.string.msg_setting_clear_confirm)
					.setNegativeButton(R.string.btn_cancel, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setPositiveButton(R.string.btn_confirm, new AlertDialog.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
						    ImageCacheCleanTask task = new ImageCacheCleanTask(SettingActivity.this);
						    task.execute();
					    }
				    })
				   .show();

				return false;
			}
		});

		enableUpdates = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_ENABLE_UPDATES);
		enableUpdates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				updateUpdatesSettings();
				return false;
			}
		});
		updateUpdatesSettings();

		updateInterval = (ListPreference)findPreference(Constants.PREFS_KEY_UPDATE_INTERVAL);
		updateInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				//启动service和注册接收器
				Intent serviceIntent = new Intent(SettingActivity.this, AutoUpdateService.class);
				SettingActivity.this.startService(serviceIntent);
				return true;
			}
		});

		checkFor = (Preference)findPreference("CHECK_FOR");
		checkFor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				createCheckForSettingDialog();
				return false;
			}
		});

		notifications = (Preference)findPreference("NOTIFICATIONS");
		notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				createNotificationsSettingDialog();
				return false;
			}
		});

		imageFolder = (Preference) findPreference(Constants.PREFS_KEY_IMAGE_FOLDER);
		imageFolder.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SheJiaoMaoApplication sheJiaoMao = (SheJiaoMaoApplication) SettingActivity.this.getApplication();
				String folderPath = sheJiaoMao.getImageFolder();
				final EditText editView = new EditText(SettingActivity.this);
				editView.setText(folderPath);
				new AlertDialog.Builder(SettingActivity.this)
					.setView(editView)
					.setPositiveButton(R.string.btn_confirm, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String path = editView.getText().toString();
							if (StringUtil.isNotEmpty(path)) {
								if (!path.startsWith(File.separator)) {
									path = File.separator + path;
								}
							} else {
								path = Constants.DCIM_PATH;
							}
							File file = new File(path);
							boolean dirOk = true;
							if (!file.exists() || file.isFile()) {
								dirOk = file.mkdirs();
							}
							if (dirOk) {
								imageFolder.getEditor().putString(Constants.PREFS_KEY_IMAGE_FOLDER, path).commit();
							}
						}
					})
					.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
				return false;
			}
		});

		detectImageInfo = (CheckBoxPreference) findPreference(Constants.PREFS_KEY_DETECT_IMAGE_INFO);
		detectImageInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				GlobalVars.IS_DETECT_IAMGE_INFO = (Boolean) newValue;
				return true;
			}
		});

		autoLoadComments = (ListPreference)findPreference(Constants.PREFS_KEY_AUTO_LOAD_COMMENTS);
		autoLoadComments.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int policy = Integer.parseInt(newValue.toString());
				GlobalVars.IS_AUTO_LOAD_COMMENTS = NetUtil.isPolicyPositive(policy);
				return true;
			}
		});
		
		about = (Preference)findPreference("ABOUT");
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String aboutMsg = getResources().getString(R.string.about,
						GlobalResource.getVersionName(SettingActivity.this));

				new AlertDialog.Builder(SettingActivity.this)
				    .setTitle(getString(R.string.title_dialog_about))
				    .setMessage(aboutMsg)
				    .create()
				    .show();
				return false;
			}
		});

	}

	private void createCheckForSettingDialog() {
		final String[] keys = new String[]{
				Constants.PREFS_KEY_CHECK_STATUSES,
				Constants.PREFS_KEY_CHECK_MENTIONS,
				Constants.PREFS_KEY_CHECK_COMMENTS,
				Constants.PREFS_KEY_CHECK_MESSAGES,
				Constants.PREFS_KEY_CHECK_FOLLOWERS
				};

		boolean[] selectedState = new boolean[keys.length];

		for (int i = 0; i < selectedState.length; i++ ) {
			selectedState[i] = prefs.getBoolean(keys[i], true);
		}

		String[] checkForTargets = new String[5];
		checkForTargets[0] = getResources().getString(R.string.label_setting_check_statuses);
		checkForTargets[1] = getResources().getString(R.string.label_setting_check_mentions);
		checkForTargets[2] = getResources().getString(R.string.label_setting_check_comments);
		checkForTargets[3] = getResources().getString(R.string.label_setting_check_messages);
		checkForTargets[4] = getResources().getString(R.string.label_setting_check_followers);

		new AlertDialog.Builder(this)
	        .setIcon(R.drawable.menu_accounts)
	        .setTitle(R.string.title_dialog_setting_check_for)
	        .setMultiChoiceItems(checkForTargets,
	        		selectedState,
	            new DialogInterface.OnMultiChoiceClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton,
	                        boolean isChecked) {
	                    updateBooleanSetting(keys[whichButton], isChecked);
	                }
	            })
	        .setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            })
	        .create()
	        .show();
	}

	private void createNotificationsSettingDialog() {
		final String[] keys = new String[]{
				Constants.PREFS_KEY_VIBRATE,
				Constants.PREFS_KEY_RINGTONE,
				Constants.PREFS_KEY_LED
				};

		boolean[] selectedState = new boolean[keys.length];

		for (int i = 0; i < selectedState.length; i++ ) {
			selectedState[i] = prefs.getBoolean(keys[i], true);
		}

		String[] checkForTargets = new String[3];
		checkForTargets[0] = getResources().getString(R.string.label_setting_notification_vibrate);
		checkForTargets[1] = getResources().getString(R.string.label_setting_notification_ringtone);
		checkForTargets[2] = getResources().getString(R.string.label_setting_notification_led);

		new AlertDialog.Builder(this)
	        .setTitle(R.string.title_dialog_setting_notification)
	        .setMultiChoiceItems(checkForTargets, selectedState,
	            new DialogInterface.OnMultiChoiceClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton,
	                        boolean isChecked) {
	                    updateBooleanSetting(keys[whichButton], isChecked);
	                }
	            })
	        .setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            })
	        .create()
	        .show();
	}

	private void updateBooleanSetting(String key, boolean value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	private void updateUpdatesSettings() {
		boolean enabled = prefs.getBoolean(Constants.PREFS_KEY_ENABLE_UPDATES, true);
		if (enabled) {
			enableUpdates.setSummary(R.string.hint_setting_updates_enabled);
		} else {
			enableUpdates.setSummary(R.string.hint_setting_updates_disabled);
		}
		updateCatalog.setEnabled(enabled);
		ringtoneCatalog.setEnabled(enabled);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
