/**
 * 
 */
package com.leoly.fuckey.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.leoly.fuckey.R;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.key.KeyEventExecuter;
import com.leoly.fuckey.services.FloatKeyService;
import com.leoly.fuckey.services.MoveKeyService;
import com.leoly.fuckey.utils.Stuls;
import com.leoly.fuckey.utils.ViewUtils;

/**
 * @author Administrator
 * 
 */
public class SettingActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fuckey_setting);
		CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Cs.START_KEY_SERVICE);
		cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference p, Object o) {
				if (Boolean.valueOf(o.toString())) {
					restartKeyService(false);
				} else {
					restartKeyService(true);
				}
				return true;
			}
		});

		SwitchPreference no_use_help = (SwitchPreference) findPreference("no_use_help");
		no_use_help
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Uri uri = Uri.parse("https://me.alipay.com/fullnexus4");
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						return false;
					}
				});

		SwitchPreference no_use_notice = (SwitchPreference) findPreference("no_use_notice");
		no_use_notice
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Intent it = new Intent();
						it.setClass(getApplicationContext(),
								ChangLogActivity.class);
						startActivity(it);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						return false;
					}
				});

		SwitchPreference floatkey_setting = (SwitchPreference) findPreference("floatkey_setting");
		floatkey_setting
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Intent it = new Intent();
						it.setClass(getApplicationContext(),
								FloatKeySetting.class);
						startActivity(it);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						return false;
					}
				});

		SwitchPreference movekey_setting = (SwitchPreference) findPreference("movekey_setting");
		movekey_setting
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Intent it = new Intent();
						it.setClass(getApplicationContext(),
								MoveKeySetting.class);
						startActivity(it);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						return false;
					}
				});

		CheckBoxPreference open_fullscreen = (CheckBoxPreference) findPreference(Cs.OPEN_FULLSCREEN);
		open_fullscreen
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						AlertDialog.Builder builder = new Builder(
								SettingActivity.this);
						builder.setTitle("重要提示！！");
						builder.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
						builder.setIcon(android.R.drawable.ic_dialog_info);
						builder.setMessage("由于此操作将会修改系统文件，而且各个手机的软件环境不一致，异常情况下可能会导致手机无法正常使用，所以本软件暂不提供全屏功能！您可以使用RE管理器打开/system/build.prop文件，手动添加qemu.hw.mainkeys=1这一行到文件末尾，注意不要有多余的空格和其它字符，保存后重启手机来手工完成此操作！");
						builder.show();
						return false;
					}
				});

		SwitchPreference movekey_resorted = (SwitchPreference) findPreference("movekey_resorted");
		movekey_resorted
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Intent it = new Intent();
						it.setClass(getApplicationContext(),
								KeySortedActivity.class);
						it.putExtra("FORCE_UPDATE", false);
						startActivity(it);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
						return false;
					}
				});

		final MultiSelectListPreference mslp = (MultiSelectListPreference) findPreference("custom_key");
		new Thread() {
			public void run() {
				Log.i(Cs.APP_NAME, "Load all applications!!");
				String[][] keys = ViewUtils
						.getAllViews(getApplicationContext());
				mslp.setEntries(keys[0]);
				mslp.setEntryValues(keys[1]);
			};
		}.start();
		mslp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Intent it = new Intent();
				it.setClass(getApplicationContext(), KeySortedActivity.class);
				it.putExtra("FORCE_UPDATE", true);
				startActivity(it);
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
				return true;
			}
		});
	}

	private void modifyBuildFile(final KeyEventExecuter executer) {
		boolean result = Stuls.modifyBuildFile(executer);
		if (result) {

			AlertDialog.Builder builder = new Builder(SettingActivity.this);
			builder.setTitle("重要提示！！");
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					executer.runRootCmd("reboot");
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage("系统文件修改完成，此操作需要重启才能生效，现在重启您的设备吗？");
			builder.show();
		} else {
			AlertDialog.Builder builder = new Builder(SettingActivity.this);
			builder.setTitle("重要提示！！");
			builder.setPositiveButton("确定", null);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage("系统文件无法修改，请使用RE管理器打开/system/build.prop文件，手动添加qemu.hw.mainkeys=1一行到文件末尾，保存后重启手机即可完成全屏。");
			builder.show();
		}
	}

	/**
	 * 重启按键服务
	 */
	private void restartKeyService(boolean isOnlyStop) {
		SharedPreferences prefer = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Class<?> service = null;
		switch (Integer.valueOf(prefer.getString("key_fire_type", "1"))) {
		case 1:
			service = FloatKeyService.class;
			break;
		case 2:
			service = MoveKeyService.class;
			break;
		default:
			service = FloatKeyService.class;
			break;
		}

		Intent intent1 = new Intent(this, FloatKeyService.class);
		stopService(intent1);
		Intent intent2 = new Intent(this, MoveKeyService.class);
		stopService(intent2);
		if (!isOnlyStop) {
			startService(new Intent(this, service));
		}
	}
}
