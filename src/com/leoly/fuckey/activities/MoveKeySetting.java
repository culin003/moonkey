/**
 * 
 */
package com.leoly.fuckey.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.leoly.fuckey.R;
import com.leoly.fuckey.utils.DensityUtil;
import com.leoly.fuckey.views.SeekBarPreference;

/**
 * @author leoly
 * 
 */
public class MoveKeySetting extends PreferenceActivity {
	private WindowManager windowManager;
	private LinearLayout layout;
	private LayoutParams appParams;
	private SharedPreferences prefer;
	private SeekBarPreference fire_height_range;
	private SeekBarPreference fire_weight_range;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.movekey_setting);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		prefer = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		int location = Integer.valueOf(prefer.getString("fire_location", "1"));

		// 创建测试窗口
		createTestWindow(location);

		ListPreference fire_location = (ListPreference) findPreference("fire_location");
		fire_location
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int value = Integer.valueOf(newValue.toString());
						setGravity(value);
						setSize(value);
						windowManager.updateViewLayout(layout, appParams);
						return true;
					}
				});

		fire_height_range = (SeekBarPreference) findPreference("fire_height_range");
		fire_height_range
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						appParams.height = DensityUtil.dip2px(Integer
								.valueOf(newValue.toString()));
						if (appParams.height == 0) {
							appParams.height = LayoutParams.FILL_PARENT;
						}
						windowManager.updateViewLayout(layout, appParams);
						return true;
					}
				});

		fire_weight_range = (SeekBarPreference) findPreference("fire_wight_range");
		fire_weight_range
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						appParams.width = DensityUtil.dip2px(Integer
								.valueOf(newValue.toString()));
						if (appParams.width == 0) {
							appParams.width = LayoutParams.FILL_PARENT;
						}
						windowManager.updateViewLayout(layout, appParams);
						return true;
					}
				});

		// 设置高度和宽度限制
		setSize(location);
	}

	private void setGravity(int value) {
		switch (value) {
		// 下
		case 1:
			appParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
			break;
		// 左
		case 2:
			appParams.gravity = Gravity.LEFT | Gravity.CENTER;
			break;
		// 右
		case 3:
			appParams.gravity = Gravity.RIGHT | Gravity.CENTER;
			break;
		default:
			appParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
			break;
		}
	}

	private void setSize(int location) {
		switch (location) {
		// 下
		case 1:
			fire_height_range.setMax(30);
			fire_weight_range.setMax(500);
			if (appParams.height > DensityUtil.dip2px(30)) {
				appParams.height = DensityUtil.dip2px(30);
				editPrefer("fire_height_range", 30, 1);
			}
			break;
		// 左
		case 2:
			// 右
		case 3:
			fire_weight_range.setMax(30);
			fire_height_range.setMax(500);
			if (appParams.width > DensityUtil.dip2px(30)) {
				appParams.width = DensityUtil.dip2px(30);
				editPrefer("fire_weight_range", 30, 1);
			}
			break;
		default:
			break;
		}
	}

	private void editPrefer(String key, Object value, int classType) {
		Editor editor = prefer.edit();
		switch (classType) {
		case 1:
			editor.putInt(key, Integer.valueOf(value.toString()));
			break;
		case 2:
			editor.putString(key, value.toString());
			break;
		case 3:
			editor.putBoolean(key, Boolean.valueOf(value.toString()));
			break;
		case 4:
			editor.putFloat(key, Float.valueOf(value.toString()));
			break;
		default:
			break;
		}

		editor.commit();
	}

	private void createTestWindow(int location) {
		DensityUtil.reset(getApplicationContext().getResources()
				.getDisplayMetrics());
		layout = (LinearLayout) LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.fire_buttom, null);
		layout.setBackgroundColor(Color.BLUE);
		windowManager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		appParams = new LayoutParams();
		setAppParams(location);
		windowManager.addView(layout, appParams);
	}

	private void setAppParams(int location) {
		// 关键属性
		setGravity(location);
		appParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		appParams.format = PixelFormat.RGBA_8888;
		appParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		appParams.flags = appParams.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		int width = prefer.getInt("fire_wight_range", 125);
		appParams.width = width == 0 ? WindowManager.LayoutParams.FILL_PARENT
				: DensityUtil.dip2px(width);
		int height = prefer.getInt("fire_height_range", 5);
		appParams.height = height == 0 ? WindowManager.LayoutParams.FILL_PARENT
				: DensityUtil.dip2px(height);
		appParams.windowAnimations = android.R.style.Animation_InputMethod;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != windowManager) {
			windowManager.removeView(layout);
		}
	}
}
